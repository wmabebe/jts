package ch.bfh.ti.jts.importer;

import static org.junit.Assert.*;
import org.junit.Test;
import ch.bfh.ti.jts.data.Net;

public class ImporterTest
{
   @Test
   public void testImport()
   {
      try
      {
         Importer importer = new Importer();
         Net net = importer.importData("src\\main\\resources\\net.net.xml");
         assertNotNull(net);
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }
}
