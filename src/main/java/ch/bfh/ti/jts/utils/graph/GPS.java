package ch.bfh.ti.jts.utils.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.bfh.ti.jts.data.Net;

public class GPS<V extends DirectedGraphVertex<V, E>, E extends DirectedGraphEdge<E, V>> {
    
    private final Net                             net;
    private final List<DirectedGraphVertex<V, E>> vertices = new LinkedList<>();
    private final List<DirectedGraphEdge<E, V>>   edges    = new LinkedList<>();
    
    public GPS(final Net net) {
        this.net = net;
        update();
    }
    
    /**
     * This method updates GPS information. Should be called when the structure
     * of the underlying net has changed.
     */
    public void update() {
        // extract all edges and vertices
        vertices.clear();
        edges.clear();
        net.getElementStream().forEach(x -> {
            if (DirectedGraphVertex.class.isInstance(x)) {
                vertices.add((DirectedGraphVertex<V, E>) x);
            } else if (DirectedGraphEdge.class.isInstance(x)) {
                edges.add((DirectedGraphEdge<E, V>) x);
            }
        });
        // parallel compute dijekstra for each vertex
        vertices.stream().parallel().forEach(x -> {
            dijekstra(x);
        });
    }
    
    /**
     * Dijekstra algorithm. TODO: implement faster with priority queue
     * 
     * @see http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
     * @param source
     *            the vertex to start search with
     */
    private void dijekstra(final DirectedGraphVertex<V, E> source) {
        // initialize
        final int sourceIndex = vertices.indexOf(source);
        final List<Double> dist = new ArrayList<>(vertices.size());
        dist.forEach(x -> x = Double.POSITIVE_INFINITY);
        dist.set(sourceIndex, 0.0);
        final List<DirectedGraphVertex<V, E>> previous = new ArrayList<>(vertices.size());
        previous.forEach(x -> x = null);
        final List<DirectedGraphVertex<V, E>> q = new ArrayList<>(vertices);
        // algorithm
        while (q.size() > 0) {
            // find vertex with min distance
            final Iterator<Double> distIter = dist.iterator();
            double maxDistance = distIter.next();
            final Iterator<DirectedGraphVertex<V, E>> qIter = q.iterator();
            DirectedGraphVertex<V, E> max = qIter.next();
            while (distIter.hasNext() && qIter.hasNext()) {
                final DirectedGraphVertex<V, E> nextVertex = qIter.next();
                final double nextVertexDistance = distIter.next();
                if (nextVertexDistance < maxDistance) {
                    maxDistance = nextVertexDistance;
                    max = nextVertex;
                }
            }
            final DirectedGraphVertex<V, E> u = max;
            final double uDistance = maxDistance;
            // remove vertex
            q.remove(u);
            u.getReachableVertices().stream().filter(v -> q.contains(v)).forEach(v -> {
                final int vIndex = q.indexOf(v);
                Double vDist = dist.get(vIndex);
                double alt = uDistance + u.getEdgeBetween(v).get().getLength();
                if (alt < vDist) {
                    vDist = alt;
                    previous.set(vIndex, u);
                }
            });
            // TODO: return something
        }
    }
}
