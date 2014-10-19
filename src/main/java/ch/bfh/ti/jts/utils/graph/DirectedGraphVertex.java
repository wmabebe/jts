package ch.bfh.ti.jts.utils.graph;

import java.util.Collection;
import java.util.Optional;

public interface DirectedGraphVertex<V extends DirectedGraphVertex<V, E>, E extends DirectedGraphEdge<E, V>> {
    
    /**
     * Get all edges with are coming from this vertex.
     * 
     * @return all edges
     */
    public Collection<E> getOutgoingEdges();
    
    /**
     * Get all vertices which are directly reachable from this vertex
     * 
     * @return neighbours
     */
    public Collection<V> getReachableVertices();
    
    /**
     * Get the edge between this and the given vertex
     * 
     * @param vertex
     *            the 'other' vertex
     * @return the edge between, might be null if there is no connection
     */
    public Optional<E> getEdgeBetween(final V vertex);
}
