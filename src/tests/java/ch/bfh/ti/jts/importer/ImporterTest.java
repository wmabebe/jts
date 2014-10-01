package ch.bfh.ti.jts.importer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import ch.bfh.ti.jts.data.Net;

public class ImporterTest {
    
    @Test
    public void testImport() {
        try {
            final Importer importer = new Importer();
            final Net net = importer.importData("src\\main\\resources\\net.net.xml");
            assertNotNull(net);
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }
}
