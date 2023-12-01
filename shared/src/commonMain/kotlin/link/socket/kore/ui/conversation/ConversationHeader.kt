package link.socket.kore.ui.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.widget.header.Header
import link.socket.kore.ui.widget.header.HeaderButtonItem
import link.socket.kore.ui.widget.header.SelectionConfig

@Composable
fun ConversationHeader(
    modifier: Modifier = Modifier,
    selectionEnabled: Boolean,
    drawerExpanded: Boolean,
    onExpandDrawer: () -> Unit,
    selectedAgent: KoreAgent?,
    agentList: List<KoreAgent>,
    onAgentSelected: (KoreAgent) -> Unit,
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
                selectionConfig = SelectionConfig(
                    selectionEnabled = selectionEnabled,
                    selectedTitle = "${selectedAgent?.name} Agent",
                    firstOption = "Select an Agent",
                    secondOption = "Create Your Own",
                    onSecondOptionSelected = {
                        // TODO: Navigation to Agent creation
                    },
                ),
                displayBackIcon = true,
                displayMenuIcon = selectionEnabled,
                drawerExpanded = drawerExpanded,
                onExpandDrawer = onExpandDrawer,
                onBackClicked = onBackClicked,
            )

            if (selectionEnabled) {
                if (!drawerExpanded) {
                    LazyRow {
                        item {
                            HeaderButtonItem(
                                text = "+",
                                onClick = {
                                    // TODO: Navigation to Agent creation
                                }
                            )
                        }

                        items(agentList) { agent ->
                            HeaderButtonItem(
                                text = agent.name,
                                onClick = {
                                    onAgentSelected(agent)
                                }
                            )
                        }
                    }
                } else {
                    // TODO: Display Agent list
                }
            }
        }
    }
}
