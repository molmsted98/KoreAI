package link.socket.kore.model.agent.capability

import link.socket.kore.model.agent.LLMAgent
import link.socket.kore.model.tool.FunctionProvider

interface AgentCapabilities : LLMAgent {

    val agentFunctions: Map<String, FunctionProvider>
        get() = mapOf(
            IOCapability.CreateFile.impl,
            IOCapability.ReadFile.impl,
            IOCapability.ParseCsv.impl,
            LLMCapability.PromptLLM(openAI, scope).impl,
        )
}
