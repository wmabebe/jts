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

- [System context](#system-context)
- [Code highlights](#code-highlights)
- [Ideas & Thoughts](#ideas-&-thoughts)
  - [Main Application logic](#main-application-logic)
  - [Data model](#data-model)
  - [Parallelization](#parallelization)
    - [Simulatables](#simulatables)
      - [layer 0](#layer-0)
      - [layer 1](#layer-1)
      - [layer 2](#layer-2)
      - [layer 3](#layer-3)
      - [layer 4](#layer-4)
    - [Renderables](#renderables)
      - [layer 0](#layer-0-1)
      - [layer 1](#layer-1-1)
      - [layer 2](#layer-2-1)
  - [Multithreading](#multithreading)
  - [Dijekstra for path finding](#dijekstra-for-path-finding)
  - [Wall clock & simulation time](#wall-clock-&-simulation-time)
  - [Agent intelligence](#agent-intelligence)
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
  - [Calendar week 1](#calendar-week-1)
  - [Calendar week 2](#calendar-week-2)
  - [Calendar week 3](#calendar-week-3)
- [Open issues](#open-issues)
- [Resources](#resources)
- [Licence](#licence)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## System context

![system context](https://raw.githubusercontent.com/winki/jts/master/doc/systemcontext.png)

## Code highlights

* [Simulatable.java][Simulatable.java]: Simulation engine which is easily extensible with new elements.
* [Command.java][Command.java]: Integrated console engine with commands that are easy extensible.
  * Command autodiscovery with reflection
* [Thinkable.java][Thinkable.java]: Easy interface for smart new agents.
* [Importer.java][Importer.java]: Import road map data from [OpenStreetMap][osm].
* [Window.java][Window.java]: Graphical user interface with 2D output.
  * Allows scrolling and zooming
  * Allows console input & selection of elements by clicking

## Ideas & Thoughts

### Main Application logic

* main()
* Loads configuration
* Simulates with thinking
* Keeps map of saved simulation states

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
    // simulate all the layers
    foreach( layer : layers ){
        foreach in parallel( simulatable : layer ){
            simulatable.simulate()
        }
    }
    // give the thinkables some time to make decisions
    foreach( thinkable : elements) {
        thinkable.think();
    }
    // add simulation state to the list of saved states
    addSimulationState( deepCopy( this ) );
}
end();
```

### Data model

Maybe the biggest decision in the beginning of our project was how to model the road network. The first input cam from our supervisor and was the approach of using a skip list to model a lane. The index should represent the position in meters on the lane.

We decided to model not only straight roads with multiple lanes but also junctions to realize more complex road networks. We oriented us on the [road network data format of the _SUMO_ simulator][sumoroadnetworks]. Therefore our basic domain objects were net, edge, junction, lane and agent.

### Parallelization

Jts is structured in layers. The class which keeps track of them is [Layers.java][Layers.java]. Its usage is shown below.

![layers class](https://raw.githubusercontent.com/winki/jts/master/doc/layers.png)

* Net stores two different layering objects
  * renderables : used for rendering
  * simulatables : used for simulation
* Net adds elements based on their implementing interfaces (Renderable, Simulatable) to the layers.
* RenderPanel gets the layering object from Net (instance: wall clock) by calling getRenderables().
* Simulation gets the layering object from Net (instance: simulation time) by calling getSimulatables().
* RenderPanel and Simulation delegate execution to all the elements in layering order. See code taken from Simulation.simulate() as example below.
  * [SortedSet<Integer>](http://docs.oracle.com/javase/7/docs/api/java/util/SortedSet.html) in Layers ensures total order according to natural order (for Integer '<') of layerKeys.
  * .parallel().foreach() runs lambda-function in parallel, see [Multithreading](#multithreading).
```java
    // delegate simulation to @{link Simulatable}s
    final Layers<Simulatable> simulatables = simulateNet.getSimulatable();
    for (final int layer : simulatables.getLayersIterator()) {
        simulatables.getLayerStream(layer).parallel().forEach(e -> {
            e.simulate(duration);
        });
    }

```

The parallelization paradigm is: Every simulatabe (s) with layer (l) is only allowed to change element states of simulatables (s2) if s2.l < s.l or s2 == s. This allows parallel simulation of all the simulatables in one layer. Due to the simple fact that there is no way java to enforce the paradigm we had to be very careful when writing new code. 

#### Simulatables

All known simulation layers and residing classes.

##### layer 0

* [Agent][Agent.java]
  1. apply agent decision
  2. update agent pysics

##### layer 1

* [Lane][Lane.java]
  1. update position of agents in lane datastructure
  2. do collisions of agent on lane

##### layer 2

* [Edge][Edge.java]
  1. switch agents between lanes on this edge

##### layer 3

* [Junction][Junction.java]
  1. select agent for despawning
  2. reroute agents between edges

##### layer 4

* [Net][Net.java]
  1. agent spawning
  2. agent despawning

#### Renderables

All knownt rendering layers and residig classes.

##### layer 0

* [Lane][Lane.java]
  1. Draw lane according to [PolyShape][PolyShape.java]
  2. Draw lane information

##### layer 1

* [Junction][Junction.java]
  1. Fill gap between edges

##### layer 2

* [Agent][Agent.java]
  1. Animate lane switching
  2. Draw agent according to [PolyShape][PolyShape.java]

### Multithreading

In jts we heavily use the with java 8 newly introduces [streams](http://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html). The [parallel()](http://docs.oracle.com/javase/8/docs/api/java/util/stream/BaseStream.html#parallel--) method in conjunction with lambda function are particularly useful for multithreading. The jre regulates instantiation of worker threads automagically and does quite a good job in scheduling work for all the workers as they are all more or less under the same load.

![load distribugion](https://raw.githubusercontent.com/winki/jts/master/doc/load_distribution.png)

* green: running
* orange: park

### Dijekstra for path finding

[GPS][GPS.java] implements a working dijekstra algorithm for [Net][Net.java]. The interfaces [DirectedGraphVertex][DirectedGraphVertex.java] and [DirectedGraphEdge][DirectedGraphEdge.java] make the dijekstra impelemntation independent from the data classes.

![dijekstra](https://raw.githubusercontent.com/winki/jts/master/doc/dijekstra.png)

* [GPS][Gps.java] does most of the computation during construction.
* Once created [GPS][GPS.java] can not be adapted to learn an other [Net][Net.java].
* Due to the generic nature of [DirectedGraphVertex][DirectedGraphVertex.java] and [DirectedGraphEdge][DirectedGraphEdge.java] any dataclass could becose a component of a graph.
* [Junction][Junction.java] uses [GPS][GPS.java] for routing [Agents][Agent.java] to edges.

### Wall clock & simulation time

The actual [simulation][Simulation.java] of the [Net][Net.java] advances in discrete quantities of time. Meanwhile the [Window][Window.java] advances according to the time the user experiences in real life. Those two components are decoupled with the getWallClockSimulationState() method in [Simulation][Simulation.java]

![times](https://raw.githubusercontent.com/winki/jts/master/doc/times.png)

### Agent intelligence

We tried to implement an artificial intelligence that could master to drive on a arbitrary road network without causing a lot of collisions.

The agent has to make three kinds of decisions:

1. How much to accelerate/deccelerate?
2. Switch lane? When yes: left or right?
3. Which turning to take on a junction?

For the acceleration we calculated a security distance, so that no collision would happen, if the agent ahead would fully break. This would work perfectly, if there are no lane changes and no junctions. But this two circumstances made things complicated. We didn't solve this problem through the lack of time.

The idea behind the lane switching decision was the following: An agent tries to drive on the rightest lane of a track whenever this is possible. If he has to slow down, because the agent ahead is too slow, his impatience increases. If the impatience reaches a specified threshold, the agent tries to switch lane to the left to overtake the slow agent ahead. One consequence of this behavior was, that the agents switched to the motorway access road in the ramp scenario. This problem could be solved if we would type the lanes (default, fast lane, motorway access road and so on).

For the turning decision we decided to provide the agent a "GPS" function. The route was then given by the spawning point and the destination. Both informations were given by the routes file. The GPS function was realized with an implementation of the _Dijkstra_ algorithm.

## Journal

### Calendar week 39

Enteee, winki

- [x] Write requirements doc

### Calendar week 40

Enteee
- [x] Basic simulator setup

winki
- [x] Projekt setup 
- [x] Basic data structure 
- [x] Implement xml importer 

### Calendar week 41

Enteee
- [x] Simulation (Decision objects, think, simulate)
  - [x] call every think method of all Intelligents (parallel) 
  - [x] fill a collection 
  - [x] loop through decisions -> simulate every decision (serial), without lane switching 

winki
- [x] Spawn agents 
- [x] Dummy AI 
- [x] Move agents 

### Calendar week 42

Enteee
- [x] Zooming 
- [x] Collisions 
- [x] Lane switching 

winki
- [x] Render agents on polygons 
- [x] Orientation of agents visible 

### Calendar week 43

Enteee
- [x] GPS-helper 

winki
- [x] Import of route-files 
- [x] Spawning of agents based on activities 

### Calendar week 44

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
- [x] Bugfix in lane
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

### Calendar week 1

Enteee

winki

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

### Calendar week 3

Enteee
- [x] Readme goes documentation
- [x] Refactoring clicking
- [x] Jcommander bug workaround
- [x] Info command
- [x] Agent despawning on no junction cross
- [x] Refactor element removal -> Advanced removal (elements)

winki

## Open issues

- [ ] Bugfixes
  - [ ] Transcendent agents when collision happend
- [ ] Agent handling on junctions
- [ ] RealsticAgents not looking beyond edge boundaries
- [ ] Area restricted tick method
- [ ] Weather / daylight
- [ ] Console command to import OpenStreetMap data

## Resources

* [Project outline][projoutl]
* [_Simulation of Urban MObility_ (_SUMO_), website][sumoweb]
* [_Simulation of Urban MObility_ (_SUMO_), wiki][sumowiki]
* [_Simulation of Urban MObility_ (_SUMO_), old website][sumowebold]
* [Traffic flow (_Wikipedia_)][wikitrafficflow]

## Licence

This software and the underlying source code is licensed under the [MIT license][license].

[Simulation.java]:src/main/java/ch/bfh/ti/jts/simulation/Simulation.java
[Simulatable.java]:src/main/java/ch/bfh/ti/jts/simulation/Simulatable.java

[Thinkable.java]:src/main/java/ch/bfh/ti/jts/ai/Thinkable.java

[Importer.java]:src/main/java/ch/bfh/ti/jts/importer/Importer.java

[Command.java]:src/main/java/ch/bfh/ti/jts/gui/console/commands/Command.java
[PolyShape.java]:src/main/java/ch/bfh/ti/jts/gui/PolyShape.java
[Window.java]:src/main/java/ch/bfh/ti/jts/gui/Window.java

[Net.java]:src/main/java/ch/bfh/ti/jts/data/Net.java
[Junction.java]:src/main/java/ch/bfh/ti/jts/data/Junction.java
[Edge.java]:src/main/java/ch/bfh/ti/jts/data/Edge.java
[Lane.java]:src/main/java/ch/bfh/ti/jts/data/Lane.java
[Agent.java]:src/main/java/ch/bfh/ti/jts/data/Agent.java

[Layers.java]:src/main/java/ch/bfh/ti/jts/utils/layers/Layers.java

[GPS.java]:src/main/java/ch/bfh/ti/jts/utils/graph/GPS.java
[DirectedGraphVertex.java]:src/main/java/ch/bfh/ti/jts/utils/graph/DirectedGraphVertex.java
[DirectedGraphEdge.java]:src/main/java/ch/bfh/ti/jts/utils/graph/DirectedGraphEdge.java

[osm]:http://www.openstreetmap.ch/
[projoutl]:https://staff.hti.bfh.ch/swp1/Projekt_1/projects.html
[sumoweb]:http://sumo.dlr.de/
[sumowebold]:http://web.archive.org/web/20140625054800/http://sumo-sim.org/
[sumowiki]:http://sumo.dlr.de/wiki/Main_Page
[sumoroadnetworks]:http://sumo.dlr.de/wiki/Networks/SUMO_Road_Networks
[wikitrafficflow]:http://en.wikipedia.org/wiki/Traffic_flow

[license]:http://opensource.org/licenses/mit-license.php
