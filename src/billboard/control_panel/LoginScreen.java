package billboard.control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen {

    private static JLabel headerLabel;
    private static JLabel statusLabel;

    public static boolean correctDetail = false;
    public static JFrame login_frame = new JFrame("Login Screen");

    public static void startLogInGUI(){
        //window: frame
        login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set up elements
        JTextField un = new JTextField(13);
        JTextField pw = new JTextField(13);
        JLabel prompt = new JLabel("Please enter username and password");
        JLabel unlabel = new JLabel("username:");
        JLabel pwlabel = new JLabel("password:");

        //prompt
        prompt.setHorizontalAlignment(SwingConstants.CENTER);
        prompt.setBounds(15, 15,250,20);

        //username
        unlabel.setBounds(30,45,90,20);
        un.setBounds(100,45,150,20);

        //password
        pwlabel.setBounds(30,80,90,20);
        pw.setBounds(100,80,150,20);

        //log in button
        JButton loginB = new JButton("Log-in");
        loginB.setBounds(100, 120, 100, 20);

        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("",JLabel.CENTER);
        statusLabel.setSize(350,100);
        headerLabel.setText("Button Demo");

        //log in button listener
        loginB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = un.getText();
                String password = pw.getText();
                if(username.equals("test") && password.equals("12345")){ //TODO: link it with username, password DB to check
                    statusLabel.setText("Ok Button is clicked here");
                    correctDetail = true;

                    login_frame.dispose();
                    MainPanel.createMainPanel();
                }
                else{
                    JOptionPane.showMessageDialog(null,"Wrong Password / Username");
                }
            }
        });

        //add elements to frame
        login_frame.getContentPane().add(prompt);
        login_frame.getContentPane().add(loginB);
        login_frame.getContentPane().add(un);
        login_frame.getContentPane().add(pw);
        login_frame.getContentPane().add(unlabel);
        login_frame.getContentPane().add(pwlabel);
        login_frame.add(statusLabel);

        //set frame size and location
        login_frame.setPreferredSize(new Dimension(300, 200));
        login_frame.setLocationRelativeTo(null);
        login_frame.pack();

        //visibility
        login_frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("I am the login panel.");
        startLogInGUI();
    }

}
