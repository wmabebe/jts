package ch.bfh.ti.jts.simulation;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.bfh.ti.jts.data.Agent;

/**
 * Calculates some key values for traffic flow statistics.
 * 
 * @author Enteee
 * @author winki
 */
public class Statistics {
    
    /**
     * Time mean speed is measured by taking a reference area on the roadway
     * over a fixed period of time. In practice, it is measured by the use of
     * loop detectors. Loop detectors, when spread over a reference area, can
     * record the signature of vehicles and can track the speed of each vehicle.
     * However, average speed measurements obtained from this method are not
     * accurate because instantaneous speeds averaged among several vehicles
     * does not account for the difference in travel time for the vehicles that
     * are traveling at different speeds over the same distance.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Traffic_flow">Traffic flow
     *      (Wikipedia)</a>
     * @param agents
     *            agents
     * @return time mean speed
     */
    public static double getTimeMeanSpeed(Collection<Agent> agents) {
        int n = agents.size();
        double sum = agents.stream().mapToDouble(Agent::getVelocity).sum();
        if (n * sum == 0) {
            // avoid division by zero
            return 0;
        }
        return 1.0 / n * sum;
    }
    
    /**
     * Space mean speed is the speed measured by taking the whole roadway
     * segment into account. Consecutive pictures or video of a roadway segment
     * track the speed of individual vehicles, and then the average speed is
     * calculated. It is considered more accurate than the time mean speed. The
     * data for space calculating space mean speed may be taken from satellite
     * pictures, a camera, or both.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Traffic_flow">Traffic flow
     *      (Wikipedia)</a>
     * @param agents
     *            agents
     * @return space mean speed
     */
    public static double getSpaceMeanSpeed(Collection<Agent> agents) {
        // only agents with positive velocities
        agents = agents.stream().filter(x -> x.getVelocity() > 0).collect(Collectors.toList());
        int n = agents.size();
        double sum = agents.stream().mapToDouble(x -> 1.0 / x.getVelocity()).sum();
        if (sum == 0) {
            // avoid division by zero
            return 0;
        }
        return n / sum;
    }
    
    /**
     * Density (k) is defined as the number of vehicles per unit length of the
     * roadway. In traffic flow, the two most important densities are the
     * critical density (kc) and jam density (kj). The maximum density
     * achievable under free flow is kc, while kj is the maximum density
     * achieved under congestion. In general, jam density is seven times the
     * critical density. Inverse of density is spacing (s), which is the
     * center-to-center distance between two vehicles.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Traffic_flow">Traffic flow
     *      (Wikipedia)</a>
     * @param numAgents
     *            number of agents on the roadway
     * @param roadwayLength
     *            length of the roadway
     * @return density
     */
    public static double getDensity(int numAgents, double roadwayLength) {
        if (roadwayLength == 0) {
            // avoid division by zero
            return 0;
        }
        return numAgents / roadwayLength;
    }
}
