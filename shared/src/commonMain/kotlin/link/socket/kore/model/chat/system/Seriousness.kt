package link.socket.kore.model.chat.system

enum class Seriousness {
    EXTREMELY,
    VERY,
    SOMEWHAT,
    SLIGHTLY,
    NOT;

    fun build(): String = "Your responses should be ${name.lowercase()} serious."
}