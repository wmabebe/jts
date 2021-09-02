package ch.bfh.ti.jts.data;

/**
 * Flow is a special spawn type. Agents are spawned in a certain frequency.
 *
 * @author Enteee
 * @author winki
 */
public class Flow extends SpawnInfo {

    private static final long serialVersionUID = 1L;

    private final double      frequency;
    private int               count;

    public Flow(final Vehicle vehicle, final Junction start, final Junction end, final double departureSpeed, final double arrivalSpeed, final double frequency) {
        super(vehicle, start, end, 0.0, 0.0, departureSpeed, 0.0, arrivalSpeed);
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

    public Junction getRouteEnd() {
        final SpawnLocation end = getEnd();
        if (end instanceof Junction) {
            return (Junction) end;
        }
        return null;
    }

    public Junction getRouteStart() {
        final SpawnLocation start = getStart();
        if (start instanceof Junction) {
            return (Junction) start;
        }
        return null;
    }

    public boolean isSpawn(final double time) {
        assert time >= 0;

        if (count / time < frequency) {
            count++;
            return true;
        }
        return false;
    }
}
