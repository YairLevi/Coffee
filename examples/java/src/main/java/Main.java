import org.levi.coffee.Window;

public class Main {
    public static void main(String[] args) {
        Window win = new Window();
        win.setTitle("Java Demo");
        win.setSize(800, 600);
        win.setHTMLFromResource("index.html");
    }
}