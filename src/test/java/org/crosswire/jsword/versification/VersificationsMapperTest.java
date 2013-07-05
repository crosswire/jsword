package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.system.SystemCatholic;
import org.crosswire.jsword.versification.system.SystemCatholic2;
import org.crosswire.jsword.versification.system.SystemKJV;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests a simple two step versifications. And the edge cases,
 * of wanting to go to KJV.
 *
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 */
public class VersificationsMapperTest {
    private static final Versification KJV = Versifications.instance().getVersification(SystemKJV.V11N_NAME);
    private static final Versification CATHOLIC = Versifications.instance().getVersification(SystemCatholic.V11N_NAME);
    private static final Versification CATHOLIC2 = Versifications.instance().getVersification(SystemCatholic2.V11N_NAME);

    @Test
    public void testTwoStepVersification() throws NoSuchVerseException {
        doTest(CATHOLIC, "Gen.1.1", CATHOLIC2, "Gen.1.4");
    }

    @Test
    public void testTwoStepVersificationUsesParts() throws NoSuchVerseException {
        doTest(CATHOLIC, "Gen.1.2", CATHOLIC2, "Gen.1.2");
        doTest(CATHOLIC, "Gen.1.3", CATHOLIC2, "Gen.1.3");
    }

    @Test
    public void testSingleStepToKJV() throws NoSuchVerseException {
        doTest(CATHOLIC, "Gen.1.1", KJV, "Exod.1.2");
        doTest(CATHOLIC, "Gen.1.2", KJV, "Exod.1.3");
        doTest(CATHOLIC, "Gen.1.3", KJV, "Exod.1.3");
    }

    @Test
    public void testMissingMappings() throws NoSuchVerseException {
        doTest(CATHOLIC, "Rev.1.1", CATHOLIC2, "Rev.1.1");
    }

    @Test
    public void testSameKey() throws NoSuchVerseException {
        doTest(CATHOLIC, "Rev.1.1", CATHOLIC, "Rev.1.1");
    }

    @Test
    public void testSingleStepFromKJV() throws NoSuchVerseException {
        doTest(KJV, "Ex.1.2", CATHOLIC, "Gen.1.1");
        doTest(KJV, "Ex.1.3", CATHOLIC, "Gen.1.2-Gen.1.3");
    }

    /**
     * Tests that we can resolve an entire passage
     */
    @Test
    public void testPassageResolves() throws NoSuchKeyException {
        final VersificationsMapper mapper = VersificationsMapper.instance();
        Key k = mapper.map(KeyUtil.getPassage(PassageKeyFactory.instance().getKey(CATHOLIC, "Gen.1.1-3")), CATHOLIC2);

        assertEquals("Gen.1.2-Gen.1.4", k.getOsisRef());
        assertVersification(CATHOLIC2, k);
    }

    /**
     * Executes a test
     *
     * @param source    the source versification
     * @param sourceKey the source key
     * @param target    the target versification
     * @param targetKey the target key
     * @throws NoSuchVerseException an exception if we can't create the source verse key.
     */
    private void doTest(Versification source, String sourceKey, Versification target, String targetKey) throws NoSuchVerseException {
        final VersificationsMapper mapper = VersificationsMapper.instance();
        Key k = mapper.mapVerse(VerseFactory.fromString(source, sourceKey), target);

        assertEquals(targetKey, k.getOsisRef());
        assertVersification(target, k);
    }

    private void assertVersification(Versification catholic2, Key k) {
        if (k instanceof Verse) {
            assertEquals(catholic2, ((Verse) k).getVersification());
        }

        if (k instanceof Passage) {
            assertEquals(catholic2, ((Passage) k).getVersification());
        }
    }
}
