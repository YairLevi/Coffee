# Welcome!
### This is a general, short overview of the package.

### Initiating a project
Initial project structure using `coffee init`. We'll assume we are using Kotlin and React with TS.
```
├── frontend
│   ├── public
│   ├── src
│   ├── package.json 
│   ├── ...
├── src
│   ├── main
│   │   ├── kotlin
│   │   ├── resources
├── .gitignore
└── pom.xml   
```

Let's add some basic kotlin classes and bind it to a boilerplate app:
```kotlin
import org.levi.coffee.Ipc
import org.levi.coffee.Window
import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.annotations.BindType

@BindType
data class Person(
    var name: String = "",
    var age: Int = 0,
    var friends: List<Person> = emptyListOf()
) {}

class Team(
    val members: List<Person> = mutableListOf()
) {
    @BindMethod
    fun addMember(p: Person) {
        members.add(p)
    }
}

fun main() {
    val win = Window()
    win.setSize(700, 700)
    win.setTitle("My Person App")
    win.setURL("http://localhost:5173")
    win.bind(
        Person(),   // will bind the Person type
        Team(),     // will bind a team object.
    )
    win.addBeforeStartCallback { println("Started app...") }
    win.addOnCloseCallback { println("Closed the app!") }
    win.run()
}
```
### Development
Now, the moment we run `coffee dev` using the CLI, the package will automatically create
a new folder inside the `frontend` folder, called `coffee`:
```
├── frontend
│   ├── coffee
│   │   ├── methods
│   │   ├── types.ts
│   │   ├── window.ts
│   │   ├── events.ts
│   ├── ...
```
First, the `types.ts` file contains all the types that were bound, converted to their corresponding typescript alternative. 
In this example, `Person`. so, the contents of that file would be:
```ts
export type Person = {
  name: string
  age: number
  friends: (Person)[]
}
```
The `methods` folder contains type safe declarations for bound methods.
In our example it will have two files, `Team.js` and `Team.d.ts`.
```ts
// Team.js
export function addMember(arg0) {
  return window["Team_addMember"](arg0)
}

// Team.d.ts

import * as t from '../types'

export function addMember(arg0: t.Person);
```
The `window.ts` is not meant to be interacted with, but the `events.ts` which uses it,
does export one thing. An IPC variable that can listen to events from the backend, and respond to them.
```ts
import { ipc } from "../coffee/events"

ipc.addHandler("some-event", () => {
  // do stuff...
})
```
In the backend, there is also a global instance of an `Ipc` class, which is used to trigger events.

### Build and Packaging.
using `coffee build`, The package currently only builds a self-contained `.jar` file, but there are some other solutions
to turn that file into an executable.
