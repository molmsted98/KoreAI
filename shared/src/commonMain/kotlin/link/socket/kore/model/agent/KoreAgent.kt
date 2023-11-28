package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import link.socket.kore.model.chat.ChatHistory

sealed interface KoreAgent {

    interface Unassisted : KoreAgent
    interface HumanAssisted : KoreAgent

    abstract class HumanAndLLMAssisted : HumanAssisted, LLMAgent {
        override var chatHistory: ChatHistory = ChatHistory.NonThreaded(emptyList())
        override var completionRequest: ChatCompletionRequest? = null
    }

    abstract class LLMAssisted : KoreAgent, LLMAgent {
        override var chatHistory: ChatHistory = ChatHistory.NonThreaded(emptyList())
        override var completionRequest: ChatCompletionRequest? = null

        override fun addUserChat(input: String) {
            error("Cannot use this function, please extend the HumanAndLLMAssisted class instead of LLMAssisted")
        }
    }
}
