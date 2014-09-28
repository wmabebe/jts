package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;
import java.util.LinkedList;

public class Lane extends Element
{
   private Edge             edge;
   private int              index;
   private double           speed;
   private double           length;
   private Shape            shape;
   private Collection<Lane> lanes;

   public Lane(Edge edge, int index, double speed, double length, Shape shape)
   {
      if (edge == null) throw new IllegalArgumentException("edge is null");
      if (shape == null) throw new IllegalArgumentException("shape is null");
      
      this.edge = edge;
      this.index = index;
      this.speed = speed;
      this.length = length;
      this.shape = shape;
      this.lanes = new LinkedList<Lane>();
   }
   
   public Edge getEdge()
   {
      return edge;
   }
   
   public int getIndex()
   {
      return index;
   }
   
   public double getSpeed()
   {
      return speed;
   }
   
   public double getLength()
   {
      return length;
   }
        
   public Collection<Lane> getLanes()
   {
      return lanes;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      g.draw(shape);
   }   
}
