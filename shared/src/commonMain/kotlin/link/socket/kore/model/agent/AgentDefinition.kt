package link.socket.kore.model.agent

import link.socket.kore.model.chat.system.Instructions
import link.socket.kore.model.chat.system.Seriousness
import link.socket.kore.model.chat.system.Tone

abstract class AgentDefinition {

    private var tone: Tone = Tone.PROFESSIONAL
    private var seriousness: Seriousness = Seriousness.VERY

    private val toneArg
        get() = AgentInput.EnumArgs(
            key = "tone",
            name = "Tone",
            value = Tone.PROFESSIONAL.name,
            possibleValues = Tone.entries.map { it.name },
        )

    private val seriousnessArg
        get() = AgentInput.EnumArgs(
            key = "seriousness",
            name = "Seriousness",
            value = Seriousness.VERY.name,
            possibleValues = Seriousness.entries.map { it.name },
        )

    abstract val name: String
    abstract val prompt: String

    open val neededInputs: List<AgentInput>
        get() = emptyList()

    open fun parseInputs(inputs: Map<String, AgentInput>) {
        tone = Tone.valueOf(inputs[toneArg.key]?.value ?: "")
        seriousness = Seriousness.valueOf(inputs[seriousnessArg.key]?.value ?: "")
    }

    val instructions: Instructions
        get() = Instructions(
            prompt = prompt,
            tone = tone,
            seriousness = seriousness,
        )

    val optionalInputs: List<AgentInput>
        get() = listOf(
            toneArg,
            seriousnessArg,
        )

}