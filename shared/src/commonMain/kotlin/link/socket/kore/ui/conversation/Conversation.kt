package link.socket.kore.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatMessage
import kotlinx.coroutines.launch
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.example.FamilyAgent
import link.socket.kore.ui.openAI
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.widget.SmallSnackbarHost

private val agent = FamilyAgent(openAI)

@Composable
fun Conversation(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    selectedAgent: KoreAgent?,
    agentList: List<KoreAgent>,
    onAgentSelected: (KoreAgent) -> Unit,
    onChatSent: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    val selectionEnabled = remember(selectedAgent) {
        derivedStateOf {
            selectedAgent == null
        }
    }

    val displaySnackbar: (String) -> Unit = { message ->
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
        }
    }

    val onHeaderAgentSelection: (KoreAgent) -> Unit = { agent ->
        onAgentSelected(agent)
        scope.launch {
            scaffoldState.drawerState.close()
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
                    selectionEnabled = selectionEnabled.value,
                    drawerExpanded = scaffoldState.drawerState.isOpen,
                    onExpandDrawer = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    selectedAgent = selectedAgent,
                    agentList = agentList,
                    onAgentSelected = onHeaderAgentSelection,
                )
            },
            bottomBar = {
                if (!selectionEnabled.value) {
                    if (agent is KoreAgent.HumanAssisted) {
                        val onSendClicked: () -> Unit = {
                            agent.addUserChat(textFieldValue.text)
                            onChatSent()
                        }

                        ChatTextEntry(
                            modifier = Modifier
                                .requiredHeight(72.dp)
                                .align(Alignment.BottomCenter),
                            textFieldValue = textFieldValue,
                            onSendClicked = onSendClicked,
                            onTextChanged = { textFieldValue = it },
                            displaySnackbar = displaySnackbar,
                        )
                    }
                }
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
                if (!selectionEnabled.value) {
                    ChatHistory(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp),
                        messages = messages,
                        isLoading = isLoading,
                        displaySnackbar = displaySnackbar,
                    )
                }
            }
        }
    }
}
