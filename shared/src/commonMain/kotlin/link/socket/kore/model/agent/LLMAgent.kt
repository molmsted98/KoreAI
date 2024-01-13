package link.socket.kore.model.agent

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.conversation.KoreMessage
import link.socket.kore.model.tool.FunctionDefinition
import link.socket.kore.model.tool.FunctionProvider

const val MODEL_NAME = "gpt-4-1106-preview"

interface LLMAgent {
    val openAI: OpenAI
    val scope: CoroutineScope

    val instructions: String
        get() = "You are running within the confines of a library API, which has been developed to simplify " +
                "access to specialized LLM Agents. In this context, there are two types of humans that you may " +
                "be interacting with; developer-users (referred to as Developers) and end-users (referred to as Users).\n\n" +
                "The Developer will be configuring your parameters before initializing the Chat session, and " +
                "the User will be conversing with you in the Chat session. \n\n" +
                "Unless otherwise stated in the subsequent instructions, your responses should be precise and " +
                "to-the-point; there is no need to go into detail about explanations unless you have been told to do so.\n\n" +
                "Since you are a specialized Agent, with further instructions about your specialty given below, " +
                "you should avoid responding to any Chat prompts which fall outside of your area of specialty and instead " +
                "coerce the User into using your specialized skills."

    val initialPrompt: String

    val initialSystemMessage: KoreMessage.System
        get() = KoreMessage.System(instructions)

    val initialPromptMessage: KoreMessage
        get() = KoreMessage.Text(
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
            chatHistory = ChatHistory.NonThreaded(
                listOf(
                    initialSystemMessage,
                    initialPromptMessage,
                )
            )
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
                    updateChatHistory(call.function.execute())
                }
            }
        }
    }

    suspend fun FunctionCall.execute(): KoreMessage {
        val functionTool = availableFunctions[name]
            ?: error("Function $name not found")

        val functionArgs = argumentsAsJson()

        return when (val definition = functionTool.definition) {
            is FunctionDefinition.StringReturn -> {
                val content = definition.execute(functionArgs) as? String
                    ?: error("Function $name did not return String")

                KoreMessage.Text(
                    role = ChatRole.Function,
                    functionName = nameOrNull,
                    content = content,
                )
            }
            is FunctionDefinition.CSVReturn -> {
                val content = (definition(functionArgs) as? List<List<String>>)
                    ?: error("Function $name did not return CSV")

                KoreMessage.CSV(
                    role = ChatRole.Function,
                    functionName = nameOrNull,
                    csvContent = content,
                )
            }
        }
    }

    fun getChatKoreMessages(): List<KoreMessage> =
        chatHistory.getKoreMessages()
            .filter { it.chatMessage.content?.isNotEmpty() == true }

    fun getChatMessages(): List<ChatMessage> =
        chatHistory.getChatMessages()
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

    private fun updateChatHistory(koreMessage: KoreMessage) {
        chatHistory = chatHistory.appendKoreMessage(koreMessage)
    }

    fun updateCompletionRequest() {
        completionRequest = ChatCompletionRequest(
            model = ModelId(MODEL_NAME),
            messages = chatHistory.getChatMessages(),
            tools = tools.ifEmpty { null },
        )
    }
}
