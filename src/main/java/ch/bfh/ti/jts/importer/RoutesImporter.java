package ch.bfh.ti.jts.importer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.Route;
import ch.bfh.ti.jts.data.Vehicle;

public class RoutesImporter extends Importer<Collection<Route>> {
    
    private final Map<String, Vehicle> vehicles = new LinkedHashMap<String, Vehicle>();
    private Net                        net;
    private Collection<Route>          routes;
    
    public void setNet(final Net net) {
        this.net = net;
    }
    
    @Override
    protected Collection<Route> extractData(Document document) {
        routes = new LinkedList<Route>();
        final Node root = document.getDocumentElement();
        final NodeList nodes = root.getChildNodes();
        for (int i = 1; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if (node.getNodeName().equals("vType")) {
                extractVecicleType(node);
            } else if (node.getNodeName().equals("vehicle")) {
                extractVecicle(node);
            }
        }
        return routes;
    }
    
    private void extractVecicleType(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        final String id = getAttribute(node, "id", String.class);
        final double accel = getAttribute(node, "accel", Double.class);
        final double decel = getAttribute(node, "decel", Double.class);
        final double length = getAttribute(node, "length", Double.class);
        final double maxSpeed = getAttribute(node, "maxSpeed", Double.class);
        final Vehicle vehicle = new Vehicle(decel, accel, 0, maxSpeed, length);
        vehicles.put(id, vehicle);
    }
    
    private void extractVecicle(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        final String type = getAttribute(node, "type", String.class);
        final double departureTime = getAttribute(node, "depart", Double.class);
        final double departurePos = getAttribute(node, "departPos", Double.class);
        final double departureSpeed = getAttribute(node, "departSpeed", Double.class);
        final double arrivalPos = getAttribute(node, "arrivalPos", Double.class);
        final double arrivalSpeed = getAttribute(node, "arrivalSpeed", Double.class);
        final Vehicle vehicle = vehicles.get(type);
        final String routeEdges = extractRouteEdges(node);
        String[] edges = routeEdges.split(" ");
        if (edges.length < 2) {
            throw new IllegalArgumentException("illegal format of attribute edges in node route");
        }
        final String edgeIdStart = edges[0];
        final String edgeIdEnd = edges[edges.length - 1];
        final Edge routeStart = (Edge) net.getElementStream(Edge.class).filter(x -> x.getName().equals(edgeIdStart)).findFirst().orElse(null);
        final Edge routeEnd = (Edge) net.getElementStream(Edge.class).filter(x -> x.getName().equals(edgeIdEnd)).findFirst().orElse(null);
        Route route = new Route(vehicle, routeStart, routeEnd, departureTime, departurePos, departureSpeed, arrivalPos, arrivalSpeed);
        routes.add(route);
    }
    
    private String extractRouteEdges(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        String routeEdges = null;
        if (node.hasChildNodes()) {
            final NodeList nodes = node.getChildNodes();
            Node child = nodes.item(1);
            if (child != null && child.getNodeName().equals("route")) {
                routeEdges = getAttribute(child, "edges", String.class);
            }
        }
        return routeEdges;
    }
}
