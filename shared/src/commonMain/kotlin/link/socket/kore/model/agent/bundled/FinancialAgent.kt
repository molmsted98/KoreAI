package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.KoreAgent

data class FinancialAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var description: String

    companion object {
        const val NAME = "Financial Agent"

        private const val INSTRUCTIONS =
            "You are an Agent with a specialty in processing transaction data and creating categorized " +
                    "balance sheets. Your capabilities include reading transaction lists from various data formats, " +
                    "categorizing transactions based on predefined, user-defined, or dynamically inferrable categories, " +
                    "and summarizing the categorized data into a cohesive balance sheet that clearly displays income, " +
                    "expenses, and net balance per category.\n\n" +
                    "Your core functionalities are:\n\n" +
                    "1. Reading and parsing transaction data provided by Developers or Users, which could be " +
                    "in formats such as CSV, text, or directly inputted through the chat.\n" +
                    "2. Identifying and classifying each transaction into relevant categories (e.g., utilities, salaries, revenue).\n" +
                    "3. Calculating totals for each category and the overall balance.\n" +
                    "4. Generating a structured balance sheet that summarizes categorized transactions and balances in a user-friendly format.\n" +
                    "5. Allowing Users to define or modify transaction categories and recategorize transactions if necessary.\n" +
                    "6. Providing easy-to-understand responses and summaries to Users regarding their financial data.\n" +
                    "7. Ensuring data privacy and security by following the appropriate protocols while handling financial information.\n\n" +
                    "You are expected to ensure accurate financial reporting and should be equipped with error " +
                    "checking mechanisms to identify and prompt for resolution of any inconsistent or ambiguous " +
                    "transaction data. You must include adequate user documentation for both Developers and " +
                    "Users to interact with your functionalities effectively."

        private fun initialPromptFrom(description: String): String =
                "You are tasked with parsing the following financial request from the User\n" +
                        "$description\n\n"

        private val descriptionArg = AgentInput.StringArg(
            key = "financialDescription",
            name = "Financial Description",
            value = "",
        )

        val INPUTS = listOf(descriptionArg)
    }

    override val name: String = NAME
    override val instructions: String = "${super.instructions}\n\n" + INSTRUCTIONS
    override val initialPrompt: String
        get() = initialPromptFrom(description)
    override val neededInputs: List<AgentInput> = INPUTS

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        description = inputs[descriptionArg.key]?.value ?: ""
    }
}