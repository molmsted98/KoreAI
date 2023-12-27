package link.socket.kore.model.conversation

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.thread.ThreadId

@OptIn(BetaOpenAI::class)
sealed interface ChatHistory {

    sealed class Threaded(
        open val assistantId: AssistantId?,
        open val chatMessages: Map<ThreadId, List<KoreMessage>>,
    ) : ChatHistory {

        data object Uninitialized : Threaded(null, emptyMap()) {

            override fun getKoreMessages(): List<KoreMessage> = emptyList()

            override fun getChatMessages(): List<ChatMessage> = emptyList()

            override fun appendMessage(message: ChatMessage): Uninitialized {
                error("Attempt to appendMessage $message to ChatHistory.Threaded.Uninitialized")
            }

            override fun appendKoreMessage(message: KoreMessage): ChatHistory {
                TODO("Not yet implemented")
            }
        }

        data class Initialized(
            override val assistantId: AssistantId,
            override val chatMessages: Map<ThreadId, List<KoreMessage>>,
        ) : Threaded(assistantId, chatMessages) {

            override fun getKoreMessages(): List<KoreMessage> {
                TODO("Not yet implemented")
            }

            override fun getChatMessages(): List<ChatMessage> {
                // TODO: Handle getting messages
                return emptyList()
            }

            override fun appendMessage(message: ChatMessage): Initialized {
                // TODO: Handle threaded message appending
                return this
            }

            override fun appendKoreMessage(message: KoreMessage): ChatHistory {
                TODO("Not yet implemented")
            }
        }
    }

    data class NonThreaded(
        val messages: List<KoreMessage>,
    ) : ChatHistory {

        override fun getKoreMessages(): List<KoreMessage> = messages

        override fun getChatMessages(): List<ChatMessage> = messages.map { it.chatMessage }

        override fun appendMessage(message: ChatMessage): NonThreaded =
            NonThreaded(messages.append(message))

        override fun appendKoreMessage(message: KoreMessage): ChatHistory =
            NonThreaded(messages.append(message))
    }

    fun getKoreMessages(): List<KoreMessage>
    fun getChatMessages(): List<ChatMessage>
    fun appendMessage(message: ChatMessage): ChatHistory
    fun appendKoreMessage(message: KoreMessage): ChatHistory
}
