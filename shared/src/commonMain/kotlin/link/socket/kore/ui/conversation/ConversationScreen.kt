package link.socket.kore.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.launch
import link.socket.kore.io.exportToFile
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.widget.SmallSnackbarHost

/**
 * Composable function that represents the conversation screen.
 *
 * @param modifier Modifier to be applied to the root layout.
 * @param listState State of the lazy list used to display chat messages.
 * @param conversation The conversation data model containing chat messages and agent information.
 * @param isLoading Boolean flag indicating whether the chat is currently loading.
 * @param onChatSent Callback function to be invoked when a chat message is sent.
 * @param onBackClicked Callback function to be invoked when the back button is clicked.
 */
@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    conversation: Conversation,
    isLoading: Boolean,
    onChatSent: (String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    /**
     * Displays a snackbar with the given message.
     *
     * @param message The message to be displayed in the snackbar.
     */
    val displaySnackbar: (String) -> Unit = { message ->
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            topBar = {
                ConversationHeader(
                    agentDefinition = conversation.agent.definition,
                    onBackClicked = onBackClicked,
                    onImportExportClicked = {
                        conversation.exportToFile()
                    }
                )
            },
            bottomBar = {
                ConversationTextEntry(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    textFieldValue = textFieldValue,
                    onSendClicked = { onChatSent(textFieldValue.text) },
                    onTextChanged = { textFieldValue = it },
                )
            },
            snackbarHost = { snackbarState ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    SmallSnackbarHost(
                        modifier = Modifier
                            .align(Alignment.BottomStart),
                        snackbarHostState = snackbarState,
                    )
                }
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeColors().background)
                    .padding(contentPadding),
            ) {
                val messages = remember(conversation) {
                    conversation.getChats()
                }

                ChatHistory(
                    modifier = Modifier
                        .fillMaxSize(),
                    listState = listState,
                    messages = messages,
                    isLoading = isLoading,
                    displaySnackbar = displaySnackbar,
                )
            }
        }
    }
}
