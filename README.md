# Java Traffic Simulator (jts)

*Java Traffic Simulator* is a traffic flow microsimulation written in Java.

This project is currently under heavy developement.

## Features

* Simulation engine which is easely extensible

```java
public interface Simulatable {
    int getSimulationLayer();
    void simulate(final double duration, final Decision decision);
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
* Basic simulator setup [done]

winku
* Projekt setup [done]
* Basic data structure [done]
* Implement xml importer [done]

#### Calendar week 41

Enteee
* Simulation (Decision objects, think, simulate)
  * call every think method of all Intelligents (parallel) [done]
  * fill a collection [done]
  * loop through decisions -> simulate every decision (serial) [done, without lane switching]

winku
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
* Draw fake laneswitch

winki
* Improvement of realistic agent [done]

### Calendar week 48

Enteee

winki
* Bugfix in lane
* Lane switching logic of realisitc agent [done]
* Implementation of traffic flows

### Calendar week 49

Enteee

winki

### Calendar week 50

Enteee

winki

### Calendar week 51

Enteee

winki

## 

## Open issues

* Bugfixes
  * Transcendent agents when collision happend
* Agent handling on junctions
* Area restricted tick method
* Constant traffic flow 
* Console can receive parameters from clickable GUI
* Console commands:
  * agent removing
  * restart simulation
* Weather / daylight
* Write project documentation

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
