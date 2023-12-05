package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import link.socket.kore.model.conversation.ChatHistory

sealed interface KoreAgent {

    val name: String

    interface Unassisted : KoreAgent {
        suspend fun executeUnassisted(): String
    }

    interface HumanAssisted : KoreAgent {
        suspend fun executeHumanAssisted(): String
    }

    abstract class HumanAndLLMAssisted : LLMAssisted(), HumanAssisted

    abstract class LLMAssisted : KoreAgent, LLMAgent {

        override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
            set(value) {
                field = value
                updateCompletionRequest()
            }

        override var completionRequest: ChatCompletionRequest? = null
    }
}
