package org.levi.coffee.internal

import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.annotations.BindType
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import kotlin.system.exitProcess

internal object CodeGenerator {
    private val log = LoggerFactory.getLogger(this::class.java)

    private const val CLIENT_FOLDER_PATH = "frontend/coffee/"
    private const val METHODS_FOLDER_PATH = CLIENT_FOLDER_PATH + "methods/"
    private const val TYPES_FILE_PATH = CLIENT_FOLDER_PATH + "types.ts"

    private const val EVENTS_API_FILE_RESOURCE = "/events.ts"
    private const val EVENTS_API_FILE_DEST = CLIENT_FOLDER_PATH + "events.ts"

    init {
        FileUtil.createOrReplaceFile(CLIENT_FOLDER_PATH)
        FileUtil.createOrReplaceFile(TYPES_FILE_PATH)
        FileUtil.createOrReplaceDirectory(METHODS_FOLDER_PATH)
    }
    
    fun generateEventsAPI() {
        FileUtil.createOrReplaceFile(EVENTS_API_FILE_DEST)

        val eventsResource = this::class.java.getResource(EVENTS_API_FILE_RESOURCE)
            ?: throw Exception("Failed to find $EVENTS_API_FILE_RESOURCE in resources.")

        File(EVENTS_API_FILE_DEST).printWriter().use { out -> out.println(eventsResource.readText()) }
        log.info("Created events API files.")
    }

    fun generateTypes(vararg objects: Any) {
        try {
            val writer = PrintWriter(TYPES_FILE_PATH)
            val classes = objects.map { it.javaClass }
            TypeConverter.boundTypes.addAll(classes.map { it.simpleName })
            for (c in classes) {
                if (!c.isAnnotationPresent(BindType::class.java)) {
                    continue
                }

                val destructedClass = TypeConverter.getDestructedClass(c)

                // Declare type and export
                writer.println("export type ${destructedClass.name} = {")

                // Add fields and map the types from java to typescript
                for (field in destructedClass.fields) {
                    val name = field.name
                    val type = TypeConverter.convert(field.type, false)
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
            val methodCount = c.declaredMethods.filter { it.isAnnotationPresent(BindMethod::class.java) }.size
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

            for (method in c.declaredMethods) {
                if (!method.isAnnotationPresent(BindMethod::class.java)) continue

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

            writer.println("import * as t from '../types';\n")

            for (method in c.declaredMethods.sortedBy { it.name }) {
                if (!method.isAnnotationPresent(BindMethod::class.java)) {
                    continue
                }

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