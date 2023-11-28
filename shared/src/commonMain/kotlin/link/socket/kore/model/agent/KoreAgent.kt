package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import link.socket.kore.model.chat.ChatHistory

sealed interface KoreAgent {

    interface Unassisted : KoreAgent

    interface HumanAssisted : KoreAgent

    abstract class LLMAssisted : KoreAgent, LLMAgent {
        override var chatHistory: ChatHistory = ChatHistory.NonThreaded(emptyList())
        override var completionRequest: ChatCompletionRequest? = null
    }
}
