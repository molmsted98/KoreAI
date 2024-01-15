@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.capability.AgentCapability
import link.socket.kore.model.capability.HumanCapability
import link.socket.kore.model.capability.IOCapability
import link.socket.kore.model.capability.LLMCapability
import link.socket.kore.model.tool.FunctionProvider

abstract class KoreAgent(
    open val conversationRepository: ConversationRepository,
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
    open val definition: AgentDefinition,
) : LLMAgent {

    abstract val name: String

    open val neededInputs: List<AgentInput> = emptyList()

    open fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        /* no-op */
    }

    abstract class HumanAssisted(
        override val conversationRepository: ConversationRepository,
        override val openAI: OpenAI,
        override val scope: CoroutineScope,
        override val definition: AgentDefinition,
    ) : KoreAgent(conversationRepository, openAI, scope, definition) {

        suspend fun executeHumanAssistance(): String {
            return "TODO"
        }

        override val availableFunctions: Map<String, FunctionProvider>
            get() = mapOf(
                AgentCapability.GetAgents.impl,
                AgentCapability.GetAgentArgs.impl,
                AgentCapability.PromptAgent(conversationRepository, openAI, scope).impl,
                HumanCapability.PromptHuman(this, scope).impl,
                IOCapability.CreateFile.impl,
                IOCapability.ReadFile.impl,
                IOCapability.ParseCsv.impl,
                LLMCapability.PromptLLM(conversationRepository, openAI, scope).impl,
            )
    }

    class HumanAndLLMAssisted(
        override val conversationRepository: ConversationRepository,
        override val openAI: OpenAI,
        override val scope: CoroutineScope,
        override val definition: AgentDefinition,
    ) : HumanAssisted(conversationRepository, openAI, scope, definition) {

        override val name: String = definition.name

        override val instructions: String = "${super.instructions}\n\n" +
                "You are an Agent that can provide answers to Chat prompts through both LLM completion, " +
                "or through Developer intervention via the CLI that was used to configure your Chat session. \n\n" +
                definition.instructions
    }
}
