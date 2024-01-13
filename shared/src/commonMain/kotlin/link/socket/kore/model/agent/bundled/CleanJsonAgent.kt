package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.KoreAgent

data class CleanJsonAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var invalidJson: String

    companion object {
        const val NAME = "Clean JSON"

        private const val INSTRUCTIONS =
            "You an Agent that is an expert in understanding JSON parsing."

        private fun initialPromptFrom(
            invalidJson: String,
        ) = "The given string is not a valid JSON:\n" +
            "$invalidJson\n\n" +
            "Plan your solution step-by-step before you fix this and produce a valid JSON, but do not reveal " +
                "this plan to the User."

        private val invalidJsonArg = AgentInput.StringArg(
            key = "invalidJson",
            name = "Invalid JSON",
            value = "",
        )

        val INPUTS = listOf(invalidJsonArg)
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
    override val initialPrompt: String
        get() = initialPromptFrom(invalidJson)
    override val neededInputs: List<AgentInput> = INPUTS

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        invalidJson = inputs[invalidJsonArg.key]?.value ?: ""
    }
}
