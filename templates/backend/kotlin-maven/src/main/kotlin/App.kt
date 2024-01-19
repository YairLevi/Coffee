import org.levi.coffee.annotations.BindMethod

class App {
    @BindMethod
    fun sayHi() {
        println("Hello there!")
    }
}