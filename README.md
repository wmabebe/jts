# Java Traffic Simulator (jts)

*Java Traffic Simulator* is a traffic flow microsimulation written in Java.

This project is currently under heavy developement.

## Features

* Simulatio of elements:
 * Agents; moving parts of the simulation
 * Lanes; where agents are moving on
 * Edge; bundling lanes together.
 * Junctions; connecting edges
 * Networks; holding elements
* Layered/parallelized simulation
* 

## Documentation

### Simulation cycle (pseudo code)

```
initialization();
load_map();
build_net();
loop {
	check_remove_agents();
	spawn_agents();
	think();
	calculate_agents_drive_distance();
	while (any_agent_has_to_drive()) {
		check_switch_lane();
		check_leave_lane();
		redirect_agents();
		check_collisions();
	}
}
end();
```

## Code Highlights

* Simulation engine which is easely extensible with new elements

```java
public interface Simulatable {
    /**
     * Known classes to layer mappings
     */
    static Map<Class<?>, Integer> KNOWN_CLASSES = new HashMap<Class<?>, Integer>() {
                                                    
                                                    private static final long serialVersionUID = 1L;
                                                    
                                                    {
                                                        put(Agent.class, 0);
                                                        put(Lane.class, 1);
                                                        put(Edge.class, 2);
                                                        put(Junction.class, 3);
                                                        put(Net.class, 4);
                                                    }
                                                };
    
    /**
     * The simulation layer of the object. 0: Simulate first 1: Simulate second
     *
     * @return the layer
     */
    default int getSimulationLayer() {
        if (!KNOWN_CLASSES.containsKey(getClass())) {
            throw new AssertionError("invalid layer", new IndexOutOfBoundsException(getClass() + " is not a known class"));
        }
        return KNOWN_CLASSES.get(getClass());
    }
    
    /**
     * Called in each simulation step
     *
     * @param duration
     *            duration to simulate in seconds
     */
    void simulate(final double duration);
}
```

* Integrated console engine with commands that are easy extensible

```java
public interface Command {
    String getName();
    String execute(Simulation simulation);
    Class<?> getTargetType();
}
```

* Easy interface for smart new agents.

```java
public interface Thinkable {
    public Decision getDecision();
    public void think();
}
```

* Import road map data from [OpenStreetMap][osm]
* Graphical user interface with 2D output
  * Allows scrolling and zooming

## Planning

### Planned features

* Lane changing
* Parameterizable AI

### Journal

#### Calendar week 39

Enteee, winki

* Write requirements doc [done]

#### Calendar week 40

Enteee
- [x] Basic simulator setup

winki
- [x] Projekt setup 
- [x] Basic data structure 
- [x] Implement xml importer 

#### Calendar week 41

Enteee
- [x] Simulation (Decision objects, think, simulate)
  - [x] call every think method of all Intelligents (parallel) 
  - [x] fill a collection 
  - [x] loop through decisions -> simulate every decision (serial), without lane switching 

winki
* Spawn agents [done]
* Dummy AI [done]
* Move agents [done]

#### Calendar week 42

Enteee
* Zooming [done]
* Collisions [done]
* Lane switching [done]

winki
* Render agents on polygons [done]
* Orientation of agents visible [done]

#### Calendar week 43

Enteee
* GPS-helper [done]

winki
* Import of route-files [done]
* Spawning of agents based on activities [done]

#### Calendar week 44

Enteee
* Commands to element redirection [done]

winki
* Embedded console, thread-safe [done]
* Spawn and time commands for console [done]

### Calendar week 45

Enteee
* GPS unit tests [done]
* Smarter agent, empty [done]

winki

### Calendar week 46

Enteee
* Draw simulation decoupling [done]
* Bugfix lane set agent override [done]

winki
* Simple map for developing agent [done]
* Fix index out of bounds bug in polyshape class [done]
* Realistic agent [done]

### Calendar week 47

Enteee
* Draw fake laneswitch [progress]
* Extended render interface with simulationStates [done] 

winki
* Improvement of realistic agent [done]

### Calendar week 48

Enteee

* Fixed interval simulation [done]
* Dynamic app sleeping [done]

winki
* Bugfix in lane
* Lane switching logic of realisitc agent [done]
* Implementation of traffic flows [done]
* Agent type can be configured in the routes xml file [done]
* Despawning of agents when spawn info of type "Flow" [done]
* Added restart command to console [done]

### Calendar week 49

Enteee
* Bugfix time conversion 10E-9 -> 1E-9 for nano [done]
* Wall clock time in Window introduced [done]
* Wall clock / simulation time decoupling [done] -> issue lag
* Restart command fixing [done]
* singleton app / window [done]
* reflection for command finding [done]
* toggleInterpolate command added [done]

winki
* Added "ramp" net [done] -> error at junctions
* Console can receive parameters from clickable GUI [done]

### Calendar week 50

Enteee

winki
* Every element has a position and can be located [done]
* RealisitcAgent uses GPS [done]

### Calendar week 51

Enteee
* Simulation lag -> fixed with average velocity [done]

winki
* Spawning and despawning only at junctions. Edges will be mapped to begin junction or end junction at importing time of the routes file [done]
* Bugfix in RealisticAgent [done]

### Calendar week 52

Enteee

winki
* Bugfix (invalid relative positions) [done]
* Agents can set turning (short-term decision) or destination (long-term decision) [done]
* Console bugfix (command argument variables must not be final!) [done]
* Help text for commands [done]
* Added remove command [done]
* Agent handling on junctions [progress]

### Calendar week 2

Enteee

* Advanced langechange [done]
* Config stuff review [done]
* Fixing collisions [done]

winki
* Record statistics data (space mean speed, time mean speed, density) [done]
* Comments, refactoring [done]
* Configuration file [done]
* Lane statistic values [done]

## Open issues

### Planned

* Bugfixes
  * Transcendent agents when collision happend
* Write project documentation

### Backlog

* Agents not looking beyond edge boundaries
* Area restricted tick method
* Weather / daylight
* Console command to import OpenStreetMap data


## Resources

* [Project outline][projoutl]
* [Simulation of Urban MObility (SUMO), old website][sumoweb]
* [Simulation of Urban MObility (SUMO), wiki][sumowiki]

## Licence

This software and the underlying source code is licensed under the [MIT license][license].

[osm]:http://www.openstreetmap.ch/
[projoutl]:https://staff.hti.bfh.ch/swp1/Projekt_1/projects.html
[sumoweb]:http://web.archive.org/web/20140625054800/http://sumo-sim.org/
[sumowiki]:http://sumo.dlr.de/wiki/Main_Page

[license]:http://opensource.org/licenses/mit-license.php
