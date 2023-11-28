package link.socket.kore.model.agent.example

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import link.socket.kore.model.agent.FunctionDefinition
import link.socket.kore.model.agent.KoreAgent

@Serializable
data class WeatherInfo(val location: String, val temperature: String, val unit: String)

data class WeatherAgent(
    override val openAI: OpenAI
) : KoreAgent.LLMAssisted() {

    companion object {
        private const val INSTRUCTIONS =
            "You are a helpful weather assistant that is an expert in meteorology."

        private const val INITIAL_PROMPT =
            "What's the weather like in San Francisco, Detroit, and Paris?"
    }

    override val instructions: String = INSTRUCTIONS
    override val initialPrompt: String = INITIAL_PROMPT
    override val availableFunctions: Map<String, FunctionDefinition> = mapOf(
        "currentWeather" to (::callCurrentWeather to Tool.function(
            name = "currentWeather",
            description = "Get the current weather in a given location",
            parameters = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("location") {
                        put("type", "string")
                        put("description", "The city, e.g. San Francisco")
                    }
                    putJsonObject("unit") {
                        put("type", "string")
                        putJsonArray("enum") {
                            add("celsius")
                            add("fahrenheit")
                        }
                    }
                }
                putJsonArray("required") {
                    add("location")
                }
            }
        )),
    )
}

private fun callCurrentWeather(args: JsonObject): String {
    val location = args.getValue("location").jsonPrimitive.content
    val unit = args["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
    return currentWeather(location, unit)
}

private fun currentWeather(location: String, unit: String): String {
    val weatherData = mapOf(
        "Tokyo" to "10",
        "San Francisco" to "72"
    )
    val temperature = weatherData[location] ?: "22"
    val weatherInfo = WeatherInfo(location, temperature, unit)
    return Json.encodeToString(weatherInfo)
}
