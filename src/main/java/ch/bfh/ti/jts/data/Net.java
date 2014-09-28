package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import ch.bfh.ti.jts.gui.Renderable;

public class Net extends Element implements Renderable
{
   private Collection<Agent>    agents;
   private Collection<Edge>     edges;
   private Collection<Junction> junctions;
   private Collection<Lane>     lanes;

   public Net()
   {
      this.agents = new LinkedList<Agent>();
      this.edges = new LinkedList<Edge>();
      this.junctions = new LinkedList<Junction>();
      this.lanes = new LinkedList<Lane>();
   }

   public Collection<Agent> getAgents()
   {
      return agents;
   }
   
   public Collection<Edge> getEdges()
   {
      return edges;
   }

   public Collection<Junction> getJunctions()
   {
      return junctions;
   }

   public Collection<Lane> getLanes()
   {
      return lanes;
   }

   @Override
   public void render(Graphics2D g)
   {  
      g.setStroke(new BasicStroke(6));
      g.setColor(Color.BLACK);
      for (Edge edge: edges)
      {         
         edge.render(g);
      }
      
      g.setStroke(new BasicStroke(1));
      g.setColor(Color.BLACK);      
      for (Junction junction: junctions)
      {         
         junction.render(g);
      }

      g.setStroke(new BasicStroke(1));
      g.setColor(Color.LIGHT_GRAY);
      for (Lane lane: lanes)
      {         
         lane.render(g);
      }
   }   
}
