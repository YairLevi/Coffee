package org.levi.coffee

import dev.webview.Webview
import org.levi.coffee.internal.CodeGenerator
import org.levi.coffee.internal.MethodBinder
import org.levi.coffee.internal.util.FileUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer
import kotlin.system.exitProcess

class Window(dev: Boolean = true) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val _webview: Webview = Webview(dev)
    private val _beforeStartCallbacks: MutableList<Runnable> = ArrayList()
    private val _onCloseCallbacks: MutableList<Runnable> = ArrayList()
    private val _bindObjects: MutableList<Any> = ArrayList()
    private var _url: String = ""

    init {
        setSize(800, 600)
    }

    fun setURL(url: String) {
        _url = url
    }

    fun setHTMLFromResource(resourcePath: String) {
        val resource = ClassLoader.getSystemClassLoader().getResource(resourcePath)
        if (resource == null) {
            log.error("Resource at $resourcePath was not found.")
            exitProcess(1)
        }
        _url = resource.toURI().toString()
    }

    fun setRawHTMLFromFile(path: String, isBase64: Boolean = false) {
        FileUtil.validateFileExists(path)
        val content = FileUtil.readText(path)
        setRawHTML(content, isBase64)
    }

    fun setRawHTML(html: String, isBase64: Boolean = false) {
        _url = "data:text/html"
        if (isBase64) {
            _url += ";base64,${Base64.getEncoder().encodeToString(html.toByteArray())}"
        } else {
            _url += ",$html"
        }
    }

    fun setTitle(title: String) {
        _webview.setTitle(title)
    }

    fun setSize(width: Int, height: Int) {
        _webview.setSize(width, height)
    }

    fun setMinSize(minWidth: Int, minHeight: Int) {
        _webview.setMinSize(minWidth, minHeight)
    }

    fun setMaxSize(maxWidth: Int, maxHeight: Int) {
        _webview.setMaxSize(maxWidth, maxHeight)
    }

    fun setFixedSize(fixedWidth: Int, fixedHeight: Int) {
        _webview.setFixedSize(fixedWidth, fixedHeight)
    }

    fun bind(vararg objects: Any) {
        _bindObjects.addAll(objects)
    }

    fun addBeforeStartCallback(r: Runnable) {
        _beforeStartCallbacks.add(r)
    }

    fun addOnCloseCallback(r: Runnable) {
        _onCloseCallbacks.add(r)
    }


    fun run() {
        CodeGenerator.generateEventsAPI()
        CodeGenerator.generateTypes(*_bindObjects.toTypedArray())
        CodeGenerator.generateFunctions(*_bindObjects.toTypedArray())
        MethodBinder.bind(_webview, *_bindObjects.toTypedArray())

        _beforeStartCallbacks.forEach(Consumer { it.run() })
        Ipc.setWebview(_webview)

        _webview.loadURL(_url)
        _webview.run()

        _onCloseCallbacks.forEach(Consumer { it.run() })
        _webview.close()
    }
}