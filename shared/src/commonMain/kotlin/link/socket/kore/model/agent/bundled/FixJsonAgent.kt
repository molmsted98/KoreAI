package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.conversation.selector.AgentInput

data class FixJsonAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var invalidJson: String

    companion object {
        const val NAME = "Clean JSON"

        private const val INSTRUCTIONS =
            "You are a helpful assistant that is an expert in understanding JSON parsing."

        private fun initialPromptFrom(
            invalidJson: String,
        ) = "The given string is not a valid JSON:\n" +
            "$invalidJson\n\n" +
            "Plan your solution step-by-step before you fix this and produce a valid JSON."

        private val invalidJsonArg = AgentInput.StringArg(
            key = "Invalid JSON",
            value = "",
        )
    }

    override val name: String = NAME
    override val instructions: String = INSTRUCTIONS
    override val initialPrompt: String by lazy { initialPromptFrom(invalidJson) }
    override val neededInputs: List<AgentInput> by lazy { listOf(invalidJsonArg) }

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        invalidJson = inputs[invalidJsonArg.key]?.value ?: ""
    }

    override suspend fun executeHumanAssistance(): String {
        // TODO: Implement human verification
        return "Test"
    }
}
