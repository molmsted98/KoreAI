package link.socket.kore.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AddCircleOutline
import androidx.compose.material.icons.twotone.ArrowUpward
import androidx.compose.material.icons.twotone.KeyboardDoubleArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.theme.iconAlpha
import link.socket.kore.ui.theme.iconButtonSize

/**
 * A composable function that represents the text entry field in a conversation UI.
 *
 * @param modifier Modifier to be applied to the root layout.
 * @param textFieldValue The current value of the text field.
 * @param onTextChanged Callback to be invoked when the text field value changes.
 * @param onSendClicked Callback to be invoked when the send button is clicked.
 */
@Composable
fun ConversationTextEntry(
    modifier: Modifier = Modifier,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onSendClicked: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    // Function to handle the completion of text input
    val onTextInputCompleted: () -> Unit = {
        onSendClicked()
        onTextChanged(textFieldValue.copy(""))
        focusManager.clearFocus()
    }

    Column {
        val isKeyboardOpen by keyboardAsState() // State to track if the keyboard is open

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp),
            elevation = 24.dp,
        ) {
            Row(
                modifier = modifier
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 24.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                // Icon button to handle keyboard visibility and attachments
                IconButton(
                    modifier = Modifier
                        .requiredSize(iconButtonSize),
                    onClick = {
                        if (isKeyboardOpen) {
                            focusManager.clearFocus()
                        } else {
                            // TODO: Handle attachments
                        }
                    }
                ) {
                    Image(
                        imageVector = if (isKeyboardOpen) {
                            Icons.TwoTone.KeyboardDoubleArrowDown
                        } else {
                            Icons.TwoTone.AddCircleOutline
                        },
                        alpha = iconAlpha,
                        contentDescription = if (isKeyboardOpen) {
                            "Closes keyboard"
                        } else {
                            "Expands attachment buttons"
                        },
                    )
                }

                // Text field for message input
                TextField(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            bottom = 8.dp,
                        )
                        .wrapContentHeight()
                        .fillMaxWidth(.9f)
                        .onPreviewKeyEvent { event ->
                            if (event.key == Key.Enter) {
                                onTextInputCompleted()
                                true
                            } else {
                                false
                            }
                        },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Gray,
                        disabledTextColor = Color.Transparent,
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    value = textFieldValue,
                    onValueChange = onTextChanged,
                    label = { Text("Your message...") },
                )

                // Icon button to send the message
                IconButton(
                    modifier = Modifier
                        .requiredSize(iconButtonSize),
                    onClick = onTextInputCompleted,
                ) {
                    Image(
                        imageVector = Icons.TwoTone.ArrowUpward,
                        alpha = iconAlpha,
                        contentDescription = "Sends the current message",
                    )
                }
            }
        }
    }
}

/**
 * A composable function that returns the state of the keyboard visibility.
 *
 * @return State<Boolean> indicating whether the keyboard is visible or not.
 */
@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
