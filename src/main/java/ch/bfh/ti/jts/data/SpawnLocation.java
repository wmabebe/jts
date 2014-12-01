package ch.bfh.ti.jts.data;

/**
 * Enables agents to spawn on this element.
 *
 * @author Mathias
 */
public interface SpawnLocation {

    /**
     * Returns the lane (most granular SpawnLocation) on which the agent should
     * spawn.
     *
     * @return lane to spawn the agent
     */
    Lane getSpawnLane();
}
