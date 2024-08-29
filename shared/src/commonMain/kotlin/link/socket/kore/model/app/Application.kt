package link.socket.kore.model.app

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository

class Application(
    scope: CoroutineScope,
) {
    val conversationRepository = ConversationRepository(scope)
}