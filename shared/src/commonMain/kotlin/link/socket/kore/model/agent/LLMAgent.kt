package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.tool.FunctionProvider

const val MODEL_NAME = "gpt-4-1106-preview"

interface LLMAgent {
    val openAI: OpenAI
    val instructions: String
    val initialPrompt: String

    val initialSystemMessage
        get() = ChatMessage(
            role = ChatRole.System,
            content = instructions,
        )

    val initialPromptMessage
        get() = ChatMessage(
            role = ChatRole.User,
            content = initialPrompt,
        )

    val availableFunctions: Map<String, FunctionProvider>
        get() = emptyMap()

    val tools: List<Tool>
        get() = availableFunctions.map { entry ->
            entry.value.definition.tool
        }.toList()

    var chatHistory: ChatHistory
    var completionRequest: ChatCompletionRequest?

    suspend fun initialize() {
        if (chatHistory is ChatHistory.Threaded.Uninitialized) {
            chatHistory = ChatHistory.NonThreaded(listOf(initialSystemMessage, initialPromptMessage))
        }
    }

    suspend fun execute(): Boolean =
        completionRequest?.let { request ->
            val completion = openAI.chatCompletion(request)
            val response = completion.choices.first()

            updateChatHistory(response.message)

            return if (response.finishReason == FinishReason.ToolCalls) {
                response.message.executePendingToolCalls()
                true
            } else {
                false
            }
        } ?: error("No CompletionRequest found to execute")

    suspend fun ChatMessage.executePendingToolCalls() {
        toolCalls?.forEach { call ->
            when (call) {
                is ToolCall.Function -> {
                    val functionResponse = call.function.execute()
                    updateChatHistory(call.function, functionResponse)
                }
            }
        }
    }

    fun FunctionCall.execute(): String {
        val functionTool = availableFunctions[name]
            ?: error("Function $name not found")

        val functionArgs = argumentsAsJson()

        return functionTool.definition.function.invoke(functionArgs) as? String
            ?: error("Function $name did not return String")
    }

    fun getChatMessages(): List<ChatMessage> =
        chatHistory.getMessages()
            // TODO: Add configurable role filter
            .filter { it.messageContent is TextContent }
            .filter { it.content?.isNotEmpty() == true }

    fun logChatHistory() {
        getChatMessages()
            .map(ChatMessage::content)
            .forEach(::println)
    }

    fun addUserChat(input: String) {
        updateChatHistory(
            ChatMessage(
                role = ChatRole.User,
                content = input,
            )
        )
    }

    private fun updateChatHistory(chatMessage: ChatMessage) {
        chatHistory = chatHistory.appendMessage(chatMessage)
    }

    private fun updateChatHistory(function: FunctionCall, response: String) {
        chatHistory = chatHistory.appendFunctionCallResponse(function, response)
    }

    fun updateCompletionRequest() {
        completionRequest = ChatCompletionRequest(
            model = ModelId(MODEL_NAME),
            messages = chatHistory.getMessages(),
            tools = tools.ifEmpty { null },
        )
    }
}
