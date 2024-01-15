package link.socket.kore.model.agent

interface AgentDefinition {
    val name: String
    val instructions: String
    val inputs: List<AgentInput>
        get() = emptyList()
    fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        /* no-op */
    }
}