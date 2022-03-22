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
    
    public static void logLightweightHandshakes(Simulation simulation, long startTime, int simIterations,List<Double> rates) {
        try {
            FileWriter myWriter = new FileWriter("handshake_"+simIterations+"_log.txt");
            
            
            /* Start */
            final long endTime = System.currentTimeMillis();
            int totalAgents = simulation.getSimNet().getAgents().size();
            int totalNeighbors = 0;
            for (Agent a: simulation.getSimNet().getAgents()) {
                totalNeighbors += a.getNeighborsSet().size();
                myWriter.write(a.getId() + "\t size= " + a.getNeighborsSet().size() + "\t neighbors=" + a.getNeighborsSet().toString() + "\n");
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
    
    public static void fixedThresholding(Simulation simulation, int timestep, int threshold, long startTime, int simIterations,List<Double> rates) {
        try {
            FileWriter myWriter = new FileWriter("fixed_thresholding_epochs"+simIterations+"_threshold"+threshold+"_log.txt",true);
            /* Start */
            final long endTime = System.currentTimeMillis();
            int totalAgents = simulation.getSimNet().getAgents().size();
            int maxedQueue = 0;
            int totalQueueSize = 0;
            int uploadSize = 0;
            for (Agent a: simulation.getSimNet().getAgents()) {
                totalQueueSize += a.getNeighborsSet().size();
                if (a.getNeighborsSet().size() >= threshold) {
                    maxedQueue ++;
                    uploadSize += a.getNeighborsSet().size();
                    a.emptyNeighborsSet();
                }
            }
            //myWriter.write("Epoch \t Uploader \t Total_agents \t compute_time\n");
            String line = String.format("%d\t %d\t %d\t %d\t %d\t %d\n", timestep,maxedQueue,uploadSize,totalQueueSize,totalAgents,(endTime - startTime));
            myWriter.write(line);
            
            /* end */
           
            myWriter.close();
        }catch(IOException ex) {
            System.out.println("Unable to open file " + "fixed_thresholding_"+simIterations+"_log.txt");
        }
    }
    
    public static void adaptiveThresholding(Simulation simulation, int timestep, float z, long startTime, int simIterations,List<Double> rates) {
        try {
            FileWriter myWriter = new FileWriter("adaptive_thresholding_epochs"+simIterations+"_z"+z+"_log.txt",true);
            final long endTime = System.currentTimeMillis();
            int totalAgents = simulation.getSimNet().getAgents().size();
            int maxedQueue = 0;
            int uploadSize = 0;
            int totalQueueSize = 0;
            int mean = 0;
            float std = 0;
            for (Agent a: simulation.getSimNet().getAgents()) {
                mean += a.getNeighborsSet().size();
            }
            mean /=  simulation.getSimNet().getAgents().size();
            for (Agent a: simulation.getSimNet().getAgents()) {
                std += Math.pow(a.getNeighborsSet().size() - mean,2);
            }
            std /= (simulation.getSimNet().getAgents().size() - 1);
            std = (float) Math.sqrt(std);
            int threshold = mean + (int)(z * std);
            for (Agent a: simulation.getSimNet().getAgents()) {
                totalQueueSize += a.getNeighborsSet().size();
                if (a.getNeighborsSet().size() >= threshold && a.getNeighborsSet().size() > 10) {
                    maxedQueue ++;
                    uploadSize += a.getNeighborsSet().size();
                    a.emptyNeighborsSet();
                }
            }
            //myWriter.write("Epoch \t Uploader \t Total_agents \t compute_time\n");
            String line = String.format("%d\t %d\t %d\t %d\t %.2f\t %d\t %d\t %d\n", timestep,maxedQueue,uploadSize,mean,std,totalQueueSize,totalAgents,(endTime - startTime));
            myWriter.write(line);
            
            myWriter.close();
        }catch(IOException ex) {
                System.out.println("Unable to open file " + "adaptive_thresholding_"+simIterations+"_log.txt");
        }
    }
}
