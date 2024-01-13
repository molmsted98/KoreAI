package link.socket.kore.model.agent.capability

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.model.agent.LLMAgent
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition

sealed interface LLMCapability : Capability {

    data class PromptLLM(
        val openAI: OpenAI,
        val scope: CoroutineScope,
    ) : LLMCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provideSuspend(
                "promptLLM",
                "Requests a Chat completion from another instance of the LLM with the given prompt. " +
                        "The returned completion should be shown to the User in order for them to understand " +
                        "what this function has executed.",
                { args: JsonObject ->
                    val prompt = args.getValue("prompt").jsonPrimitive.content
                    promptLLM(openAI, scope, prompt)
                },
                listOf(
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

        private suspend fun promptLLM(
            openAI: OpenAI,
            scope: CoroutineScope,
            prompt: String,
        ): String {
            val tempAgent = object : LLMAgent {
                override val openAI: OpenAI = openAI
                override val scope: CoroutineScope = scope

                override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
                    set(value) {
                        field = value
                        updateCompletionRequest()
                    }

                override var completionRequest: ChatCompletionRequest? = null

                override val initialPrompt: String = prompt
            }

            return with(tempAgent) {
                initialize()
                execute()
                getChatMessages().lastOrNull()?.content ?: ""
            }
        }
    }
}