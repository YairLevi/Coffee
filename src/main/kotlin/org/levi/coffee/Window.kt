package org.levi.coffee


import dev.webview.Webview
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.levi.coffee.internal.CodeGenerator
import org.levi.coffee.internal.MethodBinder
import org.levi.coffee.internal.util.FileUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.util.*
import java.util.function.Consumer
import kotlin.system.exitProcess

class Window(val args: Array<String>) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val _beforeStartCallbacks: MutableList<Runnable> = ArrayList()
    private val dev = Thread.currentThread().contextClassLoader.getResource("__jar__") == null
    private val _onCloseCallbacks: MutableList<Runnable> = ArrayList()
    private val _bindObjects = ArrayList<Any>()
    private val _webviewInitFunctions: MutableList<(wv: Webview) -> Unit> = ArrayList()

    fun setURL(url: String) {
        _webviewInitFunctions.add { it.loadURL(url) }
    }

    fun setHTMLFromResource(resourcePath: String) {
        val resource = ClassLoader.getSystemClassLoader().getResource(resourcePath)
        if (resource == null) {
            log.error("Resource at $resourcePath was not found.")
            exitProcess(1)
        }
        this.setURL(resource.toURI().toString())
    }

    fun setRawHTMLFromFile(path: String, isBase64: Boolean = false) {
        FileUtil.validateFileExists(path)
        val content = FileUtil.readText(path)
        setRawHTML(content, isBase64)
    }

    fun setRawHTML(html: String, isBase64: Boolean = false) {
        var url = "data:text/html"
        if (isBase64) {
            url += ";base64,${Base64.getEncoder().encodeToString(html.toByteArray())}"
        } else {
            url += ",$html"
        }
        this.setURL(url)
    }

    fun setTitle(title: String) {
        _webviewInitFunctions.add { it.setTitle(title) }
    }

    fun setSize(width: Int, height: Int) {
        _webviewInitFunctions.add { it.setSize(width, height) }
    }

    fun setMinSize(minWidth: Int, minHeight: Int) {
        _webviewInitFunctions.add { it.setMinSize(minWidth, minHeight) }
    }

    fun setMaxSize(maxWidth: Int, maxHeight: Int) {
        _webviewInitFunctions.add { it.setMaxSize(maxWidth, maxHeight) }
    }

    fun setFixedSize(fixedWidth: Int, fixedHeight: Int) {
        _webviewInitFunctions.add { it.setFixedSize(fixedWidth, fixedHeight) }
    }

    fun bind(vararg objects: Any) {
        for (o in objects) {
            _bindObjects.add(o)
        }
    }

    fun addBeforeStartCallback(r: Runnable) {
        _beforeStartCallbacks.add(r)
    }

    fun addOnCloseCallback(r: Runnable) {
        _onCloseCallbacks.add(r)
    }


    fun run() {
        var isGenerateOnly = false
        if (args.size == 1) {
            isGenerateOnly = args[0] == "generate"
        }

        if (isGenerateOnly) {
            val cg = CodeGenerator()
            cg.generateTypes(*_bindObjects.toTypedArray())
            cg.generateFunctions(*_bindObjects.toTypedArray())
            cg.generateEventsAPI()
            exitProcess(0)
        }

        // I know, Oh no, duplicate code...
        if (dev) {
            val cg = CodeGenerator()
            cg.generateTypes(*_bindObjects.toTypedArray())
            cg.generateFunctions(*_bindObjects.toTypedArray())
            cg.generateEventsAPI()
        }

        var server: NettyApplicationEngine? = null
        if (!dev) {
            val s = ServerSocket(0);
            val prodPort = s.localPort
            s.close()
            server = embeddedServer(Netty, port = prodPort, host = "localhost") {
                routing {
                    staticResources("/", "dist") {
                        default("index.html")
                        preCompressed(CompressedFileType.GZIP)
                    }
                }
            }
            server.start()
            _webviewInitFunctions.add { it.loadURL("http://localhost:$prodPort") }
        }

        val _webview = Webview(dev)
        _webviewInitFunctions.forEach { it.invoke(_webview) }
        MethodBinder.bind(_webview, *_bindObjects.toTypedArray())
        Ipc.setWebview(_webview)
        _beforeStartCallbacks.forEach(Consumer { it.run() })

        _webview.run()

        _onCloseCallbacks.forEach(Consumer { it.run() })
        _webview.close()

        server?.stop()

    }
}