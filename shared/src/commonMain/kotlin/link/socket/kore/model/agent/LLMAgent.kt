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
val MODEL_ID = ModelId(MODEL_NAME)

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
                "guide the User into using your specialized skills.\n\n" +
                "You should always initiate the conversation by asking the User for what they need assistance in completing."

    val initialSystemMessage: KoreMessage.System
        get() = KoreMessage.System(instructions)

    val availableFunctions: Map<String, FunctionProvider>
        get() = emptyMap()

    val tools: List<Tool>
        get() = availableFunctions.map { entry ->
            entry.value.definition.tool
        }.toList()

    suspend fun execute(completionRequest: ChatCompletionRequest): Pair<List<KoreMessage>, Boolean> {
        val completion = openAI.chatCompletion(completionRequest)
        val response = completion.choices.first()
        val responseMessage = KoreMessage.Text(
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

    suspend fun ChatMessage.executePendingToolCalls(): List<KoreMessage> =
        toolCalls?.map { call ->
            when (call) {
                is ToolCall.Function -> {
                    call.function.execute()
                }
            }
        } ?: emptyList()

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

    fun createCompletionRequest(chatHistory: ChatHistory): ChatCompletionRequest =
        ChatCompletionRequest(
            model = MODEL_ID,
            messages = chatHistory.getChatMessages(),
            tools = tools.ifEmpty { null },
        )
}
