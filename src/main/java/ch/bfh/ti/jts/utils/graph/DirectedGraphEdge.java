package ch.bfh.ti.jts.utils.graph;

public interface DirectedGraphEdge<E extends DirectedGraphEdge<E, V>, V extends DirectedGraphVertex<V, E>> {
    
    /**
     * Check if vertex is at beginning of this edge
     *
     * @param vertex
     *            the vertex to check
     * @return {@code true} if vertex is at beginning, {@code false} otherwise
     */
    public default boolean comesFrom(final V vertex) {
        return getStart() == vertex;
    }
    
    /**
     * Get the vertex at the end of this edge
     *
     * @return end vertex
     */
    public V getEnd();
    
    /**
     * Get the weight of this edge
     *
     * @return the weight of this edge
     */
    public double getWeight();
    
    /**
     * Get the vertex at the start of this edge
     *
     * @return start vertex
     */
    public V getStart();
    
    /**
     * Check if vertex is at the end of this edge
     *
     * @param vertex
     *            the vertex to check
     * @return {@code true} if vertex is at end, {@code false} otherwise
     */
    public default boolean goesTo(final V vertex) {
        return getEnd() == vertex;
    }
}
