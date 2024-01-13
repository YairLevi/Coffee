import org.levi.coffee.Ipc
import org.levi.coffee.annotations.BindMethod

class Calculator {
    private var calCount = 0

    @BindMethod
    fun add(a: Double, b: Double): Double {
        calCount++
        dispatch()
        return a + b
    }
    @BindMethod
    fun sub(a: Double, b: Double): Double {
        calCount++
        dispatch()
        return a - b
    }
    @BindMethod
    fun mul(a: Double, b: Double): Double {
        calCount++
        dispatch()
        return a * b
    }
    @BindMethod
    fun div(a: Double, b: Double): Double {
        calCount++
        dispatch()
        return a / b
    }

    @BindMethod
    fun get(): Int {
        return this.calCount
    }

    private fun dispatch() {
        if (calCount % 3 == 0) {
            Ipc.invoke("count")
        }
    }
}