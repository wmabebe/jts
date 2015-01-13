# Java Traffic Simulator (jts)

*Java Traffic Simulator (jts)* is an agent based micro simulation on real transport networks. Designt for easy use. The project is relaized as part of the module: Project 1 at Bern University of Applied Sciences.

This project is currently under heavy developement.

## Features

* Easy but highly configurable with java properties
* Simulation of elements:
 * Agents; moving parts of the simulation
 * Lanes; where agents are moving on
 * Edge; bundling lanes together.
 * Junctions; connecting edges
 * Networks; holding elements
* Import of open street map data
* Layered/parallelized simulation
* Independent simulation and drawing
* Simulation interpolation for smooth drawing
* GPS implementation with dijekstra

## Table of contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Java Traffic Simulator (jts)](#java-traffic-simulator-jts)
  - [Features](#features)
  - [Table of contents](#table-of-contents)
  - [System context](#system-context)
  - [Documentation](#documentation)
    - [Code logic](#code-logic)
      - [App](#app)
      - [Simulatables](#simulatables)
        - [layer 0](#layer-0)
        - [layer 1](#layer-1)
        - [layer 2](#layer-2)
        - [layer 3](#layer-3)
        - [layer 4](#layer-4)
  - [Code Highlights](#code-highlights)
  - [Planning](#planning)
    - [Planned features](#planned-features)
    - [Journal](#journal)
      - [Calendar week 39](#calendar-week-39)
      - [Calendar week 40](#calendar-week-40)
      - [Calendar week 41](#calendar-week-41)
      - [Calendar week 42](#calendar-week-42)
      - [Calendar week 43](#calendar-week-43)
      - [Calendar week 44](#calendar-week-44)
    - [Calendar week 45](#calendar-week-45)
    - [Calendar week 46](#calendar-week-46)
    - [Calendar week 47](#calendar-week-47)
    - [Calendar week 48](#calendar-week-48)
    - [Calendar week 49](#calendar-week-49)
    - [Calendar week 50](#calendar-week-50)
    - [Calendar week 51](#calendar-week-51)
    - [Calendar week 52](#calendar-week-52)
    - [Calendar week 2](#calendar-week-2)
  - [Open issues](#open-issues)
    - [Planned](#planned)
    - [Backlog](#backlog)
  - [Resources](#resources)
  - [Licence](#licence)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## System context

![alt text][system_context]

## Documentation

### Code logic

#### App 

```java
// load configuration
initialization();
// load net from xml files
loadNet();
// load agent routes & traffic flows
loadRoutes();
// app run
showWindow();
loop {
	// remove agents which reached their target
	checkRemoveAgents();
	// spawn new agents according to routs & flows
	spawnAgents();
	// give the thinkables some time to make decisions
	foreach( thinkable : elements) {
		thinkable.think();
	}
	// simulate all the layers
	foreach( layer : layers ){
		foreach in parallel( simulatable : layer ){
			simulatable.simulate()
		}
	}
}
end();
```

#### Simulatables

* Every simulatabe (s) with layer (l) is only allowed to change element states of simulatables (s2) if s2.l < s.l or s2 == s

##### layer 0

* agent
 1. apply agent decision
 2. update agent pysics

##### layer 1

* lane
 1. update position of agents in lane datastructure
 2. do collisions of agent on lane

##### layer 2

* edge
 1. switch agents between lanes on this edge

##### layer 3

* junction
 1. select agent for despawning
 2. reroute agents between edges

##### layer 4

* net
 1. agent spawning
 2. agent despawning

## Code Highlights

* Simulation engine which is easely extensible with new elements
** New simulatables can overwrite getSimulationLayer()

```java
public interface Simulatable {
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
- [x] Spawn agents 
- [x] Dummy AI 
- [x] Move agents 

#### Calendar week 42

Enteee
- [x] Zooming 
- [x] Collisions 
- [x] Lane switching 

winki
- [x] Render agents on polygons 
- [x] Orientation of agents visible 

#### Calendar week 43

Enteee
- [x] GPS-helper 

winki
- [x] Import of route-files 
- [x] Spawning of agents based on activities 

#### Calendar week 44

Enteee
- [x] Commands to element redirection 

winki
- [x] Embedded console, thread-safe 
- [x] Spawn and time commands for console 

### Calendar week 45

Enteee
- [x] GPS unit tests 
- [x] Smarter agent, empty 

winki

### Calendar week 46

Enteee
- [x] Draw simulation decoupling 
- [x] Bugfix lane set agent override 

winki
- [x] Simple map for developing agent 
- [x] Fix index out of bounds bug in polyshape class 
- [x] Realistic agent 

### Calendar week 47

Enteee
- [x] Draw fake laneswitch 
- [x] Extended render interface with simulationStates 

winki
- [x] Improvement of realistic agent 

### Calendar week 48

Enteee

- [x] Fixed interval simulation 
- [x] Dynamic app sleeping 

winki
* Bugfix in lane
- [x] Lane switching logic of realisitc agent 
- [x] Implementation of traffic flows 
- [x] Agent type can be configured in the routes xml file 
- [x] Despawning of agents when spawn info of type "Flow" 
- [x] Added restart command to console 

### Calendar week 49

Enteee
- [x] Bugfix time conversion 10E-9 -> 1E-9 for nano 
- [x] Wall clock time in Window introduced 
- [x] Wall clock / simulation time decoupling -> issue lag 
- [x] Restart command fixing 
- [x] singleton app / window 
- [x] reflection for command finding 
- [x] toggleInterpolate command added 

winki
- [x] Added "ramp" net -> error at junctions 
- [x] Console can receive parameters from clickable GUI 

### Calendar week 50

Enteee

winki
- [x] Every element has a position and can be located 
- [x] RealisitcAgent uses GPS 

### Calendar week 51

Enteee
- [x] Simulation lag -> fixed with average velocity 

winki
- [x] Spawning and despawning only at junctions. Edges will be mapped to begin junction or end junction at importing time of the routes file 
- [x] Bugfix in RealisticAgent 

### Calendar week 52

Enteee

winki
- [x] Bugfix (invalid relative positions) 
- [x] Agents can set turning (short-term decision) or destination (long-term decision) 
- [x] Console bugfix (command argument variables must not be final!) 
- [x] Help text for commands 
- [x] Added remove command 
- [ ] Agent handling on junctions

### Calendar week 2

Enteee

- [x] Advanced langechange 
- [x] Config stuff review 
- [x] Fixing collisions 

winki
- [x] Record statistics data (space mean speed, time mean speed, density) 
- [x] Comments, refactoring 
- [x] Configuration file 
- [x] Lane statistic values 

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

[system_context]: https://raw.githubusercontent.com/winki/jts/master/doc/systemcontext.png "system context"

[osm]:http://www.openstreetmap.ch/
[projoutl]:https://staff.hti.bfh.ch/swp1/Projekt_1/projects.html
[sumoweb]:http://web.archive.org/web/20140625054800/http://sumo-sim.org/
[sumowiki]:http://sumo.dlr.de/wiki/Main_Page

[license]:http://opensource.org/licenses/mit-license.php
