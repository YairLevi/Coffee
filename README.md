> **_NOTE:_**
> 
> This package is still in development, and I do intend on adding some more features in the future. For now, I plan on making
> an example folder with some docs as well. For any requests for features feel free to submit an issue\pull request.


# Coffee

Coffee is a light and basic package for writing desktop applications using familiar web technologies and frameworks.

----
## Get Started
**1. Maven**

Add to `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<!-- ... -->

<dependencies>
    <dependency>
        <groupId>com.github.YairLevi</groupId>
        <artifactId>Coffee</artifactId>
        <version>latest_version</version>
    </dependency>
</dependencies>
```
**2. Gradle** 

Add it in your root build.gradle at the end of repositories:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
Add dependency
```groovy
dependencies {
	implementation 'com.github.YairLevi:Coffee2:0.1.1'
}
```
3. JAR
[Will be added later on. If you want, you could still download the source and build.]
____
## Example
The code for both Java and Kotlin is pretty much the same.
```kotlin
import org.levi.coffee.Ipc
import org.levi.coffee.Window
import org.levi.coffee.annotations.BindMethod
import org.levi.coffee.annotations.BindType

@BindType
class MyClass(
    val name: String = "",
    var age: Int = 0,
    val hobbies: List<String> = emptyList(),
) {
    @BindMethod
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    @BindMethod
    fun incrementAndInvoke() {
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
    // Or some html... using win.setHTML("<!DOCTYPE...")
    win.bind(
        MyClass(),
    )
    win.addBeforeStartCallback { println("Started app...") }
    win.addOnCloseCallback { println("Closed the app!") }

    win.run()
}

```
