package link.socket.kore.ui.conversation.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.widget.header.Header

@Composable
fun ConversationHeader(
    modifier: Modifier = Modifier,
    selectionState: AgentSelectionState,
    onAgentSelected: (KoreAgent) -> Unit,
    onHeaderAgentSubmission: (AgentSelectionState.PartiallySelected) -> Unit,
    onBackClicked: () -> Unit,
) {
    val selectionEnabled = remember(selectionState) {
        derivedStateOf { selectionState !is AgentSelectionState.Selected }
    }

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
                title = if (selectionEnabled.value) {
                    "Select an Agent"
                } else {
                    selectionState.agentName ?: ""
                },
                displayBackIcon = true,
                onBackClicked = onBackClicked,
            )

            when (selectionState) {
                is AgentSelectionState.Unselected -> {
                    ConversationAgentSelector(
                        modifier = Modifier
                            .fillMaxWidth(),
                        agentList = selectionState.agentList,
                        onAgentSelected = onAgentSelected,
                    )
                }
                is AgentSelectionState.PartiallySelected -> {
                    if (selectionState.neededInputs.isNotEmpty()) {
                        ConversationAgentSetup(
                            modifier = Modifier
                                .fillMaxWidth(),
                            selectionState = selectionState,
                            onHeaderAgentSubmission = onHeaderAgentSubmission,
                        )
                    }
                }
                is AgentSelectionState.Selected -> {
                    // no-op
                }
            }
        }
    }
}
