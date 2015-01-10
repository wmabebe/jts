package ch.bfh.ti.jts.utils.graph;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for directed graph vertices.
 * 
 * @author Enteee
 * @author winki
 * @param <E>
 *            edge type
 * @param <V>
 *            vertex type
 */
public interface DirectedGraphVertex<V extends DirectedGraphVertex<V, E>, E extends DirectedGraphEdge<E, V>> {
    
    /**
     * Get the edge going from this to the given vertex
     *
     * @param vertex
     *            the 'other' vertex
     * @return the edge between, Optional might be not set if there is no
     *         connection
     */
    Optional<E> getEdgeBetween(final V vertex);
    
    /**
     * Get all edges with are coming from this vertex.
     *
     * @return all edges
     */
    Collection<E> getOutgoingEdges();
    
    /**
     * Get all vertices which are directly reachable from this vertex
     *
     * @return neighbours
     */
    Collection<V> getReachableVertices();
}
