import org.levi.coffee.Ipc
import org.levi.coffee.Window
import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.annotations.BindType
import java.io.BufferedReader
import java.io.File
import java.util.*

@BindType
class Person(
    val name: String = "",
    var age: Int = 0,
    val hobbies: List<String> = emptyList(),
    val string: Map<Person, List<Person>> = emptyMap(),
) {


    @BindMethod
    fun addTwoNumbers(a: Int, b: Int): Int {
        return a + b;
    }

    @BindMethod
    fun incrementAndPrint() {
        age++;
        println("My age increased to $age")
        println("invoking event...")
        Ipc.invoke("event")
    }
}

fun readHTML(filePath: String): String {
    val bufferedReader: BufferedReader = File(filePath).bufferedReader()
    val inputString = bufferedReader.use { it.readText() }
    return inputString
}

fun main() {
    val win = Window()
    win.setSize(700, 700)
    win.setTitle("My first Javatron app!")


    win.setURL(ClassLoader.getSystemClassLoader().getResource("dist/index.html")!!.toURI().toString())
    win.bind(
        Person(),
    )
    win.addBeforeStartCallback { println("Started app...") }
    win.addOnCloseCallback { println("Closed the app!") }

    win.run()
}
