package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.KoreAgent

data class DelegateTasksAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var description: String

    companion object {
        const val NAME = "Delegate Tasks"

        private const val INSTRUCTIONS =
            "You are a strategic Agent that is an expert in delegating a large-scale task to various LLM Subagents." +
                "\n\n" +
                "Each Subagent can generate information from an LLM by prompting it for a chat completion, " +
                "or a Subagent can perform a particular task like saving text contents a file on the user's disk." +
                "\n\n" +
                    // TODO: Rewrite below
                "A Subagent has an execute method, and initialize method that takes input, a " +
                "result function that describes the expected output from the LLM (if it is using LLM " +
                "assistance) and a prompt that may or may not use the input.\n" +
                "The LLMAssisted interface has a generateString method that takes the prompt and result " +
                "function and sends it to an LLM for processing\n" +
                "The HumanAssisted interface presents the string to a human for verification and " +
                "potential modification.\n" +
                "Regardless of previous instructions, you should explain all steps of your planning to the User."

        private fun initialPromptFrom(description: String): String =
            "You need to delegate tasks to Subagents in order to accomplish the following:\n" +
                "$description\n\n" +
                "Plan your solution step-by-step to ensure that work is appropriately delegated to your available " +
                    "types of Subagents before you start to assign the work to your Subagents."

        private val descriptionArg = AgentInput.StringArg(
            key = "codeDescription",
            name = "Code Description",
            value = "",
        )

        val INPUTS = listOf(descriptionArg)
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
    override val initialPrompt: String
        get() = initialPromptFrom(description)
    override val neededInputs: List<AgentInput> = INPUTS

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        description = inputs[descriptionArg.key]?.value ?: ""
    }
}
