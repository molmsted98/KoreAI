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
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.launch
import link.kore.shared.config.KotlinConfig
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.LLMAgent
import link.socket.kore.ui.conversation.Conversation
import kotlin.time.Duration.Companion.seconds

val openAI = OpenAI(
    token = KotlinConfig.openai_api_key,
    timeout = Timeout(socket = 45.seconds),
    logging = LoggingConfig(logLevel = LogLevel.All),
)

@Composable
fun App() {
    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        val scope = rememberCoroutineScope()

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
            isLoading = true

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
            Conversation(
                modifier = Modifier
                    .fillMaxSize(),
                messages = messages,
                isLoading = isLoading,
                onAgentSelected = onAgentSelected,
                onChatSent = { shouldRerun = true },
            )
        }
    }
}
