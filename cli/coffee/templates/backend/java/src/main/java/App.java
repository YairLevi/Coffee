import org.levi.coffee.annotations.BindMethod;

public class App {
    @BindMethod
    public void sayHi() {
        System.out.println("Hello there!");
    }
}