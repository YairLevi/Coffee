import org.levi.coffee.Window;


public class Main {
    public static void main(String[] args) {
        Window w = new Window(true);
        w.setSize(800, 600);
        w.setTitle("Java coffee app!");
        w.bind(new App());
        w.run();
    }
}