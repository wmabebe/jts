package ch.bfh.ti.jts;
import java.io.*;
import java.util.List;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.simulation.Simulation;

public class Handshake {
    
    public static void logHandshakes(Simulation simulation, long startTime, int simIterations,List<Double> rates) {
        try {
            FileWriter myWriter = new FileWriter("handshake_"+simIterations+"_log.txt");
            
            
            /* Start */
            final long endTime = System.currentTimeMillis();
            int totalAgents = simulation.getSimNet().getAgents().size();
            int totalNeighbors = 0;
            for (Agent a: simulation.getSimNet().getAgents()) {
                totalNeighbors += a.getNeighborsQueue().size();
                myWriter.write(a.getId() + "\t" + a.getNeighborsQueue().size() + "\t" + a.getNeighborsQueue().toString() + "\n");
            }
            myWriter.write("#Handshake Rate: " + rates.toString() + "\n");
            myWriter.write("#Total agents : " + totalAgents + "\n");
            myWriter.write("#Avg neighbors: " + totalNeighbors/totalAgents + "\n");
            myWriter.write("#Total execution time: " + (endTime - startTime) + "\n");
            
            /* end */
           
            myWriter.close();
            
          } catch (IOException e) {
            System.out.println("An error occurred while logging.");
            e.printStackTrace();
          }
    }
}
