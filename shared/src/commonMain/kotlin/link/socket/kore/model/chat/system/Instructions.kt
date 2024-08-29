package link.socket.kore.model.chat.system

data class Instructions(
    val prompt: String,
    val tone: Tone = Tone.PROFESSIONAL,
    val seriousness: Seriousness = Seriousness.VERY,
) {
    private var includeTone: Boolean = true
    private var includeSeriousness: Boolean = true

    fun build(): String = toneSection() + seriousnessSection() + "\n\n" + prompt

    private fun toneSection() = if (includeTone) {
        tone.build() + "\n\n"
    } else {
        ""
    }
    private fun seriousnessSection() = if (includeSeriousness) {
        seriousness.build() + "\n\n"
    } else {
        ""
    }
}