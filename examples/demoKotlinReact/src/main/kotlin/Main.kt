import org.levi.coffee.Ipc
import org.levi.coffee.Window

fun main() {
    val w = Window(dev = true)
    w.setSize(800, 600)
    w.setURL("http://localhost:5173")
    w.setTitle("Kotlin coffee app!")

    w.bind(
        App(),
        Person()
    )
    w.run()
}