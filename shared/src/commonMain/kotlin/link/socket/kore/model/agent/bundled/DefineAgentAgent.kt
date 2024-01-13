package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent

data class DefineAgentAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    companion object {
        const val NAME = "Define Agent"

        private const val INSTRUCTIONS =
            "You are an Agent that is an expert in writing a LLM Agent descriptions, which includes both the " +
                    "system instructions and the initial Chat prompt for the Agent as described by the Developer. " +
                    "You should use your own system instructions and initial User Chat prompt as an example of what the " +
                    "Developer is looking for in your response. Your output should only be the Agent's system instructions " +
                    "that matches the Agent description from the User"
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
}