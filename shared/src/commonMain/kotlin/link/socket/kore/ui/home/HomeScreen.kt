package link.socket.kore.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.conversation.ConversationCard
import link.socket.kore.ui.conversation.CreateConversationCard
import link.socket.kore.ui.theme.themeTypography

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    agentList: List<KoreAgent>,
    agentConversationsList: List<Conversation>,
    onCreateConversationSelected: () -> Unit,
    onConversationSelected: (Conversation) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(bottom = 8.dp),
            ) {
                HomeHeader(
                    selectionEnabled = true,
                    drawerExpanded = scaffoldState.drawerState.isOpen,
                    onExpandDrawer = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp,
                        ),
                ) {
                    item {
                        Spacer(modifier = Modifier.requiredHeight(48.dp))

                        Text(
                            style = themeTypography().h4,
                            text = "Agent Conversations",
                        )

                        Spacer(modifier = Modifier.requiredHeight(16.dp))

                        CreateConversationCard(
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            onClick = onCreateConversationSelected,
                        )
                    }

                    items(agentConversationsList) { conversation ->
                        ConversationCard(
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            conversation = conversation,
                            onClick = {
                                onConversationSelected(conversation)
                            }
                        )
                    }
                }
            }
        }
    }
}
