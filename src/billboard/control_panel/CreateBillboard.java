package billboard.control_panel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class CreateBillboard extends JFrame{

    public static JFrame create_frame = new JFrame("Create Billboard Panel");

    static JComboBox c1;
    static JComboBox c2;

    //TODO: Users with the “Create Billboards” permission can create new billboards
    static void createBillboard(){

        create_frame.setLayout(null);

        JPanel creating_panel = new JPanel();
        JPanel name_panel = new JPanel();
        JPanel txt_panel = new JPanel();
        JPanel bg_panel = new JPanel();
        JPanel preview_panel = new JPanel();

        create_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        creating_panel.setBounds(0,0,300,500);
        preview_panel.setBounds(300,0,500,500);
        preview_panel.setBackground(Color.gray);
//        creating_panel.setBackground(Color.gray);


        //setting billboard name
        JLabel billName = new JLabel("Billboard Name:");
        JTextField nameField = new JTextField(15);

//        billName.setBounds(30,45,90,20);
//        nameField.setBounds(130,45,150,20);

        //setting text colour
        JLabel txtColour = new JLabel("Text Colour:");
//        txtColour.setBounds(30,80,90,20);
        //setting text colour: combobox
        String textColourList[] = { "Black", "White"};
        c1 = new JComboBox(textColourList);
//        c1.setBounds(130,80,90,20);

        //setting background colour
        JLabel bgColour = new JLabel("Background Colour:");
//        bgColour.setBounds(30,120,90,20);
        //setting background colour: combobox
        String bgColourList[] = { "Black", "White", "Red", "Blue", "Yellow"};
        c2 = new JComboBox(bgColourList);
//        c1.setBounds(130,120,90,20);

        name_panel.add(billName);
        name_panel.add(nameField);
        txt_panel.add(txtColour);
        txt_panel.add(c1);
        bg_panel.add(bgColour);
        bg_panel.add(c2);


        creating_panel.add(name_panel);
        creating_panel.add(txt_panel);
        creating_panel.add(bg_panel);



//        create_frame.getContentPane().add(nameField);
//        create_frame.getContentPane().add(billName);
//
//        create_frame.getContentPane().add(txtColour);
//        create_frame.getContentPane().add(c1);
//
//        create_frame.getContentPane().add(bgColour);

//        creating_panel.add(name_panel);



        create_frame.add(creating_panel);
        create_frame.add(preview_panel);

        create_frame.setPreferredSize(new Dimension(800, 500));

        create_frame.pack();
        create_frame.setLocationRelativeTo(null);

        //visibility
        create_frame.setVisible(true);
    }


    public static void main(String[] arg){
        createBillboard();
    }
}
