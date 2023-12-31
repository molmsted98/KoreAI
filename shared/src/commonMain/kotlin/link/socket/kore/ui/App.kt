package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import link.kore.shared.config.KotlinConfig
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.agent.LLMAgent
import link.socket.kore.model.agent.MODEL_NAME
import link.socket.kore.model.agent.bundled.CapabilityAgent
import link.socket.kore.model.agent.bundled.CreateCodeAgent
import link.socket.kore.model.agent.bundled.FixJsonAgent
import link.socket.kore.model.agent.bundled.GenerateSubagentAgent
import link.socket.kore.model.agent.bundled.ModifyFileAgent
import link.socket.kore.model.conversation.Conversation
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
    GenerateSubagentAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    CapabilityAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    CreateCodeAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    ModifyFileAgent(
        openAI = openAI,
        scope = CoroutineScope(Dispatchers.IO),
    ),
    FixJsonAgent(
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

        var selectedScreen by remember { mutableStateOf(Screen.HOME) }
        var selectedConversation by remember { mutableStateOf<Conversation?>(null) }

        var agentInitialized by remember { mutableStateOf(false) }
        var shouldRerun by remember { mutableStateOf(true) }
        var isLoading by remember { mutableStateOf(false) }

        val onAgentSelected: (KoreAgent) -> Unit = { newAgent ->
            // Initialize new Agent in the Conversation before continuing
            shouldRerun = false
            agentInitialized = false
            isLoading = true

            // Construct Conversation if this is the initial Agent selection
            selectedConversation = if (selectedConversation == null) {
                // TODO: Allow selection of these parameters
                Conversation(
                    title = "New Conversation",
                    model = ModelId(MODEL_NAME),
                    agent = newAgent,
                )
            } else {
                selectedConversation?.copy(agent = newAgent)
            }

            scope.launch {
                selectedConversation?.agent?.let { agent ->
                    when (agent) {
                        is KoreAgent.HumanAndLLMAssisted -> agent.initialize()
                        is KoreAgent.HumanAssisted -> agent.executeHumanAssistance()
                    }
                }
            }

            agentInitialized = true
            shouldRerun = true
        }

        LaunchedEffect(selectedConversation) {
            if (selectedConversation == null) {
                agentInitialized = false
                shouldRerun = false
                isLoading = false
            }
        }

        LaunchedEffect(selectedConversation, shouldRerun, agentInitialized) {
            if (selectedConversation?.agent != null && shouldRerun) {
                isLoading = true
            }

            if (agentInitialized) {
                (selectedConversation?.agent as? LLMAgent)?.apply {
                    do {
                        shouldRerun = execute()
                    } while (shouldRerun)
                    logChatHistory()
                }

                isLoading = false
            }
        }

        Box {
            when (selectedScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        agentConversationsList = emptyList(), // TODO: Cache recent conversations
                        onCreateConversationSelected = {
                            selectedConversation = null
                            selectedScreen = Screen.CONVERSATION
                        },
                        onConversationSelected = { newConversation ->
                            selectedConversation = newConversation
                            onAgentSelected(newConversation.agent)
                            selectedScreen = Screen.CONVERSATION
                        }
                    )
                }

                Screen.CONVERSATION -> {
                    ConversationScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        existingConversation = selectedConversation,
                        isLoading = isLoading,
                        agentList = agentList,
                        onAgentSelected = onAgentSelected,
                        onChatSent = {
                            shouldRerun = true
                        },
                        onBackClicked = {
                            selectedConversation = null
                            selectedScreen = Screen.HOME
                        }
                    )
                }
            }
        }
    }
}
