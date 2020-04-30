package billboard.control_panel;

import javax.swing.*;
import java.awt.*;

public class MainPanel {

    static void createMainPanel(){

        //checks if login was successful then deletes the login panel and creates the main control panel
//        if (LoginScreen.correctDetail) {
//            LoginScreen.login_frame.dispose(); //TODO: if login successful, delete the window and create new one
//        }

        JFrame main_panel = new JFrame("Main Control Panel");

        main_panel.setPreferredSize(new Dimension(600, 400));
        main_panel.setLocationRelativeTo(null);
        main_panel.pack();

        //visibility
        main_panel.setVisible(true);
    }

    public static void main(String[] args) {
        LoginScreen.startLogInGUI();

        if (LoginScreen.correctDetail) createMainPanel();
//        createMainPanel();
        System.out.println("I am the main control panel.");
    }
}
