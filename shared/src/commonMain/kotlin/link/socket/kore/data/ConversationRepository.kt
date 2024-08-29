package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.chat.Chat
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.model.conversation.ConversationId
import link.socket.kore.util.randomUUID

/**
 * ConversationRepository handles the creation and execution of conversations as well as adding user chats to a given conversation.
 *
 * @property scope - CoroutineScope for managing coroutines
 */
class ConversationRepository(
    override val scope: CoroutineScope,
) : Repository<ConversationId, Conversation>(scope) {

    private var selectedConversation: ConversationId? = null

    /**
     * Creates a new conversation with the given agent and an optional initial message.
     * The new conversation is initialized and stored in the repository.
     *
     * @param agent The agent to be used in the conversation
     * @param initialMessage An optional initial chat message
     * @return The ConversationId of the created conversation
     */
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

    /**
     * Runs the conversation with the given ConversationId. It repeatedly processes
     * the completion request until there are no more tools to execute.
     *
     * @param conversationId The ID of the conversation to be run
     */
    suspend fun runConversation(conversationId: ConversationId) {
        var shouldRerun = false

        do {
            getValue(conversationId)?.apply {
                val completionRequest = getCompletionRequest()
                val ranTools = agent.execute(completionRequest) { message ->
                    storeValue(conversationId, add(message))
                }

                shouldRerun = ranTools
            }
        } while (shouldRerun)
    }

    /**
     * Adds a user chat to the conversation with the given ConversationId.
     * After adding the user chat, it runs the conversation.
     *
     * @param conversationId The ID of the conversation
     * @param input The user input to be added as a chat
     */
    suspend fun addUserChat(conversationId: ConversationId, input: String) {
        getValue(conversationId)?.apply {
            storeValue(conversationId, addUserChat(input))
            runConversation(conversationId)
        }
    }
}
