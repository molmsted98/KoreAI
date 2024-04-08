package link.socket.kore.model.agent

import link.socket.kore.model.chat.system.Instructions

interface AgentDefinition {
    val name: String
    val instructions: Instructions
    val inputs: List<AgentInput>
        get() = emptyList()
    fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        /* no-op */
    }
}