import org.levi.coffee.annotations.BindType

@BindType
class Person(
    var age: Int = 0,
    var name: String = "",
    var hobbies: MutableSet<String> = mutableSetOf()
) {
    fun addHobby(hobby: String) {
        hobbies.add(hobby)
    }

    fun removeHobby(hobby: String) {
        hobbies.remove(hobby)
    }
}