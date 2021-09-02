package ch.bfh.ti.jts.importer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Flow;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.Route;
import ch.bfh.ti.jts.data.SpawnInfo;
import ch.bfh.ti.jts.data.Vehicle;
import ch.bfh.ti.jts.exceptions.ArgumentNullException;

/**
 * Imports SUMO routes files.
 *
 * @see <a
 *      href="http://sumo.dlr.de/wiki/Definition_of_Vehicles,_Vehicle_Types,_and_Routes">SUMO
 *      Definition of Vehicles, Vehicle Types, and Routes</a>
 * @author Enteee
 * @author winki
 */
public class RoutesImporter extends Importer<Collection<SpawnInfo>> {

    private final Map<String, Vehicle> vehicles = new LinkedHashMap<String, Vehicle>();
    private Net                        net;
    private Collection<SpawnInfo>      routes;

    @Override
    protected Collection<SpawnInfo> extractData(final Document document) {
        routes = new LinkedList<SpawnInfo>();
        final Node root = document.getDocumentElement();
        final NodeList nodes = root.getChildNodes();
        for (int i = 1; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if (node.getNodeName().equals("vType")) {
                extractVecicleType(node);
            } else if (node.getNodeName().equals("vehicle")) {
                extractVecicle(node);
            } else if (node.getNodeName().equals("flow")) {
                extractFlow(node);
            }

        }
        return routes;
    }

    private void extractFlow(final Node node) {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        final String type = getAttribute(node, "type", String.class);
        final double departureSpeed = getAttribute(node, "departSpeed", Double.class);
        final double arrivalSpeed = getAttribute(node, "arrivalSpeed", Double.class);
        final double frequency = getAttribute(node, "frequency", Double.class);
        final Vehicle vehicle = vehicles.get(type);
        final String routeEdges = extractRouteJunctions(node);
        final String[] edges = routeEdges.split(" ");
        if (edges.length < 2) {
            throw new IllegalArgumentException("illegal format of attribute edges in node route");
        }
        final String edgeIdStart = edges[0];
        final String edgeIdEnd = edges[edges.length - 1];
        final Junction routeStart = (Junction) net.getElementStream(Junction.class).filter(x -> x.getName().equals(edgeIdStart)).findFirst().orElse(null);
        final Junction routeEnd = (Junction) net.getElementStream(Junction.class).filter(x -> x.getName().equals(edgeIdEnd)).findFirst().orElse(null);
        final SpawnInfo route = new Flow(vehicle, routeStart, routeEnd, departureSpeed, arrivalSpeed, frequency);
        routes.add(route);
    }

    private String extractRouteEdges(final Node node) {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        String routeEdges = null;
        if (node.hasChildNodes()) {
            final NodeList nodes = node.getChildNodes();
            final Node child = nodes.item(1);
            if (child != null && child.getNodeName().equals("route")) {
                routeEdges = getAttribute(child, "edges", String.class);
            }
        }
        return routeEdges;
    }

    private String extractRouteJunctions(final Node node) {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        String routeEdges = null;
        if (node.hasChildNodes()) {
            final NodeList nodes = node.getChildNodes();
            final Node child = nodes.item(1);
            if (child != null && child.getNodeName().equals("route")) {
                routeEdges = getAttribute(child, "junctions", String.class);
            }
        }
        return routeEdges;
    }

    private void extractVecicle(final Node node) {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        final String type = getAttribute(node, "type", String.class);
        final double departureTime = getAttribute(node, "depart", Double.class);
        final double departurePos = getAttribute(node, "departPos", Double.class);
        final double departureSpeed = getAttribute(node, "departSpeed", Double.class);
        final double arrivalPos = getAttribute(node, "arrivalPos", Double.class);
        final double arrivalSpeed = getAttribute(node, "arrivalSpeed", Double.class);
        final Vehicle vehicle = vehicles.get(type);
        final String routeEdges = extractRouteEdges(node);
        final String[] edges = routeEdges.split(" ");
        if (edges.length < 2) {
            throw new IllegalArgumentException("illegal format of attribute edges in node route");
        }
        final String edgeIdStart = edges[0];
        final String edgeIdEnd = edges[edges.length - 1];
        final Edge routeStart = (Edge) net.getElementStream(Edge.class).filter(x -> x.getName().equals(edgeIdStart)).findFirst().orElse(null);
        final Edge routeEnd = (Edge) net.getElementStream(Edge.class).filter(x -> x.getName().equals(edgeIdEnd)).findFirst().orElse(null);

        // take junctions for spawning
        final Junction jStart = routeStart.getStart();
        final Junction jEnd = routeEnd.getEnd();

        final SpawnInfo route = new Route(vehicle, jStart, jEnd, departureTime, departurePos, departureSpeed, arrivalPos, arrivalSpeed);
        routes.add(route);
    }

    private void extractVecicleType(final Node node) {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        final String id = getAttribute(node, "id", String.class);
        final double accel = getAttribute(node, "accel", Double.class);
        final double decel = getAttribute(node, "decel", Double.class);
        final double length = getAttribute(node, "length", Double.class);
        final double maxSpeed = getAttribute(node, "maxSpeed", Double.class);
        final String agent = getAttribute(node, "agent", String.class);
        final Vehicle vehicle = new Vehicle(-decel, accel, 0, maxSpeed, length, agent);
        vehicles.put(id, vehicle);
    }

    public void setNet(final Net net) {
        this.net = net;
    }
}
