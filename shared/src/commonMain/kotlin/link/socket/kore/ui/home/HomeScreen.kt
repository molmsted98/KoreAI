package link.socket.kore.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.conversation.ConversationCard
import link.socket.kore.ui.conversation.CreateConversationCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    agentConversationsList: List<Conversation>,
    onCreateConversationSelected: () -> Unit,
    onConversationSelected: (Conversation) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            topBar = {
                HomeHeader()
            }
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp,
                    ),
            ) {
                item {
                    CreateConversationCard(
                        modifier = Modifier
                            .padding(
                                top = 24.dp,
                                bottom = 8.dp,
                            ),
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
