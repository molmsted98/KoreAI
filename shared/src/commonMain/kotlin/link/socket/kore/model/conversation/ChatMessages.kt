package link.socket.kore.model.conversation

import com.aallam.openai.api.chat.ChatMessage

fun List<KoreMessage>.append(
    newMessage: ChatMessage,
): List<KoreMessage> =
    mutableListOf(
        *this.toTypedArray(),
        KoreMessage.Text.fromChatMessage(newMessage),
    )

fun List<KoreMessage>.append(
    newMessage: KoreMessage
): List<KoreMessage> =
    mutableListOf(
        *this.toTypedArray(),
        newMessage,
    )
