package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.RangedPassage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/** Manage conversion of verses to a specific versification
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 */
public class VersificationConverter {
    private final VersificationsMapper versificationsMapper = VersificationsMapper.instance();

    /**
     * Flexible converter for the generic Key base class.
     * Return the key in the required versification, mapping if necessary
     * Currently only handles Passage, RangedPassage, and Verse
     */
    public Key convert(Key key, Versification toVersification) {
        try {
            if (key instanceof RangedPassage) {
                return convert((RangedPassage)key, toVersification);
            } else if (key instanceof VerseRange) {
                return convert((VerseRange)key, toVersification);
            } else if (key instanceof Passage) {
                return convert((Passage)key, toVersification);
            } else if (key instanceof Verse) {
                return convert((Verse)key, toVersification);
            }
        } catch (Exception e) {
            // unexpected problem during mapping
            log.warn("JSword Versification mapper failed to map {} to {}", key.getOsisID(), toVersification.getName());
        }
        return PassageKeyFactory.instance().createEmptyKeyList(toVersification);
    }

    public Passage convert(Passage passage, Versification toVersification) {
        return versificationsMapper.map(passage, toVersification);
    }

    public RangedPassage convert(RangedPassage rangedPassage, Versification toVersification) {
        RangedPassage result = new RangedPassage(toVersification);
        Iterator<VerseRange> iter = rangedPassage.rangeIterator(RestrictionType.NONE);
        while (iter.hasNext()) {
            result.add(convert(iter.next(), toVersification));
        }
        return result;
    }

    public VerseRange convert(VerseRange verseRange, Versification toVersification) {
        Verse startVerse = verseRange.getStart();
        Verse endVerse = verseRange.getEnd();

        Verse convertedStartVerse = convert(startVerse, toVersification);
        Verse convertedEndVerse = convert(endVerse, toVersification);

        return new VerseRange(toVersification, convertedStartVerse, convertedEndVerse);
    }

    /** Return the verse in the required versification, mapping if necessary
     */
    public Verse convert(Verse verse, Versification toVersification) {
        Verse mappedVerse = convertVerseStrictly(verse, toVersification);
        if (mappedVerse != null) return mappedVerse;
        // just try to retain information by forcing creation of a similar verse with the new v11n
        return new Verse(toVersification, verse.getBook(), verse.getChapter(), verse.getVerse());
    }

    public boolean isConvertibleTo(Verse verse, Versification v11n) {
        return convertVerseStrictly(verse, v11n)!=null;
    }

    /**
     * Convert the verse correctly to the v11n or return null
     */
    private Verse convertVerseStrictly(Verse verse, Versification toVersification) {
        try {
            Key key = versificationsMapper.mapVerse(verse, toVersification);
            if (!key.isEmpty()) {
                Verse mappedVerse = KeyUtil.getVerse(key);
                // If target v11n does not contain mapped verse then an exception normally occurs and the ordinal is set to 0
                if (mappedVerse.getOrdinal() > 0) {
                    return mappedVerse;
                }
            }
        } catch (Exception e) {
            // unexpected problem during mapping
            log.warn("JSword Versification mapper failed to map {} from {} to {}", verse.getOsisID(), verse.getVersification().getName(),
                     toVersification.getName());
            throw e;
        }
        return null;
    }

    private static final Logger log = LoggerFactory.getLogger(VersificationConverter.class);
}
