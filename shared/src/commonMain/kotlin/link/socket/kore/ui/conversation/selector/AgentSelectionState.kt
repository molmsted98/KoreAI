package link.socket.kore.ui.conversation.selector

import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.KoreAgent

sealed class AgentSelectionState {

    data class Unselected(
        val agentList: List<KoreAgent>,
    ) : AgentSelectionState() {
        override val agentName: String? = null
    }

    data class PartiallySelected(
        val agent: KoreAgent,
        val neededInputs: List<AgentInput>,
    ) : AgentSelectionState() {
        override val agentName: String = agent.name
    }

    data class Selected(
        val agent: KoreAgent,
    ) : AgentSelectionState() {
        override val agentName: String = agent.name
    }

    abstract val agentName: String?
}
