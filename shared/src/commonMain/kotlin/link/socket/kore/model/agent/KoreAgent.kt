@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.capability.AgentCapability
import link.socket.kore.model.capability.IOCapability
import link.socket.kore.model.tool.FunctionProvider

open class KoreAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
    val definition: AgentDefinition,
    val conversationRepository: ConversationRepository,
) : LLMAgent {

    val name: String = definition.name

    override val instructions: String
        get() = "${super.instructions}\n\n" +
                "You are an Agent that can provide answers to Chat prompts through LLM completion.\n" +
                definition.instructions.build()

    override val availableFunctions: Map<String, FunctionProvider>
        get() = mapOf(
            AgentCapability.GetAgents.impl,
            AgentCapability.GetAgentArgs.impl,
            AgentCapability.PromptAgent(conversationRepository, openAI, scope).impl,
            IOCapability.CreateFile.impl,
            IOCapability.ReadFile.impl,
            IOCapability.ParseCsv.impl,
        )
}
