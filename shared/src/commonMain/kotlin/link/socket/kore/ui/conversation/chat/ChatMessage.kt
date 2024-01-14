package link.socket.kore.ui.conversation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material.icons.twotone.ReplayCircleFilled
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.core.Role
import com.mikepenz.markdown.compose.Markdown
import link.socket.kore.model.conversation.KoreMessage
import link.socket.kore.ui.theme.*

@Composable
fun ChatMessage(
    modifier: Modifier = Modifier,
    message: KoreMessage,
    displaySnackbar: (String) -> Unit,
    showRegenerate: Boolean,
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
            when (message) {
                is KoreMessage.System,
                is KoreMessage.Text -> {
                    val messageContent = message.chatMessage.content ?: ""

                    if (message.role == Role.User) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = themeTypography().body1,
                            textAlign = TextAlign.End,
                            text = messageContent,
                        )
                    } else {
                        Markdown(
                            modifier = Modifier
                                .fillMaxWidth(),
                            content = messageContent
                        )
                    }
                }
                is KoreMessage.CSV -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        message.csvContent.forEach { line ->
                            Row {
                                val colWidth = (1f / line.size)
                                for (cell in line) {
                                    Text(
                                        modifier = Modifier
                                            .border(BorderStroke(1.dp, Color.DarkGray))
                                            .padding(2.dp)
                                            .fillMaxWidth(colWidth),
                                        text = cell,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Actions
            if (message.role != Role.User) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    if (showRegenerate) {
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

                    val clipboardManager = LocalClipboardManager.current

                    IconButton(
                        modifier = Modifier
                            .requiredSize(iconButtonSize),
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString(message.chatMessage.content ?: ""),
                            )
                            displaySnackbar("Copied to clipboard")
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
