package link.socket.kore.ui.selection

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput

sealed class AgentSelectionState {

    data class Unselected(
        val agentList: List<AgentDefinition>,
    ) : AgentSelectionState() {
        override val agentName: String? = null
    }

    data class PartiallySelected(
        val agent: AgentDefinition,
        val neededInputs: List<AgentInput>,
    ) : AgentSelectionState() {
        override val agentName: String = agent.name
    }

    data class Selected(
        val agent: AgentDefinition,
    ) : AgentSelectionState() {
        override val agentName: String = agent.name
    }

    abstract val agentName: String?
}
