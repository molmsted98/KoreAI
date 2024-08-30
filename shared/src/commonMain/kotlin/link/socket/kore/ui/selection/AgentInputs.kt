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

/**
 * Composable function to display and handle agent inputs.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param partiallySelectedAgent The agent definition that is partially selected.
 * @param neededInputs List of required agent inputs.
 * @param optionalInputs List of optional agent inputs.
 * @param onAgentSubmission Callback function to handle agent submission.
 */
@Composable
fun AgentInputs(
    modifier: Modifier = Modifier,
    partiallySelectedAgent: AgentDefinition,
    neededInputs: List<AgentInput>,
    optionalInputs: List<AgentInput>,
    onAgentSubmission: (AgentDefinition) -> Unit,
) {
    // State to hold the input values
    val inputValues by remember {
        mutableStateOf(
            mutableMapOf<String, AgentInput>()
        )
    }

    // Composable function to render different types of inputs
    val inputComposable: @Composable (AgentInput) -> Unit = @Composable { input ->
        when (input) {
            is AgentInput.EnumArgs -> {
                EnumInput(
                    modifier = Modifier.fillMaxWidth(),
                    input = input,
                    possibleValues = input.possibleValues,
                    onValueChanged = { value ->
                        inputValues[input.key] = value
                    },
                )
            }
            is AgentInput.StringArg -> {
                StringInput(
                    modifier = Modifier.fillMaxWidth(),
                    input = input,
                    onValueChanged = { value ->
                        inputValues[input.key] = value
                    },
                )
            }
            is AgentInput.ListArg -> {
                ListInput(
                    modifier = Modifier.fillMaxWidth(),
                    input = input,
                    onValueChanged = { value ->
                        inputValues[input.key] = value
                    },
                )
            }
        }
    }

    // Main column layout for the agent inputs
    Column(
        modifier = modifier.wrapContentHeight(),
    ) {
        // Display the agent name
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            style = themeTypography().subtitle1,
            text = "${partiallySelectedAgent.name} Agent",
            textAlign = TextAlign.Center,
        )

        // LazyColumn to display needed and optional inputs
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

            // Submit button to finalize and submit the agent
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val finalizedAgent = partiallySelectedAgent.apply {
                            parseInputs(inputValues)
                        }
                        onAgentSubmission(finalizedAgent)
                    },
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        style = themeTypography().button,
                        text = "Submit",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * Composable function to display an enum input.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param input The enum input definition.
 * @param possibleValues List of possible values for the enum.
 * @param onValueChanged Callback function to handle value changes.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EnumInput(
    modifier: Modifier = Modifier,
    input: AgentInput.EnumArgs,
    possibleValues: List<String>,
    onValueChanged: (AgentInput.EnumArgs) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        // Display the input name
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = input.name,
        )

        var showDropdown by remember { mutableStateOf(false) }
        // Card to display the selected value and trigger dropdown
        Card(
            modifier = Modifier.padding(12.dp),
            onClick = {
                showDropdown = true
            },
        ) {
            Text(
                text = input.value,
            )
        }
        // Dropdown menu to select a value
        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
        ) {
            possibleValues.forEach { enumValue ->
                DropdownMenuItem(
                    onClick = {
                        onValueChanged(
                            input.copy(
                                value = enumValue,
                            )
                        )
                        showDropdown = false
                    },
                ) {
                    Text(enumValue)
                }
            }
        }
    }
}

/**
 * Composable function to display a string input.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param input The string input definition.
 * @param onValueChanged Callback function to handle value changes.
 */
@Composable
private fun StringInput(
    modifier: Modifier = Modifier,
    input: AgentInput.StringArg,
    onValueChanged: (AgentInput.StringArg) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    // TextField to input string value
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

/**
 * Composable function to display a list input.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param input The list input definition.
 * @param onValueChanged Callback function to handle value changes.
 */
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

    // Column layout for list items
    Column(
        modifier = modifier.wrapContentHeight(),
    ) {
        for (index in 0..<itemCount) {
            var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

            // TextField for each list item
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

        // Button to add more list items
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(),
            onClick = {
                itemCount++
            },
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = themeTypography().button,
                text = "Add List Item",
                textAlign = TextAlign.Center,
            )
        }
    }
}
