package billboard.viewer;

/**
 * Billboard Viewer main.
 * The Billboard Viewer is a non-interactive full-screen GUI application that displays billboard contents. The current
 * billboard to display is requested from the server every 15 seconds.
 */
public class Viewer {
    public static void main(String[] args) {
        // Create the billboard viewer GUI object
        BillboardFrame billboard = new BillboardFrame();
        
        billboard.setVisible(true);
    }
}
