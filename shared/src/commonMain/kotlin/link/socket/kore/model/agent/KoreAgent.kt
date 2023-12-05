@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.lordcodes.turtle.ShellLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.model.conversation.ChatHistory
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.ParameterDefinition
import link.socket.kore.ui.conversation.selector.AgentInput
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.IOException

sealed interface KoreAgent : LLMAgent {

    val name: String

    val neededInputs: List<AgentInput>
    fun parseNeededInputs(inputs: Map<String, AgentInput>)

    interface HumanAssisted : KoreAgent {
        suspend fun executeHumanAssistance(): String
    }

    abstract class HumanAndLLMAssisted(
        override val scope: CoroutineScope,
    ) : KoreAgent, HumanAssisted {

        override var chatHistory: ChatHistory = ChatHistory.Threaded.Uninitialized
            set(value) {
                field = value
                updateCompletionRequest()
            }

        override var completionRequest: ChatCompletionRequest? = null

        override val availableFunctions: Map<String, FunctionProvider> =
            mapOf(
                FunctionProvider.provide(
                    "executeHumanAssisted",
                    "Prompts the user through a CLI to either enter text, or to confirm text that you have generated",
                    ::callHumanAssistance,
                ),
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
                        )
                    )
                ),
                FunctionProvider.provide(
                    "readFile",
                    "Reads a file on the local disk with the given name, and returns the content of the file after executing.",
                    ::callCreateFile,
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
                    )
                )
            )

        fun callHumanAssistance(): String {
            var response = ""

            scope.launch {
                response = executeHumanAssistance()
            }

            return response
        }
    }

    fun callCreateFile(args: JsonObject): String {
        val folderPath = args.getValue("folderPath").jsonPrimitive.content
        val fileName = args.getValue("fileName").jsonPrimitive.content
        val fileContent = args.getValue("fileContent").jsonPrimitive.content

        var response = ""

        scope.launch {
            response = createFile(folderPath, fileName, fileContent)
        }

        return response
    }

    private fun callReadFile(args: JsonObject): String {
        val folderPath = args.getValue("folderPath").jsonPrimitive.content
        val fileName = args.getValue("fileName").jsonPrimitive.content

        var response = ""

        scope.launch {
            response = readFile(folderPath, fileName)
        }

        return response
    }

    /*
     * Creates a file on the local disk with the given name and content, and returns the status of the file
     * creation after executing.
     *
     * @param folderPath the path where the file should be created, relative to the user's home directory
     * @param fileName the name of the file to create
     * @param fileContent the content that the new file should contain
     */
    suspend fun createFile(
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
    suspend fun readFile(
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
}
