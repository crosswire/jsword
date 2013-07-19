package org.crosswire.jsword.versification;

import org.crosswire.jsword.versification.system.*;
import org.junit.Assert;
import org.crosswire.common.config.ConfigException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author chrisburrell
 */
@RunWith(Parameterized.class)
public class FileVersificationMappingTest {
    private String v11nName;

    /**
     * @param v11nName the v11n name we are testing
     */
    public FileVersificationMappingTest(String v11nName) {
        this.v11nName = v11nName;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {SystemLeningrad.V11N_NAME},
                {SystemSynodal.V11N_NAME},
                {SystemVulg.V11N_NAME},
                {SystemGerman.V11N_NAME}
        };
        return Arrays.asList(data);
    }

    @Test
    public void testVersifications() throws IOException, ConfigException {
        final Versification versification = Versifications.instance().getVersification(v11nName);
        FileVersificationMapping m = new FileVersificationMapping(versification);
        VersificationToKJVMapper mapper = new VersificationToKJVMapper(versification, m);
        Assert.assertFalse(mapper.hasErrors());
    }
}
