package ch.bfh.ti.jts.ai;

/**
 * Interface implemented by each {@link Element} which can make decisions.
 *
 * @author ente
 */
public interface Thinkable {
    
    /**
     * Get the local decision of this thinkable
     *
     * @return the decision
     */
    
    public Decision getDecision();
    
    /**
     * Called in parallel for each object before simulation. The objects can
     * influence the simulation by modifying the returned object of
     * {@link Thinkable#getDecision()}. This method should not modify any data
     * but the object returned by {@link Thinkable#getDecision()}.
     */
    public void think();
}
