package link.socket.kore.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.KoreAgent

@Composable
fun ConversationHeader(
    modifier: Modifier = Modifier,
    drawerExpanded: Boolean,
    selectedAgent: KoreAgent?,
    agentList: List<KoreAgent>,
    onAgentSelected: (KoreAgent) -> Unit,
) {
    Surface(
        elevation = 16.dp,
    ) {
        Column(
            modifier = modifier
                .wrapContentHeight()
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        // TODO: Handle back press
                    }
                ) {
                    Image(
                        imageVector = Icons.TwoTone.ArrowBack,
                        contentDescription = "Back",
                    )
                }

                if (selectedAgent == null) {
                    if (drawerExpanded) {
                        Row {
                            Text("Select an Agent")
                            Spacer(modifier = Modifier.requiredWidth(8.dp))
                            Text("or")
                            Spacer(modifier = Modifier.requiredWidth(8.dp))
                            TextButton(
                                onClick = {
                                    // TODO: Handle Agent creation
                                }
                            ) {
                                Text("Create Your Own")
                            }
                        }
                    } else {
                        Text("Select an Agent")
                    }
                } else {
                    Text("${selectedAgent.name} Agent")
                }

                IconButton(
                    onClick = {
                        // TODO: Handle back press
                    }
                ) {
                    Image(
                        imageVector = Icons.TwoTone.Menu,
                        contentDescription = "Back",
                    )
                }
            }

            if (!drawerExpanded) {
                LazyRow {
                    items(agentList) { agent ->
                        TextButton(
                            modifier = Modifier
                                .padding(end = 8.dp),
                            onClick = {
                                onAgentSelected(agent)
                            },
                        ) {
                            Text(agent.name)
                        }
                    }
                }
            } else {


            }
        }
    }
}
