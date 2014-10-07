package ch.bfh.ti.jts.simulation;

import java.time.Duration;

import ch.bfh.ti.jts.data.Element;

@FunctionalInterface
public interface Simulatable {
    
    /**
     * Called for each simulation step
     * 
     * @param oldSelf
     *            old copy of this element
     * @param duration
     *            duration to simulate
     */
    public void simulate(final Element oldSelf, final Duration duration);
}
