package link.socket.kore.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.ui.widget.header.Header

/**
 * A composable function that displays the header for a conversation screen.
 *
 * @param modifier A [Modifier] for this composable. Defaults to [Modifier].
 * @param agentDefinition An [AgentDefinition] object containing the details of the agent.
 * @param onBackClicked A lambda function to be invoked when the back button is clicked.
 * @param onImportExportClicked A lambda function to be invoked when the import/export button is clicked.
 */
@Composable
fun ConversationHeader(
    modifier: Modifier = Modifier,
    agentDefinition: AgentDefinition,
    onBackClicked: () -> Unit,
    onImportExportClicked: () -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = 16.dp,
    ) {
        Header(
            title = agentDefinition.name,
            displayBackIcon = true,
            onBackClicked = onBackClicked,
            onActionIconClicked = onImportExportClicked,
        ) {
            Image(
                imageVector = Icons.TwoTone.Share,
                contentDescription = "Share",
            )
        }
    }
}
