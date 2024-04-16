package link.socket.kore.model.capability

import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.bundled.agentArgsList
import link.socket.kore.model.agent.bundled.agentNameList
import link.socket.kore.model.agent.bundled.getAgentDefinition
import link.socket.kore.model.chat.Chat
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

        // TODO: Change return type to List
        private suspend fun promptAgent(
            openAI: OpenAI,
            scope: CoroutineScope,
            agentName: String,
            prompt: String,
        ): String {
            val agent = KoreAgent.HumanAndLLMAssisted(
                conversationRepository,
                openAI,
                scope,
                agentName.getAgentDefinition(),
            )

            val initialMessage = Chat.Text(
                role = ChatRole.User,
                content = prompt,
            )

            val conversationId = conversationRepository.createConversation(agent, initialMessage)
            conversationRepository.runConversation(conversationId)

            return conversationRepository
                .getValue(conversationId)
                ?.conversationHistory
                ?.getKoreMessages()
                ?.lastOrNull()
                ?.chatMessage
                ?.content ?: ""
        }
    }
}