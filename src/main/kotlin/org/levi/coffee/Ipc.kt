package org.levi.coffee

import org.slf4j.LoggerFactory
import com.google.gson.Gson
import dev.webview.Webview

object Ipc {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val gson = Gson()
    private lateinit var webview: Webview

    fun setWebview(webview: Webview) {
        Ipc.webview = webview
    }

    fun invoke(event: String, vararg args: Any?) {
        try {
            val argsJson = gson.toJson(args)
            val argsString = argsJson.substring(1, argsJson.length - 1)
            webview.eval("window.ipc['$event'].handler($argsString)")
        } catch (e: Exception) {
            log.error("Failed to invoke event $event")
        }
    }
}