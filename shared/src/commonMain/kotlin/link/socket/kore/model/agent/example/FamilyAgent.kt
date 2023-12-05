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

data class FamilyAgent(
    override val openAI: OpenAI
) : KoreAgent.HumanAndLLMAssisted()  {

    companion object {
        const val NAME = "Family Information"

        private const val INSTRUCTIONS =
            "You are a helpful assistant that knows about my family. To avoid perpetuating cultural stereotypes, " +
                "you should prompt the user to describe which roles each of their parents fit (e.g. Mom, Dad, Grandma), " +
                "and how many parents they have."

        private const val INITIAL_PROMPT =
            "What are my parent's names?"
    }

    override val name: String = NAME
    override val instructions: String = INSTRUCTIONS
    override val initialPrompt: String = INITIAL_PROMPT

    override val availableFunctions: Map<String, FunctionProvider> = mapOf(
        FunctionProvider.provide(
            name = "parentName",
            description = "Get the name of a particular parent based on their role.",
            function = ::callParentName,
            parameterList = listOf(
                ParameterDefinition(
                    name = "role",
                    isRequired = true,
                    definition = buildJsonObject {
                        put("type", "string")
                        put("description", "The parental role that you are querying for; e.g. Mom, Dad, Father, Grandma, etc.")
                        putJsonArray("enum") {
                            add("Mom")
                            add("Dad")
                            add("Other")
                        }
                    },
                )
            )
        )
    )

    override suspend fun executeHumanAssisted(): String {
        // TODO: Implement human verification
        return "Test"
    }
}

private fun callParentName(args: JsonObject): String {
    val role = args.getValue("role").jsonPrimitive.content
    return parentName(role)
}

private fun parentName(role: String): String {
    val parentData = mapOf(
        "Mom" to "Lynn",
        "Dad" to "James",
        "Other" to "Parker",
    )
    return parentData[role] ?: "Unknown"
}
