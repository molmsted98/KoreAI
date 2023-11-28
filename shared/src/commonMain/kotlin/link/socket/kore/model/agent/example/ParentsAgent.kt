package link.socket.kore.model.agent.example

import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition

data class ParentsAgent(
    override val openAI: OpenAI
) : KoreAgent.LLMAssisted() {

    private var parentRoles: Pair<String, String>? = null

    companion object {
        private const val INSTRUCTIONS =
            "You are a helpful assistant that knows about my family."

        private const val INITIAL_PROMPT =
            "What are my parent's names?"
    }

    override val instructions: String = INSTRUCTIONS
    override val initialPrompt: String = INITIAL_PROMPT

    override val availableFunctions: Map<String, FunctionProvider> = mapOf(
        FunctionProvider.provide(
            name = "parentName",
            description = "Get the name of a particular parent.",
            function = ::callParentName,
            parameterList = listOf(
                ParameterDefinition(
                    name = "role",
                    isRequired = true,
                    definition = buildJsonObject {
                        put("type", "string")
                        put("description", "The parent that you are querying for; e.g. Mom, Dad, Father, etc.")
                        putJsonArray("enum") {
                            add("Mom")
                            add("Dad")
                        }
                    },
                )
            )
        )
    )
}

private fun callSetParentRoles(args: JsonObject): String {
    val role = args.getValue("role").jsonPrimitive.content
    return parentName(role)
}

private fun setParentRoles(parentRoles: Pair<String, String>) {

}

private fun callParentName(args: JsonObject): String {
    val role = args.getValue("role").jsonPrimitive.content
    return parentName(role)
}

private fun parentName(role: String): String {
    val parentData = mapOf(
        "Mom" to "Lynn",
        "Dad" to "James",
    )
    return parentData[role] ?: "Unknown"
}
