package ch.bfh.ti.jts.ai;

import ch.bfh.ti.jts.data.Agent;

public abstract class Brain
{
   private Agent agent;
   
   public Brain(Agent agent)
   {
      if (agent == null) throw new IllegalArgumentException();
      
      this.agent = agent;
   }
   
   public abstract Decision think();
}
