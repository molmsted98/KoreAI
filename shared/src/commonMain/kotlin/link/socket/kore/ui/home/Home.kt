package link.socket.kore.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.theme.themeTypography
import link.socket.kore.ui.widget.AgentCard
import link.socket.kore.ui.widget.header.SelectionConfig
import link.socket.kore.ui.widget.header.SelectionHeader

@Composable
fun Home(
    modifier: Modifier = Modifier,
    agentList: List<KoreAgent>,
) {
    Column(
        modifier = modifier,
    ) {
        SelectionHeader(
            selectionConfig = SelectionConfig(
                selectionEnabled = true,
                selectedTitle = "",
                firstOption = "Select a Conversation",
                secondOption = "Browse All Agents",
                onSecondOptionSelected = {
                    // TODO: Navigation to Agent list
                }
            ),
        )

        Text(
            style = themeTypography().h4,
            text = "Assistants",
        )

        // TODO: Display available Assistants

        Text(
            style = themeTypography().h4,
            text = "Agents",
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items(agentList) { agent ->
                AgentCard(agent = agent)
            }
        }
    }
}
