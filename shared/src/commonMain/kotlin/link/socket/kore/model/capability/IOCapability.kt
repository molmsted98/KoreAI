package link.socket.kore.model.capability

//import com.lordcodes.turtle.ShellLocation
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.model.tool.LLMCSVFunction1
import link.socket.kore.model.tool.LLMFunction1
import link.socket.kore.model.tool.ParameterDefinition

sealed interface IOCapability : Capability {

    val readFileParams
        get() = listOf(
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

    data object CreateFile : IOCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provide(
                "createFile",
                "Creates a file on the local disk with the given name and content, and returns the status of the file creation after executing.",
                { args: JsonObject ->
                    val folderPath = args.getValue("folderPath").jsonPrimitive.content
                    val fileName = args.getValue("fileName").jsonPrimitive.content
                    val fileContent = args.getValue("fileContent").jsonPrimitive.content
                    createFile(folderPath, fileName, fileContent)
                } as LLMFunction1,
                listOf(
                    ParameterDefinition(
                        name = "folderPath",
                        isRequired = true,
                        definition = buildJsonObject {
                            put("type", "string")
                            put("description", "The path where the file should be created, relative to the user's home directory.")
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
            )

        /*
         * Creates a file on the local disk with the given name and content, and returns the status of the file
         * creation after executing.
         *
         * @param folderPath the path where the file should be created, relative to the user's home directory
         * @param fileName the name of the file to create
         * @param fileContent the content that the new file should contain
         */
        private fun createFile(
            folderPath: String,
            fileName: String,
            fileContent: String,
        ): String {
//            val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath
//            val filePath = "$workingDirPath/$fileName".toPath()
//
//            println(filePath)
//            try {
//                FileSystem.SYSTEM.write(filePath) {
//                    writeUtf8(fileContent)
//                }
//            } catch (e: IOException) {
//                return "File $fileName creation failed with exception: $e."
//            }
//
//            return "File $fileName created successfully."
            return "TODO"
        }
    }

    data object ReadFile : IOCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provide(
                "readFile",
                "Reads a file on the local disk with the given name, and returns the content of the file after executing.",
                { args: JsonObject ->
                    val folderPath = args.getValue("folderPath").jsonPrimitive.content
                    val fileName = args.getValue("fileName").jsonPrimitive.content
                    readFile(folderPath, fileName)
                } as LLMFunction1,
                readFileParams,
            )

        /*
         * Reads a file on the local disk with the given name, and returns the content of the file after executing.
         *
         * @param folderPath the path where the file should be read from, relative to the user's home directory
         * @param fileName the name of the file to read
         */
        private fun readFile(
            folderPath: String,
            fileName: String,
        ): String {
//            val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath
//            val filePath = "$workingDirPath/$fileName".toPath()
//            var result = ""
//
//            try {
//                FileSystem.SYSTEM.read(filePath) {
//                    while (true) {
//                        val line = readUtf8Line() ?: break
//                        result += (line + "\n")
//                    }
//                }
//            } catch (e: IOException) {
//                return "Reading file $fileName failed with exception: $e."
//            }
//
//            return result
            return "TODO"
        }
    }

    data object ParseCsv : IOCapability {

        override val impl: Pair<String, FunctionProvider> =
            FunctionProvider.provideCSV(
                "parseCsv",
                "Reads a CSV file on the local disk with the given name, and returns the table of strings after executing.",
                { args: JsonObject ->
                    val folderPath = args.getValue("folderPath").jsonPrimitive.content
                    val fileName = args.getValue("fileName").jsonPrimitive.content
                    parseCsv(folderPath, fileName)
                } as LLMCSVFunction1,
                readFileParams,
            )

        private fun parseCsv(
            folderPath: String,
            fileName: String,
        ): List<List<String>> {
//            val workingDirPath = ShellLocation.HOME.resolve(folderPath).absolutePath
//            val filePath = "$workingDirPath/$fileName".toPath()
//            val lines = mutableListOf<String>()
//
//            try {
//                FileSystem.SYSTEM.read(filePath) {
//                    while (true) {
//                        val line = readUtf8Line() ?: break
//                        lines.add(line)
//                    }
//                }
//            } catch (e: IOException) {
//                return listOf(listOf("Reading file $fileName failed with exception: $e."))
//            }
//
//            return lines.map { it.split(",") }
            return listOf(listOf("TODO"))
        }
    }

}