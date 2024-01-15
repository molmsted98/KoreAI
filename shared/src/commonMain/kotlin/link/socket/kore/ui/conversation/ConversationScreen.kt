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
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.conversation.chat.ChatHistory
import link.socket.kore.ui.conversation.selector.AgentSelectionState
import link.socket.kore.ui.conversation.selector.ConversationHeader
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.widget.SmallSnackbarHost

@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    existingConversation: Conversation?,
    isLoading: Boolean,
    agentList: List<AgentDefinition>,
    onAgentSelected: (AgentDefinition) -> Unit,
    onChatSent: (String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    var partiallySelectedAgent by remember { mutableStateOf<AgentDefinition?>(null) }

    val selectionState = remember(existingConversation, partiallySelectedAgent) {
        derivedStateOf {
            when {
                partiallySelectedAgent != null ->
                    AgentSelectionState.PartiallySelected(
                        agent = partiallySelectedAgent!!,
                        neededInputs = partiallySelectedAgent!!.inputs,
                    )

                existingConversation != null ->
                    AgentSelectionState.Selected(existingConversation.agent.agentDefinition)

                else ->
                    AgentSelectionState.Unselected(agentList)
            }
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

    val onHeaderAgentSelection: (AgentDefinition) -> Unit = { agent ->
        if (agent.inputs.isNotEmpty()) {
            partiallySelectedAgent = agent
        } else {
            onAgentSelected(agent)
        }
    }

    val onHeaderAgentSubmission: (AgentSelectionState.PartiallySelected) -> Unit = { state ->
        onAgentSelected(state.agent)
        partiallySelectedAgent = null
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
                    selectionState = selectionState.value,
                    onAgentSelected = onHeaderAgentSelection,
                    onHeaderAgentSubmission = onHeaderAgentSubmission,
                    onBackClicked = onBackClicked,
                )
            },
            bottomBar = {
                if (selectionState.value is AgentSelectionState.Selected) {
                    ConversationTextEntry(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        textFieldValue = textFieldValue,
                        onSendClicked = { onChatSent(textFieldValue.text) },
                        onTextChanged = { textFieldValue = it },
                    )
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
                if (selectionState.value is AgentSelectionState.Selected) {
                    val messages = existingConversation?.getChatKoreMessages() ?: emptyList()

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
}
