package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import link.socket.kore.Application
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.conversation.ConversationId
import link.socket.kore.ui.conversation.ConversationScreen
import link.socket.kore.ui.home.HomeScreen
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography

enum class Screen {
    HOME, CONVERSATION;
}

@Composable
fun App() {
    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        val scope = rememberCoroutineScope()
        val application = remember(scope) { Application(scope) }

        var selectedScreen by remember { mutableStateOf(Screen.HOME) }
        var selectedConversationId by remember { mutableStateOf<ConversationId?>(null) }

        val conversationListState = rememberLazyListState()

        val selectedConversationValue = selectedConversationId?.let { id ->
            application
                .conversationRepository
                .observeValue(id)
                .collectAsState(null)
        } ?: mutableStateOf(null)

        val selectedConversation = remember(selectedConversationId, selectedConversationValue) {
            derivedStateOf {
                if (selectedConversationId != null) {
                    selectedConversationValue.value
                } else {
                    null
                }
            }
        }

        var isLoading by remember { mutableStateOf(false) }

        val onExecutingConversation = {
            isLoading = true
        }

        val onConversationFinishedExecuting = {
            isLoading = false

            if (selectedConversationId != null) {
                scope.launch {
                    val conversation = application.conversationRepository.getValue(selectedConversationId!!)
                    val lastIndex = conversation?.getChatKoreMessages()?.lastIndex ?: 0

                    delay(500)
                    conversationListState.animateScrollToItem(lastIndex)
                }
            }
        }

        val onAgentSelected: (KoreAgent) -> Unit = { newAgent ->
            // Construct Conversation if this is the initial Agent selection
            if (selectedConversation.value == null) {
                val conversationId = application.conversationRepository.createConversation(newAgent)
                selectedConversationId = conversationId

                scope.launch {
                    onExecutingConversation()
                    application.conversationRepository.runConversation(conversationId)
                    onConversationFinishedExecuting()
                }
            }
        }

        Box {
            when (selectedScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        agentConversationsList = emptyList(), // TODO: Cache recent conversations
                        onCreateConversationSelected = {
                            selectedConversationId = null
                            selectedScreen = Screen.CONVERSATION
                        },
                        onConversationSelected = { newConversation ->
                            selectedConversationId = newConversation.id
                            onAgentSelected(newConversation.agent)
                            selectedScreen = Screen.CONVERSATION
                        }
                    )
                }

                Screen.CONVERSATION -> {
                    ConversationScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        listState = conversationListState,
                        existingConversation = selectedConversation.value,
                        isLoading = isLoading,
                        agentList = application.agentList,
                        onAgentSelected = onAgentSelected,
                        onChatSent = { input ->
                            selectedConversationId?.let { id ->
                                scope.launch {
                                    onExecutingConversation()
                                    application.conversationRepository.addUserChat(id, input)
                                    onConversationFinishedExecuting()
                                }
                            }
                        },
                        onBackClicked = {
                            selectedConversationId = null
                            selectedScreen = Screen.HOME
                        }
                    )
                }
            }
        }
    }
}
