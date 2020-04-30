package billboard.control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel {

    static void createMainPanel(){

        //checks if login was successful then deletes the login panel and creates the main control panel
//        if (LoginScreen.correctDetail) {
//            LoginScreen.login_frame.dispose(); //TODO: if login successful, delete the window and create new one
//        }

        JFrame main_frame = new JFrame("Main Control Panel");
//        super("Main Control Panel");
        JPanel main_panel = new JPanel(new GridLayout(3,1));

        JButton createB = new JButton("Create Billboards");
        JButton listB = new JButton("List Billboards");
        JButton scheduleB = new JButton("Schedule Billboards");
        JButton editB = new JButton("Edit Users");

        //----------------------------------Button Listeners
        //createB button listener
        createB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //TODO: Need to disable any functions on the main panel
                CreateBillboard.createBillboard();
            }
        });

        //-----------------------------------------------------------------------

        main_panel.add(createB);
        main_panel.add(listB);
        main_panel.add(scheduleB);
        main_panel.add(editB);

        main_frame.add(main_panel);

        main_frame.setPreferredSize(new Dimension(600, 300));
        main_frame.pack();
        main_frame.setLocationRelativeTo(null);

        //visibility
        main_frame.setVisible(true);
    }

    public static void main(String[] args) {
        LoginScreen.startLogInGUI();

        if (LoginScreen.correctDetail) createMainPanel();
//        createMainPanel();
        System.out.println("I am the main control panel.");
    }
}
