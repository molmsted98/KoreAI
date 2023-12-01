package link.socket.kore.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import link.socket.kore.ui.theme.iconAlpha
import link.socket.kore.ui.theme.iconButtonSize
import link.socket.kore.ui.theme.nonContentCardColor
import link.socket.kore.ui.theme.separatorIconSize
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography

@Composable
fun ChatMessage(
    modifier: Modifier = Modifier,
    message: ChatMessage,
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
                                AnnotatedString(message.content ?: ""),
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
