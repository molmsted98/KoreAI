package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent


/*
 * This Agent is responsible for generating code based on the user's description and list of technologies,
 * for creating a file on the local disk with the generated code, and for showing the file in the user's
 * default text editor when run in standalone mode.
 *
 * @constructor primary constructor description
 * @param description an overview of what the generated code should accomplish
 * @param technologies a list of code technologies (i.e. languages, frameworks) for the Agent to use
 */
data class GenerateCodeAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
    val description: String,
    val technologies: List<String>,
) : KoreAgent.HumanAndLLMAssisted() {

    companion object {
        const val NAME = "Write Code"

        private fun instructionsFrom(technologies: List<String>): String =
            "You are a helpful assistant that is an expert programmer in:\n" +
                "${technologies.joinToString(", ")}.\n" +
                "You are tasked with generating code that can be executed, using only the languages or frameworks " +
                "that you are a specified expert in.\n" +
                "After generating the requested code, you should ask the user to verify the file's contents, " +
                "and then you should save the generated code file to their local disk."

        private fun initialPromptFrom(description: String): String =
                "The description of the task is:\n" +
                "$description\n" +
                "\n\n" +
                "Plan your solution step-by-step before you start coding."
    }

    override val name: String = NAME
    override val instructions: String = instructionsFrom(technologies)
    override val initialPrompt: String = initialPromptFrom(description)

    override suspend fun executeHumanAssisted(): String {
        // TODO: Implement human verification
        return "Test"
    }
}
