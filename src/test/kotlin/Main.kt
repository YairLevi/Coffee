import dev.webview.Webview
import org.levi.coffee.Window
import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.internal.CodeGenerator
import spark.Spark.*


fun main() {
    staticFiles.location("/dist2")
    port(4567)
    init()

//    val wv = Webview(true)
//    wv.setSize(700, 700)
//    wv.setTitle("My first Javatron app!")
//
//    wv.bind("Basic_echo") { a -> Basic.echo(); a }
//
//
//    wv.loadURL("http://localhost:4567")
//    wv.run()
//    wv.close()

///////////////////////////////////////////////

//    val cg = CodeGenerator()
//    cg.generate(Basic())

    val win = Window()
    win.setSize(700, 700)
    win.setTitle("My first Javatron app!")
//
//    win.bindSingle("Basic_echo") { args ->
//        println("Here in Basic_echo!")
//        args
//    }
    win.bind(Basic())
//
    win.addBeforeStartCallback { println("Started app...") }
    win.addOnCloseCallback { println("Closed the app!") }

    win.setURL("http://localhost:4567")
    win.run()
    stop()
}
