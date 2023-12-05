package link.socket.kore.model.agent.bundled

import com.lordcodes.turtle.ShellLocation
import link.socket.kore.model.agent.KoreAgent
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath

/*
 * This Agent is responsible for reading a file on the local disk with the given name, and returning the
 * content of the file after executing.
 *
 * @constructor primary constructor description
 * @param folderPath the path where the file should be read from, relative to your home directory
 * @param fileName the name of the file to read
 */
data class ReadFileAgent(
    val folderPath: String,
    val fileName: String,
) : KoreAgent.Unassisted {

    companion object {
        const val NAME = "Read File"
    }

    override val name: String = NAME

    override suspend fun executeUnassisted(): String {
        val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath
        val filePath = "$workingDirPath/$fileName".toPath()
        var result = ""

        try {
            FileSystem.SYSTEM.read(filePath) {
                while (true) {
                    val line = readUtf8Line() ?: break
                    result += (line + "\n")
                }
            }
        } catch (e: IOException) {
            return "Reading file $fileName failed with exception: $e."
        }

        return result
    }
}
