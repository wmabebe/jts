package ch.bfh.ti.jts.data;

import static ch.bfh.ti.jts.utils.Helpers.getHeatColor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.Main;
import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.LaneChange;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.exceptions.ArgumentNullException;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Config;
import ch.bfh.ti.jts.utils.Helpers;

/**
 * Abstract agents which are the moving objects in the simulation (cars i.e.).
 *
 * @author Enteee
 * @author winki
 */
public abstract class Agent extends Element implements Thinkable, Simulatable, Renderable {
    
    private static final long    serialVersionUID                    = 1L;
    private static final Color[] colors                              = new Color[] { Color.WHITE, new Color(200, 200, 200), Color.GRAY, Color.LIGHT_GRAY, Color.BLUE, Color.RED,
            new Color(185, 122, 87), new Color(0, 128, 0)           };
    public final static boolean  CHANGE_LANE_ANIMATED                = Config.getInstance().getBool("agent.langechang.animation", true);
    /**
     * Duration [s] of change line animation.
     */
    public final static double   CHANGE_LANE_ANIMATION_DURATION      = Config.getInstance().getDouble("agent.lanechange.animation.duration", 0.3, 0.01, 20.0);
    /**
     * Length of the debug acceleration indicator
     */
    public final static double   ACCELERATION_DEBUG_INDICATOR_LENGTH = Config.getInstance().getDouble("agent.acceleration.debug.indicator.lenght", 5, 1, 100);
    /**
     * Decision object.
     */
    private final Decision       decision                            = new Decision();
    /**
     * Current lane.
     */
    private Lane                 lane;
    /**
     * The velocity of an agent in [m/s]
     */
    private double               velocity;
    /**
     * The acceleration of a agent in [m/s^2]
     */
    private double               acceleration;
    /**
     * Distance in [m] from the start of {@link Agent#lane}
     */
    private double               lanePosition;
    /**
     * Vehicle of this agent
     */
    private Vehicle              vehicle;
    /**
     * Optional spawning information of this agent. Can be null.
     */
    private SpawnInfo            spawnInfo;
    /**
     * How many times did the agent collide?
     */
    private int                  collisionCount;
    /**
     * Color.
     */
    private final Color          color;
    
    /**
     * Maximum Handshake distance
     */
    
    private final double DIST = 250.0;
    
    /**
     * Count the number of neighbors since last rate sampling.
     * Increase for each neighbor added and always reset to 0 after sampling.
     */
    private int neighborCountSinceLastSampling;
    
    /**
     * Keep track of neighbors on same lane that are in proximity
     */
    private HashMap<Agent,Double> neighborsQueue = new HashMap<Agent,Double>();
    
    public Agent() {
        super("Agent");
        color = getRandomColor();
    }
    
    /**
     * This agent collided for some reason with something
     */
    public void collide() {
        setVelocity(0.0);
        collisionCount++;
    }
    
    /**
     * @return the absolute distance to the end of the line.
     */
    public double getAbsoluteDistanceOnLaneLeft() {
        return getLane().getLength() - getLanePosition();
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public int getCollisionCount() {
        return collisionCount;
    }
    
    /**
     * Gets the color of the agent. Mode is configurable.
     *
     * @return color
     */
    private Color getColor() {
        final String mode = Config.getInstance().getEnum("agent.render.colormode", new String[] { "normal", "velocity" });
        if ("normal".equals(mode)) {
            return color;
        }
        if ("velocity".equals(mode)) {
            return getHeatColor(getVelocity(), vehicle.getMinVelocity(), vehicle.getMaxVelocity());
        }
        throw new RuntimeException("illegal color mode");
    }
    
    @Override
    public Decision getDecision() {
        return decision;
    }
    
    public double getDistanceToNextAgent() {
        final double oPosition = getLane().getNextAgentsOnLine(this).stream().mapToDouble(x -> x.getLanePosition()).min().orElse(0.0);
        final double tPosition = getLanePosition();
        final double delta = oPosition - tPosition;
        return Helpers.clamp(delta, 0.0, Double.MAX_VALUE);
    }
    
    /**
     * Add new neighbors to the queue if they fall within
     * specified range of this vehicle. Increment neighborsCount
     * value;
     */
    public void makeHandshakeByEnqueuingNeighbors() {
        for (Agent agent: lane.getAgentsInOrder()) {
            double delta = Math.abs(this.getLanePosition() - agent.getLanePosition());
            if(!neighborsQueue.containsKey(agent) && !this.equals(agent) && delta <= DIST) {
                neighborsQueue.put(agent,delta);
                neighborCountSinceLastSampling++;
            }
        }
    }
    
    /**
     * Return the rate of handshakes since the last rate sampling.
     * Reset the neighborsCount to 0.
     * @param timeSpan
     * @return rate of handshakes
     */
    public double getLatestHandshakeRate(int timeSpan) {
        double rate = neighborCountSinceLastSampling / timeSpan;
        neighborCountSinceLastSampling = 0;
        return rate;
    }
    
    public Lane getLane() {
        return lane;
    }
    
    public HashMap<Agent,Double> getNeighborsQueue(){
        return this.neighborsQueue;
    }
    
    /**
     * @return absolute position on the lane. From the start to the current
     *         position of the agent.
     */
    public double getLanePosition() {
        return lanePosition;
    }
    
    @Override
    public Point2D getPosition() {
        return getLane().getPolyShape().getRelativePosition(getRelativeLanePosition());
    }
    
    private Color getRandomColor() {
        final int index = ThreadLocalRandom.current().nextInt(colors.length);
        return colors[index];
    }
    
    /**
     * @return relative position on lane.
     */
    public double getRelativeLanePosition() {
        if (getLane() == null) {
            throw new ArgumentNullException("lane");
        }
        if (getLane().getLength() == 0) {
            throw new RuntimeException("lane lnegth is 0");
        }
        return getLanePosition() / getLane().getLength();
    }
    
    @Override
    public int getRenderLayer() {
        return Renderable.KNOWN_CLASSES.get(Agent.class);
    }
    
    @Override
    public int getSimulationLayer() {
        return Simulatable.KNOWN_CLASSES.get(Agent.class);
    }
    
    public SpawnInfo getSpawnInfo() {
        return spawnInfo;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public void init(final double positionOnLane, final Vehicle vehicle, final double velocity) {
        init(positionOnLane, vehicle, velocity, null);
    }
    
    public void init(final double positionOnLane, final Vehicle vehicle, final double velocity, final SpawnInfo spawnInfo) {
        setLanePosition(positionOnLane);
        setVehicle(vehicle);
        setVelocity(velocity);
        setSpawnInfo(spawnInfo);
    }
    
    /**
     * @return @{code true} if agent is at end of lane and want's to leave the
     *         edge, @{code false} otherwise
     */
    public boolean isEdgeLeaveCandidate() {
        return getRelativeLanePosition() > 1.0;
    }
    
    /**
     * @return @{code true} if agent want's to change lane to an other lane on
     *         the same edge, @{code false} otherwise.
     */
    public boolean isLaneChangeCandidate() {
        //@formatter:off
        return  getVelocity() > 0
                && getDecision().getLaneChange() == LaneChange.RIGHT
                && getLane().getRightLane().isPresent() || getDecision().getLaneChange() == LaneChange.LEFT
                && getLane().getLeftLane().isPresent();
        //@formatter:on
    }
    
    public boolean isOnLane() {
        //@formatter:off
        return  !isLaneChangeCandidate()
                && !isEdgeLeaveCandidate();
        // @formatter:on
    }
    
    @Override
    public void render(final Graphics2D g) {
        final Point2D position = getPosition();
        final double x = position.getX();
        final double y = position.getY();
        final double orientation = getLane().getPolyShape().getRelativeOrientation(getRelativeLanePosition());
        g.setStroke(new BasicStroke(1));
        g.setColor(getColor());
        g.translate(x, y);
        g.rotate(orientation);
        g.fill(vehicle.getShape());
        if (Config.getInstance().getBool("agent.render.infos", false)) {
            g.setFont(App.FONT);
            g.drawString("Agent " + getId(), -9, 5);
        }
        if (Main.DEBUG) {
            final Color c = g.getColor();
            g.setColor(Color.RED);
            final double acceleration = getAcceleration();
            int accelerationIndicatorLength = 0;
            if (acceleration > 0) {
                accelerationIndicatorLength = (int) (acceleration / getVehicle().getMaxAcceleration() * ACCELERATION_DEBUG_INDICATOR_LENGTH);
            } else {
                accelerationIndicatorLength = (int) -(acceleration / getVehicle().getMinAcceleration() * ACCELERATION_DEBUG_INDICATOR_LENGTH);
            }
            g.setStroke(new BasicStroke(.5f));
            g.drawLine(0, 0, accelerationIndicatorLength, 0);
            g.drawOval((int) -ACCELERATION_DEBUG_INDICATOR_LENGTH, (int) -ACCELERATION_DEBUG_INDICATOR_LENGTH, (int) ACCELERATION_DEBUG_INDICATOR_LENGTH * 2,
                    (int) ACCELERATION_DEBUG_INDICATOR_LENGTH * 2);
            g.setColor(c);
        }
        g.rotate(-orientation);
        g.translate(-x, -y);
    }
    
    @Override
    public void render(final Graphics2D g, final NavigableMap<Double, Net> simulationStates) {
        if (CHANGE_LANE_ANIMATED) {
            final Point2D position = getPosition();
            final double x = position.getX();
            final double y = position.getY();
            double xChangeLaneShift = 0;
            double yChangeLaneShift = 0;
            // translate to agent
            g.translate(x, y);
            final double wallClockTime = App.getInstance().getSimulation().getWallClockTime();
            // check old simulation states
            final Optional<Element.ElementInTime> lastAgentStateBeforeLaneChange = simulationStates.headMap(wallClockTime).values().stream().map(oldNet -> {
                return new Element.ElementInTime(oldNet.getSimulationTime(), oldNet.getElement(getId()));
            }).filter(oldAgentInTime -> {
                final Agent oldAgent = (Agent) oldAgentInTime.getElement();
                // lane changed on same edge?
                // @formatter:off
                return  oldAgent != null
                        && (
                                getLane().getLeftLane().isPresent()
                                && oldAgent.getLane().getId() == getLane().getLeftLane().get().getId() || getLane().getRightLane().isPresent()
                                && oldAgent.getLane().getId() == getLane().getRightLane().get().getId()
                                );
                // @formatter:on
                    }).sorted().findFirst();
            if (lastAgentStateBeforeLaneChange.isPresent()) {
                final double lastTimeBeforeChange = lastAgentStateBeforeLaneChange.get().getTime();
                final Agent lastAgentStateBeforeChange = (Agent) lastAgentStateBeforeLaneChange.get().getElement();
                final double lastLaneChangeRelativeTime = wallClockTime - lastTimeBeforeChange;
                final double changeLaneAnimationDurationLeft = CHANGE_LANE_ANIMATION_DURATION - lastLaneChangeRelativeTime;
                if (changeLaneAnimationDurationLeft > 0) {
                    final double extrapolatedRelativePosition = getRelativeLanePosition() + getVelocity() * changeLaneAnimationDurationLeft / getLane().getLength();
                    // only animate lane change if change is fully on this lane
                    if (extrapolatedRelativePosition >= 0 && extrapolatedRelativePosition <= 1) {
                        final Point2D extrapolatedPosition = getLane().getPolyShape().getRelativePosition(extrapolatedRelativePosition);
                        final double changeLaneFactor = 1 - changeLaneAnimationDurationLeft / CHANGE_LANE_ANIMATION_DURATION;
                        final Point2D changeLanePosition = new Point2D.Double(changeLaneFactor * (extrapolatedPosition.getX() - lastAgentStateBeforeChange.getPosition().getX())
                                + lastAgentStateBeforeChange.getPosition().getX(), changeLaneFactor * (extrapolatedPosition.getY() - lastAgentStateBeforeChange.getPosition().getY())
                                + lastAgentStateBeforeChange.getPosition().getY());
                        xChangeLaneShift = changeLanePosition.getX() - x;
                        yChangeLaneShift = changeLanePosition.getY() - y;
                        if (Main.DEBUG) {
                            g.setColor(Color.GREEN);
                            g.setStroke(new BasicStroke(.5f));
                            g.translate(-x, -y);
                            g.drawOval((int) extrapolatedPosition.getX() - 2, (int) extrapolatedPosition.getY() - 2, 4, 4);
                            g.drawOval((int) lastAgentStateBeforeChange.getPosition().getX() - 2, (int) lastAgentStateBeforeChange.getPosition().getY() - 2, 4, 4);
                            g.drawLine((int) lastAgentStateBeforeChange.getPosition().getX(), (int) lastAgentStateBeforeChange.getPosition().getY(), (int) extrapolatedPosition.getX(),
                                    (int) extrapolatedPosition.getY());
                            g.setColor(Color.RED);
                            g.drawLine((int) lastAgentStateBeforeChange.getPosition().getX(), (int) lastAgentStateBeforeChange.getPosition().getY(), (int) changeLanePosition.getX(),
                                    (int) changeLanePosition.getY());
                            g.translate(x, y);
                        }
                        g.translate(xChangeLaneShift, yChangeLaneShift);
                    }
                }
            }
            g.translate(-x, -y);
            Renderable.super.render(g, simulationStates);
            g.translate(-xChangeLaneShift, -yChangeLaneShift);
        } else {
            Renderable.super.render(g, simulationStates);
        }
    }
    
    public void setAcceleration(final double acceleration) {
        this.acceleration = Helpers.clamp(acceleration, vehicle.getMinAcceleration(), vehicle.getMaxAcceleration());
    }
    
    public void setLane(final Lane lane) {
        this.lane = lane;
    }
    
    public void setLanePosition(final double lanePosition) {
        this.lanePosition = lanePosition;
    }
    
    /**
     * Sets a new lane of this agent and sets {@link Agent#lanePosition}
     * relative to new edge lane.
     *
     * @param nextEdgeLane
     */
    public void setNextEdgeLane(final Lane nextEdgeLane) {
        if (nextEdgeLane == null) {
            throw new IllegalArgumentException("nextEdgeLane");
        }
        setLanePosition(getLanePosition() - lane.getLength());
        lane = nextEdgeLane;
    }
    
    private void setSpawnInfo(final SpawnInfo spawnInfo) {
        this.spawnInfo = spawnInfo;
    }
    
    private void setVehicle(final Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle");
        }
        this.vehicle = vehicle;
    }
    
    private void setVelocity(final double velocity) {
        this.velocity = Helpers.clamp(velocity, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
    }
    
    @Override
    public void simulate(final double duration) {
        // update acceleration
        setAcceleration(getDecision().getAcceleration());
        final double oldVelocity = getVelocity();
        // update velocity
        setVelocity(oldVelocity + getAcceleration() * duration);
        // update position
        setLanePosition(getLanePosition() + (oldVelocity + getVelocity()) / 2 * duration);
        makeHandshakeByEnqueuingNeighbors();
    }
    
    @Override
    public String toString() {
        return String.format("Agent{ id: %d, v: %.2f, a: %.2f, lanePosition: %.2f, relativeLanePosition: %.2f }", getId(), velocity, acceleration, lanePosition, getRelativeLanePosition());
    }
}
