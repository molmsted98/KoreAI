package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.conversation.selector.AgentInput

/*
 * This Agent is responsible for generating code based on the user's description and list of technologies,
 * for creating a file on the local disk with the generated code, and for showing the file in the user's
 * default text editor when run in standalone mode.
 *
 * @constructor primary constructor description
 * @param description an overview of what the generated code should accomplish
 * @param technologies a list of code technologies (i.e. languages, frameworks) for the Agent to use
 */
data class CreateCodeAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var description: String
    private lateinit var technologies: String

    companion object {
        const val NAME = "Write Code"

        private fun instructionsFrom(technologies: String): String =
            "You are a helpful assistant that is an expert programmer in:\n" +
                "$technologies.\n" +
                "You are tasked with generating code that can be executed, using only the languages or frameworks " +
                "that you are a specified expert in.\n" +
                "After generating the requested code, you should ask the user to verify the file's contents, " +
                "and then you should save the generated code file to their local disk.\n" +
                "All generated files should be placed in a folder called KoreAI-Test in the user's home directory."

        private fun initialPromptFrom(description: String): String =
            "The description of the task is:\n" +
                "$description\n" +
                "\n\n" +
                "Plan your solution step-by-step before you start coding."

        private val descriptionArg = AgentInput.StringArg(
            key = "Code Description",
            value = "",
        )

        private val technologiesArg = AgentInput.ListArg(
            key = "Technology List",
            textFieldLabel = "Technology Name",
            listValue = emptyList(),
        )
    }

    override val name: String = NAME
    override val instructions: String by lazy { instructionsFrom(technologies) }
    override val initialPrompt: String by lazy { initialPromptFrom(description) }
    override val neededInputs: List<AgentInput> = listOf(descriptionArg, technologiesArg)

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        description = inputs[descriptionArg.key]?.value ?: ""
        technologies = inputs[technologiesArg.key]?.value ?: ""
    }

    override suspend fun executeHumanAssistance(): String {
        // TODO: Implement human verification
        return "Test"
    }
}
