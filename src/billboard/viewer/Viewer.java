package billboard.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Viewer {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        // Create the billboard viewer GUI object
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = createBillboardViewer();

        // Format the billboard GUI according the specifications of a selected XML document
        String filePath = "xml_docs/billboard10.xml";
        BillboardFormatter.format(frame, filePath);

        frame.setVisible(true);
    }


    /**
     * Creates a new full screen JFrame object that will be the billboard viewer.
     * This method also adds functionality so the program will exit if ESC or Left Mouse Button are pressed.
     *
     * @return The new billboard viewer GUI object.
     */
    private static JFrame createBillboardViewer()
    {
        JFrame frame = new JFrame("Billboard Viewer");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    System.exit(0);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        return frame;
    }
}
