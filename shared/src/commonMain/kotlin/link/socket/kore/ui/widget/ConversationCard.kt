package link.socket.kore.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.theme.conversationCardHeight
import link.socket.kore.ui.theme.themeTypography

/**
 * A Composable function that displays a card representing a conversation.
 *
 * @param modifier A [Modifier] for this composable.
 * @param conversation The [Conversation] object containing the conversation details.
 * @param onClick A lambda function to be invoked when the card is clicked.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConversationCard(
    modifier: Modifier = Modifier,
    conversation: Conversation,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(conversationCardHeight),
        elevation = 2.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp,
                ),
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                style = themeTypography().h6,
                text = conversation.title,
            )

            Text(
                modifier = Modifier
                    .padding(bottom = 4.dp),
                style = themeTypography().body1,
                text = "${conversation.agent.name} Agent",
            )

            Text(
                style = themeTypography().body1,
                text = conversation.getChatPreview(),
            )
        }
    }
}

/**
 * Extension function for the [Conversation] class to get a preview of the last chat message.
 *
 * @return A [String] containing the content of the last chat message, or "..." if there are no messages.
 */
private fun Conversation.getChatPreview(): String =
    getChats()
        .lastOrNull()
        ?.chatMessage
        ?.content
        ?: "..."
