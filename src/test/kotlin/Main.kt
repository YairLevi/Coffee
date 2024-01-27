import org.levi.coffee.Window
import org.levi.coffee.annotations.BindType

@BindType
class App(
    val name: String = "",
    val age: Int = 0,
    val list: List<String> = emptyList(),
    val map: Map<String, Int> = emptyMap(),
)

fun main(args: Array<String>) {
    val win = Window(dev = true, args = args)
    win.setSize(700, 700)
    win.setTitle("My first Javatron app!")

    win.setURL("http://localhost:5173")
    win.bind(
        App()
    )

    win.addBeforeStartCallback { println("Started app...") }
    win.addOnCloseCallback { println("Closed the app!") }

    win.run()
}
