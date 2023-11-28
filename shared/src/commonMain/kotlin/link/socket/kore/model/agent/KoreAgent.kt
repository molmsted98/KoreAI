package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import link.socket.kore.model.chat.ChatHistory

sealed interface KoreAgent {

    interface Unassisted : KoreAgent

    interface HumanAssisted : KoreAgent {
        fun humanEditString(originalValue: String?): String? {
            // TODO: Prompt user with system editor on String
            return originalValue
        }
    }

    abstract class LLMAssisted : KoreAgent, LLMAgent {

        override var chatHistory: ChatHistory = ChatHistory.NonThreaded(emptyList())
        override var completionRequest: ChatCompletionRequest? = null

        fun handleResult(result: String) {

        }
    }
}
