package link.socket.kore.model.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.Function
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import link.socket.kore.model.agent.FunctionDefinition
import link.socket.kore.model.agent.LLMAgent
import link.socket.kore.model.chat.ChatHistory

@OptIn(BetaOpenAI::class)
data class KoreAssistant(
    override val openAI: OpenAI,
    private val existingAssistantId: AssistantId?,
    private val modelId: ModelId,
    private val name: String,
    override val instructions: String,
    override val initialPrompt: String,
    override val availableFunctions: Map<String, FunctionDefinition>,
) : LLMAgent {

    override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
    override var completionRequest: ChatCompletionRequest? = null

    private val assistantTools: List<AssistantTool> = tools.map { tool ->
        AssistantTool.FunctionTool(
            Function(
                name = tool.function.name,
                description = tool.description ?: error("Function ${tool.function.name} missing description"),
                parameters = tool.function.parameters,
            )
        )
    } + listOf(
        AssistantTool.CodeInterpreter,
        AssistantTool.RetrievalTool,
    )

    private lateinit var assistant: Assistant

    override suspend fun initialize() {
        // TODO: Query for existing assistant if `assistantId` is passed

        assistant = openAI.assistant(
            request = AssistantRequest(
                name = name,
                tools = assistantTools,
                model = modelId,
            )
        )
    }
}
