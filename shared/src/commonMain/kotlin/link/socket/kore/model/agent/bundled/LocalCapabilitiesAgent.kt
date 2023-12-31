package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.conversation.selector.AgentInput

data class LocalCapabilitiesAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    companion object {
        const val NAME = "Local Capabilities"

        private fun instructionsFrom(capabilityList: List<String>) =
            "You are an Agent with the following Capabilities that you are able to call via Function Tools:\n" +
                capabilityList.joinToString("\n")

        private const val INITIAL_PROMPT =
            "Tell the User about the Capabilities that you are able to provide, and help the User to execute " +
                "these Capabilities. Make sure to ask the User to provide any required arguments before you " +
                "attempt to execute any of your Capabilities."
    }

    override val name: String = NAME
    override val instructions: String by lazy {
        "${super.instructions}\n\n" + instructionsFrom(agentFunctions.map { (key, _) -> key })
    }
    override val initialPrompt: String = INITIAL_PROMPT
    override val neededInputs: List<AgentInput> = emptyList()

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
    }

    override suspend fun executeHumanAssistance(): String {
        TODO("Not yet implemented")
    }
}
