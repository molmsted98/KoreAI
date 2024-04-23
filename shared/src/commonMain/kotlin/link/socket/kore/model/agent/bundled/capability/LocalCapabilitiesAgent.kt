package link.socket.kore.model.agent.bundled.capability

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput

data object LocalCapabilitiesAgent : AgentDefinition() {

    private lateinit var capabilities: String

    private val capabilitiesArg = AgentInput.ListArg(
        key = "capabilityList",
        name = "Capability Name",
        listValue = emptyList(),
    )

    override val name: String = "Local Capabilities"

    override val prompt: String
        get() = instructionsFrom(capabilities)

    override val neededInputs: List<AgentInput> = listOf(capabilitiesArg)

    override fun parseInputs(inputs: Map<String, AgentInput>) {
        super.parseInputs(inputs)
        capabilities = inputs[capabilitiesArg.key]?.value ?: ""
    }

    private fun instructionsFrom(capabilityList: String) =
        "You are an Agent with the following Capabilities that you are able to call via Function Tools:\n" +
                capabilityList + "\n\n" +
                "Tell the User about the Capabilities that you are able to provide, and help the User to execute " +
                "these Capabilities. Make sure to ask the User to provide any required arguments before you " +
                "attempt to execute any of your Capabilities."
}
