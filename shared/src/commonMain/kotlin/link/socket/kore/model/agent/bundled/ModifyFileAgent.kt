package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import link.socket.kore.model.agent.KoreAgent

data class ModifyFileAgent(
    override val openAI: OpenAI,
    val filepath: String,
    val description: String,
    val technologies: List<String>,
) : KoreAgent.HumanAssisted, KoreAgent.LLMAssisted() {

    companion object {
        const val NAME = "Change File"

        private fun instructionsFrom(technologies: List<String>): String =
            "You are a helpful assistant that is an expert programmer in:\n" +
                "${technologies.joinToString(", ")}.\n"

        private fun initialPromptFrom(filepath: String, description: String): String {
            // TODO: Get filepath contents
            val fileContents = ""

            return "You are tasked with making user-requested changes to an existing file.\n\n" +
                "Here are the original file contents:\n" +
                "$fileContents\n" +
                "\n\n" +
                "The description of the changes to make is:\n" +
                "$description\n" +
                "\n\n" +
                "Plan your solution step-by-step before making the necessary changes to this file."
        }
    }

    override val name: String = NAME
    override val instructions: String = instructionsFrom(technologies)
    override val initialPrompt: String = initialPromptFrom(filepath, description)
}
