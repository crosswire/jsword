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
 * Copyright: 2013 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.KeyValuePair;
import org.crosswire.common.util.LucidRuntimeException;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RangedPassage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.SimpleOsisParser;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Versification mapper allows you to a map a given verse to the KJV versification,
 * or unmap it from the KJV versification into your own versification.
 * <p>
 * A properties-like file will contain the non-KJV versification as they key, and the KJV versification value
 * as the target value... Duplicate keys are allowed.
 * </p>
 * <p>
 * i.e. Gen.1.1=Gen.1.2 means Gen.1.1 in X versification is Gen.1.2 in the KJV versification
 * </p>
 * <p>
 * You can specify a range on either side. If a range is present on both sides, they have to have the same number of
 * verses, i.e. verses are mapped verse by verse to each other<br/>
 * Gen.1.1-Gen.1.2=Gen.1.2-Gen.1.3 means Gen.1.1=Gen.1.2 and Gen.1.2=Gen.1.3<br/>
 *<br/>
 * Note: if the cardinality of the left & KJV sides are different by only one, the algorithm makes the
 * assumption that verse 0 should be disregarded in both ranges.
 * </p>
 * <p>
 * Mappings can be specified by offset. In this case, be aware this maps verse 0 as well. So for example:
 * </p>
 * <p>
 * Ps.19-Ps.20=-1 means Ps.19.0=Ps.18.50, Ps.19.1=Ps.19.0, Ps.19.2=Ps.19.1, etc.<br/>
 * It does not make much sense to have an offset for a single verse, so this is not supported.
 * Offsetting for multiple ranges however does, and operates range by range, i.e. each range is calculated separately.
 * Offsetting is somewhat equivalent to specifying ranges, and as a result, the verse 0 behaviour is identical.
 * </p>
 * <p>
 * You can use part-mappings. This is important if you want to preserve transformations from one side to another without
 * losing resolution of the verse.
 * </p>
 * <p>
 * For example,<br/>
 * if V1 defines Gen.1.1=Gen.1.1, Gen.1.2=Gen1.1<br/>
 * if V2 defines Gen.1.1=Gen.1.1, Gen.1.2=Gen.1.1<br/>
 * then, mapping from V1=>KJV and KJV=>V2 gives you Gen.1.1=>Gen.1.1=>Gen.1.1-Gen.1.2 which is inaccurate if in fact
 * V1(Gen.1.1) actually equals V2(Gen.1.1). So instead, we use a split on the right hand-side:
 * </p>
 * <p>
 * For example,<br/>
 * V1 defines Gen.1.1=Gen1.1!a, Gen.1.2=Gen.1.1!b<br/>
 * V2 defines Gen.1.1=Gen1.1!a, Gen.1.2=Gen.1.1!b<br/>
 * then, mapping from V1=>KJV and KJV=>V2 gives you Gen.1.1=>Gen.1.1!a=>Gen.1.1, which is now accurate.
 * A part is a string fragment placed after the end of a key reference. We cannot use # because that is commonly known
 * as a comment in real properties-file. Using a marker, means we can have meaningful part names if we so choose.
 * Parts of ranges are not supported.
 * </p>
 * <p>
 * Note: splits should never be seen by a user. The mapping from one versification to another is abstracted
 * such that the user can simply request the mapping between 2 verse (ranges).
 * </p>
 * <p>
 * Unmapped verses can be specified by inventing ids, either for whole sections, or verse by verse (this would
 * come in handy if two versifications have the same content, but the KJV doesn't). A section must be preceded
 * with a '?' character indicating that there will be no need to try and look up a reference.
 * Gen.1.1=?NewPassage
 * </p>
 * <p>
 * Since not specifying a verse mappings means there is a 1-2-1 unchanged mapping, we need a way of specifying
 * absent verses altogether:<br/>
 * ?=Gen.1.1<br/>
 * ?=Gen.1.5<br/>
 * means that the non-KJV book simply does not contain verses Gen.1.1 and Gen.1.5 and therefore can't
 * be mapped.
 * </p>
 * <p>
 * We allow some global flags (one at present):<br/>
 * !zerosUnmapped : means that any mapping to or from a zero verse
 * </p>
 * <p>
 * TODO(CJB): think about whether when returning, we should clone, or make things immutable.
 * </p>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author chrisburrell
 */
public class VersificationToKJVMapper {

    /**
     * @param mapping the mappings from one versification to another
     */
    public VersificationToKJVMapper(Versification nonKjv, final FileVersificationMapping mapping) {
        absentVerses = createEmptyPassage(KJV);
        toKJVMappings = new HashMap<VerseKey, List<QualifiedKey>>();
        fromKJVMappings = new HashMap<QualifiedKey, Passage>();
        this.nonKjv = nonKjv;
        processMappings(mapping);
        trace();
    }

    /**
     * This is the crux of the decoding facility.  The properties are expanded.
     *
     * @param mappings the input mappings, in a contracted, short-hand form
     */
    private void processMappings(FileVersificationMapping mappings) {
        final List<KeyValuePair> entries = mappings.getMappings();
        for (KeyValuePair entry : entries) {
            try {
                processEntry(entry);
            } catch (NoSuchKeyException ex) {
                // TODO(CJB): should we throw a config exception?
                LOGGER.error("Unable to process entry [{}] with value [{}]", entry.getKey(), entry.getValue(), ex);
                hasErrors = true;
            } catch (Exception ex) {
                // TODO(CJB): should we throw a config exception?
                LOGGER.error("Unable to process entry [{}] with value [{}]", entry.getKey(), entry.getValue(), ex);
                hasErrors = true;
            }
        }
    }

    private void processEntry(final KeyValuePair entry) throws NoSuchKeyException {
        String leftHand = entry.getKey();
        String kjvHand = entry.getValue();

        if (leftHand == null || leftHand.length() == 0) {
            LOGGER.error("Left hand must have content");
            return;
        }

        // we allow some global flags properties - for a want of a better syntax!
        if ("!zerosUnmapped".equals(leftHand)) {
            this.zerosUnmapped = true;
            return;
        }

        // At this point, the left hand side must be a Verse or a VerseRange
        // It cannot be prefixed by +, ?, or !.
        QualifiedKey left = getRange(this.nonKjv, leftHand, null);

        // The right hand side can start with ? which means that the left maps to nothing in the KJV.
        // The ? leads a section name
        QualifiedKey kjv = getRange(KJV, kjvHand, left.getKey());
        addMappings(left, kjv);
    }

    /**
     * Adds a 1-Many mapping, by simply listing out all the properties. There is probably
     * a better way for storing this, perhaps in a tree - but for simplicity, we're going to list them out.
     *
     * @param leftHand  the left hand side operand
     * @param kjvVerses the verses that are mapped by the left hand side
     */
    private void addMappings(final QualifiedKey leftHand, final QualifiedKey kjvVerses) throws NoSuchVerseException {
        if (leftHand.getAbsentType() == QualifiedKey.Qualifier.ABSENT_IN_LEFT) {
            this.absentVerses.addAll(kjvVerses.getKey());
        } else if (leftHand.getKey().getCardinality() == 1) {
            add1ToManyMappings(leftHand.getVerse(), kjvVerses);
        } else {
            addManyToMany(leftHand, kjvVerses);
        }
    }

    /**
     * Adds a many to many mapping, mappings all the verses on the left hand side to all the verses on the right hand side.
     * We support 2 types: Many-to-1 and Many-to-Many.
     *
     * @param leftHand is assumed to be many
     * @param kjvVerses could be 1 or many
     */
    private void addManyToMany(final QualifiedKey leftHand, final QualifiedKey kjvVerses) {
        VerseKey leftKeys = leftHand.getKey();
        VerseKey kjvKeys = kjvVerses.getKey();
        Iterator<Key> leftIter = leftKeys.iterator();

        boolean isKJVMany = kjvKeys != null && kjvKeys.getCardinality() != 1;

        if (isKJVMany) {
            // We detect if the keys are 1-apart from each other. If so, then we skip verse 0 on both sides.
            int diff = Math.abs(leftKeys.getCardinality() - kjvKeys.getCardinality());

            if (diff > 1) {
                reportCardinalityError(leftKeys, kjvKeys);
            }
            boolean skipVerse0 = diff == 1;

            Iterator<Key> kjvIter = kjvKeys.iterator();
            while (leftIter.hasNext()) {
                Verse leftVerse = (Verse) leftIter.next();

                // hasNext() and next() have to be paired
                if (!kjvIter.hasNext()) {
                    reportCardinalityError(leftKeys, kjvKeys);
                }

                Verse rightVerse = (Verse) kjvIter.next();

                // Identity mapping is the default
                // We might want to report this
                if (leftVerse.getVerse() == rightVerse.getVerse()) {
                    continue;
                }

                QualifiedKey kjvKey = new QualifiedKey(rightVerse);

                // When the lists are of lengths differing by one
                // it is because 0 is extra.
                // When this is encountered on the left side,
                // we map it and the next verse to the current
                // right verse.
                // In this block we'll handle the case of mapping 0
                // and at the end of the loop we'll handle the next verse.
                if (skipVerse0 && leftVerse.getVerse() == 0) {

                    addForwardMappingFromSingleKeyToRange(leftVerse, kjvKey);
                    addKJVToMapping(kjvKey, leftVerse);

                    if (!leftIter.hasNext()) {
                        reportCardinalityError(leftKeys, kjvKeys);
                    }

                    leftVerse = (Verse) leftIter.next();
                }

                // Likewise for the right side,
                // we map it and the next verse to the current
                // left verse.
                // Likewise for this block, it only handles mapping 0
                // Code at the end will map the following verse
                if (skipVerse0 && rightVerse.getVerse() == 0) {

                    addForwardMappingFromSingleKeyToRange(leftVerse, kjvKey);
                    addKJVToMapping(kjvKey, leftVerse);

                    if (!kjvIter.hasNext()) {
                        reportCardinalityError(leftKeys, kjvKeys);
                    }

                    rightVerse = (Verse) kjvIter.next();
                    kjvKey = new QualifiedKey(rightVerse);
                }

                // Now do the normal case mapping
                addForwardMappingFromSingleKeyToRange(leftVerse, kjvKey);
                addKJVToMapping(kjvKey, leftVerse);
            }

            // Check to see if, having exhausted left that there is more
            // on the right
            if (kjvIter.hasNext()) {
                reportCardinalityError(leftKeys, kjvKeys);
            }
        } else {
            while (leftIter.hasNext()) {
                final Verse leftKey = (Verse) leftIter.next();
                addForwardMappingFromSingleKeyToRange(leftKey, kjvVerses);
                addKJVToMapping(kjvVerses, leftKey);
            }
        }

    }

    /**
     * If for some reason cardinalities of keys are different, we report it.
     *
     * @param leftHand  the left hand key
     * @param kjvVerses the kjv qualified key
     */
    private void reportCardinalityError(final VerseKey leftKeys, final VerseKey kjvKeys) {
        // TODO (CJB): change this to a neater exception
        // then something went wrong, as we have remaining verses
        throw new LucidRuntimeException(String.format("%s has a cardinality of %s whilst %s has a cardinality of %s.",
                leftKeys, Integer.toString(leftKeys.getCardinality()),
                kjvKeys, Integer.toString(kjvKeys.getCardinality())));
    }

    /**
     * If leftKey is non-null (i.e. not attached to a simple specifier, then adds to the kjvTo mappings
     *
     * @param kjvVerses the kjv verses
     * @param leftKey   the left-hand key, possibly null.
     */
    private void addKJVToMapping(final QualifiedKey kjvVerses, final Verse leftKey) {
        // NOTE(DMS): Both kjvVerses and left key are each a single verse
        if (leftKey != null) {
            getNonEmptyKey(this.fromKJVMappings, kjvVerses).addAll(leftKey);

            // If we have a part, then we need to add the whole verse as well...
            if (!kjvVerses.isWhole()) {
                getNonEmptyKey(this.fromKJVMappings, QualifiedKey.create(kjvVerses.getKey().getWhole())).addAll(leftKey);
            }
        }
    }

    /**
     * A simple two way entry between 2 1-1 entries.
     *
     * @param leftHand the verse on the left side, left is assumed to be 1 verse only
     * @param kjvHand  the KJV reference
     * @throws NoSuchVerseException
     */
    private void add1ToManyMappings(final Verse leftHand, final QualifiedKey kjvHand) throws NoSuchVerseException {
        addForwardMappingFromSingleKeyToRange(leftHand, kjvHand);
        addReverse1ToManyMappings(leftHand, kjvHand);
    }

    /**
     * Adds the data into the reverse mappings. Caters for 1-2-1 and 1-2-Many mappings
     *
     * @param leftHand the reference of the left hand reference
     * @param kjvHand  the kjv reference/key, qualified with the part
     */
    private void addReverse1ToManyMappings(final Verse leftHand, final QualifiedKey kjvHand) {
        //add the reverse mapping, for 1-1 mappings
        if (kjvHand.getAbsentType() == QualifiedKey.Qualifier.ABSENT_IN_KJV || kjvHand.getKey().getCardinality() == 1) {
            // TODO(CJB): deal with parts
            addKJVToMapping(kjvHand, leftHand);
        } else {
            //add the 1-many mappings
            //expand the key and add them all
            //Parts are not supported on ranges...
            Iterator<Key> kjvKeys = kjvHand.getKey().iterator();
            while (kjvKeys.hasNext()) {
                addKJVToMapping(new QualifiedKey(KeyUtil.getVerse(kjvKeys.next())), leftHand);
            }
        }
    }

    /**
     * Adds a forward mappings from left to KJV.
     *
     * @param leftHand the left hand reference (corresponding to a non-kjv versification)
     * @param kjvHand  the kjv reference (with part if applicable).
     */
    private void addForwardMappingFromSingleKeyToRange(final Verse leftHand, final QualifiedKey kjvHand) {
        if (leftHand == null) {
            return;
        }

        getNonEmptyMappings(this.toKJVMappings, leftHand).add(kjvHand);
    }

    /**
     * Gets a non-empty key list, either new or the one existing in the map already.
     *
     * @param mappings the map from key to list of values
     * @param key      the key
     * @return the non-empty mappings list
     */
    private VerseKey getNonEmptyKey(final Map<QualifiedKey, Passage> mappings, final QualifiedKey key) {
        Passage matchingVerses = mappings.get(key);
        if (matchingVerses == null) {
            matchingVerses = createEmptyPassage(this.nonKjv);
            mappings.put(key, matchingVerses);
        }
        return matchingVerses;
    }

    /**
     * Gets a non-empty list, either new or the one existing in the map already
     *
     * @param mappings the map from key to list of values
     * @param key      the key
     * @param <T>      the type of the key
     * @param <S>      the type of the value
     * @return the separate list of verses
     */
    private <T, S> List<S> getNonEmptyMappings(final Map<T, List<S>> mappings, final T key) {
        List<S> matchingVerses = mappings.get(key);
        if (matchingVerses == null) {
            matchingVerses = new ArrayList<S>();
            mappings.put(key, matchingVerses);
        }
        return matchingVerses;
    }

    /**
     * Gets the input range as a single verse or throws an exception
     *
     * @param verseKey the verses
     * @return the separate list of verses
     * @deprecated no replacement
     */
    @Deprecated
    private QualifiedKey getRangeAsVerse(final Versification versification, String verseKey) throws NoSuchKeyException {
        final QualifiedKey range = getRange(versification, verseKey, null);
        if (range.getKey() instanceof VerseRange) {
            return new QualifiedKey(((VerseRange) range.getKey()).getStart());
        }
        return range;
    }

    /**
     * Expands a reference to all its verses
     *
     * @param versesKey the verses
     * @return the separate list of verses
     */
    private QualifiedKey getRange(final Versification versification, String versesKey, VerseKey offsetBasis) throws NoSuchKeyException {
        //deal with absent keys in left & absent keys in right, which are simply marked by a '?'
        if (versesKey == null || versesKey.length() == 0) {
            throw new NoSuchKeyException(JSMsg.gettext("Cannot understand [{0}] as a chapter or verse.", versesKey));
        }

        char firstChar = versesKey.charAt(0);
        switch (firstChar) {
            case '?':
                // TODO(CJB): The class JavaDoc has ? on the left side
                // Where is that in any of the mapping code, other than in tests?
                // NOTE(DMS): Does not return a range of any kind.
                return getAbsentQualifiedKey(versification, versesKey);
            case '+':
            case '-':
                // TODO(CJB): Is + or - allowed only on the right hand side
                // NOTE(DMS): Always returns a QualifiedKey containing a Passage having a VerseRange of 1 or more verses
                return getOffsetQualifiedKey(versification, versesKey, offsetBasis);
            default:
                return getExistingQualifiedKey(versification, versesKey);
        }
    }

    /**
     * Deals with offset markers, indicating a passage is +x or -x verses from this one.
     *
     * @param versification the versification of the passed in key
     * @param versesKey     the text of the reference we are trying to parse
     * @return the qualified key representing this
     */
    private QualifiedKey getOffsetQualifiedKey(final Versification versification, final String versesKey, VerseKey offsetBasis) throws NoSuchKeyException {
        if (offsetBasis == null || offsetBasis.getCardinality() == 0) {
            // TODO(CJB): internationalize
            throw new NoSuchKeyException(JSMsg.gettext("Unable to offset the given key [{0}]", offsetBasis));
        }
        int offset = Integer.parseInt(versesKey.substring(1));

        // Convert key immediately to the target versification system, namely the KJV, since it is the only
        // one supported. Convert by ref - since the whole purpose of this is to define equivalents.

         VerseRange vr = null;
         if (offsetBasis instanceof VerseRange) {
             vr = (VerseRange) offsetBasis;
         } else if (offsetBasis instanceof Passage) {
             Iterator iter = ((Passage) offsetBasis).rangeIterator(RestrictionType.NONE);
             if (iter.hasNext()) {
                 vr = (VerseRange) iter.next();
             }
         }
         if (vr == null) {
             // TODO(CJB): internationalize
             throw new NoSuchKeyException(JSMsg.gettext("Unable to offset the given key [{0}]", offsetBasis));
         }

         Verse vrStart = vr.getStart();
         Verse start = vrStart.reversify(versification);
         // While you can add a negative number, these are optimized for their operation
         if (offset < 0) {
             start = versification.subtract(start, -offset);
         } else if (offset > 0) {
             start = versification.add(start, offset);
         }
         Verse end = start;
         if (vr.getCardinality() > 1) {
             end = versification.add(start, vr.getCardinality() - 1);
         }
         VerseRange newvr = new VerseRange(versification, start, end);
         return new QualifiedKey(newvr);
    }

    /**
     * @param versification
     * @param offset
     * @param verseRange
     * @return a VerseRange
     * @deprecated no replacement
     */
    @Deprecated
    private VerseRange getNewVerseRange(final Versification versification, final int offset, final VerseRange verseRange) {
        // TODO(CJB): See comment in previous method for a better way.
        // The Versification class has optimized methods to compute a verse offset from another.
        final Verse newStart = new Verse(versification, verseRange.getStart().getOrdinal() + offset);
        final Verse newEnd = new Verse(versification, verseRange.getEnd().getOrdinal() + offset);
        return new VerseRange(versification, newStart, newEnd);
    }

    /**
     * Deals with real keys found in the versification.
     *
     * @param versification the versification of the passed in key
     * @param versesKey     the text of the reference we are trying to parse
     * @return the qualified key representing this
     */
    private QualifiedKey getExistingQualifiedKey(final Versification versification, final String versesKey) {
        return new QualifiedKey(SimpleOsisParser.parseOsisRef(versification, versesKey));
    }

    /**
     * Deals with absent markers, whether absent in the KJV or absent in the current versification.
     *
     * @param versification the versification of the passed in key
     * @param versesKey     the text of the reference we are trying to parse
     * @return the qualified key representing this
     */
    private QualifiedKey getAbsentQualifiedKey(final Versification versification, final String versesKey) {
        if (versification.equals(this.nonKjv)) {
            // we're dealing with a '?', and therefore an ABSENT_IN_LEFT scenarios.
            // we do not support any other ? markers on the left
            return new QualifiedKey();
        }
        // we're dealing with a ? on the KJV side, therefore we must be looking at
        // a section name
        return new QualifiedKey(versesKey);
    }

    /**
     * Converts the input to the KJV versification
     *
     * @return the equivalent key
     * @deprecated no replacement
     */
    @Deprecated
    private String map(final String key) throws NoSuchKeyException {
        // TODO(CJB): Consider changing the parameter to Verse key.
        // Converting a Verse to a string is expensive.
        // Parsing the string is expensive.
        final QualifiedKey range = getRangeAsVerse(nonKjv, key);
        // TODO(CJB): Consider changing return type to a VerseRange or a Verse
        // Converting a Passage, VerseRange or Verse to a string is expensive
        return map((Verse) range.getKey()).getOsisRef();
    }

    /**
     * @return the qualified keys associated with the input key.
     */
    private List<QualifiedKey> getQualifiedKeys(final Key leftKey) {
        return this.toKJVMappings.get(leftKey);
    }

    /**
     * Returns the key in the target versification, by using the OsisRef. Note: if the key doesn't exist
     * in the other versification, it is most probably because that key doesn't exist at all. So we'll log a warning,
     * but return an empty key.
     * <p>
     * There is one exception however, and that is, for Versifications that don't use verse 0s we allow a global
     * flag to prevent mappings for verse 0.
     * </p>
     *
     * @param qualifiedKey the qualified key containing the OSIS key ref.
     * @return the same key represented by the OSIS ref, except that it is the target versification.
     * @deprecated no replacement
     */
    @Deprecated
    private QualifiedKey getKeyRefInDifferentVersification(final QualifiedKey qualifiedKey, Versification target) {
        if (this.zerosUnmapped && isZero(qualifiedKey)) {
            return new QualifiedKey();
        }
        return qualifiedKey.reversify(target);
    }

    /**
     * Qualified key to test for a verse 0
     * @param qualifiedKey the qualified key
     * @return true, if the qualified key represents verse 0
     * @deprecated no replacement
     */
    @Deprecated
    private boolean isZero(final QualifiedKey qualifiedKey) {
        VerseKey k = qualifiedKey.getKey();
        if (k == null) {
            // This is not a verse that can be reversified.
            return true;
        }

        Iterator<Key> keys = k.iterator();
        if (keys.hasNext() && ((Verse) keys.next()).getVerse() == 0) {
            // true if we don't have any more keys in our set
            return !keys.hasNext();
        }

        //no keys in iterator
        return false;
    }

    /**
     * Converts the input to the KJV versification, but returns the qualified key representation, i.e. not necessarily
     * an OSIS representation. The key needs to represent a single verse
     * <p>
     * This key is useful if different versifications (say Dan 3 in the 2 Catholic versifications) have the same
     * section which is not present in the KJV versification.
     * </p>
     * <p>
     * Its sister method, taking in a Key, and returning a QualifiedKey will be more helpful, generally speaking
     * </p>
     * 
     * @return the equivalent key, which may or may not be used to look up a reference in a book.
     * @deprecated no replacement
     */
    @Deprecated
    private String mapToQualifiedKey(final String verseKey) throws NoSuchKeyException {
        // TODO(CJB): Consider changing the parameter to Verse key.
        // Converting a Verse to a string is expensive.
        // Parsing the string is expensive.
        final QualifiedKey qualifiedVerse = getRangeAsVerse(nonKjv, verseKey);
        List<QualifiedKey> qualifiedKeys = map(qualifiedVerse);

        StringBuilder representation = new StringBuilder(128);
        for (int i = 0; i < qualifiedKeys.size(); i++) {
            final QualifiedKey qk = qualifiedKeys.get(i);
            String sectionName = qk.getSectionName();
            VerseKey vk = qk.getKey();
            if (sectionName != null) {
                representation.append(qk.getSectionName());
            } else if (vk != null) {
                representation.append(vk.getOsisRef());
            }
            if (sectionName != null || vk != null) {
                if (i < qualifiedKeys.size() - 1) {
                    representation.append(' ');
                }
            }
        }
        return representation.toString();
    }

    /**
     * Maps the full qualified key to its proper equivalent in the KJV.
     *
     * @param qualifiedKey the qualified key
     * @return the list of matching qualified keys in the KJV versification.
     */
    public List<QualifiedKey> map(QualifiedKey qualifiedKey) {
        VerseKey key = qualifiedKey.getKey();
        if (key instanceof Verse) {
            List<QualifiedKey> kjvKeys = this.getQualifiedKeys(key);
            if (kjvKeys == null || kjvKeys.size() == 0) {
                //then we found no mapping, so we're essentially going to return the same key back...
                //unless it's a verse 0 and then we'll check the global flag.
                kjvKeys = new ArrayList<QualifiedKey>();
                kjvKeys.add(qualifiedKey.reversify(KJV));
                return kjvKeys;
            }
            return kjvKeys;
        }

        return new ArrayList<QualifiedKey>();
    }

    /**
     * Converts the input to the KJV versification
     *
     * @return the equivalent key
     * @deprecated no replacement
     */
    @Deprecated
    private Key map(final Verse leftKey) {
        List<QualifiedKey> qualifiedKeys = map(new QualifiedKey(leftKey));

        //convert qualified keys into a passage representation, since that's what the user is after.
        Passage keyList = createEmptyPassage(KJV);
        for (QualifiedKey qualifiedKey : qualifiedKeys) {
            //we may bits in here, that don't exist in the KJV
            if (qualifiedKey.getKey() != null) {
                keyList.addAll(qualifiedKey.getKey());
            }
        }

        return keyList;
    }

    /**
     * Converts a KJV verse to the target versification
     *
     * @return the key in the left-hand versification
     * @deprecated no replacement
     */
    @Deprecated
    private String unmap(final String kjvVerse) throws NoSuchKeyException {
        // TODO(CJB): Consider changing the parameter to Verse key.
        // Converting a Verse to a string is expensive.
        // Parsing the string is expensive.
        // TODO(CJB): Consider changing return type to a VerseRange or a Verse
        // Converting a Passage, VerseRange or Verse to a string is expensive
        return unmap(getRangeAsVerse(KJV, kjvVerse)).getOsisRef();
    }

    /**
     * Converts a KJV verse to the target versification, from a qualified key, rather than a real key
     *
     * @return the key in the left-hand versification
     */
    public VerseKey unmap(final QualifiedKey kjvVerse) {
        // TODO(CJB): cope for parts?
        Passage left = this.fromKJVMappings.get(kjvVerse);

        if (left == null && !kjvVerse.isWhole()) {
            // Try again, but without the part this time
            left = this.fromKJVMappings.get(new QualifiedKey(kjvVerse.getVerse().getWhole()));
        }

        //if we have no mapping, then we are in 1 of two scenarios
        //the verse is either totally absent, or the verse is not part of the mappings, meaning it is a straight map
        if (left == null) {
            VerseKey vk = kjvVerse.getKey();
            if (vk != null && this.absentVerses.contains(vk)) {
                return createEmptyPassage(KJV);
            }
            return kjvVerse.reversify(this.nonKjv).getKey();
        }
        return left;
    }

    /**
     * Converts a KJV verse to the target versification
     *
     * @return the key in the left-hand versification
     * @deprecated no replacement
     */
    @Deprecated
    private Key unmap(final VerseKey kjvVerse) {
        return unmap(QualifiedKey.create(kjvVerse));
    }

    /**
     * Outputs the mappings for debug purposes to the log file.
     */
    private void trace() {
        if (LOGGER.isTraceEnabled()) {
            PrintStream ps = null;
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ps = new PrintStream(os);
                dump(ps);
                String output = os.toString("UTF8");
                LOGGER.trace(output);
            } catch (UnsupportedEncodingException e) {
            } finally {
                IOUtil.close(ps);
            }
        }
    }

    /**
     * Dump a debug representation of this map to the output stream.
     * 
     * @param out
     */
    public void dump(PrintStream out) {
        String nonKjvName = this.nonKjv.getName();
        out.println("##############################");
        out.print(String.format("Mapping between KJV and %s", nonKjvName));
        out.println("##############################");
        out.println("    ******************************");
        out.println("    Forward mappings towards KJV");
        out.println("    ******************************");
        for (Map.Entry<VerseKey, List<QualifiedKey>> entry : this.toKJVMappings.entrySet()) {
            List<QualifiedKey> kjvVerses = entry.getValue();
            String osisRef = entry.getKey().getOsisRef();
            for (QualifiedKey q : kjvVerses) {
                out.println(String.format("\t(%s) %s => (KJV) %s",
                        nonKjvName,
                        osisRef,
                        q.toString()));
            }
        }

        out.println("    ******************************");
        out.println("    Absent verses in left versification:");
        out.println(String.format("\t[%s]", this.absentVerses.getOsisRef()));
        out.println("    ******************************");
        out.println("    Backwards mappings from KJV");
        out.println("    ******************************");
        for (Map.Entry<QualifiedKey, Passage> entry : this.fromKJVMappings.entrySet()) {
            out.println(String.format("\t(KJV): %s => (%s) %s",
                    entry.getKey().toString(),
                    nonKjvName,
                    entry.getValue().getOsisRef()));
        }
    }

    /**
     * Returns whether we initialised with errors
     */
    boolean hasErrors() {
        return hasErrors;
    }

    /** Simplify creation of an empty passage object of the default type, with the required v11n.
     * 
     * @param versification required v11n for new Passage
     * @return              empty Passage
     */
    private Passage createEmptyPassage(Versification versification) {
        return new RangedPassage(versification);
    }

    /* the 'from' or 'left' versification */
    private Versification nonKjv;

    /* the absent verses, i.e. those present in the KJV, but not in the left versification */
    private Passage absentVerses;
    private Map<VerseKey, List<QualifiedKey>> toKJVMappings;
    private Map<QualifiedKey, Passage> fromKJVMappings;
    private boolean zerosUnmapped;
    private boolean hasErrors;

    private static final Versification KJV = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);
    private static final Logger LOGGER = LoggerFactory.getLogger(VersificationToKJVMapper.class);
}
