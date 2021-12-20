package Luxemburg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MapPanel extends JPanel {
    private static boolean selectionMade = false;
    private static int nodeStart;
    private static int nodeEnd;

    public MapPanel() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.BLACK);
        Harta map = new Harta();
        repaint();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!selectionMade){
                    selectionMade = true;
                    Point pointStart = e.getPoint();
                    //System.out.println("X: " +pointStart.x + " Y: " + pointStart.y);
                    nodeStart = Harta.SearchNodeWithCoordinates(pointStart.x, pointStart.y);
                    System.out.println("Node Start ID= " + nodeStart);
                } else if (selectionMade){
                    selectionMade = false;
                    Point pointEnd = e.getPoint();
                    //System.out.println("X: " +pointEnd.x + " Y: " + pointEnd.y);
                    nodeEnd = Harta.SearchNodeWithCoordinates(pointEnd.x, pointEnd.y);
                    System.out.println("Node End ID= " + nodeEnd);
                    if (nodeStart != -1 && nodeEnd != -1){
                        AddSecondPanel(nodeStart, nodeEnd);
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void AddSecondPanel(int nodeStart, int nodeEnd){
        JPanel secondPanel = SecondPanel.newPanel();
        JFrame newFrame = new JFrame("Alegeti algoritmul cu care vreti sa gasiti drumul cel mai scurt dintre nodurile selectate");
        newFrame.add(secondPanel);
        newFrame.setSize(700,100);
        newFrame.setResizable(false);
        newFrame.setLocationRelativeTo(null);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setVisible(true);

        JButton djikstraButton = new JButton("Djikstra");
        JButton bellmanFordButton = new JButton("Bellman-Ford");

        djikstraButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Harta.DijkstraShortestPath(nodeStart,nodeEnd);
                MapPanel.super.repaint();
                newFrame.setVisible(false);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        bellmanFordButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Harta.BellmanFordShortestPath(nodeStart, nodeEnd);
                MapPanel.super.repaint();
                newFrame.setVisible(false);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        newFrame.add(djikstraButton,BorderLayout.PAGE_START);
        newFrame.add(bellmanFordButton,BorderLayout.PAGE_END);
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        for (Node node : Harta.nodeList){
            node.drawNode(g);
        }
        for (Arc arc : Harta.arcList){
            arc.drawArc(g);
        }
    }
}
