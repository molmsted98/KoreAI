package link.socket.kore.io

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.util.toSnakeCase

private const val EXPORT_FOLDER = "KoreAI-Test"
private const val FILE_EXTENSION = ".chatlog"

fun Conversation.exportToFile() {
    val filename = getFilename()
    val content = asString()
    createFile(EXPORT_FOLDER, filename, content)
}

fun Conversation.getFilename(): String =
    title.toSnakeCase().plus(FILE_EXTENSION)

fun Conversation.asString(): String =
        buildString {
            appendLine("---")
            appendLine("agent: ${agent.name}")
            appendLine("---")

            val chats = getChats()
            getChats().forEachIndexed { index, chat ->
                val chatRole = chat.role.role.capitalize(Locale.current)
                val chatLine = "\n" + "[$chatRole]" + "\n\n" + chat.chatMessage.content
                appendLine(chatLine)

                if (index != chats.size - 1) {
                    appendLine("\n---")
                }
            }
        }

