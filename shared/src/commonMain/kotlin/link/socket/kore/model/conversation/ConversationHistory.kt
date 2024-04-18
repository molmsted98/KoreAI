package link.socket.kore.model.conversation

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.thread.ThreadId
import link.socket.kore.model.chat.Chat

@OptIn(BetaOpenAI::class)
sealed interface ConversationHistory {

    sealed class Threaded(
        open val assistantId: AssistantId?,
        open val chatMessages: Map<ThreadId, List<Chat>>,
    ) : ConversationHistory {

        data object Uninitialized : Threaded(null, emptyMap()) {

            override fun getChats(): List<Chat> = emptyList()

            override fun appendMessage(message: ChatMessage): Uninitialized {
                error("Attempt to appendMessage $message to ChatHistory.Threaded.Uninitialized")
            }

            override fun appendChat(message: Chat): ConversationHistory {
                TODO("Not yet implemented")
            }
        }

        data class Initialized(
            override val assistantId: AssistantId,
            override val chatMessages: Map<ThreadId, List<Chat>>,
        ) : Threaded(assistantId, chatMessages) {

            override fun getChats(): List<Chat> {
                TODO("Not yet implemented")
            }

            override fun appendMessage(message: ChatMessage): Initialized {
                // TODO: Handle threaded message appending
                return this
            }

            override fun appendChat(message: Chat): ConversationHistory {
                TODO("Not yet implemented")
            }
        }
    }

    data class NonThreaded(
        val messages: List<Chat>,
    ) : ConversationHistory {

        override fun getChats(): List<Chat> = messages

        override fun appendChat(message: Chat): ConversationHistory =
            NonThreaded(messages.append(message))

        override fun appendMessage(message: ChatMessage): NonThreaded =
            NonThreaded(messages.append(message))
    }

    fun getChats(): List<Chat>
    fun appendMessage(message: ChatMessage): ConversationHistory
    fun appendChat(message: Chat): ConversationHistory
}

fun List<Chat>.append(
    newMessage: ChatMessage,
): List<Chat> =
    mutableListOf(
        *this.toTypedArray(),
        Chat.Text.fromChatMessage(newMessage),
    )

fun List<Chat>.append(
    newMessage: Chat
): List<Chat> =
    mutableListOf(
        *this.toTypedArray(),
        newMessage,
    )
