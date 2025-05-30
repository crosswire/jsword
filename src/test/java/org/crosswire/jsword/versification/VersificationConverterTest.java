package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.SystemKJVA;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 */
public class VersificationConverterTest {

	private VersificationConverter versificationConverter;

	private final Verse SEGOND_JOHN_3_16 = new Verse(TestData.SEGOND, BibleBook.JOHN, 3, 16);

	@Before
	public void setup() {
		versificationConverter = new VersificationConverter();
	}

	@Test
	public void isConvertibleTo() throws Exception {
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.KJV), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.NRSV), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.GERMAN), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.LUTHER), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.KJVA), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.SYNODAL), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.SYNODAL_PROT), is(true));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.VULGATE), is(true));
		// contain no NT books
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.MT), is(false));
		assertThat(versificationConverter.isConvertibleTo(SEGOND_JOHN_3_16, TestData.LENINGRAD), is(false));
	}

	@Test
	public void testConvert() throws Exception {
	    // Try to convert all verses in all versifications to KJVA
	    Iterator<String> iterator = Versifications.instance().iterator();
        int lastKjvOrdinal = TestData.KJVA.getLastVerse().getOrdinal();

        while (iterator.hasNext()) {
            Versification v11n = Versifications.instance().getVersification(iterator.next());
            for(int i=0; i<=v11n.getLastVerse().getOrdinal(); i++) {
                Verse v = new Verse(v11n, i);
                Verse vKjv = versificationConverter.convert(v, TestData.KJVA);
                int newOrdinal = vKjv.getOrdinal();
                assertThat("valid ordinal "+ newOrdinal, newOrdinal >= 0 && newOrdinal <= lastKjvOrdinal);
            }
        }
    }
    @Test
    public void testKJVAOrdinalRange() throws Exception {
	    // Make sure that KJVA versification range does not change without notice.

        Versification KJVA = Versifications.instance().getVersification(SystemKJVA.V11N_NAME);
        Verse lastVerse = KJVA.getLastVerse();
        int lastKjvOrdinal = lastVerse.getOrdinal();

        assertThat(lastKjvOrdinal, is(38272));
        assertThat(lastVerse.getOsisID(), is("Rev.22.21"));
        assertThat(KJVA.getAllVerses().getStart().getOrdinal(), is(0));
        assertThat(KJVA.getAllVerses().getEnd().getOrdinal(), is(38272));

    }
}
