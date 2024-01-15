package link.socket.kore.model.agent.bundled.code

import link.socket.kore.model.agent.AgentDefinition

object CleanJsonAgent : AgentDefinition {

    override val name: String = "Clean JSON"

    override val instructions: String =
        "You an Agent that is an expert in understanding JSON parsing. Plan your solution step-by-step " +
                "before you fix the invalid input, and provide a valid JSON object back to the User."
}
