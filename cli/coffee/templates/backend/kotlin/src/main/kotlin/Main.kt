import org.levi.coffee.Window

fun main(args: Array<String>) {
    val w = Window(args)
    w.setSize(800, 600)
    w.setURL("http://localhost:5173")
    w.setTitle("Kotlin coffee app!")
    w.bind(
        App()
    )
    w.run()
}