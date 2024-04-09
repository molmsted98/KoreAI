package link.socket.kore.ui.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import link.socket.kore.Application
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.createAgent
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.widget.AgentCard

@Composable
fun SelectionScreen(
    modifier: Modifier = Modifier,
    application: Application,
    agentList: List<AgentDefinition>,
    onAgentSelected: (KoreAgent) -> Unit,
    onBackClicked: () -> Unit,
) {
    var partiallySelectedAgent by remember { mutableStateOf<AgentDefinition?>(null) }

    val selectionState = remember(partiallySelectedAgent) {
        derivedStateOf {
            when {
                partiallySelectedAgent != null ->
                    AgentSelectionState.PartiallySelected(
                        agent = partiallySelectedAgent!!,
                        neededInputs = partiallySelectedAgent!!.inputs,
                    )

                else ->
                    AgentSelectionState.Unselected(agentList.map { it.name })
            }
        }
    }

    val onAgentCardSelection: (AgentDefinition) -> Unit = { agentDefinition ->
        if (agentDefinition.inputs.isNotEmpty()) {
            partiallySelectedAgent = agentDefinition
        } else {
            onAgentSelected(application.createAgent(agentDefinition))
        }
    }

    val onHeaderAgentSubmission: (KoreAgent) -> Unit = { agent ->
        onAgentSelected(agent)
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                SelectionHeader(
                    application = application,
                    selectionState = selectionState.value,
                    onHeaderAgentSubmission = onHeaderAgentSubmission,
                    onBackClicked = onBackClicked,
                )
            },
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeColors().background)
                    .padding(contentPadding),
            ) {
                if (selectionState.value is AgentSelectionState.Unselected) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        items(agentList) {agent ->
                            AgentCard(
                                agent = agent,
                                onClick = onAgentCardSelection,
                            )
                        }
                    }
                }
            }
        }
    }
}
