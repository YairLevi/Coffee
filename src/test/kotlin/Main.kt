import org.levi.coffee.Ipc
import org.levi.coffee.Window
import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.annotations.BindType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("Main")

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

fun main() {
    val win = Window()
    win.setSize(700, 700)
    win.setTitle("My first Javatron app!")
    win.setURL("http://localhost:5173")
    win.bind(
        Person(),
    )
    win.addBeforeStartCallback { log.info("Started app...") }
    win.addOnCloseCallback { log.info("Closed the app!") }

    win.run()
}
