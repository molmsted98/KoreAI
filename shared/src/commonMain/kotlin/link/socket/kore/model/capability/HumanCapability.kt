package link.socket.kore.model.capability

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.tool.FunctionProvider

sealed interface HumanCapability : Capability {

    data class PromptHuman(
        val agent: KoreAgent.HumanAssisted,
        val scope: CoroutineScope,
    ) : HumanCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provide(
                "executeHumanAssisted",
                "Prompts the user through a CLI to either enter text, or to confirm text that you have generated",
                ::callHumanAssistance,
            )

        private fun callHumanAssistance(): String {
            var response = ""

            scope.launch {
                response = agent.executeHumanAssistance()
            }

            return response
        }
    }
}