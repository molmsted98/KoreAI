package link.socket.kore.model.agent.bundled.kore

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.chat.system.Instructions

data class LocalCapabilitiesAgent(
    val inputMap: Map<String, AgentInput>
) : AgentDefinition {

    private var capabilities: String

    private val capabilitiesArg = AgentInput.ListArg(
        key = "capabilityList",
        name = "Capability Name",
        listValue = emptyList(),
    )

    override val name: String = NAME

    override val instructions: Instructions
        get() = Instructions(instructionsFrom(capabilities))

    override val inputs: List<AgentInput> = listOf(capabilitiesArg)

    init {
        capabilities = inputMap[capabilitiesArg.key]?.value ?: ""
    }

    companion object {
        const val NAME = "Local Capabilities"

        private fun instructionsFrom(capabilityList: String) =
            "You are an Agent with the following Capabilities that you are able to call via Function Tools:\n" +
                    capabilityList + "\n\n" +
                    "Tell the User about the Capabilities that you are able to provide, and help the User to execute " +
                    "these Capabilities. Make sure to ask the User to provide any required arguments before you " +
                    "attempt to execute any of your Capabilities."
    }
}
