package org.levi.coffee.internal

import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess


internal object FileManager {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun createOrReplaceDirectory(pathString: String) {
        if (
            !with(File(pathString)) {
                deleteRecursively()
                mkdirs()
            }
        ) {
            log.error("Error occurred while creating or replacing directory")
            exitProcess(1)
        }
    }

    fun createOrReplaceFile(path: String) {
        try {
            with(File(path)) {
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    throw IOException("Failed to create parent dirs for file.")
                }
                if (!createNewFile()) {
                    delete()
                    createNewFile()
                }
            }
        } catch (e: IOException) {
            log.error("Error creating a types file for the frontend.", e)
            exitProcess(1)
        }
    }
}