package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.agent.KoreAgent

data class CleanJsonAgent(
    override val conversationRepository: ConversationRepository,
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(conversationRepository, openAI, scope) {

    companion object {
        const val NAME = "Clean JSON"

        private const val INSTRUCTIONS =
            "You an Agent that is an expert in understanding JSON parsing. Plan your solution step-by-step " +
                    "before you fix the invalid input, and provide a valid JSON object back to the User."
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
}
