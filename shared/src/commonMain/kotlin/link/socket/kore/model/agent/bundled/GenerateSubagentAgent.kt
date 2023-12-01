package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import link.socket.kore.model.agent.KoreAgent

data class GenerateSubagentAgent(
    override val openAI: OpenAI,
    private val description: String,
) : KoreAgent.HumanAssisted, KoreAgent.LLMAssisted() {

    companion object {
        const val NAME = "Delegate Tasks"

        private fun instructionsFrom(): String {
            // TODO: Get code from files
            val generateCodeAgentContent = ""
            val saveFileAgentContent = ""

            return "You are a helpful assistant that is an expert programmer in Kotlin and at delegating Subagent LLM Agents.\n" +
                "\n\n" +
                "A Subagent can generate information from an LLM like this:\n" +
                "$generateCodeAgentContent\n" +
                "\n" +
                "Or a Subagent can perform a particular task like this:\n" +
                "$saveFileAgentContent\n" +
                "\n\n" +
                "A Subagent has an execute method, and initialize method that takes input, a " +
                "result function that describes the expected output from the LLM (if it is using LLM " +
                "assistance) and a prompt that may or may not use the input.\n" +
                "The LLMAssisted interface has a generateString method that takes the prompt and result " +
                "function and sends it to an LLM for processing\n" +
                "The HumanAssisted interface presents the string to a human for verification and " +
                "potential modification."
        }

        private fun initialPromptFrom(description: String): String =
            "You need to build a new Sub-agent agent that does the following:\n" +
                "$description\n\n" +
                "Plan your solution step-by-step to ensure work is appropriately delegated to Subagents before you start."
    }

    override val name: String = NAME
    override val instructions: String = instructionsFrom()
    override val initialPrompt: String = initialPromptFrom(description)
}
