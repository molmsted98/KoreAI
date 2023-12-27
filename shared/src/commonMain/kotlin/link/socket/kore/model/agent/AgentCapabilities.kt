package link.socket.kore.model.agent

import com.lordcodes.turtle.ShellLocation
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.IOException

interface AgentCapabilities {

    val agentFunctions: Map<String, FunctionProvider>
        get() = mapOf(
            FunctionProvider.provide(
                "createFile",
                "Creates a file on the local disk with the given name and content, and returns the status of the file creation after executing.",
                ::callCreateFile,
                listOf(
                    ParameterDefinition(
                        name = "folderPath",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put(
                                "description",
                                "The path where the file should be created, relative to the user's home directory."
                            )
                        }
                    ),
                    ParameterDefinition(
                        name = "fileName",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The name of the file to create.")
                        }
                    ),
                    ParameterDefinition(
                        name = "fileContent",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The content that the new file should contain.")
                        }
                    ),
                ),
            ),
            FunctionProvider.provide(
                "readFile",
                "Reads a file on the local disk with the given name, and returns the content of the file after executing.",
                ::callReadFile,
                listOf(
                    ParameterDefinition(
                        name = "folderPath",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put(
                                "description",
                                "The path where the file should be read from, relative to the user's home directory."
                            )
                        }
                    ),
                    ParameterDefinition(
                        name = "fileName",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The name of the file to read.")
                        }
                    ),
                ),
            ),
            FunctionProvider.provideCSV(
                "parseCsv",
                "Reads a CSV file on the local disk with the given name, and returns the table of strings after executing.",
                ::callParseCsv,
                listOf(
                    ParameterDefinition(
                        name = "folderPath",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put(
                                "description",
                                "The path where the file should be read from, relative to the user's home directory."
                            )
                        }
                    ),
                    ParameterDefinition(
                        name = "fileName",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The name of the file to read.")
                        }
                    ),
                ),
            ),
        )

    private fun callCreateFile(args: JsonObject): String {
        val folderPath = args.getValue("folderPath").jsonPrimitive.content
        val fileName = args.getValue("fileName").jsonPrimitive.content
        val fileContent = args.getValue("fileContent").jsonPrimitive.content
        return createFile(folderPath, fileName, fileContent)
    }

    private fun callReadFile(args: JsonObject): String {
        val folderPath = args.getValue("folderPath").jsonPrimitive.content
        val fileName = args.getValue("fileName").jsonPrimitive.content
        return readFile(folderPath, fileName)
    }

    private fun callParseCsv(args: JsonObject): List<List<String>> {
        val folderPath = args.getValue("folderPath").jsonPrimitive.content
        val fileName = args.getValue("fileName").jsonPrimitive.content
        return parseCsv(folderPath, fileName)
    }

    /*
     * Creates a file on the local disk with the given name and content, and returns the status of the file
     * creation after executing.
     *
     * @param folderPath the path where the file should be created, relative to the user's home directory
     * @param fileName the name of the file to create
     * @param fileContent the content that the new file should contain
     */
    fun createFile(
        folderPath: String,
        fileName: String,
        fileContent: String,
    ): String {
        val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath
        val filePath = "$workingDirPath/$fileName".toPath()

        println(filePath)
        try {
            FileSystem.SYSTEM.write(filePath) {
                writeUtf8(fileContent)
            }
        } catch (e: IOException) {
            return "File $fileName creation failed with exception: $e."
        }

        return "File $fileName created successfully."
    }

    /*
     * Reads a file on the local disk with the given name, and returns the content of the file after executing.
     *
     * @param folderPath the path where the file should be read from, relative to the user's home directory
     * @param fileName the name of the file to read
     */
    fun readFile(
        folderPath: String,
        fileName: String,
    ): String {
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

    fun parseCsv(
        folderPath: String,
        fileName: String,
    ): List<List<String>> {
        val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath
        val filePath = "$workingDirPath/$fileName".toPath()
        val lines = mutableListOf<String>()

        try {
            FileSystem.SYSTEM.read(filePath) {
                while (true) {
                    val line = readUtf8Line() ?: break
                    lines.add(line)
                }
            }
        } catch (e: IOException) {
            return listOf(listOf("Reading file $fileName failed with exception: $e."))
        }

        return lines.map { it.split(",") }
    }
}
