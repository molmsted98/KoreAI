package link.socket.kore.model.capability

import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.conversation.KoreMessage
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition

sealed interface LLMCapability : Capability {

    data class PromptLLM(
        val conversationRepository: ConversationRepository,
        val openAI: OpenAI,
        val scope: CoroutineScope,
    ) : LLMCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provideSuspend(
                "promptLLM",
                "Requests a Chat completion from another instance of the LLM with the given " +
                        "instructions and prompt. " +
                        "The returned completion should be shown to the User in order for them to understand " +
                        "what this function has executed.",
                { args: JsonObject ->
                    val instructions = args.getValue("instructions").jsonPrimitive.content
                    val prompt = args.getValue("prompt").jsonPrimitive.content
                    promptLLM(openAI, scope, instructions, prompt)
                },
                listOf(
                    ParameterDefinition(
                        name = "instructions",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The System instructions that are given to the LLM.")
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
        private suspend fun promptLLM(
            openAI: OpenAI,
            scope: CoroutineScope,
            instructions: String,
            prompt: String,
        ): String {
            val agentDefinition = object : AgentDefinition {
                override val name: String = ""
                override val instructions: String = instructions
            }

            val tempAgent = KoreAgent.HumanAndLLMAssisted(conversationRepository, openAI, scope, agentDefinition)

            val initialMessage = KoreMessage.Text(
                role = ChatRole.User,
                content = prompt,
            )

            val conversationId = conversationRepository.createConversation(tempAgent, initialMessage)
            conversationRepository.runConversation(conversationId)

            return conversationRepository
                .getValue(conversationId)
                ?.chatHistory
                ?.getKoreMessages()
                ?.lastOrNull()
                ?.chatMessage
                ?.content ?: ""
        }
    }
}