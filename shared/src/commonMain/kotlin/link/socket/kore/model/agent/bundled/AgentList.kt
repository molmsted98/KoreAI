package link.socket.kore.model.agent.bundled

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.bundled.code.CleanJsonAgent
import link.socket.kore.model.agent.bundled.code.WriteCodeAgent
import link.socket.kore.model.agent.bundled.general.*
import link.socket.kore.model.agent.bundled.kore.*

val codeAgents: List<AgentDefinition> = listOf(
    CleanJsonAgent,
    WriteCodeAgent,
)

val generalAgents: List<AgentDefinition> = listOf(
    BusinessAgent,
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
    ReActAgent,
    DefineAgentAgent,
    DelegateTasksAgent,
    LocalCapabilitiesAgent,
    ModifyFileAgent,
    WritePromptAgent,
    ComparePromptsAgent,
)

val agentList: List<AgentDefinition> = listOf(
    *codeAgents.toTypedArray(),
    *generalAgents.toTypedArray(),
    *koreAgents.toTypedArray(),
)

val agentNameList: List<String> = agentList.map { it.name }

val agentArgsList: List<String> = agentList.map { agent ->
    "${agent.name}(" + (agent.inputs.joinToString(", ") { input ->
        input.key + ": " + when (input) {
            is AgentInput.StringArg -> "String"
            is AgentInput.ListArg -> "List<String>"
        }
    }) + ")"
}

fun String.getAgentDefinition(): AgentDefinition = when (this) {
    BusinessAgent.name -> BusinessAgent
    CareerAgent.name -> CareerAgent
    CleanJsonAgent.name -> CleanJsonAgent
    CookingAgent.name -> CookingAgent
    ComparePromptsAgent.name -> ComparePromptsAgent
    DefineAgentAgent.name -> DefineAgentAgent
    DelegateTasksAgent.name -> DelegateTasksAgent
    DIYAgent.name -> DIYAgent
    FinancialAgent.name -> FinancialAgent
    HealthAgent.name -> HealthAgent
    LanguageAgent.name -> LanguageAgent
    LocalCapabilitiesAgent.name -> LocalCapabilitiesAgent
    MediaAgent.name -> MediaAgent
    ModifyFileAgent.name -> ModifyFileAgent
    ReActAgent.name -> ReActAgent
    StudyAgent.name -> StudyAgent
    TechAgent.name -> TechAgent
    TravelAgent.name -> TravelAgent
    WriteCodeAgent.name -> WriteCodeAgent
    else -> throw IllegalArgumentException("Unknown Agent $this")
}