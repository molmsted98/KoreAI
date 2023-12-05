@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import kotlinx.coroutines.launch
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.tool.FunctionProvider

sealed interface KoreAgent {

    val name: String

    interface Unassisted : KoreAgent {
        suspend fun executeUnassisted(): String
    }

    interface HumanAssisted : KoreAgent {
        suspend fun executeHumanAssisted(): String
    }

    abstract class HumanAndLLMAssisted : LLMAssisted(), HumanAssisted {

        override val availableFunctions: Map<String, FunctionProvider> = mapOf(
            FunctionProvider.provide(
                "executeHumanAssisted",
                "Prompts the user through a CLI to either enter text, or to confirm text that you have generated",
                ::launchExecuteHumanAssisted,
            )
        )

        fun launchExecuteHumanAssisted(): String {
            var response = ""

            scope.launch {
                response = executeHumanAssisted()
            }

            return response
        }
    }

    abstract class LLMAssisted : KoreAgent, LLMAgent {

        override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
            set(value) {
                field = value
                updateCompletionRequest()
            }

        override var completionRequest: ChatCompletionRequest? = null
    }
}
