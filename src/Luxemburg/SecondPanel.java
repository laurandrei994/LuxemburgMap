package Luxemburg;

import javax.swing.*;
import java.awt.*;

public class SecondPanel extends JPanel{

    public static JPanel newPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setVisible(true);
        return panel;
    }
}
