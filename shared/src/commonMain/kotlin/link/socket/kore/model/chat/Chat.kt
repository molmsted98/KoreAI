package link.socket.kore.model.chat

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole

sealed class Chat(
    open val role: ChatRole,
    open val chatMessage: ChatMessage,
) {
    data class System(
        val instructions: String,
    ) : Chat(
        ChatRole.System,
        ChatMessage(
            role = ChatRole.System,
            content = instructions,
        )
    )

    data class Text(
        override val role: ChatRole,
        val content: String,
        val functionName: String? = null,
    ) : Chat(
        role,
        ChatMessage(
            role = role,
            name = functionName,
            content = content,
        )
    ) {
        companion object {
            fun fromChatMessage(chatMessage: ChatMessage): Text =
                Text(
                    role = chatMessage.role,
                    functionName = chatMessage.name,
                    content = chatMessage.content ?: "",
                )
        }
    }

    data class CSV(
        override val role: ChatRole,
        val csvContent: CSVContent,
        val functionName: String? = null,
    ) : Chat(
        role,
        ChatMessage(
            role = role,
            name = functionName,
            content = with(csvContent) {
                val resultString = StringBuilder()
                forEach { line ->
                    line.forEach { cell ->
                        resultString.append(cell)
                        resultString.append("\t")
                    }
                    resultString.append("\n")
                }
                resultString.toString()
            },
        )
    )
}