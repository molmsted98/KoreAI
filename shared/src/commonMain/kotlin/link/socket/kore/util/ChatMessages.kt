package link.socket.kore.util

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall

fun List<ChatMessage>.append(newMessage: ChatMessage): List<ChatMessage> =
    mutableListOf(
        *this.toTypedArray(),
        ChatMessage(
            role = newMessage.role,
            content = newMessage.content.orEmpty(),
        )
    )

fun List<ChatMessage>.append(
    functionCall: FunctionCall,
    functionResponse: String,
): List<ChatMessage> =
    mutableListOf(
        *this.toTypedArray(),
        ChatMessage(
            role = ChatRole.Function,
            name = functionCall.name,
            content = functionResponse,
        )
    )
