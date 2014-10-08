package ch.bfh.ti.jts.importer;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;

public class Importer {
    
    private DocumentBuilderFactory      documentBuilderFactory;
    private DocumentBuilder             documentBuilder;
    private final Net                   net             = new Net();
    private final Collection<Node>      edgesNodes      = new LinkedList<Node>();
    private final Collection<Node>      connectionNodes = new LinkedList<Node>();
    private final Map<String, Junction> junctions       = new LinkedHashMap<String, Junction>();
    private final Map<String, Edge>     edges           = new LinkedHashMap<String, Edge>();
    private final Map<String, Lane>     lanes           = new LinkedHashMap<String, Lane>();
    
    public Importer() {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Net importData(final String path) {
        try {
            final Document document = documentBuilder.parse(path);
            extractData(document, net);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        return net;
    }
    
    private void extractData(final Document document, final Net net) {
        final Node root = document.getDocumentElement();
        final NodeList nodes = root.getChildNodes();
        for (int i = 1; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if (node.getNodeName().equals("location")) {
                extractLocation(node);
            } else if (node.getNodeName().equals("edge")) {
                // add to dictionary, extract later
                edgesNodes.add(node);
            } else if (node.getNodeName().equals("junction")) {
                extractJunction(node);
            } else if (node.getNodeName().equals("connection")) {
                // add to dictionary, extract later
                connectionNodes.add(node);
            }
        }
        // now we have the junctions. extract the edges
        for (final Node node : edgesNodes) {
            extractEdge(node);
        }
        // now we have the edges. extract the connections
        for (final Node node : connectionNodes) {
            extractConnection(node);
        }
    }
    
    private void extractLocation(final Node node) {
    }
    
    private void extractJunction(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        if (!node.hasAttributes()) {
            throw new IllegalArgumentException("node has no attributes");
        }
        final String id = node.getAttributes().getNamedItem("id").getNodeValue();
        final double x = Double.valueOf(node.getAttributes().getNamedItem("x").getNodeValue());
        final double y = Double.valueOf(node.getAttributes().getNamedItem("y").getNodeValue());
        final Shape shape = getShape(node.getAttributes().getNamedItem("shape").getNodeValue());
        final Junction junction = new Junction(x, y, shape);
        junctions.put(id, junction);
        net.addElement(junction);
    }
    
    private void extractEdge(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        if (!node.hasAttributes()) {
            throw new IllegalArgumentException("node has no attributes");
        }
        final String id = node.getAttributes().getNamedItem("id").getNodeValue();
        final String from = node.getAttributes().getNamedItem("from").getNodeValue();
        final String to = node.getAttributes().getNamedItem("to").getNodeValue();
        final int priority = Integer.valueOf(node.getAttributes().getNamedItem("priority").getNodeValue());
        final Junction start = junctions.get(from);
        final Junction end = junctions.get(to);
        final Edge edge = new Edge(start, end, priority);
        edges.put(id, edge);
        net.addElement(edge);
        final NodeList nodes = node.getChildNodes();
        for (int i = 1; i < nodes.getLength(); i++) {
            final Node child = nodes.item(i);
            if (child.getNodeName().equals("lane")) {
                extractLane(child, edge);
            }
        }
    }
    
    private void extractLane(final Node node, final Edge edge) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        if (!node.hasAttributes()) {
            throw new IllegalArgumentException("node has no attributes");
        }
        final String id = node.getAttributes().getNamedItem("id").getNodeValue();
        final int index = Integer.valueOf(node.getAttributes().getNamedItem("index").getNodeValue());
        final double speed = Double.valueOf(node.getAttributes().getNamedItem("speed").getNodeValue());
        final double length = Double.valueOf(node.getAttributes().getNamedItem("speed").getNodeValue());
        final Shape shape = getShape(node.getAttributes().getNamedItem("shape").getNodeValue());
        final Lane lane = new Lane(edge, index, speed, length, shape);
        edge.getLanes().add(lane);
        net.addElement(lane);
        lanes.put(id, lane);
    }
    
    private Shape getShape(final String shapeString) {
        if (shapeString == null) {
            throw new IllegalArgumentException("shape is null");
        }
        if (!shapeString.contains(" ")) {
            throw new IllegalArgumentException("shape has wrong format");
        }
        final Path2D path = new Path2D.Double();
        final String[] points = shapeString.split(" ");
        if (points.length == 0) {
            throw new IllegalArgumentException("shape is no path");
        }
        for (int i = 0; i < points.length; i++) {
            final String point = points[i];
            final String[] coordinates = point.split(",");
            if (coordinates.length != 2) {
                throw new IllegalArgumentException("invalid coordinates");
            }
            if (i == 0) {
                path.moveTo(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
            } else {
                path.lineTo(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
            }
        }
        path.closePath();
        return path;
    }
    
    private void extractConnection(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        if (!node.hasAttributes()) {
            throw new IllegalArgumentException("node has no attributes");
        }
        final String from = node.getAttributes().getNamedItem("from").getNodeValue();
        final String to = node.getAttributes().getNamedItem("to").getNodeValue();
        final String fromLane = node.getAttributes().getNamedItem("fromLane").getNodeValue();
        final String toLane = node.getAttributes().getNamedItem("toLane").getNodeValue();
        final Lane laneFrom = lanes.get(String.format("%s_%s", from, fromLane));
        final Lane laneTo = lanes.get(String.format("%s_%s", to, toLane));
        laneFrom.getLanes().add(laneTo);
    }
    
    public void addTestAgents(Net net) {
        final int numAgents = 50;
        for (int i = 0; i < numAgents; i++) {
            Agent agent = new Agent();
            
            // get first lane...
            Lane lane = (Lane) net.getElements().stream().filter(x -> x.getClass() == Lane.class).findAny().get();
            
            agent.setLane(lane);
            agent.setPosition(Math.random());
            agent.setVelocity(10.0);
            
            net.addElement(agent);
        }
    }
}
