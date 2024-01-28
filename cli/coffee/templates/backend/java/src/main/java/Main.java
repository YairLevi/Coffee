import org.levi.coffee.Window;


public class Main {
    public static void main(String[] args) {
        Window w = new Window(args);
        w.setSize(800, 600);
        w.setTitle("Java coffee app!");
        w.setURL("http://localhost:5173");
        w.bind(new App());
        w.run();
    }
}