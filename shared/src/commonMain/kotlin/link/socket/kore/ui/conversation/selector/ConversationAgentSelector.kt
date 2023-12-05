package link.socket.kore.ui.conversation.selector

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.widget.header.HeaderButtonItem

@Composable
fun ConversationAgentSelector(
    modifier: Modifier = Modifier,
    drawerExpanded: Boolean,
    agentList: List<KoreAgent>,
    onAgentSelected: (KoreAgent) -> Unit,
) {
    if (!drawerExpanded) {
        LazyRow(
            modifier = modifier,
        ) {
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
