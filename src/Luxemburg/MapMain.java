package Luxemburg;

import javax.swing.*;

public class MapMain {
    private static void initUI() {
        JFrame frame = new JFrame("Harta Luxemburg");
        frame.add(new MapPanel());

        frame.setSize(1200,1000);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                initUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
