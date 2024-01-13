package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.KoreAgent

data class LocalCapabilitiesAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    companion object {
        const val NAME = "Local Capabilities"

        private fun instructionsFrom(capabilityList: List<String>) =
            "You are an Agent with the following Capabilities that you are able to call via Function Tools:\n" +
                capabilityList.joinToString("\n") + "\n\n" +
                    "Tell the User about the Capabilities that you are able to provide, and help the User to execute " +
                    "these Capabilities. Make sure to ask the User to provide any required arguments before you " +
                    "attempt to execute any of your Capabilities."
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + instructionsFrom(availableFunctions.map { (key, _) -> key })
}
