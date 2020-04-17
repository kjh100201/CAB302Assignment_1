package billboard.viewer;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Fullscreen billboard display. An instance of this serves as the billboard viewer.
 */
public class BillboardFrame extends JFrame {
    /**
     * Constructs a new fullscreen billboard frame extended from JFrame. The billboards contents is generated from
     * the the XML file on the listed file path.
     */
    public BillboardFrame() {
        // Set the look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Unable to get system look and feel");
            System.exit(-1);
        }

        // Format the billboard GUI according the specifications of a selected XML document
        String xmlFilePath = "xml_docs/billboard10.xml";

        // Set as full screen application
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setExitInputs();
        BillboardDisplay billboard = new BillboardDisplay(xmlFilePath);
        getContentPane().add(billboard);
    }


    /**
     * Sets up the billboard window so the program will exit if ESC or Left Mouse Button are pressed.
     */
    public void setExitInputs() {
        addKeyListener(new KeyListener() {
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

        addMouseListener(new MouseListener() {
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
    }
}