package link.socket.kore.model.capability

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import link.socket.kore.io.createFile
import link.socket.kore.io.parseCsv
import link.socket.kore.io.readFile
import link.socket.kore.model.tool.FunctionProvider
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
                    createFileImpl(folderPath, fileName, fileContent)
                },
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
        private fun createFileImpl(
            folderPath: String,
            fileName: String,
            fileContent: String,
        ): String {
            val result = createFile(folderPath, fileName, fileContent)
            return result.getOrNull() ?: "Failed to create file"
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
                    readFileImpl(folderPath, fileName)
                },
                readFileParams,
            )

        /*
         * Reads a file on the local disk with the given name, and returns the content of the file after executing.
         *
         * @param folderPath the path where the file should be read from, relative to the user's home directory
         * @param fileName the name of the file to read
         */
        private fun readFileImpl(
            folderPath: String,
            fileName: String,
        ): String {
            val result = readFile(folderPath, fileName)
            return result.getOrNull() ?: "Failed to read file"
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
                    parseCsvImpl(folderPath, fileName)
                },
                readFileParams,
            )

        private fun parseCsvImpl(
            folderPath: String,
            fileName: String,
        ): List<List<String>> {
            val result = parseCsv(folderPath, fileName)
            return result.getOrNull() ?: listOf(listOf("Failed to parse CSV"))
        }
    }

}