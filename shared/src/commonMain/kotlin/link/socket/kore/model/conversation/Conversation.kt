package link.socket.kore.model.conversation

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatRole
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.chat.Chat

typealias ConversationId = String

data class Conversation(
    val id: ConversationId,
    val title: String,
    val agent: KoreAgent,
    val conversationHistory: ConversationHistory = ConversationHistory.Threaded.Uninitialized
) {

    fun initialize(initialMessage: Chat? = null): Conversation =
        if (conversationHistory is ConversationHistory.Threaded.Uninitialized) {
            copy(
                conversationHistory = ConversationHistory.NonThreaded(
                    initialMessage?.let { message ->
                        listOf(agent.initialSystemMessage, message)
                    } ?: listOf(agent.initialSystemMessage)
                )
            )
        } else {
            // TODO: Allow for Threaded history
            this
        }

    fun addUserChat(input: String): Conversation =
        add(
            Chat.Text(
                role = ChatRole.User,
                content = input,
            )
        )

    fun getCompletionRequest(): ChatCompletionRequest =
        agent.createCompletionRequest(conversationHistory)


    fun getChatKoreMessages(): List<Chat> =
        conversationHistory.getKoreMessages()
            .filter { it.chatMessage.content?.isNotEmpty() == true }

    fun add(chat: Chat): Conversation =
        copy(
            conversationHistory = conversationHistory.appendKoreMessage(chat),
        )
}
