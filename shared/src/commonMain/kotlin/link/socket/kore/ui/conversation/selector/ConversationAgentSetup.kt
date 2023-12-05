package link.socket.kore.ui.conversation.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.ui.theme.themeTypography

@Composable
fun ConversationAgentSetup(
    modifier: Modifier = Modifier,
    selectionState: AgentSelectionState.PartiallySelected,
    onHeaderAgentSubmission: (AgentSelectionState.PartiallySelected) -> Unit,
) {
    val inputs by remember {
        mutableStateOf(
            mutableMapOf<String, AgentInput>()
        )
    }

    val onStringInputChanged: (AgentInput.StringArg) -> Unit = { input ->
        inputs[input.key] = input
    }

    val onListInputChanged: (AgentInput.ListArg) -> Unit = { input ->
        inputs[input.key] = input
    }

    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            style = themeTypography().h6,
            text = selectionState.agentName,
            textAlign = TextAlign.Center,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items(selectionState.neededInputs) { input ->
                when (input) {
                    is AgentInput.StringArg -> {
                        StringInput(
                            modifier = Modifier
                                .fillMaxWidth(),
                            input = input,
                            onValueChanged = onStringInputChanged,
                        )
                    }

                    is AgentInput.ListArg -> {
                        ListInput(
                            modifier = Modifier
                                .fillMaxWidth(),
                            input = input,
                            onValueChanged = onListInputChanged,
                        )
                    }
                }
            }

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        onHeaderAgentSubmission(
                            selectionState.copy(
                                agent = selectionState.agent.apply {
                                    parseNeededInputs(inputs)
                                }
                            )
                        )
                    },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = themeTypography().button,
                        text = "Submit",
                    )
                }
            }
        }
    }
}

@Composable
private fun StringInput(
    modifier: Modifier = Modifier,
    input: AgentInput.StringArg,
    onValueChanged: (AgentInput.StringArg) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    TextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { value ->
            textFieldValue = value
            onValueChanged(
                input.copy(value = value.text)
            )
        },
        label = { Text(input.key) },
    )

    Spacer(modifier = Modifier.requiredHeight(8.dp))
}

@Composable
private fun ListInput(
    modifier: Modifier = Modifier,
    input: AgentInput.ListArg,
    onValueChanged: (AgentInput.ListArg) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    TextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { value ->
            textFieldValue = value
            onValueChanged(
                input.copy(listValue = listOf(value.text))
            )
        },
        label = { Text(input.textFieldLabel) },
    )

    Spacer(modifier = Modifier.requiredHeight(8.dp))
}
