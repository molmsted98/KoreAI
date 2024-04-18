package link.socket.kore.util

/*
 * Converts "Agent Conversation" to "agent_conversation"
 */
fun String.toSnakeCase(): String =
    lowercase().replace(" ", "_")

