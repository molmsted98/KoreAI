package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import link.kore.shared.config.KotlinConfig
import link.socket.kore.Application
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.bundled.*
import link.socket.kore.model.conversation.ConversationId
import link.socket.kore.ui.conversation.ConversationScreen
import link.socket.kore.ui.home.HomeScreen
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography
import kotlin.time.Duration.Companion.hours

val openAI = OpenAI(
    token = KotlinConfig.openai_api_key,
    timeout = Timeout(socket = 1.hours),
    logging = LoggingConfig(logLevel = LogLevel.All),
)

// TODO: Inject OpenAI & CoroutineScope into Agents
private val agentList: List<KoreAgent> = listOf(
    DefineAgentAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    DelegateTasksAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    LocalCapabilitiesAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    WriteCodeAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    ModifyFileAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    CleanJsonAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    FinancialAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
)

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
                        agentList = agentList,
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
