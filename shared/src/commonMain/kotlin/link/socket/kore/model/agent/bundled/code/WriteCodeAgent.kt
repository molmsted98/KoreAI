package link.socket.kore.model.agent.bundled.code

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput

data object WriteCodeAgent : AgentDefinition() {

    private var technologies: String = "Any language or framework"

    private val technologiesArg = AgentInput.ListArg(
        key = "technologyList",
        name = "Technology Name",
        listValue = emptyList(),
    )

    override val name: String = "Write Code"

    override val prompt: String
        get() = instructionsFrom(technologies)

    override val neededInputs: List<AgentInput> = listOf(technologiesArg)

    override fun parseInputs(inputs: Map<String, AgentInput>) {
        technologies = inputs[technologiesArg.key]?.value ?: ""
    }

    private fun instructionsFrom(technologies: String?): String = """
        You are an Agent that is a programming expert, and are proficient in the following technologies: $technologies.
        Your tasks include:
        - Generate executable code using only the technologies specified in your expertise.
        - Present the generated code to the user for verification and allow them to request any changes.
        - Save the final version of the code to the user's local disk.
        - Ensure all generated files are saved in a folder named 'KoreAI-Test' located in the user's home directory.

        Note: Before writing the code, plan your solution step-by-step.
        
        Be sure to adhere to any request from the User regarding constraints around the code that you should be creating.
        Example: If a User asks to improve documentation, ensure that the code you generate _only_ includes comments and does not modify the existing code.
    """.trimIndent()
}
