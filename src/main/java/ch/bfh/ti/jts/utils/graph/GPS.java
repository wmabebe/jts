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

/**
 * GPS class that implements the dijekstra algorithm to find shortest paths.
 * 
 * @author Enteee
 * @author winki
 * @param <V>
 *            vertex type
 * @param <E>
 *            edge type
 */
public class GPS<V extends DirectedGraphVertex<V, E>, E extends DirectedGraphEdge<E, V>> {
    
    private final List<V>                                             vertices = new LinkedList<>();
    private final List<E>                                             edges    = new LinkedList<>();
    private final ConcurrentHashMap<V, ConcurrentHashMap<V, List<V>>> routes   = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public GPS(final Net net) {
        // extract all edges and vertices
        net.getElementStream().forEach(x -> {
            if (DirectedGraphVertex.class.isInstance(x)) {
                vertices.add((V) x);
            } else if (DirectedGraphEdge.class.isInstance(x)) {
                edges.add((E) x);
            }
        });
        // parallel compute dijekstra for each vertex
        vertices.stream().sequential().forEach(start -> {
            final Map<V, V> previous = dijekstra(start);
            final ConcurrentHashMap<V, List<V>> destinations = new ConcurrentHashMap<>();
            vertices.stream().sequential().forEach(destination -> {
                final List<V> route = new LinkedList<>();
                V next = destination;
                while (previous.get(next) != null) {
                    route.add(0, next); // prepend
                    next = previous.get(next);
                }
                destinations.put(destination, route);
            });
            routes.put(start, destinations);
        });
    }
    
    /**
     * Dijekstra algorithm. TODO: implement faster with priority queue
     *
     * @see http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
     * @param source
     *            the vertex to start search with
     */
    private Map<V, V> dijekstra(final V source) {
        // initialize
        final Map<V, Double> dist = new HashMap<>();
        final Map<V, V> previous = new HashMap<>();
        vertices.forEach(x -> {
            dist.put(x, Double.POSITIVE_INFINITY);
            previous.put(x, null);
        });
        dist.put(source, 0.0);
        final List<V> q = new ArrayList<>(vertices);
        // algorithm
        while (q.size() > 0) {
            // find vertex with min distance
            final Iterator<V> qIter = q.iterator();
            V min = qIter.next();
            double minDistance = dist.get(min);
            while (qIter.hasNext()) {
                final V nextVertex = qIter.next();
                final double nextVertexDistance = dist.get(nextVertex);
                if (nextVertexDistance < minDistance) {
                    minDistance = nextVertexDistance;
                    min = nextVertex;
                }
            }
            final V u = min;
            final double uDistance = minDistance;
            // remove vertex
            q.remove(u);
            // update all reachable vertices
            u.getReachableVertices().stream().filter(v -> q.contains(v)).forEach(v -> {
                final double vDist = dist.get(v);
                final Optional<E> edgeBetween = u.getEdgeBetween(v);
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
    
    public Optional<E> getNextEdge(final V from, final V to) {
        Optional<E> edgeBetween = Optional.empty();
        final List<V> path = routes.get(from).get(to);
        if (path.size() > 0) {
            edgeBetween = from.getEdgeBetween(path.get(0));
        }
        return edgeBetween;
    }
}
