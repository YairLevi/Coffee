package org.levi.coffee

import org.levi.coffee.internal.CodeGenerator.generateEventsAPI
import org.levi.coffee.internal.CodeGenerator.generateFunctions
import org.levi.coffee.internal.CodeGenerator.generateTypes
import dev.webview.Webview
import org.levi.coffee.internal.MethodBinder
import java.util.*
import java.util.function.Consumer

class Window (withDevTools: Boolean = true) {
    private val _webview: Webview = Webview(withDevTools)
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

    fun setHTML(html: String) {
        _url = "data:text/html,$html"
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
        generateEventsAPI()
        generateTypes(*_bindObjects.toTypedArray())
        generateFunctions(*_bindObjects.toTypedArray())
        MethodBinder.bind(_webview, *_bindObjects.toTypedArray())

        _beforeStartCallbacks.forEach(Consumer { it.run() })
        Ipc.setWebview(_webview)
        _webview.loadURL(_url)
        _webview.run()
        _onCloseCallbacks.forEach(Consumer { it.run() })
        _webview.close()
    }
}