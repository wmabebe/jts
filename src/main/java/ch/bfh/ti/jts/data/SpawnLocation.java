package ch.bfh.ti.jts.data;

/**
 * Enables agents to spawn on this element.
 *
 * @author Enteee
 * @author winki
 */
public interface SpawnLocation {

    /**
     * Returns the lane (lowest granular SpawnLocation) on which the agent
     * should spawn.
     *
     * @return lane to spawn the agent
     */
    Lane getSpawnLane();
}
