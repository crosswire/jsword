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

import java.util.List;

import org.crosswire.jsword.passage.OsisParser;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RangedPassage;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.system.SystemCatholic;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some tests based on the principles outlined in the Javadoc of {@link VersificationToKJVMapper }
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Chris Burrell
 */
public class VersificationToKJVMapperTest {
    private final FileVersificationMapping properties = new FileVersificationMapping();
    private VersificationToKJVMapper mapper;
    private OsisParser osisParser = new OsisParser();

    @Test
    public void testSimpleMapping() {
        addProperty("Gen.1.1", "Gen.1.2");
        init();
        Assert.assertEquals("Gen.1.2", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.1", unmap("Gen.1.2"));
    }

    @Test
    public void testTwoLeftMappings() {
        addProperty("Gen.1.1", "Gen.1.1");
        addProperty("Gen.1.2", "Gen.1.1");
        init();

        //map always go to 1.1
        Assert.assertEquals("Gen.1.1", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.1", map("Gen.1.2"));
        Assert.assertEquals("Gen.1.1-Gen.1.2", unmap("Gen.1.1"));
    }

    @Test
    public void testLeftRangeMappings() {
        addProperty("Gen.1.1-Gen.1.2", "Gen.1.1");
        init();
        Assert.assertEquals("Gen.1.1", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.1", map("Gen.1.2"));
        Assert.assertEquals("Gen.1.1-Gen.1.2", unmap("Gen.1.1"));
    }

    @Test
    public void testTwoRightMappings() {
        addProperty("Gen.1.1", "Gen.1.1");
        addProperty("Gen.1.1", "Gen.1.2");
        init();
        Assert.assertEquals("Gen.1.1-Gen.1.2", map("Gen.1.1"));
    }

    @Test
    public void testRightRangeMappings() {
        addProperty("Gen.1.1", "Gen.1.1-Gen.1.2");
        init();

        Assert.assertEquals("Gen.1.1-Gen.1.2", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.1", unmap("Gen.1.1"));
        Assert.assertEquals("Gen.1.1", unmap("Gen.1.2"));
    }


    @Test
    public void testMissingMapping() {
        init();

        Assert.assertEquals("Gen.1.1", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.1", unmap("Gen.1.1"));
    }

    @Test
    public void testRangeToRange() {
        addProperty("Gen.1.1-Gen.1.3", "Gen.1.2-Gen.1.4");
        init();

        Assert.assertEquals("Gen.1.2", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.3", map("Gen.1.2"));
        Assert.assertEquals("Gen.1.4", map("Gen.1.3"));
        Assert.assertEquals("Gen.1.1", unmap("Gen.1.2"));
        Assert.assertEquals("Gen.1.2", unmap("Gen.1.3"));
        Assert.assertEquals("Gen.1.3", unmap("Gen.1.4"));
    }

    @Test
    public void testMappingWithPositiveOffset() {
        addProperty("Gen.1.1-Gen.1.2", "+1");
        init();

        Assert.assertEquals("Gen.1.2", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.3", map("Gen.1.2"));

        Assert.assertEquals("Gen.1.1", unmap("Gen.1.2"));
        Assert.assertEquals("Gen.1.2", unmap("Gen.1.3"));
    }

    @Test
    public void testPartsAreReturned() {
        addProperty("Gen.1.1", "Gen.1.3!a");
        addProperty("Gen.1.2", "Gen.1.3!b");
        init();


        Assert.assertEquals("Gen.1.3!a", mapToQualifiedKey("Gen.1.1"));
        Assert.assertEquals("Gen.1.3!b", mapToQualifiedKey("Gen.1.2"));
        Assert.assertEquals("Gen.1.3", map("Gen.1.1"));
        Assert.assertEquals("Gen.1.3", map("Gen.1.2"));
        Assert.assertEquals("Gen.1.1", unmap("Gen.1.3!a"));
        Assert.assertEquals("Gen.1.2", unmap("Gen.1.3!b"));
        Assert.assertEquals("Gen.1.1-Gen.1.2", unmap("Gen.1.3"));
    }

    @Test
    public void testExtraUnmappedVersesSingle() {
        addProperty("Dan.3.32", "?StoryOfThreeYoungMen.1.1");
        init();

        Assert.assertEquals("?StoryOfThreeYoungMen.1.1", mapToQualifiedKey("Dan.3.32"));
        Assert.assertEquals("Dan.3.32", mapper.unmap(new QualifiedKey("?StoryOfThreeYoungMen.1.1")).getOsisRef());
    }

    @Test
    public void testExtraUnmappedVersesWithRange() {
        addProperty("Dan.3.31-Dan.3.68", "?StoryOfThreeYoungMen");
        init();

        Assert.assertEquals("?StoryOfThreeYoungMen", mapToQualifiedKey("Dan.3.31"));
        Assert.assertEquals("?StoryOfThreeYoungMen", mapToQualifiedKey("Dan.3.32"));
        Assert.assertEquals("?StoryOfThreeYoungMen", mapToQualifiedKey("Dan.3.45"));
        Assert.assertEquals("Dan.3.31-Dan.3.68", mapper.unmap(new QualifiedKey("?StoryOfThreeYoungMen")).getOsisRef());
    }

    @Test
    public void testAbsentVerses() {
        addProperty("?", "Gen.1.1");
        init();
        Assert.assertEquals("", unmap("Gen.1.1"));
    }

    @Test
    public void testAbsentVersesWithRange() {
        addProperty("?", "Gen.1.1-Gen.1.3");
        init();
        Assert.assertEquals("", unmap("Gen.1.1"));
        Assert.assertEquals("", unmap("Gen.1.2"));
        Assert.assertEquals("", unmap("Gen.1.3"));
        Assert.assertEquals("Gen.1.4", unmap("Gen.1.4"));
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

    public String map(final String key) {
        VerseRange vr = osisParser.parseOsisRef(NON_KJV, key);
        final QualifiedKey range = new QualifiedKey(vr);
        List<QualifiedKey> qualifiedKeys = mapper.map(range);
        Passage vk = new RangedPassage(KJV);
        for (QualifiedKey qualifiedKey : qualifiedKeys) {
            //we may bits in here, that don't exist in the KJV
            if (qualifiedKey.getKey() != null) {
                vk.addAll(qualifiedKey.getKey().getWhole());
            }
        }

        return vk.getOsisRef();
    }

    public String unmap(final String kjvVerse) {
        return mapper.unmap(new QualifiedKey(osisParser.parseOsisRef(KJV, kjvVerse))).getOsisRef();
    }

    public String mapToQualifiedKey(final String verseKey) {
        VerseRange vk = osisParser.parseOsisRef(NON_KJV, verseKey);
        final QualifiedKey qualifiedVerse = new QualifiedKey(vk);
        List<QualifiedKey> qualifiedKeys = mapper.map(qualifiedVerse);

        StringBuilder representation = new StringBuilder(128);
        int len = qualifiedKeys.size();
        for (int i = 0; i < len; i++) {
            final QualifiedKey qk = qualifiedKeys.get(i);
            if (i > 0) {
                representation.append(' ');
            }
            representation.append(qk);

        }
        return representation.toString();
    }

    private static final Versification KJV = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);
    private static final Versification NON_KJV = Versifications.instance().getVersification(SystemCatholic.V11N_NAME);
}
