@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.ui.conversation.selector.AgentInput

sealed interface KoreAgent : LLMAgent, AgentCapabilities {

    val name: String

    val neededInputs: List<AgentInput>
    fun parseNeededInputs(inputs: Map<String, AgentInput>)

    interface HumanAssisted : KoreAgent {
        suspend fun executeHumanAssistance(): String

        override val availableFunctions: Map<String, FunctionProvider>
            get() = agentFunctions.plus(
                FunctionProvider.provide(
                    "executeHumanAssisted",
                    "Prompts the user through a CLI to either enter text, or to confirm text that you have generated",
                    ::callHumanAssistance,
                ),
            )

        fun callHumanAssistance(): String {
            var response = ""

            scope.launch {
                response = executeHumanAssistance()
            }

            return response
        }
    }

    abstract class HumanAndLLMAssisted(
        override val scope: CoroutineScope,
    ) : KoreAgent, HumanAssisted {

        override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
            set(value) {
                field = value
                updateCompletionRequest()
            }

        override var completionRequest: ChatCompletionRequest? = null
    }
}
