package link.socket.kore.ui.selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.Application
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.createAgent
import link.socket.kore.ui.theme.themeTypography

@Composable
fun ConversationAgentSetup(
    modifier: Modifier = Modifier,
    application: Application,
    selectionState: AgentSelectionState.PartiallySelected,
    onHeaderAgentSubmission: (KoreAgent) -> Unit,
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
        modifier = modifier
            .wrapContentHeight(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            style = themeTypography().subtitle1,
            text = selectionState.agentName,
            textAlign = TextAlign.Center,
        )

        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
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
                        val agent = application.createAgent(selectionState.agent).apply {
                            parseNeededInputs(inputs)
                        }
                        onHeaderAgentSubmission(agent)
                    },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = themeTypography().button,
                        text = "Submit",
                        textAlign = TextAlign.Center,
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
        label = { Text(input.name) },
    )

    Spacer(modifier = Modifier.requiredHeight(8.dp))
}

@Composable
private fun ListInput(
    modifier: Modifier = Modifier,
    input: AgentInput.ListArg,
    onValueChanged: (AgentInput.ListArg) -> Unit,
) {
    var itemCount by remember { mutableStateOf(1) }

    val inputs by remember {
        mutableStateOf(
            mutableListOf("")
        )
    }

    Column(
        modifier = modifier
            .wrapContentHeight(),
    ) {
        for (index in 0..<itemCount) {
            var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

            TextField(
                modifier = modifier,
                value = textFieldValue,
                onValueChange = { value ->
                    textFieldValue = value
                    if (inputs.getOrNull(index) == null) {
                        inputs.add(index, value.text)
                    } else {
                        inputs[index] = value.text
                    }
                    onValueChanged(
                        input.copy(listValue = inputs)
                    )
                },
                label = { Text(input.name) },
            )

            Spacer(modifier = Modifier.requiredHeight(8.dp))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(),
            onClick = {
                itemCount++
            },
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                style = themeTypography().button,
                text = "Add List Item",
                textAlign = TextAlign.Center,
            )
        }
    }
}
