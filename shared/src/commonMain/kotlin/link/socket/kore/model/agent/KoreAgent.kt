@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.capability.AgentCapability
import link.socket.kore.model.agent.capability.HumanCapability
import link.socket.kore.model.agent.capability.IOCapability
import link.socket.kore.model.agent.capability.LLMCapability
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.tool.FunctionProvider

sealed interface KoreAgent : LLMAgent {

    val name: String

    val neededInputs: List<AgentInput>
    fun parseNeededInputs(inputs: Map<String, AgentInput>)

    interface HumanAssisted : KoreAgent {
        suspend fun executeHumanAssistance(): String

        override val availableFunctions: Map<String, FunctionProvider>
            get() = mapOf(
                AgentCapability.GetAgents.impl,
                AgentCapability.GetAgentArgs.impl,
                AgentCapability.PromptAgent(openAI, scope).impl,
                HumanCapability.PromptHuman(this, scope).impl,
                IOCapability.CreateFile.impl,
                IOCapability.ReadFile.impl,
                IOCapability.ParseCsv.impl,
                LLMCapability.PromptLLM(openAI, scope).impl,
            )
    }

    abstract class HumanAndLLMAssisted(
        override val scope: CoroutineScope,
    ) : KoreAgent, HumanAssisted {

        override val instructions: String = "${super<KoreAgent>.instructions}\n\n" +
                "You are an Agent that can provide answers to Chat prompts through both LLM completion, " +
                "or through Developer intervention via the CLI that was used to configure your Chat session."

        override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
            set(value) {
                field = value
                updateCompletionRequest()
            }

        override var completionRequest: ChatCompletionRequest? = null

        override val neededInputs: List<AgentInput> = emptyList()

        override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
           /* no-op */
        }

        override suspend fun executeHumanAssistance(): String {
            // TODO: Implement
            return ""
        }
    }
}
