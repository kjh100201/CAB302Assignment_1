package billboard.control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class CreateBillboard {

    //TODO: Users with the “Create Billboards” permission can create new billboards
    static void createBillboard(){

        JFrame main_frame = new JFrame("Create Billboard Panel");

        JLabel nameLabel = new JLabel("Billboard Name:");

        JTextField bName = new JTextField(13);
        bName.setBounds(100,45,150,20);

        main_frame.setPreferredSize(new Dimension(800, 500));

        main_frame.pack();
        main_frame.setLocationRelativeTo(null);

        //visibility
        main_frame.setVisible(true);
    }


    public static void main(String[] arg){
        createBillboard();
    }
}
