package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.launch
import link.kore.shared.config.KotlinConfig
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.LLMAgent
import link.socket.kore.model.agent.bundled.FixJsonAgent
import link.socket.kore.model.agent.bundled.GenerateCodeAgent
import link.socket.kore.model.agent.bundled.GenerateSubagentAgent
import link.socket.kore.model.agent.bundled.ModifyFileAgent
import link.socket.kore.model.agent.bundled.SaveFileAgent
import link.socket.kore.model.agent.example.FamilyAgent
import link.socket.kore.ui.conversation.Conversation
import link.socket.kore.ui.home.Home
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography
import kotlin.time.Duration.Companion.seconds

val openAI = OpenAI(
    token = KotlinConfig.openai_api_key,
    timeout = Timeout(socket = 45.seconds),
    logging = LoggingConfig(logLevel = LogLevel.All),
)

// TODO: Allow user specification of default values
private val agentList: List<KoreAgent> = listOf(
    GenerateSubagentAgent(
        openAI = openAI,
        description = "Create an Android screen that displays a list of items containing names and images " +
            "of popular cereal brands.",
    ),
    FamilyAgent(openAI),
    GenerateCodeAgent(
        openAI = openAI,
        description = "How should I parse a .csv file to display in a Kotlin Multiplatform app?",
        technologies = listOf("Kotlin")
    ),
    ModifyFileAgent(
        openAI = openAI,
        filepath = "",
        description = "",
        technologies = emptyList(),
    ),
    SaveFileAgent(
        filepath = "",
        fileContent = "",
    ),
    FixJsonAgent(
        openAI = openAI,
        invalidJson = "{foo\"bar",
    ),
)

@OptIn(BetaOpenAI::class)
@Composable
fun App() {
    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        val scope = rememberCoroutineScope()

        var selectedConversation by remember { mutableStateOf<ThreadId?>(null) }

        var shouldRerun by remember { mutableStateOf(true) }
        var isLoading by remember { mutableStateOf(false) }
        var messages by remember { mutableStateOf(emptyList<ChatMessage>()) }
        var agent by remember { mutableStateOf<KoreAgent?>(null) }

        val onAgentSelected: (KoreAgent) -> Unit = { newAgent ->
            agent = newAgent
            (agent as? LLMAgent)?.let { llmAgent ->
                isLoading = true
                scope.launch {
                    llmAgent.initialize()
                }
            }
        }

        LaunchedEffect(agent, shouldRerun) {
            if (agent != null && shouldRerun) {
                isLoading = true
            }

            (agent as? LLMAgent)?.apply {
                do {
                    messages = getChatMessages()
                    shouldRerun = execute()
                } while (shouldRerun)

                messages = getChatMessages()
                logChatHistory()
            }

            isLoading = false
        }

        Box {
            selectedConversation?.let { threadId ->
                // TODO: Load proper conversation based on `threadId`
                Conversation(
                    modifier = Modifier
                        .fillMaxSize(),
                    messages = messages,
                    isLoading = isLoading,
                    selectedAgent = agent,
                    agentList = agentList,
                    onAgentSelected = onAgentSelected,
                    onChatSent = { shouldRerun = true },
                )
            } ?: run {
                Home(
                    agentList = agentList,
                )
            }
        }
    }
}
