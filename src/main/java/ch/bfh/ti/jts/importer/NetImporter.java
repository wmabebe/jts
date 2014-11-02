package ch.bfh.ti.jts.importer;

import java.awt.Shape;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.gui.App;
import ch.bfh.ti.jts.gui.data.PolyShape;

public class NetImporter extends Importer<Net> {
    
    private Net                         net;
    private final Collection<Node>      edgesNodes      = new LinkedList<Node>();
    private final Collection<Node>      connectionNodes = new LinkedList<Node>();
    private final Map<String, Junction> junctions       = new LinkedHashMap<String, Junction>();
    private final Map<String, Edge>     edges           = new LinkedHashMap<String, Edge>();
    private final Map<String, Lane>     lanes           = new LinkedHashMap<String, Lane>();
    
    private void extractConnection(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        final String from = getAttribute(node, "from", String.class);
        final String to = getAttribute(node, "to", String.class);
        final String fromLane = getAttribute(node, "fromLane", String.class);
        final String toLane = getAttribute(node, "toLane", String.class);
        final Lane laneFrom = lanes.get(String.format("%s_%s", from, fromLane));
        final Lane laneTo = lanes.get(String.format("%s_%s", to, toLane));
        laneFrom.getLanes().add(laneTo);
        if (App.DEBUG) {
            Logger.getGlobal().log(Level.INFO, "Adding connection:" + laneFrom + " -> " + laneTo);
        }
    }
    
    @Override
    protected Net extractData(final Document document) {
        net = new Net();
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
        return net;
    }
    
    private void extractEdge(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        final String id = getAttribute(node, "id", String.class);
        final String from = getAttribute(node, "from", String.class);
        final String to = getAttribute(node, "to", String.class);
        final int priority = getAttribute(node, "priority", Integer.class);
        final Junction start = junctions.get(from);
        final Junction end = junctions.get(to);
        final Edge edge = new Edge(id, start, end, priority);
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
    
    private void extractJunction(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        final String id = getAttribute(node, "id", String.class);
        final double x = getAttribute(node, "x", Double.class);
        final double y = getAttribute(node, "y", Double.class);
        final PolyShape polyShape = new PolyShape(getAttribute(node, "shape", String.class));
        final Shape shape = polyShape.getShape();
        final Junction junction = new Junction(id, x, y, shape);
        junctions.put(id, junction);
        net.addElement(junction);
    }
    
    private void extractLane(final Node node, final Edge edge) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        final String id = getAttribute(node, "id", String.class);
        final int index = getAttribute(node, "index", Integer.class);
        final double speed = getAttribute(node, "speed", Double.class);
        final double length = getAttribute(node, "length", Double.class);
        final PolyShape polyShape = new PolyShape(getAttribute(node, "shape", String.class));
        final Lane lane = new Lane(id, edge, index, speed, length, polyShape);
        net.addElement(lane);
        lanes.put(id, lane);
    }
    
    private void extractLocation(final Node node) {
    }
}
