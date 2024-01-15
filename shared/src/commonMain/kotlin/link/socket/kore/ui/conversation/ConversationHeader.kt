package link.socket.kore.ui.conversation

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.ui.widget.header.Header

@Composable
fun ConversationHeader(
    modifier: Modifier = Modifier,
    agentDefinition: AgentDefinition,
    onBackClicked: () -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = 16.dp,
    ) {
        Header(
            title = agentDefinition.name,
            displayBackIcon = true,
            onBackClicked = onBackClicked,
        )
    }
}
