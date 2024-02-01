/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2014 - 2016
 *
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.system.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests a simple two step versifications. And the edge cases,
 * of wanting to go to KJV.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Chris Burrell
 */
public class VersificationsMapperTest {
    private static final Versification KJVA = Versifications.instance().getVersification(SystemKJVA.V11N_NAME);
    private static final Versification CATHOLIC = Versifications.instance().getVersification(SystemCatholic.V11N_NAME);
    private static final Versification CATHOLIC2 = Versifications.instance().getVersification(SystemCatholic2.V11N_NAME);
    private static final Versification SYNODAL = Versifications.instance().getVersification(SystemSynodal.V11N_NAME);

    @Test
    public void testTwoStepVersification() throws NoSuchVerseException {
        doTest(CATHOLIC2, "1Sam.20.42", SYNODAL, "1Sam.20.43");
        doTest(CATHOLIC2, "Ps.35.1", SYNODAL, "Ps.34.1");
    }

    @Test
    public void testTwoStepVersificationUsesParts() throws NoSuchVerseException {
        doTest(CATHOLIC, "Tob.7.11", CATHOLIC2, "Tob.7.11");
        doTest(CATHOLIC, "Tob.7.12", CATHOLIC2, "Tob.7.12");
        doTest(CATHOLIC, "Tob.10.14", CATHOLIC2, "Tob.10.14");
        doTest(CATHOLIC, "Dan.3.52", CATHOLIC2, "Dan.3.52");
        doTest(CATHOLIC, "Ps.35.1", CATHOLIC2, "Ps.35.1");
    }

    @Test
    public void testSingleStepToKJV() throws NoSuchVerseException {
        doTest(CATHOLIC2, "Gen.32.1", KJVA, "Gen.31.55");
        doTest(CATHOLIC2, "Dan.3.60", KJVA, "PrAzar.1.38");
        doTest(CATHOLIC2, "Esth.15.5", KJVA, "AddEsth.15.2");
        doTest(CATHOLIC2, "Dan.3.52", KJVA, "PrAzar.1.29-PrAzar.1.30");
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
        doTest(KJVA, "Gen.31.55", CATHOLIC, "Gen.32.1");
        doTest(KJVA, "AddEsth.13.5", CATHOLIC2, "Esth.13.5");
        doTest(KJVA, "Tob.11.1", CATHOLIC, "Tob.10.14 Tob.11.1");
    }

    @Test
    public void testMapVerseZero() throws NoSuchVerseException {
        doTest(KJVA, "Gen.1.0", KJVA, "Gen.1.0");
        doTest(KJVA, "Gen.1.0", SYNODAL, "Gen.1.0");
        doTest(KJVA, "Ps.50.0", KJVA, "Ps.50.0");
        doTest(KJVA, "Ps.50.0", CATHOLIC, "Ps.50.1");
        doTest(KJVA, "Ps.50.0", SYNODAL, "Ps.49.1");
        doTest(SYNODAL, "Ps.49.1", KJVA, "Ps.50.0-Ps.50.1");
    }

    /**
     * Tests that we can resolve an entire passage
     * 
     * @throws NoSuchKeyException 
     */
    @Test
    public void testPassageResolves() throws NoSuchKeyException {
        final VersificationsMapper mapper = VersificationsMapper.instance();
        Key k = mapper.map(KeyUtil.getPassage(PassageKeyFactory.instance().getKey(CATHOLIC, "Gen.32")), CATHOLIC2);

        Assert.assertEquals("Gen.32", k.getOsisRef());
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

        Assert.assertEquals(targetKey, k.getOsisRef());
        assertVersification(target, k);
    }

    private void assertVersification(Versification catholic2, Key k) {
        if (k instanceof Verse) {
            Assert.assertEquals(catholic2, ((Verse) k).getVersification());
        }

        if (k instanceof Passage) {
            Assert.assertEquals(catholic2, ((Passage) k).getVersification());
        }
    }
}
