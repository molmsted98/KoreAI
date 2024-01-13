package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent

data class DelegateTasksAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

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
                "Regardless of previous instructions, you should explain all steps of your planning to the User. " +
                    "Plan your solution step-by-step to ensure that work is appropriately delegated to your available " +
                    "types of Subagents before you start to assign the work to your Subagents."
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
}
