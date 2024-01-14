package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

        val onAgentSelected: (KoreAgent) -> Unit = { newAgent ->
            // Construct Conversation if this is the initial Agent selection
            if (selectedConversation.value == null) {
                val conversationId = application.conversationRepository.createConversation(newAgent)
                selectedConversationId = conversationId

                scope.launch {
                    isLoading = true
                    application.conversationRepository.runConversation(conversationId)
                    isLoading = false
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
                        existingConversation = selectedConversation.value,
                        isLoading = isLoading,
                        agentList = application.agentList,
                        onAgentSelected = onAgentSelected,
                        onChatSent = { input ->
                            selectedConversationId?.let { id ->
                                scope.launch {
                                    isLoading = true
                                    application.conversationRepository.addUserChat(id, input)
                                    isLoading = false
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
