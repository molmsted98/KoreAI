package link.socket.kore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import link.kore.shared.config.KotlinConfig
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.example.ParentsAgent
import kotlin.time.Duration.Companion.seconds

val openAI = OpenAI(
    token = KotlinConfig.openai_api_key,
    timeout = Timeout(socket = 45.seconds),
    logging = LoggingConfig(logLevel = LogLevel.None),
)
val agent = ParentsAgent(openAI)

@Composable
fun App() {
    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        var shouldRerun by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var messages by remember { mutableStateOf(emptyList<ChatMessage>()) }
        var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

        LaunchedEffect(Unit) {
            isLoading = true
            agent.initialize()
        }

        LaunchedEffect(shouldRerun) {
            isLoading = true

            with(agent) {
                do {
                    messages = getChatMessages()
                    shouldRerun = execute()
                } while (shouldRerun)

                messages = getChatMessages()
                logChatHistory()
            }

            isLoading = false
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeColors().background),
        ) {
            ChatHistory(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                messages = messages,
                isLoading = isLoading,
            )

            // TODO: Add Thread selection header

            if (agent is KoreAgent.HumanAssisted) {
                val onSendClicked: () -> Unit = {
                    agent.addUserChat(textFieldValue.text)
                    shouldRerun = true
                }

                ChatTextEntry(
                    modifier = Modifier
                        .requiredHeight(72.dp)
                        .align(Alignment.BottomCenter),
                    textFieldValue = textFieldValue,
                    onSendClicked = onSendClicked,
                    onTextChanged = { textFieldValue= it}
                )
            }
        }
    }
}
