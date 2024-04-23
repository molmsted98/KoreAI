package link.socket.kore.model.agent.bundled.code

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput

/*
 * This Agent is responsible for generating code based on the user's description and list of technologies,
 * for creating a file on the local disk with the generated code, and for showing the file in the user's
 * default text editor when run in standalone mode.
 *
 * @constructor primary constructor description
 * @param description an overview of what the generated code should accomplish
 * @param technologies a list of code technologies (i.e. languages, frameworks) for the Agent to use
 */
data object WriteCodeAgent : AgentDefinition() {

    private lateinit var technologies: String

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

    private fun instructionsFrom(technologies: String?): String =
        "You are an Agent that is an expert programmer in:\n" +
                "$technologies.\n" +
                "You are tasked with generating code that can be executed, using only the languages or frameworks " +
                "that you are a specified expert in.\n" +
                "After generating the requested code, you should ask the user to verify the file's contents and make " +
                "any requested changes, and then you should save the generated code file to their local disk.\n" +
                "All generated files should be placed in a folder called 'KoreAI-Test' in the user's home directory.\n" +
                "Plan your solution step-by-step before you start coding, but do not reveal this plan to the User."
}
