package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

public class Edge extends Element
{
   private Junction         start;
   private Junction         end;
   private int              priority;
   private Collection<Lane> lanes;

   public Edge(Junction start, Junction end, int priority)
   {
      if (start == null) throw new IllegalArgumentException("start is null");
      if (end == null) throw new IllegalArgumentException("end is null");
      
      this.start = start;
      this.end = end;
      this.priority = priority;
      this.lanes = new LinkedList<Lane>();
   }

   public Junction getStart()
   {
      return start;
   }

   public Junction getEnd()
   {
      return end;
   }

   public int getPriority()
   {
      return priority;
   }

   public Collection<Lane> getLanes()
   {
      return lanes;
   }

   @Override
   public void render(Graphics2D g)
   {
      g.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
   }
}
