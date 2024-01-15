package link.socket.kore.ui.selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.Application
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.widget.header.Header

@Composable
fun SelectionHeader(
    modifier: Modifier = Modifier,
    application: Application,
    selectionState: AgentSelectionState,
    onHeaderAgentSubmission: (KoreAgent) -> Unit,
    onBackClicked: () -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = 16.dp,
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
        ) {
            Header(
                title = selectionState.agentName ?: "",
                displayBackIcon = true,
                onBackClicked = onBackClicked,
            )

            when (selectionState) {
                is AgentSelectionState.PartiallySelected -> {
                    if (selectionState.neededInputs.isNotEmpty()) {
                        ConversationAgentSetup(
                            modifier = Modifier
                                .fillMaxWidth(),
                            application = application,
                            selectionState = selectionState,
                            onHeaderAgentSubmission = onHeaderAgentSubmission,
                        )
                    }
                }
                is AgentSelectionState.Unselected,
                is AgentSelectionState.Selected -> {
                    // no-op
                }
            }
        }
    }
}
