package link.socket.kore.model.tool

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.reflect.KFunction1

typealias LLMFunction = KFunction1<JsonObject, String>
typealias FunctionDefinition = Pair<LLMFunction, Tool>

data class ParameterDefinition(
    val name: String,
    val isRequired: Boolean,
    val definition: JsonObject,
)

abstract class FunctionProvider(
    val name: String,
    val definition: FunctionDefinition,
) {

    companion object {
        fun functionProvider(
            name: String,
            description: String,
            function: LLMFunction,
            parametersJson: String,
        ): FunctionProvider = object : FunctionProvider(
            name,
            functionImpl(name, description, function, parametersJson)
        ) {}

        fun functionProvider(
            name: String,
            description: String,
            function: LLMFunction,
            parameterList: List<ParameterDefinition>,
        ): FunctionProvider = object : FunctionProvider(
            name,
            functionImpl(name, description, function, parameterList)
        ) {}

        private fun functionImpl(
            name: String,
            description: String,
            function: KFunction1<JsonObject, String>,
            parametersJson: String,
        ): FunctionDefinition =
            function to Tool.function(
                name = name,
                description = description,
                parameters = Parameters.fromJsonString(parametersJson),
            )

        private fun functionImpl(
            name: String,
            description: String,
            function: KFunction1<JsonObject, String>,
            parameterList: List<ParameterDefinition>,
        ): FunctionDefinition =
            function to Tool.function(
                name = name,
                description = description,
                parameters = Parameters.buildJsonObject {
                    put("type", "object")
                    putJsonObject("properties") {
                        parameterList.forEach { parameter ->
                            put(parameter.name, parameter.definition)
                        }
                    }
                    putJsonArray("required") {
                        parameterList.forEach { parameter ->
                            if (parameter.isRequired) {
                                add(parameter.name)
                            }
                        }
                    }
                }
            )
    }
}
