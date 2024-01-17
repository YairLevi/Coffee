package org.levi.coffee.internal

import org.levi.coffee.annotations.BindType
import org.levi.coffee.internal.util.FileUtil
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import kotlin.system.exitProcess

internal class CodeGenerator {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val CLIENT_FOLDER_PATH = "frontend/coffee/"
    private val METHODS_FOLDER_PATH = CLIENT_FOLDER_PATH + "methods/"
    private val TYPES_FILE_PATH = CLIENT_FOLDER_PATH + "types.ts"

    private val ipcResources = listOf("events.ts", "window.ts")

    init {
        FileUtil.createOrReplaceDirectory(CLIENT_FOLDER_PATH)
        FileUtil.createOrReplaceFile(TYPES_FILE_PATH)
        FileUtil.createOrReplaceDirectory(METHODS_FOLDER_PATH)
    }

    fun generateEventsAPI() {
        for (resource in ipcResources) {
            FileUtil.createOrReplaceFile(CLIENT_FOLDER_PATH + resource)

            val eventsResource = this::class.java.getResource("/$resource")
                ?: throw Exception("Failed to find $resource in resources.")

            File(CLIENT_FOLDER_PATH + resource).printWriter().use { out -> out.println(eventsResource.readText()) }
        }
        log.info("Created events API files.")
    }

    fun generateTypes(vararg objects: Any) {
        try {
            val writer = PrintWriter(TYPES_FILE_PATH)
            val classes = objects.map { it::class.java }
            TypeConverter.boundTypes.addAll(classes.map { it.simpleName })
            for (c in classes) {
                if (!c.isAnnotationPresent(BindType::class.java)) {
                    continue
                }

                val fieldsToBind = BindFilter.fieldsOf(c)

                // Declare type and export
                writer.println("export type ${c.simpleName} = {")

                // Add fields and map the types from java to typescript
                for (field in fieldsToBind) {
                    val name = field.name
                    val type = TypeConverter.convert(field.genericType, false)
                    writer.println("\t$name: $type")
                }
                writer.println("}\n")

                log.info("Created type: ${c.simpleName}")
            }
            writer.close()
        } catch (e: IOException) {
            log.error("Failed to generate a types file `types.ts`.", e)
            exitProcess(1)
        }
    }

    fun generateFunctions(vararg objects: Any) {
        for (c in objects.map { it.javaClass }) {
            val methodCount = BindFilter.methodsOf(c).size
            if (methodCount == 0) {
                continue
            }

            createJavascriptFunctions(c)
            createTypescriptDeclarations(c)
        }
    }

    private fun createJavascriptFunctions(c: Class<*>) {
        try {
            val className = c.simpleName
            val path = "$METHODS_FOLDER_PATH$className.js"
            FileUtil.createOrReplaceFile(path)
            val writer = PrintWriter(path)

            val methodsToBind = BindFilter.methodsOf(c)

            for (method in methodsToBind) {
                val methodName = method.name
                val argsString = method.parameters.joinToString(",") { it.name }
                writer.println("export function $methodName($argsString) {")
                writer.println("\treturn window[\"${className}_$methodName\"]($argsString);")
                writer.println("}\n")
            }

            writer.close()
        } catch (e: IOException) {
            log.error("Failed to create javascript function for class ${c.simpleName}.", e)
            exitProcess(1)
        }
    }

    private fun createTypescriptDeclarations(c: Class<*>) {
        try {
            val path = "$METHODS_FOLDER_PATH${c.simpleName}.d.ts"
            FileUtil.createOrReplaceFile(path)
            val writer = PrintWriter(path)

            val methodsToBind = BindFilter.methodsOf(c)

            writer.println("import * as t from '../types';\n")

            for (method in methodsToBind) {
                val argsString = method.parameters.joinToString(", ") {
                    "${it.name}: ${TypeConverter.convert(it.parameterizedType, true)}"
                }
                val convertedReturnType = TypeConverter.convert(method.genericReturnType, true)
                var returnTypeString = ": Promise<$convertedReturnType>"
                if (convertedReturnType == "void") returnTypeString = ""

                writer.println("export function ${method.name}($argsString)$returnTypeString;\n")

                log.info("Created function ${method.name} of class ${c.simpleName}")
            }

            writer.close()
        } catch (e: IOException) {
            log.error("Failed to create typescript declarations for class ${c.simpleName}.", e)
            exitProcess(1)
        }
    }
}