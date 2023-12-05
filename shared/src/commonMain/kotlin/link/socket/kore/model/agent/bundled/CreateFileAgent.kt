package link.socket.kore.model.agent.bundled

import com.lordcodes.turtle.ShellLocation
import link.socket.kore.model.agent.KoreAgent
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath

/*
 * This Agent is responsible for creating a file on the local disk with the given name and content.
 *
 * @constructor primary constructor description
 * @param folderPath the path where the file should be created, relative to your home directory
 * @param fileName the name of the file to create
 * @param fileContent the content that the new file should contain
 */
data class CreateFileAgent(
    val folderPath: String,
    val fileName: String,
    val fileContent: String,
) : KoreAgent.Unassisted {

    companion object {
        const val NAME = "Create File"
    }

    override val name: String = NAME

    override suspend fun executeUnassisted(): String {
        val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath

        try {
            FileSystem.SYSTEM.write("$workingDirPath/$fileName".toPath()) {
                writeUtf8(fileContent)
            }
        } catch (e: IOException) {
            return "File $fileName creation failed with exception: $e."
        }

        return "File $fileName created successfully."
    }
}
