import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material.icons.twotone.ReplayCircleFilled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import link.kore.shared.config.KotlinConfig
import link.socket.kore.model.agent.example.ParentsAgent
import kotlin.time.Duration.Companion.seconds

val openAI = OpenAI(
    token = KotlinConfig.openai_api_key,
    timeout = Timeout(socket = 45.seconds),
    logging = LoggingConfig(logLevel = LogLevel.None),
)
val agent = ParentsAgent(openAI)

val backgroundColor = Color(0xFFF2F2F7)
val bodyTextColor = Color(0xFF575758)
val nonContentCardColor = Color(0xFFF8F8F8)
val iconAlpha = 0.5f
val separatorIconSize = 4.dp
val iconButtonSize = 48.dp

@Composable
fun themeColors() = MaterialTheme.colors.copy(
    background = backgroundColor,
)

@Composable
fun themeTypography() = with(MaterialTheme.typography) {
    copy(
        body1 = body1.copy(color = bodyTextColor),
        body2 = body2.copy(color = bodyTextColor),
    )
}

@Composable
fun themeShapes() = with(MaterialTheme.shapes) {
    copy(
        small = small.copy(CornerSize(4.dp)),
    )
}

@Composable
fun App() {
    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        var isLoading by remember { mutableStateOf(false) }
        var messages by remember { mutableStateOf(emptyList<ChatMessage>()) }

        LaunchedEffect(Unit) {
            isLoading = true

            with(agent) {
                initialize()

                var rerun: Boolean

                do {
                    messages = getChatMessages()
                    rerun = execute()
                } while (rerun)

                messages = getChatMessages()
                logChatHistory()
            }

            isLoading = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeColors().background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
            ) {
                items(messages) { message ->
                    ChatMessage(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        message = message,
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessage(
    modifier: Modifier = Modifier,
    message: ChatMessage,
) {
    Surface(
        modifier = modifier
            .wrapContentHeight(),
        elevation = 1.dp,
        shape = themeShapes().small,
        color = if (message.role != Role.Assistant && message.role != Role.User) {
            nonContentCardColor
        } else {
            themeColors().surface
        }
    ) {
        // Card
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            // Info
            Row(
                modifier = Modifier
                    .requiredHeight(48.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (message.role != Role.User) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            style = themeTypography().h6,
                            text = message.role.role.replaceFirstChar { it.uppercase() },
                        )

                        Spacer(modifier = Modifier.requiredWidth(8.dp))

                        Image(
                            modifier = Modifier
                                .requiredSize(separatorIconSize),
                            imageVector = Icons.Default.Circle,
                            alpha = iconAlpha,
                            contentDescription = "Separator",
                        )

                        Spacer(modifier = Modifier.requiredWidth(8.dp))
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = themeTypography().body2,
                    textAlign = if (message.role == Role.User) {
                        TextAlign.End
                    } else {
                        TextAlign.Start
                    },
                    text = "11/27/23, 10:03pm", // TODO: Add proper timestamp
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = themeTypography().body2,
                    textAlign = TextAlign.End,
                    text = "27 tokens", // TODO: Add proper token count
                )
            }

            // Content
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                style = themeTypography().body1,
                textAlign = if (message.role == Role.User) {
                    TextAlign.End
                } else {
                    TextAlign.Start
                },
                text = message.content ?: "",
            )

            // Actions
            if (message.role != Role.User) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    if (message.role == Role.Assistant) {
                        IconButton(
                            modifier = Modifier
                                .requiredSize(iconButtonSize),
                            onClick = {
                                // TODO: Handle click
                            }
                        ) {
                            Image(
                                imageVector = Icons.TwoTone.ReplayCircleFilled,
                                alpha = iconAlpha,
                                contentDescription = "Regenerate Response",
                            )
                        }
                    }

                    IconButton(
                        modifier = Modifier
                            .requiredSize(iconButtonSize),
                        onClick = {
                            // TODO: Handle click
                        }
                    ) {
                        Image(
                            imageVector = Icons.TwoTone.FileCopy,
                            alpha = iconAlpha,
                            contentDescription = "Copy Response",
                        )
                    }
                }
            }
        }
    }
}
