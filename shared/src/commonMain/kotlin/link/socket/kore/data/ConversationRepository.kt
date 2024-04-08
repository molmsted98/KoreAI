package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.model.conversation.ConversationId
import link.socket.kore.model.chat.Chat
import link.socket.kore.util.randomUUID

class ConversationRepository(
    override val scope: CoroutineScope,
) : Repository<ConversationId, Conversation>(scope) {

    private var selectedConversation: ConversationId? = null

    fun createConversation(
        agent: KoreAgent,
        initialMessage: Chat? = null,
    ): ConversationId {
        val key = randomUUID()
        selectedConversation = key

        val conversation = Conversation(
            id = key,
            title = "Test Conversation",
            agent = agent,
        )
        storeValue(key, conversation.initialize(initialMessage))

        return key
    }

    suspend fun runConversation(conversationId: ConversationId) {
        var shouldRerun = false

        do {
            getValue(conversationId)?.apply {
                val completionRequest = getCompletionRequest()
                val (newMessages, ranTools) = agent.execute(completionRequest)

                newMessages.forEach { message ->
                    storeValue(conversationId, add(message))
                }

                shouldRerun = ranTools
            }
        } while (shouldRerun)
    }

    suspend fun addUserChat(conversationId: ConversationId, input: String) {
        getValue(conversationId)?.apply {
            storeValue(conversationId, addUserChat(input))
            runConversation(conversationId)
        }
    }
}