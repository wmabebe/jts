package ch.bfh.ti.jts.utils.graph;

import java.util.Collection;
import java.util.Optional;

public interface DirectedGraphVertex<V extends DirectedGraphVertex<V, E>, E extends DirectedGraphEdge<E, V>> {

    /**
     * Get the edge going from this to the given vertex
     *
     * @param vertex
     *            the 'other' vertex
     * @return the edge between, Optional might be not set if there is no
     *         connection
     */
    public Optional<E> getEdgeBetween(final V vertex);

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
}
