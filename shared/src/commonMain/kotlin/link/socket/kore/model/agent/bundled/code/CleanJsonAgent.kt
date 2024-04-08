package link.socket.kore.model.agent.bundled.code

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.chat.system.Instructions

object CleanJsonAgent : AgentDefinition {

    override val name: String = "Clean JSON"

    override val instructions: Instructions = Instructions(
        "You an Agent that is an expert in understanding JSON parsing. Plan your solution step-by-step " +
                "before you fix the invalid input, and provide a valid JSON object back to the User."
    )
}
