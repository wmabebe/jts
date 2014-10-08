package ch.bfh.ti.jts.ai;

/**
 * Interface implemented by each {@link Element} which can make decisions.
 *
 * @author ente
 */
@FunctionalInterface
public interface Thinkable {
    
    /**
     * Called in parallel for each element before simulation. The element can
     * influence the simulation with the passed {@link Decision} object. This
     * method should not modify any data but the {@link Decision} object.
     *
     * @param decision
     *            for next simulation step
     */
    public void think(final Decision decision);
}
