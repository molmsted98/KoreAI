package link.socket.kore.model.capability

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.*
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.bundled.agentArgsList
import link.socket.kore.model.agent.bundled.agentNameList
import link.socket.kore.model.agent.bundled.getAgentDefinition
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition

sealed interface AgentCapability : Capability {

    data object GetAgents : AgentCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provide(
                "getAgents",
                "Returns a list of available LLM Agents.",
                GetAgents::getAgents,
            )

        private fun getAgents(): String = agentNameList.joinToString(", ")
    }

    data object GetAgentArgs : AgentCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provide(
                "getAgentArgs",
                "Returns a list of available LLM Agents, along with their respective arguments.",
                GetAgentArgs::getAgentArgs,
            )

        private fun getAgentArgs(): String = agentArgsList.joinToString("\n\n")
    }

    data class PromptAgent(
        val conversationRepository: ConversationRepository,
        val openAI: OpenAI,
        val scope: CoroutineScope,
    ) : AgentCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provideSuspend(
                "promptAgent",
                "Requests a Chat completion from another LLM instance with the given prompt and an optional User response to the LLM's initial Chat. " +
                        "The completion that is returned from the other LLM instance should be shown to the User as part of your response.",
                { args: JsonObject ->
                    val agent = args.getOrElse("agent") {
                        JsonPrimitive("")
                    }.jsonPrimitive.content
                    val prompt = args.getValue("prompt").jsonPrimitive.content
                    val initialUserResponse = args.getOrElse("response") {
                        JsonPrimitive("")
                    }.jsonPrimitive.content

                    promptAgent(openAI, scope, agent, prompt, initialUserResponse)
                },
                listOf(
                    ParameterDefinition(
                        name = "agent",
                        isRequired = false,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The name of the LLM Agent that should be completing the prompt.")
                        }
                    ),
                    ParameterDefinition(
                        name = "prompt",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The System Instructions to use in case an Agent is not specified.")
                        }
                    ),
                    ParameterDefinition(
                        name = "response",
                        isRequired = false,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "An example of a User response to what the initial Agent response would be. This is used to return the _second_ generated Agent response.")
                        }
                    )
                )
            )

        // TODO: Change return type to List
        private suspend fun promptAgent(
            openAI: OpenAI,
            scope: CoroutineScope,
            agentName: String?,
            prompt: String,
            userResponse: String?,
        ): String {
            val agent = KoreAgent(
                openAI,
                scope,
                agentName.getAgentDefinition(prompt),
                conversationRepository,
            )

            val conversationId = conversationRepository.createConversation(agent, null)

            // Optionally get the second response after a User provides their initial reply
            if (userResponse != null) {
                // Get the initial response from just providing the System Instructions
                conversationRepository.runConversation(conversationId)
                conversationRepository.addUserChat(conversationId, userResponse)
            } else {
                // Otherwise get the initial response from just providing the System Instructions
                conversationRepository.runConversation(conversationId)
            }

            return conversationRepository
                .getValue(conversationId)
                ?.conversationHistory
                ?.getChats()
                ?.lastOrNull()
                ?.chatMessage
                ?.content ?: ""
        }
    }
}