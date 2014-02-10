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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.system.SystemCatholic;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Some tests based on the principles outlined in the Javadoc of {@link VersificationToKJVMapper }
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author chrisburrell
 */
public class VersificationToKJVMapperTest {
    private final FileVersificationMapping properties = new FileVersificationMapping();
    private VersificationToKJVMapper mapper;

    @Test
    public void testSimpleMapping() throws NoSuchKeyException {
        addProperty("Gen.1.1", "Gen.1.2");
        init();
        assertEquals("Gen.1.2", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.1", mapper.unmap("Gen.1.2"));
    }

    @Test
    public void testTwoLeftMappings() throws NoSuchKeyException {
        addProperty("Gen.1.1", "Gen.1.1");
        addProperty("Gen.1.2", "Gen.1.1");
        init();

        //map always go to 1.1
        assertEquals("Gen.1.1", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.1", mapper.map("Gen.1.2"));
        assertEquals("Gen.1.1-Gen.1.2", mapper.unmap("Gen.1.1"));
    }

    @Test
    public void testLeftRangeMappings() throws NoSuchKeyException {
        addProperty("Gen.1.1-2", "Gen.1.1");
        init();
        assertEquals("Gen.1.1", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.1", mapper.map("Gen.1.2"));
        assertEquals("Gen.1.1-Gen.1.2", mapper.unmap("Gen.1.1"));
    }

    @Test
    public void testTwoRightMappings() throws NoSuchKeyException {
        addProperty("Gen.1.1", "Gen.1.1");
        addProperty("Gen.1.1", "Gen.1.2");
        init();
        assertEquals("Gen.1.1-Gen.1.2", mapper.map("Gen.1.1"));
    }

    @Test
    public void testRightRangeMappings() throws NoSuchKeyException {
        addProperty("Gen.1.1", "Gen.1.1-2");
        init();

        assertEquals("Gen.1.1-Gen.1.2", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.1", mapper.unmap("Gen.1.1"));
        assertEquals("Gen.1.1", mapper.unmap("Gen.1.2"));
    }


    @Test
    public void testMissingMapping() throws NoSuchKeyException {
        init();

        assertEquals("Gen.1.1", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.1", mapper.unmap("Gen.1.1"));
    }

    @Test
    public void testRangeToRange() throws NoSuchKeyException {
        addProperty("Gen.1.1-3", "Gen.1.2-4");
        init();

        assertEquals("Gen.1.2", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.3", mapper.map("Gen.1.2"));
        assertEquals("Gen.1.4", mapper.map("Gen.1.3"));
        assertEquals("Gen.1.1", mapper.unmap("Gen.1.2"));
        assertEquals("Gen.1.2", mapper.unmap("Gen.1.3"));
        assertEquals("Gen.1.3", mapper.unmap("Gen.1.4"));
    }

    @Test
    public void testMappingWithPositiveOffset() throws NoSuchKeyException {
        addProperty("Gen.1.1-2", "+1");
        init();

        assertEquals("Gen.1.2", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.3", mapper.map("Gen.1.2"));

        assertEquals("Gen.1.1", mapper.unmap("Gen.1.2"));
        assertEquals("Gen.1.2", mapper.unmap("Gen.1.3"));
    }

    @Test
    public void testPartsAreReturned() throws NoSuchKeyException {
        addProperty("Gen.1.1", "Gen.1.3@a");
        addProperty("Gen.1.2", "Gen.1.3@b");
        init();


        assertEquals("Gen.1.3@a", mapper.mapToQualifiedKey("Gen.1.1"));
        assertEquals("Gen.1.3@b", mapper.mapToQualifiedKey("Gen.1.2"));
        assertEquals("Gen.1.3", mapper.map("Gen.1.1"));
        assertEquals("Gen.1.3", mapper.map("Gen.1.2"));
        assertEquals("Gen.1.1", mapper.unmap("Gen.1.3@a"));
        assertEquals("Gen.1.2", mapper.unmap("Gen.1.3@b"));
        assertEquals("Gen.1.1-Gen.1.2", mapper.unmap("Gen.1.3"));
    }

    @Test
    public void testExtraUnmappedVersesSingle() throws NoSuchKeyException {
        addProperty("Dan.3.32", "?StoryOfThreeYoungMen.1.1");
        init();

        assertEquals("?StoryOfThreeYoungMen.1.1", mapper.mapToQualifiedKey("Dan.3.32"));
        assertEquals("Dan.3.32", mapper.unmap(new QualifiedKey("?StoryOfThreeYoungMen.1.1")).getOsisRef());
    }

    @Test
    public void testExtraUnmappedVersesWithRange() throws NoSuchKeyException {
        addProperty("Dan.3.31-68", "?StoryOfThreeYoungMen");
        init();

        assertEquals("?StoryOfThreeYoungMen", mapper.mapToQualifiedKey("Dan.3.31"));
        assertEquals("?StoryOfThreeYoungMen", mapper.mapToQualifiedKey("Dan.3.32"));
        assertEquals("?StoryOfThreeYoungMen", mapper.mapToQualifiedKey("Dan.3.45"));
        assertEquals("Dan.3.31-Dan.3.68", mapper.unmap(new QualifiedKey("?StoryOfThreeYoungMen")).getOsisRef());
    }

    @Test
    public void testAbsentVerses() throws NoSuchKeyException {
        addProperty("?", "Gen.1.1");
        init();
        assertEquals("", mapper.unmap("Gen.1.1"));
    }

    @Test
    public void testAbsentVersesWithRange() throws NoSuchKeyException {
        addProperty("?", "Gen.1.1-3");
        init();
        assertEquals("", mapper.unmap("Gen.1.1"));
        assertEquals("", mapper.unmap("Gen.1.2"));
        assertEquals("", mapper.unmap("Gen.1.3"));
        assertEquals("Gen.1.4", mapper.unmap("Gen.1.4"));
    }

    /**
     * Checks that we have the same hashcode and equals methods
     */
    @Test
    public void testVerseAndPassageHaveSameHashCode() throws NoSuchKeyException {
        Versification kjv = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);
        Verse v = VerseFactory.fromString(kjv, "Gen.1.2");
        Key p = PassageKeyFactory.instance().getKey(kjv, "Gen.1.2");

        assertEquals(v, p);
        assertEquals(p, v);
        assertEquals(v.hashCode(), p.hashCode());
    }

    /**
     * Initialises the object under test.
     */
    private void init() {
        mapper = new VersificationToKJVMapper(Versifications.instance().getVersification(SystemCatholic.V11N_NAME), properties);
    }

    /**
     * Helper method to add a property to our mappings
     *
     * @param left  the left key
     * @param right the kjv key
     */
    private void addProperty(final String left, final String right) {
        properties.addProperty(left, right);
    }
}
