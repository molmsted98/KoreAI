package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import link.socket.kore.model.agent.KoreAgent

data class GenerateCodeAgent(
    override val openAI: OpenAI,
    val description: String,
    val technologies: List<String>,
) : KoreAgent.HumanAssisted, KoreAgent.LLMAssisted() {

    companion object {
        fun instructionsFrom(technologies: List<String>): String =
            "You are a helpful assistant that is an expert programmer in:\n" +
                "${technologies.joinToString(", ")}.\n"

        fun initialPromptFrom(description: String, technologies: List<String>): String =
            "You are tasked with writing code using the following technologies:\n" +
                "${technologies.joinToString(", ")}.\n" +
                "\n\n" +
                "The description of the task is:\n" +
                "$description\n" +
                "\n\n" +
                "Plan your solution step-by-step before you start coding."
    }

    override val instructions: String = instructionsFrom(technologies)
    override val initialPrompt: String = initialPromptFrom(description, technologies)
}
