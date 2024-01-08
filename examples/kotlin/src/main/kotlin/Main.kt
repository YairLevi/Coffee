import org.levi.coffee.Window

fun main() {
    val win = Window()

    win.setTitle("Kotlin Demo")
    win.setSize(800, 600)
    win.setHTMLFromResource("index.html")
}