package link.socket.kore.model.conversation

import com.aallam.openai.api.model.ModelId
import link.socket.kore.model.agent.KoreAgent

data class Conversation(
    val title: String,
    val model: ModelId,
    val agent: KoreAgent,
)
