package link.socket.kore.model.agent.bundled.code

import link.socket.kore.model.agent.AgentDefinition

object CleanJsonAgent : AgentDefinition() {

    override val name: String = "Clean JSON"

    override val prompt: String = """
        You are an Agent that is an expert in understanding JSON parsing. 
        Start by asking the User for their input JSON data.
        Plan your solution step-by-step before responding to the User.
        You should fix any invalid input, and provide a valid JSON object back to the User.
    """.trimIndent()
}
