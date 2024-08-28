package link.socket.kore.model.agent.bundled.prompt

import link.socket.kore.model.agent.AgentDefinition

class TestAgentAgent(override val prompt: String) : AgentDefinition() {
    override val name: String = "Empty System Prompt"
}