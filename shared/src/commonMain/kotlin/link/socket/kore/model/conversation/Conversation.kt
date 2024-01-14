package link.socket.kore.model.conversation

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatRole
import link.socket.kore.model.agent.KoreAgent

typealias ConversationId = String

data class Conversation(
    val id: ConversationId,
    val title: String,
    val agent: KoreAgent,
    val chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
) {

    fun initialize(initialMessage: KoreMessage? = null): Conversation =
        if (chatHistory is ChatHistory.Threaded.Uninitialized) {
            copy(
                chatHistory = ChatHistory.NonThreaded(
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
            KoreMessage.Text(
                role = ChatRole.User,
                content = input,
            )
        )

    fun getCompletionRequest(): ChatCompletionRequest =
        agent.createCompletionRequest(chatHistory)


    fun getChatKoreMessages(): List<KoreMessage> =
        chatHistory.getKoreMessages()
            .filter { it.chatMessage.content?.isNotEmpty() == true }

    fun add(koreMessage: KoreMessage): Conversation =
        copy(
            chatHistory = chatHistory.appendKoreMessage(koreMessage),
        )
}
