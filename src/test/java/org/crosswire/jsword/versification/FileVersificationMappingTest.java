package org.crosswire.jsword.versification;

import org.crosswire.common.config.ConfigException;
import org.crosswire.jsword.versification.system.SystemLeningrad;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Test;

import java.io.IOException;

/**
 * @author chrisburrell
 */
public class FileVersificationMappingTest {
    @Test
    public void testLeningrad() throws IOException, ConfigException {
        final Versification versification = Versifications.instance().getVersification(SystemLeningrad.V11N_NAME);
        FileVersificationMapping m = new FileVersificationMapping(versification);
        new VersificationToKJVMapper(versification, m);
        //TODO: fix test to track whether errors have occurred.
    }
}
