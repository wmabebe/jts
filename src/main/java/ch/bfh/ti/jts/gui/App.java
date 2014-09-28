package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.importer.Importer;

public class App
{
   private Window window;
   private Net    net;

   public App()
   {
      this.net = new Importer().importData("src\\main\\resources\\net.net.xml");
      this.window = new Window(g -> {
         render(g);
      });
   }

   public void run()
   {
      init();

      while (isRunning() && !Thread.interrupted())
      {
         update();
         window.render();
      }

      end();
   }

   private void init()
   {
      window.setVisible(true);
   }

   private boolean isRunning()
   {
      return true;
   }

   private void update()
   {}

   private void render(Graphics2D g)
   {
      net.render(g);
   }

   private void end()
   {}
}
