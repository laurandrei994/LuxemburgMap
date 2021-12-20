package Luxemburg;


import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class Harta {
    public static final float WIDTH = 1200.f;
    public static final float HEIGHT = 1000.f;
    private static final int LIMIT = 5;

    protected static final String filepath = "src/Luxemburg/map2.xml";
    public static Vector<Luxemburg.Node> nodeList;
    public static LinkedList<Arc>[] adjacencyList;
    public static Vector<Arc> arcList;
    private static int maxLongitude = 0;
    private static int maxLatitude = 0;
    private static int minLongitude = Integer.MAX_VALUE;
    private static int minLatitude = Integer.MAX_VALUE;

    public Harta() {

        try {
            Read();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ScaleCoordinates();
    }

    protected static void Read() throws ParserConfigurationException, IOException, SAXException {
        long startTime = System.currentTimeMillis();
        File fXmlFile = new File(Harta.filepath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();
        nodeList = new Vector<>();

        NodeList nList = doc.getElementsByTagName("node");

        for (int temp = 0; temp < 42314; temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                nodeList.add(new Luxemburg.Node(Integer.parseInt(eElement.getAttribute("id")),
                        Integer.parseInt(eElement.getAttribute("longitude")),
                        Integer.parseInt(eElement.getAttribute("latitude"))));
            }
        }

        adjacencyList = new LinkedList[nodeList.size()];
        for (int i = 0; i < nodeList.size(); i++) {
            adjacencyList[i] = new LinkedList<>();
        }
        arcList = new Vector<>();

        nList = doc.getElementsByTagName("arc");
        for (int temp = 0; temp < 100358; temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                arcList.add(new Arc(Integer.parseInt(eElement.getAttribute("from")), Integer.parseInt(eElement.getAttribute("to")), Integer.parseInt(eElement.getAttribute("length")), Color.WHITE));
                AddArcInAdjacencyList(Integer.parseInt(eElement.getAttribute("from")), Integer.parseInt(eElement.getAttribute("to")), Integer.parseInt(eElement.getAttribute("length")));
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n\nReading and parsing the XML file took " + (endTime - startTime) + " milliseconds \n");
    }

    private static void SetMargins() {
        for (Luxemburg.Node node : nodeList) {
            int latitude = node.getLatitude();
            int longitude = node.getLongitude();

            if (maxLatitude < latitude)
                maxLatitude = latitude;
            if (maxLongitude < longitude)
                maxLongitude = longitude;
            if (minLatitude > latitude)
                minLatitude = latitude;
            if (minLongitude > longitude)
                minLongitude = longitude;
        }
    }

    private static void AddArcInAdjacencyList(int nodeStartID, int nodeEndID, int cost) {
        Arc tempArc = new Arc(nodeStartID, nodeEndID, cost, Color.BLUE);
        adjacencyList[nodeStartID].add(tempArc);
        tempArc = new Arc(nodeEndID, nodeStartID, cost, Color.BLUE);
        adjacencyList[nodeEndID].add(tempArc);
    }

    protected static void ScaleCoordinates() {
        SetMargins();
        for (Luxemburg.Node node : nodeList) {
            int longitude = node.getLongitude();
            int latitude = node.getLatitude();
            int length = maxLongitude - minLongitude;
            int width = maxLatitude - minLatitude;

            float scaleX = WIDTH / length;
            float scaleY = HEIGHT / width;

            longitude = (int) ((longitude - minLongitude) * scaleY);
            latitude = (int) ((latitude - minLatitude) * scaleX);

            node.setLatitude(latitude);
            node.setLongitude(longitude);
        }
    }

    protected static int SearchNodeWithCoordinates(int x, int y) {
        for (Luxemburg.Node node : nodeList) {
            if (Math.abs(node.getLongitude() - y) < LIMIT && Math.abs(node.getLatitude() - x) < LIMIT) {
                return node.getId();
            }
        }
        return -1;
    }

    protected static void DijkstraShortestPath(int start, int end) {
        long startTime = System.currentTimeMillis();
        int numberOfNodes = nodeList.size();
        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(numberOfNodes, Comparator.comparingInt(Pair::getFirst));

        int[] distance = new int[numberOfNodes];
        for (int index = 0; index < numberOfNodes; index++)
            distance[index] = Integer.MAX_VALUE;
        pq.add(new Pair<>(0, start));
        distance[start] = 0;

        boolean[] visited = new boolean[numberOfNodes];

        int[] parent = new int[numberOfNodes];
        for (int index = 0; index < numberOfNodes; index++) {
            parent[index] = -1;
        }

        while (!pq.isEmpty()) {
            int u = pq.peek().getSecond();
            pq.poll();
            visited[u] = true;
            for (Arc arc : adjacencyList[u]) {
                int v = arc.getIdEnd();
                int cost = arc.getCost();
                if (!visited[v] && distance[v] > distance[u] + cost) {
                    parent[v] = u;
                    distance[v] = distance[u] + cost;
                    pq.add(new Pair<>(distance[v], v));
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Djikstra Algorithm took " + (endTime - startTime) + " milliseconds \n");
        PrintPath(parent, end, Color.RED);
    }

    protected static void BellmanFordShortestPath(int start, int end) {
        long startTime = System.currentTimeMillis();

        int numberOfNodes = nodeList.size();
        PriorityQueue<Pair<Integer,Integer>> pq = new PriorityQueue<>(numberOfNodes, Comparator.comparingInt(Pair::getFirst));

        int[] distance = new int[numberOfNodes];
        boolean[] visited = new boolean[numberOfNodes];
        int[] negativeCycle = new int[numberOfNodes];
        int[] parent = new int[numberOfNodes];
        boolean checkNegativeCycle = true;

        for (int index = 0; index < numberOfNodes; index++){
            distance[index] = Integer.MAX_VALUE;
            visited[index] = false;
            parent[index] = -1;
        }

        distance[start] = 0;
        visited[start] = true;

        pq.add(new Pair<>(0,start));
        while (!pq.isEmpty()){
            int node = pq.peek().getSecond();
            pq.poll();
            visited[node] = false;
            for (int index = 0; index < adjacencyList[node].size(); index++){
                int neighbour = adjacencyList[node].get(index).getIdEnd();
                if (distance[neighbour] > distance[node] + adjacencyList[node].get(index).getCost()){
                    distance[neighbour] = distance[node] + adjacencyList[node].get(index).getCost();
                    parent[neighbour] = node;
                    negativeCycle[neighbour] += 1;
                    if (negativeCycle[neighbour] > numberOfNodes - 1){
                        System.out.println("Negative cycle found!! \n");
                        checkNegativeCycle = false;
                        break;
                    }
                    if (!visited[neighbour]){
                        visited[neighbour] = true;
                        pq.add(new Pair<>(distance[neighbour],neighbour ));
                    }
                }
            }
            if (!checkNegativeCycle){
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Bellman-Ford Algorithm took " + (endTime - startTime) + " milliseconds\n");
        PrintPath(parent, end, Color.YELLOW);
    }

    private static void PrintPath(int[] parent, int nodeID, Color color) {
        for (int node = nodeID; parent[node] != -1; node = parent[node]) {
            int start = parent[node];
            for (Arc arc : arcList) {
                if ((arc.getIdStart() == start || arc.getIdEnd() == start) && (arc.getIdEnd() == node || arc.getIdStart() == node)) {
                    arc.setColor(color);
                }
            }
        }
    }
}
