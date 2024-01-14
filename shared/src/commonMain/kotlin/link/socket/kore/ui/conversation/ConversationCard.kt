package link.socket.kore.ui.conversation

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
import link.socket.kore.model.conversation.ChatHistory.Threaded.Uninitialized.getChatMessages
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.ui.theme.agentCardHeight
import link.socket.kore.ui.theme.themeTypography

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
            .requiredHeight(agentCardHeight),
        elevation = 2.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
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
                text = getChatPreview(),
            )
        }
    }
}

private fun getChatPreview(): String =
    getChatMessages()
        .lastOrNull()
        ?.content
        ?: "..."
