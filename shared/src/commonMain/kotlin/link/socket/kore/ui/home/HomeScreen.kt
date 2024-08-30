package link.socket.kore.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.widget.ConversationCard

/**
 * Composable function that represents the Home Screen of the application.
 *
 * @param modifier Modifier to be applied to the HomeScreen.
 * @param agentConversationsList List of conversations to be displayed.
 * @param onCreateConversationSelected Callback to be invoked when the create conversation button is clicked.
 * @param onConversationSelected Callback to be invoked when a conversation is selected.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    agentConversationsList: List<Conversation>,
    onCreateConversationSelected: () -> Unit,
    onConversationSelected: (Conversation) -> Unit,
) {
    // State of the scaffold, used to control the drawer and snackbar
    val scaffoldState = rememberScaffoldState()

    // Main container for the HomeScreen
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        // Scaffold provides the basic structure for the screen, including top bar and floating action button
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            topBar = {
                HomeHeader() // Top bar of the HomeScreen
            },
            floatingActionButton = {
                // Floating action button to create a new conversation
                Box(
                    modifier = Modifier
                        .padding(
                            end = 8.dp,
                            bottom = 16.dp,
                        )
                ) {
                    FloatingActionButton(
                        onClick = onCreateConversationSelected,
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Add,
                            contentDescription = "Create Conversation"
                        )
                    }
                }
            }
        ) { contentPadding ->
            // LazyColumn to display the list of conversations
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp,
                    ),
            ) {
                // Spacer to add some space at the top of the list
                item {
                    Spacer(
                        modifier = Modifier
                            .requiredHeight(24.dp)
                    )
                }

                // Iterate through the list of conversations and display each one using ConversationCard
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
