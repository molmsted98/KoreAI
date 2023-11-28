package link.socket.kore.model.chat

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.thread.ThreadId
import link.socket.kore.model.chat.ChatHistory.Threaded.Uninitialized.chatMessages
import link.socket.kore.util.append

@OptIn(BetaOpenAI::class)
sealed interface ChatHistory {

    sealed class Threaded(
        open val assistantId: AssistantId?,
        open val chatMessages: Map<ThreadId, List<ChatMessage>>,
    ) : ChatHistory {

        data object Uninitialized : Threaded(null, emptyMap()) {

            override fun getMessages(): List<ChatMessage> = emptyList()

            override fun appendMessage(message: ChatMessage): Uninitialized {
                error("Attempt to appendMessage $message to ChatHistory.Threaded.Uninitialized")
            }

            override fun appendFunctionCallResponse(call: FunctionCall, response: String): ChatHistory {
                error("Attempt to appendFunctionCallResponse ($call, $response) to ChatHistory.Threaded.Uninitialized")
            }
        }

        data class Initialized(
            override val assistantId: AssistantId,
            override val chatMessages: Map<ThreadId, List<ChatMessage>>,
        ) : Threaded(assistantId, chatMessages) {

            override fun getMessages(): List<ChatMessage> {
                // TODO: Handle getting messages
                return emptyList()
            }

            override fun appendMessage(message: ChatMessage): Initialized {
                // TODO: Handle threaded message appending
                return this
            }

            override fun appendFunctionCallResponse(call: FunctionCall, response: String): Initialized {
                // TODO: Handle threaded message appending
                return this
            }
        }
    }

    data class NonThreaded(
        val chatMessages: List<ChatMessage>,
    ) : ChatHistory {

        override fun getMessages(): List<ChatMessage> = chatMessages

        override fun appendMessage(message: ChatMessage): NonThreaded =
            NonThreaded(chatMessages.append(message))

        override fun appendFunctionCallResponse(call: FunctionCall, response: String): ChatHistory =
            NonThreaded(chatMessages.append(call, response))
    }

    fun getMessages(): List<ChatMessage>
    fun appendMessage(message: ChatMessage): ChatHistory
    fun appendFunctionCallResponse(call: FunctionCall, response: String): ChatHistory
}
