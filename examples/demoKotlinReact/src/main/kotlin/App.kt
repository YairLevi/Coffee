import org.levi.coffee.Ipc
import org.levi.coffee.annotations.BindAllMethods

@BindAllMethods
class App(
    val persons: MutableList<Person> = mutableListOf()
) {
    fun newPerson(person: Person) {
        persons.add(person)
        Ipc.invoke("ppl-count")
    }

    fun getPersonsByName(name: String): Person {
        return persons.first { it.name == name }
    }

    fun getAll(): List<Person> {
        return persons
    }
}