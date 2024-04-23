package link.socket.kore.ui.selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.ui.theme.themeTypography

@Composable
fun AgentSelectionInputs(
    modifier: Modifier = Modifier,
    partiallySelectedAgent: AgentDefinition,
    neededInputs: List<AgentInput>,
    optionalInputs: List<AgentInput>,
    onAgentSubmission: (AgentDefinition) -> Unit,
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

    val inputComposable: @Composable (AgentInput) -> Unit = @Composable { input ->
        when (input) {

            is AgentInput.EnumArgs -> {
                EnumInput(
                    modifier = Modifier
                        .fillMaxWidth(),
                    input = input,
                    possibleValues = input.possibleValues,
                    onValueChanged = { enumValue ->
                        inputs[input.key] = input.copy(value = enumValue)
                    },
                )
            }

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

    Column(
        modifier = modifier
            .wrapContentHeight(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            style = themeTypography().subtitle1,
            text = "${partiallySelectedAgent.name} Agent",
            textAlign = TextAlign.Center,
        )

        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            items(neededInputs) { input ->
                inputComposable(input)
            }

            items(optionalInputs) { input ->
                inputComposable(input)
            }

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        val finalizedAgent = partiallySelectedAgent.apply {
                            parseInputs(inputs)
                        }
                        onAgentSubmission(finalizedAgent)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EnumInput(
    modifier: Modifier = Modifier,
    input: AgentInput.EnumArgs,
    possibleValues: List<String>,
    onValueChanged: (String) -> Unit,
) {
    var showDropdown by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        onClick = { showDropdown = true },
    ) {
        Text(
            text = input.value,
        )
    }
    DropdownMenu(
        expanded = showDropdown,
        onDismissRequest = { showDropdown = false },
    ) {
        possibleValues.forEach { enumValue ->
            DropdownMenuItem(
                onClick = {
                    onValueChanged(enumValue)
                    showDropdown = false
                },
            ) {
                Text(enumValue)
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
