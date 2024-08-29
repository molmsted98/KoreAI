package link.socket.kore.model.agent

import link.socket.kore.model.chat.system.Instructions
import link.socket.kore.model.chat.system.Seriousness
import link.socket.kore.model.chat.system.Tone

/**
 * Abstract class representing the definition of an Agent.
 */
abstract class AgentDefinition {

    private var tone: Tone = Tone.PROFESSIONAL
    private var seriousness: Seriousness = Seriousness.VERY

    /**
     * Argument for the tone setting. Represents different tones an agent can have.
     */
    private val toneArg
        get() = AgentInput.EnumArgs(
            key = "tone",
            name = "Tone",
            value = Tone.PROFESSIONAL.name,
            possibleValues = Tone.entries.map { it.name },
        )

    /**
     * Argument for the seriousness setting. Represents different seriousness levels an agent can have.
     */
    private val seriousnessArg
        get() = AgentInput.EnumArgs(
            key = "seriousness",
            name = "Seriousness",
            value = Seriousness.VERY.name,
            possibleValues = Seriousness.entries.map { it.name },
        )

    /**
     * Name of the Agent. Must be overridden by subclasses.
     */
    abstract val name: String

    /**
     * Prompt to be used by the Agent. Must be overridden by subclasses.
     */
    abstract val prompt: String

    /**
     * List of needed inputs for the Agent. Can be overridden by subclasses.
     */
    open val neededInputs: List<AgentInput>
        get() = emptyList()

    /**
     * List of optional inputs for the agent, including tone and seriousness.
     */
    val optionalInputs: List<AgentInput>
        get() = listOf(
            toneArg,
            seriousnessArg,
        )

    /**
     * Parses the inputs and sets the tone and seriousness based on provided values.
     * @param inputs Map of input keys to AgentInput values.
     */
    open fun parseInputs(inputs: Map<String, AgentInput>) {
        tone = Tone.valueOf(inputs[toneArg.key]?.value ?: "")
        seriousness = Seriousness.valueOf(inputs[seriousnessArg.key]?.value ?: "")
    }

    /**
     * Constructs the System instructions to be used by the Agent, including prompt, tone, and seriousness.
     */
    val instructions: Instructions
        get() = Instructions(
            prompt = prompt,
            tone = tone,
            seriousness = seriousness,
        )
}
