package ch.bfh.ti.jts.console.commands;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Element;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.Route;
import ch.bfh.ti.jts.data.SpawnInfo;
import ch.bfh.ti.jts.data.Vehicle;

import com.beust.jcommander.Parameter;

public class SpawnCommand implements Command {

    @Parameter(names = { "-number", "-n" }, description = "Number of vehicles to spawn")
    private final int number = 1;

    @Override
    public String execute(final Object executor) {
        final Net net = (Net) executor;

        // get net data
        final List<Element> edges = net.getElementStream(Edge.class).collect(Collectors.toList());

        // generate routes
        final Collection<SpawnInfo> routes = new LinkedList<>();
        for (int i = 0; i < number; i++) {

            final Vehicle vehicle = new Vehicle();

            // random route, random position
            final Edge routeStart = (Edge) edges.get(ThreadLocalRandom.current().nextInt(edges.size()));
            final Edge routeEnd = (Edge) edges.get(ThreadLocalRandom.current().nextInt(edges.size()));
            final double position = ThreadLocalRandom.current().nextDouble();
            final double departureTime = net.getTimeTotal();
            final double speed = vehicle.getMaxVelocity();

            final SpawnInfo route = new Route(vehicle, routeStart, routeEnd, departureTime, position, speed, 0.0, 0.0);
            routes.add(route);
        }
        net.addRoutes(routes);

        return String.format("%d vehicles spawned", number);
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public Class<?> getTargetType() {
        return Net.class;
    }
}
