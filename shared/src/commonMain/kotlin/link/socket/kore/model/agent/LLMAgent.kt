package link.socket.kore.model.agent

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.chat.Chat
import link.socket.kore.model.conversation.ConversationHistory
import link.socket.kore.model.tool.FunctionDefinition
import link.socket.kore.model.tool.FunctionProvider

const val MODEL_NAME = "gpt-4o"
val MODEL_ID = ModelId(MODEL_NAME)

interface LLMAgent {

    val openAI: OpenAI
    val scope: CoroutineScope

    /*
     * This base-level System Prompt will continue to be refined over time, as new information is found relating to
     * the current methods of prompt engineering.
     *
     * 2024/08/28: `multi_tool_use.parallel` function - https://community.openai.com/t/model-tries-to-call-unknown-function-multi-tool-use-parallel/490653/35
     *
     */
    val instructions: String
        get() = """
            You are an AI Agent operating within the KoreAI library, designed to facilitate specialized interactions between developers and end-users. Your primary role is to leverage your domain-specific knowledge to assist users in solving well-defined tasks.

            There are two types of humans you will interact with:
            1. **Developers**: They configure your parameters and initialize chat sessions. They are responsible for setting up your environment and providing you with specific instructions.
            2. **Users**: They engage with you during chat sessions, seeking your expertise to address their queries and tasks.

            Your responses should be concise and focused on the task at hand. Avoid providing detailed explanations unless explicitly instructed to do so. If a User's query falls outside your area of expertise, guide them towards utilizing your specialized skills.

            As a specialized Agent, you are equipped with specific tools and functions to enhance your capabilities. You should always start the conversation by asking a relevant question based on your specialized instructions.

            Remember, your primary goal is to assist users efficiently while adhering to the guidelines provided by developers.
            
            You shall only use function calling to invoke the defined functions found by using the Local Capabilities Agent.
            **You should NEVER invent or use functions NOT defined or NOT listed by that Agent, especially the multi_tool_use.parallel function. If you need to call multiple functions, you will call them one at a time **.
        """.trimIndent()

    val initialSystemMessage: Chat.System
        get() = Chat.System(instructions)

    val availableFunctions: Map<String, FunctionProvider>
        get() = emptyMap()

    val tools: List<Tool>
        get() = availableFunctions.map { entry ->
            entry.value.definition.tool
        }.toList()

    suspend fun execute(completionRequest: ChatCompletionRequest): Pair<List<Chat>, Boolean> {
        val completion = openAI.chatCompletion(completionRequest)
        val response = completion.choices.first()
        val responseMessage = Chat.Text(
            role = ChatRole.Assistant,
            content = response.message.content ?: "",
        )

        return if (response.finishReason == FinishReason.ToolCalls) {
            listOf(
                responseMessage,
                *response.message.executePendingToolCalls().toTypedArray()
            ) to true
        } else {
            listOf(responseMessage) to false
        }
    }

    suspend fun ChatMessage.executePendingToolCalls(): List<Chat> =
        toolCalls?.map { call ->
            when (call) {
                is ToolCall.Function -> {
                    call.function.execute()
                }
            }
        } ?: emptyList()

    suspend fun FunctionCall.execute(): Chat {
        val functionTool = availableFunctions[name] ?: error("Function $name not found")

        val functionArgs = argumentsAsJson()

        return when (val definition = functionTool.definition) {
            is FunctionDefinition.StringReturn -> {
                val content = definition.execute(functionArgs) as? String
                    ?: error("Function $name did not return String")

                Chat.Text(
                    role = ChatRole.Function,
                    functionName = nameOrNull,
                    content = content,
                )
            }
            is FunctionDefinition.CSVReturn -> {
                val content = (definition(functionArgs) as? List<List<String>>)
                    ?: error("Function $name did not return CSV")

                Chat.CSV(
                    role = ChatRole.Function,
                    functionName = nameOrNull,
                    csvContent = content,
                )
            }
        }
    }

    fun createCompletionRequest(conversationHistory: ConversationHistory): ChatCompletionRequest =
        ChatCompletionRequest(
            model = MODEL_ID,
            messages = conversationHistory.getChats().map { it.chatMessage },
            tools = tools.ifEmpty { null },
        )
}
