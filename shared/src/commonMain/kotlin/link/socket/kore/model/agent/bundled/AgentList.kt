package link.socket.kore.model.agent.bundled

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.bundled.code.CleanJsonAgent
import link.socket.kore.model.agent.bundled.code.WriteCodeAgent
import link.socket.kore.model.agent.bundled.general.*
import link.socket.kore.model.agent.bundled.kore.DefineAgentAgent
import link.socket.kore.model.agent.bundled.kore.DelegateTasksAgent
import link.socket.kore.model.agent.bundled.kore.LocalCapabilitiesAgent
import link.socket.kore.model.agent.bundled.kore.ModifyFileAgent

val codeAgents: List<AgentDefinition> = listOf(
    CleanJsonAgent,
    WriteCodeAgent,
)

val generalAgents: List<AgentDefinition> = listOf(
    CareerAgent,
    CookingAgent,
    DIYAgent,
    FinancialAgent,
    HealthAgent,
    LanguageAgent,
    MediaAgent,
    StudyAgent,
    TechAgent,
    TravelAgent,
)

val koreAgents: List<AgentDefinition> = listOf(
    DefineAgentAgent,
    DelegateTasksAgent,
    LocalCapabilitiesAgent,
    ModifyFileAgent,
)

val agentList: List<AgentDefinition> = listOf(
    *codeAgents.toTypedArray(),
    *generalAgents.toTypedArray(),
    *koreAgents.toTypedArray(),
)

fun String.getAgentDefinition(): AgentDefinition = when (this) {
    CareerAgent.name -> CareerAgent
    CleanJsonAgent.name -> CleanJsonAgent
    CookingAgent.name -> CookingAgent
    DefineAgentAgent.name -> DefineAgentAgent
    DelegateTasksAgent.name -> DelegateTasksAgent
    DIYAgent.name -> DIYAgent
    FinancialAgent.name -> FinancialAgent
    HealthAgent.name -> HealthAgent
    LanguageAgent.name -> LanguageAgent
    LocalCapabilitiesAgent.name -> LocalCapabilitiesAgent
    MediaAgent.name -> MediaAgent
    ModifyFileAgent.name -> ModifyFileAgent
    StudyAgent.name -> StudyAgent
    TechAgent.name -> TechAgent
    TravelAgent.name -> TravelAgent
    WriteCodeAgent.name -> WriteCodeAgent
    else -> throw IllegalArgumentException("Unknown Agent $this")
}