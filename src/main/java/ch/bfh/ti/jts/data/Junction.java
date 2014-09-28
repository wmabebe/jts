package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;
import java.util.LinkedList;

public class Junction extends Element
{
   private double           x;
   private double           y;
   private Shape            shape;
   private Collection<Edge> edges;

   public Junction(double x, double y, Shape shape)
   {
      if (shape == null) throw new IllegalArgumentException("shape is null");
      
      this.x = x;
      this.y = y;
      this.shape = shape;
      this.edges = new LinkedList<Edge>();
   }
   
   public double getX()
   {
      return x;
   }
   
   public double getY()
   {
      return y;
   }
   
   public Collection<Edge> getEdges()
   {
      return edges;
   }
   
   @Override
   public void render(Graphics2D g)
   {
      g.fill(shape);
   }
}
