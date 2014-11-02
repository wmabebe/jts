package ch.bfh.ti.jts.utils.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ch.bfh.ti.jts.data.Net;

public class GPS<V extends DirectedGraphVertex<V, E>, E extends DirectedGraphEdge<E, V>> {
    
    private final Net                                                                                                                         net;
    private final List<DirectedGraphVertex<V, E>>                                                                                             vertices = new LinkedList<>();
    private final List<DirectedGraphEdge<E, V>>                                                                                               edges    = new LinkedList<>();
    private final ConcurrentHashMap<DirectedGraphVertex<V, E>, ConcurrentHashMap<DirectedGraphVertex<V, E>, List<DirectedGraphVertex<V, E>>>> routes   = new ConcurrentHashMap<>();
    
    public GPS(final Net net) {
        this.net = net;
        update();
    }
    
    public Optional<E> getNextEdge(V from, V to) {
        final DirectedGraphVertex<V, E> nextVertex = routes.get(from).get(to).get(0);
        return nextVertex.getEdgeBetween(from);
    }
    
    /**
     * Dijekstra algorithm. TODO: implement faster with priority queue
     *
     * @see http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
     * @param source
     *            the vertex to start search with
     */
    private Map<DirectedGraphVertex<V, E>, DirectedGraphVertex<V, E>> dijekstra(final DirectedGraphVertex<V, E> source) {
        // initialize
        final Map<DirectedGraphVertex<V, E>, Double> dist = new HashMap<>();
        final Map<DirectedGraphVertex<V, E>, DirectedGraphVertex<V, E>> previous = new HashMap<>();
        vertices.forEach(x -> {
            dist.put(x, Double.POSITIVE_INFINITY);
            previous.put(x, null);
        });
        dist.put(source, 0.0);
        final List<DirectedGraphVertex<V, E>> q = new ArrayList<>(vertices);
        // algorithm
        while (q.size() > 0) {
            // find vertex with min distance
            final Iterator<DirectedGraphVertex<V, E>> qIter = q.iterator();
            DirectedGraphVertex<V, E> min = qIter.next();
            double minDistance = dist.get(min);
            while (qIter.hasNext()) {
                final DirectedGraphVertex<V, E> nextVertex = qIter.next();
                final double nextVertexDistance = dist.get(nextVertex);
                if (nextVertexDistance < minDistance) {
                    minDistance = nextVertexDistance;
                    min = nextVertex;
                }
            }
            final DirectedGraphVertex<V, E> u = min;
            final double uDistance = minDistance;
            // remove vertex
            q.remove(u);
            // update all reachable vertices
            u.getReachableVertices().stream().filter(v -> q.contains(v)).forEach(v -> {
                double vDist = dist.get(v);
                Optional<E> edgeBetween = u.getEdgeBetween(v);
                double alt = uDistance;
                if (edgeBetween.isPresent()) {
                    alt += edgeBetween.get().getWeight();
                }
                if (alt < vDist) {
                    dist.put(v, alt);
                    previous.put(v, u);
                }
            });
        }
        return previous;
    }
    
    /**
     * This method updates GPS information. Should be called when the structure
     * of the underlying net has changed.
     */
    @SuppressWarnings("unchecked")
    public void update() {
        // clear all information
        vertices.clear();
        edges.clear();
        routes.clear();
        // extract all edges and vertices
        net.getElementStream().forEach(x -> {
            if (DirectedGraphVertex.class.isInstance(x)) {
                vertices.add((DirectedGraphVertex<V, E>) x);
            } else if (DirectedGraphEdge.class.isInstance(x)) {
                edges.add((DirectedGraphEdge<E, V>) x);
            }
        });
        // parallel compute dijekstra for each vertex
        vertices.stream().sequential().forEach(start -> {
            final Map<DirectedGraphVertex<V, E>, DirectedGraphVertex<V, E>> previous = dijekstra(start);
            final ConcurrentHashMap<DirectedGraphVertex<V, E>, List<DirectedGraphVertex<V, E>>> destinations = new ConcurrentHashMap<>();
            vertices.stream().sequential().forEach(destination -> {
                final List<DirectedGraphVertex<V, E>> route = new LinkedList<>();
                DirectedGraphVertex<V, E> next = destination;
                while (previous.containsKey(next)) {
                    next = previous.get(next);
                    route.add(0, next); // prepend
                }
                destinations.put(destination, route);
            });
            routes.put(start, destinations);
        });
    }
}
