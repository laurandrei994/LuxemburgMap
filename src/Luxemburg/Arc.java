package Luxemburg;

import java.awt.*;

public class Arc implements Comparable{
    private final int idStart;
    private int idEnd;
    private final int cost;
    private Color color;

    public Arc(int idStart, int idEnd, int cost, Color color) {
        this.idStart = idStart;
        this.idEnd = idEnd;
        this.cost = cost;
        this.color = color;
    }

    public int getIdStart() {
        return idStart;
    }

    public int getIdEnd() {
        return idEnd;
    }

    public void setIdEnd(int idEnd) {
        this.idEnd = idEnd;
    }

    public int getCost() {
        return cost;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int compareTo(Object o){
        Arc other = (Arc) o;
        return Integer.compare(this.cost, other.cost);
    }

    public void drawArc(Graphics g){
        g.setColor(color);
        g.drawLine(Harta.nodeList.get(idStart).getLatitude(), Harta.nodeList.get(idStart).getLongitude(), Harta.nodeList.get(idEnd).getLatitude(), Harta.nodeList.get(idEnd).getLongitude());
    }
}
