import org.levi.coffee.Window

fun main() {
    val w = Window(dev = true)
    w.setSize(800, 600)
    w.setTitle("Kotlin coffee app!")
    w.bind(
        App()
    )
    w.run()
}