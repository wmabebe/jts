package ch.bfh.ti.jts.ai;

/**
 * Enumeration for the possible lane changes.
 *
 * @author Enteee
 * @author winki
 */
public enum LaneChange {
    /**
     * No lane change.
     */
    NONE,
    /**
     * Take lane with next higher index if existent.
     */
    LEFT,
    /**
     * Take lane with next lower index if existent.
     */
    RIGHT;
}
