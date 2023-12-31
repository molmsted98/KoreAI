package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.conversation.selector.AgentInput

data class DelegateTasksAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var description: String

    companion object {
        const val NAME = "Delegate Tasks"

        private const val INSTRUCTIONS =
            "You are a strategic Agent that is an expert programmer in Kotlin and at delegating LLM Subagents." +
                "\n\n" +
                "A Subagent can generate information from an LLM by prompting it for a chat completion, " +
                "or a Subagent can perform a particular task like saving text contents a file on the user's disk." +
                "\n\n" +
                "A Subagent has an execute method, and initialize method that takes input, a " +
                "result function that describes the expected output from the LLM (if it is using LLM " +
                "assistance) and a prompt that may or may not use the input.\n" +
                "The LLMAssisted interface has a generateString method that takes the prompt and result " +
                "function and sends it to an LLM for processing\n" +
                "The HumanAssisted interface presents the string to a human for verification and " +
                "potential modification."

        private fun initialPromptFrom(description: String): String =
            "You need to delegate tasks to Subagents in order to accomplish the following:\n" +
                "$description\n\n" +
                "Plan your solution step-by-step to ensure that work is appropriately delegated to your available " +
                    "types of Subagents before you start to assign the work to your Subagents, but do not reveal " +
                    "this plan to the User."

        private val descriptionArg = AgentInput.StringArg(
            key = "Code Description",
            value = "",
        )
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
    override val initialPrompt: String by lazy { initialPromptFrom(description) }
    override val neededInputs: List<AgentInput> = listOf(descriptionArg)

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        description = inputs[descriptionArg.key]?.value ?: ""
    }

    override suspend fun executeHumanAssistance(): String {
        // TODO: Implement human verification
        return "Test"
    }
}
