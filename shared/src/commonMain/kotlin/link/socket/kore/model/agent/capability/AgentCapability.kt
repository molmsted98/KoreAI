package link.socket.kore.model.agent.capability

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.model.agent.bundled.*
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition
import link.socket.kore.ui.conversation.selector.AgentInput

sealed interface AgentCapability : Capability {

    data object GetAgents : AgentCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provide(
                "getAgents",
                "Returns a list of available LLM Agents, along with .",
                ::getAgents,
            )

        private fun getAgents(): String = listOf(
            CleanJsonAgent.NAME to CleanJsonAgent.INPUTS,
            DefineAgentAgent.NAME to DefineAgentAgent.INPUTS,
            DelegateTasksAgent.NAME to DelegateTasksAgent.INPUTS,
            FinancialAgent.NAME to FinancialAgent.INPUTS,
            LocalCapabilitiesAgent.NAME to emptyList(),
            ModifyFileAgent.NAME to ModifyFileAgent.INPUTS,
            WriteCodeAgent.NAME to WriteCodeAgent.INPUTS,
        ).joinToString("\n\n") { (name, inputs) ->
            "$name(" + (inputs.joinToString(", ") { input ->
                input.key + ": " + when (input) {
                    is AgentInput.StringArg -> "String"
                    is AgentInput.ListArg -> "List<String>"
                }
            }) + ")"
        }
    }

    data class PromptAgent(
        val openAI: OpenAI,
        val scope: CoroutineScope,
    ) : AgentCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provideSuspend(
                "promptAgent",
                "Requests a Chat completion from another LLM Agent instance with the given prompt. " +
                        "The returned completion should be shown to the User in order for them to understand " +
                        "what this function has executed.",
                { args: JsonObject ->
                    val agent = args.getValue("agent").jsonPrimitive.content
                    val prompt = args.getValue("prompt").jsonPrimitive.content
                    promptAgent(openAI, scope, agent, prompt)
                },
                listOf(
                    ParameterDefinition(
                        name = "agent",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The name of the LLM Agent that will be completing the prompt.")
                        }
                    ),
                    ParameterDefinition(
                        name = "prompt",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The prompt that needs to be completed by an LLM.")
                        }
                    )
                )
            )

        private suspend fun promptAgent(
            openAI: OpenAI,
            scope: CoroutineScope,
            agent: String,
            prompt: String,
        ): String {
            val agent = when (agent) {
                CleanJsonAgent.NAME -> CleanJsonAgent(openAI, scope)
                DefineAgentAgent.NAME -> DefineAgentAgent(openAI, scope)
                DelegateTasksAgent.NAME -> DelegateTasksAgent(openAI, scope)
                FinancialAgent.NAME -> FinancialAgent(openAI, scope)
                LocalCapabilitiesAgent.NAME -> LocalCapabilitiesAgent(openAI, scope)
                ModifyFileAgent.NAME -> ModifyFileAgent(openAI, scope)
                WriteCodeAgent.NAME -> WriteCodeAgent(openAI, scope)
                else -> throw IllegalArgumentException("Unknown Agent $agent")
            }

            return with(agent) {
                initialize()
                execute()
                getChatMessages().lastOrNull()?.content ?: ""
            }
        }
    }
}