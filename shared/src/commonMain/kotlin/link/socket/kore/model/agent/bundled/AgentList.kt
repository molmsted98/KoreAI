package link.socket.kore.model.agent.bundled

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.bundled.capability.LocalCapabilitiesAgent
import link.socket.kore.model.agent.bundled.capability.ModifyFileAgent
import link.socket.kore.model.agent.bundled.code.CleanJsonAgent
import link.socket.kore.model.agent.bundled.code.WriteCodeAgent
import link.socket.kore.model.agent.bundled.general.*
import link.socket.kore.model.agent.bundled.prompt.ComparePromptsAgent
import link.socket.kore.model.agent.bundled.prompt.TestAgentAgent
import link.socket.kore.model.agent.bundled.prompt.WritePromptAgent
import link.socket.kore.model.agent.bundled.reasoning.DelegateTasksAgent
import link.socket.kore.model.agent.bundled.reasoning.ReActAgent

val capabilityAgents = listOf(
    LocalCapabilitiesAgent,
    ModifyFileAgent,
)

val codeAgents = listOf(
    CleanJsonAgent,
    WriteCodeAgent,
)

val generalAgents = listOf(
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

val promptAgents = listOf(
    ComparePromptsAgent,
    TestAgentAgent(""),
    WritePromptAgent,
)

val reasoningAgents = listOf(
    DelegateTasksAgent,
    ReActAgent,
)

val agentList: List<AgentDefinition> = listOf(
    *capabilityAgents.toTypedArray(),
    *codeAgents.toTypedArray(),
    *generalAgents.toTypedArray(),
    *promptAgents.toTypedArray(),
    *reasoningAgents.toTypedArray(),
)

val agentNameList: List<String> = agentList.map { it.name }

val agentArgsList: List<String> = agentList.map { agent ->
    "${agent.name}(" + (agent.neededInputs.joinToString(", ") { input ->
        input.key + ": " + when (input) {
            is AgentInput.EnumArgs -> "Enum"
            is AgentInput.StringArg -> "String"
            is AgentInput.ListArg -> "List<String>"
        }
    }) + ")"
}

fun String?.getAgentDefinition(prompt: String): AgentDefinition = when (this) {
    BusinessAgent.name -> BusinessAgent
    CareerAgent.name -> CareerAgent
    CleanJsonAgent.name -> CleanJsonAgent
    CookingAgent.name -> CookingAgent
    ComparePromptsAgent.name -> ComparePromptsAgent
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
    WritePromptAgent.name -> WritePromptAgent
    else -> TestAgentAgent(prompt)
}