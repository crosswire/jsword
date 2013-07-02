package org.crosswire.jsword.versification;

import org.crosswire.common.util.KeyValuePair;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * TODO: think about whether when returning, we should clone, or make things immutable.
 * <p/>
 * A Versification mapper allows you to a map a given verse to the KJV versification,
 * or unmap it from the KJV versification into your own versification.
 * <p/>
 * A properties-like file will contain the non-KJV versification as they key, and the KJV versification value
 * as the target value... Duplicate keys are allowed.
 * <p/>
 * i.e. Gen.1.1=Gen.1.2 means Gen.1.1 in X versification is Gen.1.2 in the KJV versification
 * <p/>
 * You can specify a range on either side. If a range is present on both sides, they have to have the same number of
 * verses, i.e. verses are mapped verse by verse to each other
 * Gen.1.1-2=Gen.1.2-3 means Gen.1.1=Gen.1.2 and Gen.1.2=Gen.1.3
 * <p/>
 * Mappings can be specified by offset. In this case, be aware this maps verse 0 as well. So for example:
 * <p/>
 * Ps.19-20=-1 means Ps.19.0=doesn't map, Ps.19.1=Ps.19.1, Ps.19.2=Ps.19.1, etc.
 * It does not make much sense to have an offset for a single verse, so this is not supported.
 * Offsetting for multiple ranges however does, and operates range by range, i.e. each range is calculated separately.
 * <p/>
 * You can use part-mappings. This is important if you want to preserve transformations from one side to another without
 * losing resolution of the verse.
 * <p/>
 * For example,
 * if V1 defines Gen.1.1=Gen1.1, Gen1.2=Gen1.1
 * if V2 defines Gen.1.1=Gen1.1, Gen.1.2=Gen.1.1
 * then, mapping from V1=>KJV and KJV=>V2 gives you Gen.1.1=>Gen.1.1=>Gen.1.1-2 which is inaccurate if in fact
 * V1(Gen.1.1) actually equals V2(Gen.1.1). So instead, we use a split on the right hand-side:
 * <p/>
 * For example,
 * V1 defines Gen.1.1=Gen1.1@a, Gen1.2=Gen1.1@b
 * V2 defines Gen.1.1=Gen1.1@a, Gen.1.2=Gen.1.1@b
 * then, mapping from V1=>KJV and KJV=>V2 gives you Gen.1.1=>Gen.1.1a=>Gen.1.1, which is now accurate.
 * A part is a string fragment placed after the end of a key reference. We cannot use # because that is commonly known
 * as a comment in real properties-file. Using a marker, means we can have meaningful part names if we so choose.
 * Parts of ranges are not supported.
 * <p/>
 * Note: splits should never be seen by a user. The mapping from one versification to another is abstracted
 * such that the user can simply request the mapping between 2 verse (ranges).
 * <p/>
 * Unmapped verses can be specified by inventing ids, either for whole sections, or verse by verse (this would
 * come in handy if two versifications have the same content, but the KJV doesn't). A section must be preceded
 * with a '?' character indicating that there will be no need to try and look up a reference.
 * Gen.1.1=?NewPassage
 * <p/>
 * Since not specifying a verse mappings means there is a 1-2-1 unchanged mapping, we need a way of specifying
 * absent verses altogether:
 * ?=Gen1.1;Gen.1.5;
 * means that the non-KJV book simply does not contain verses Gen.1.1 and Gen.1.5 and therefore can't
 * be mapped.
 *
 * @author chrisburrell
 */
public class VersificationToKJVMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersificationToKJVMapper.class);
    private static final Versification KJV = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);
    public static final char PART_MARKER = '@';

    /* the 'from' or 'left' versification */
    private Versification nonKjv;

    /* the absent verses, i.e. those present in the KJV, but not in the left versification */
    private Key absentVerses = PassageKeyFactory.instance().createEmptyKeyList(KJV);
    private Map<Key, List<QualifiedKey>> toKJVMappings = new HashMap<Key, List<QualifiedKey>>();
    private Map<QualifiedKey, Key> fromKJVMappings = new HashMap<QualifiedKey, Key>();

    /**
     * @param mapping the mappings from one versification to another
     */
    public VersificationToKJVMapper(Versification nonKjv, final FileVersificationMapping mapping) {
        this.nonKjv = nonKjv;
        processMappings(mapping);
        trace();
    }

    /**
     * We getRange properties to their real meanings. This is the crux of the decoding facility.
     *
     * @param mappings the input mappings, in a contracted, short-hand form
     * @return the properties expanded
     */
    private void processMappings(FileVersificationMapping mappings) {
        final List<KeyValuePair> entries = mappings.getMappings();
        for (KeyValuePair entry : entries) {
            try {
                processEntry(entry);
            } catch (NoSuchKeyException ex) {
                LOGGER.error("Unable to process entry [{}] with value [{}]", entry.getKey(), entry.getValue(), ex);
            }
        }
    }

    private void processEntry(final KeyValuePair entry) throws NoSuchKeyException {
        String leftHand = (String) entry.getKey();
        String kjvHand = (String) entry.getValue();
        QualifiedKey left = getRange(this.nonKjv, leftHand, null);
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
            add1ToManyMappings(leftHand.getKey(), kjvVerses);
        } else {
            addManyToMany(leftHand, kjvVerses);
        }
    }

    /**
     * Adds a many to many mapping, mappings all the verses on the left hand side to all the verses on the right hand side.
     * We support 2 types: Many-to-1 and Many-to-Many.
     *
     * @param leftHand, is assumed to be many
     * @param kjvVerses could be 1 or many
     */
    private void addManyToMany(final QualifiedKey leftHand, final QualifiedKey kjvVerses) {
        Iterator<Key> leftKeys = leftHand.getKey().iterator();

        boolean isKJVMany = kjvVerses.getAbsentType() != QualifiedKey.Qualifier.ABSENT_IN_KJV && kjvVerses.getKey().getCardinality() != 1;
        Iterator<Key> kjvKeys = null;

        while (leftKeys.hasNext()) {
            final Key leftKey = leftKeys.next();

            if (isKJVMany) {
                if (kjvKeys == null) {
                    kjvKeys = kjvVerses.getKey().iterator();
                }
                QualifiedKey kjvKey = new QualifiedKey(kjvKeys.next());
                addForwardMappingFromSingleKeyToRange(leftKey, kjvKey);
                addKJVToMapping(kjvKey, leftKey);

            } else {
                addForwardMappingFromSingleKeyToRange(leftKey, kjvVerses);
                addKJVToMapping(kjvVerses, leftKey);
            }
        }
    }

    /**
     * If leftKey is non-null (i.e. not attached to a simple specifier, then adds to the kjvTo mappings
     *
     * @param kjvVerses the kjv verses
     * @param leftKey   the left-hand key, possibly null.
     */
    private void addKJVToMapping(final QualifiedKey kjvVerses, final Key leftKey) {
        if (leftKey != null) {
            getNonEmptyKey(this.fromKJVMappings, kjvVerses).addAll(leftKey);
        }
    }

    /**
     * A simple two way entry between 2 1-1 entries.
     *
     * @param leftHand the verse on the left side, left is assumed to be 1 verse only
     * @param kjvHand  the KJV reference
     * @throws NoSuchVerseException
     */
    private void add1ToManyMappings(final Key leftHand, final QualifiedKey kjvHand) throws NoSuchVerseException {
        addForwardMappingFromSingleKeyToRange(leftHand, kjvHand);
        addReverse1ToManyMappings(leftHand, kjvHand);
    }

    /**
     * Adds the data into the reverse mappings. Caters for 1-2-1 and 1-2-Many mappings
     *
     * @param leftHand the reference of the left hand reference
     * @param kjvHand  the kjv reference/key, qualified with the part
     */
    private void addReverse1ToManyMappings(final Key leftHand, final QualifiedKey kjvHand) {
        //add the reverse mapping, for 1-1 mappings
        if (kjvHand.getAbsentType() == QualifiedKey.Qualifier.ABSENT_IN_KJV || kjvHand.getKey().getCardinality() == 1) {
            //TODO: deal with parts
            addKJVToMapping(kjvHand, leftHand);
        } else {
            //add the 1-many mappings
            //expand the key and add them all
            //Parts are not supported on ranges...
            Iterator<Key> kjvKeys = kjvHand.getKey().iterator();
            while (kjvKeys.hasNext()) {
                addKJVToMapping(new QualifiedKey(kjvKeys.next()), leftHand);
            }
        }
    }

    /**
     * Adds a forward mappings from left to KJV.
     *
     * @param leftHand the left hand reference (corresponding to a non-kjv versification)
     * @param kjvHand  the kjv reference (with part if applicable).
     */
    private void addForwardMappingFromSingleKeyToRange(final Key leftHand, final QualifiedKey kjvHand) {
        if (leftHand == null) {
            return;
        }

        getNonEmptyMappings(this.toKJVMappings, leftHand).add(kjvHand);
    }

    /**
     * Gets a non-empty key list, either new or the one existing in the map already.
     * <p/>
     *
     * @param mappings the map from key to list of values
     * @param key      the key
     * @return
     */
    private Key getNonEmptyKey(final Map<QualifiedKey, Key> mappings, final QualifiedKey key) {
        Key matchingVerses = mappings.get(key);
        if (matchingVerses == null) {
            matchingVerses = new RocketPassage(this.nonKjv);
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
     * @return
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
     * Expands a reference to all its verses
     *
     * @param versesKey the verses
     * @return the separate list of verses
     *         TODO: cope for '+' and for parts
     */
    private QualifiedKey getRange(final Versification versification, String versesKey) throws NoSuchKeyException {
        return getRange(versification, versesKey, null);
    }

    /**
     * Expands a reference to all its verses
     *
     * @param versesKey the verses
     * @return the separate list of verses
     *         TODO: cope for '+' and for parts
     */
    private QualifiedKey getRange(final Versification versification, String versesKey, Key offsetBasis) throws NoSuchKeyException {
        //deal with absent keys in left & absent keys in right, which are simply marked by a '?'
        if (versesKey == null || versesKey.length() == 0) {
            throw new NoSuchKeyException(JSMsg.gettext("Cannot understand [{0}] as a chapter or verse.", versesKey));
        }

        char firstChar = versesKey.charAt(0);
        switch (firstChar) {
            case '?':
                return getAbsentQualifiedKey(versification, versesKey);
            case '+':
            case '-':
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
    private QualifiedKey getOffsetQualifiedKey(final Versification versification, final String versesKey, Key offsetBasis) throws NoSuchKeyException {
        if (offsetBasis == null || offsetBasis.getCardinality() == 0) {
            //TODO: internationalise
            throw new NoSuchKeyException(JSMsg.gettext("Unable to offset the given key [{}]", offsetBasis));
        }
        int offset = Integer.parseInt(versesKey.substring(1));

        //convert key immediately to the our target versification system, namely the KJV, since it is the only
        //one supported. Convert by ref - since the whole purpose of this is to define equivalents.
        QualifiedKey approximateQualifiedKey = this.getExistingQualifiedKey(versification, offsetBasis.getOsisID());
        Key approximateKey = approximateQualifiedKey.getKey();

        //we now need to apply the offset to our key... So if it's a negative offset, we need to add the keys
        //that occur before the key and therefore blur the key
        Key newKey = null;
        if (approximateKey instanceof VerseRange) {
            newKey = getNewVerseRange(versification, offset, (VerseRange) approximateKey);
        } else if (approximateKey instanceof AbstractPassage) {
            Iterator<Key> rangeIterator = ((AbstractPassage) approximateKey).rangeIterator(RestrictionType.NONE);
            newKey = new RocketPassage(versification);
            while (rangeIterator.hasNext()) {
                final Key nextInRange = rangeIterator.next();
                if (nextInRange instanceof VerseRange) {
                    newKey.addAll(getNewVerseRange(versification, offset, (VerseRange) nextInRange));
                } else {
                    throw new UnsupportedOperationException("Not sure how to parse key of type: " +
                            nextInRange.getClass() + " found in range " + nextInRange);
                }
            }
        }
        approximateQualifiedKey.setKey(newKey);

        //no longer approximate
        return approximateQualifiedKey;
    }

    private VerseRange getNewVerseRange(final Versification versification, final int offset, final VerseRange verseRange) {
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
    private QualifiedKey getExistingQualifiedKey(final Versification versification, final String versesKey) throws NoSuchKeyException {
        //if we have a part, then we extra it. Unless we're mapping whole books, a single alpha character has to signify a part.
        String reference = versesKey;
        String part = null;
        int indexOfPart = versesKey.lastIndexOf(PART_MARKER);
        if(indexOfPart != -1) {
            reference = reference.substring(0, indexOfPart);
            part = versesKey.substring(indexOfPart);
        }
        return new QualifiedKey(PassageKeyFactory.instance().getKey(versification, reference), part);
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
            //we're dealing with a '?', and therefore an ABSENT_IN_LEFT scenarios.
            //we do not support any other ? markers on the left
            return new QualifiedKey(QualifiedKey.Qualifier.ABSENT_IN_LEFT);
        }
        //we're dealing with a ? on the KJV side, therefore we must be looking at
        // a section name
        return new QualifiedKey(versesKey);
    }


    /**
     * Converts the input to the KJV versification
     *
     * @return the equivalent key
     */
    public String map(final String key) throws NoSuchKeyException {
        return map(getRange(nonKjv, key).getKey()).getOsisRef();
    }

    /**
     * Converts the input to the KJV versification, but returns the qualified key representation, i.e. not necessarily
     * an OSIS representation.
     * <p/>
     * This key is useful if different versifications (say Dan 3 in the 2 Catholic versifications) have the same
     * section which is not present in the KJV versification.
     *
     * Its sister method, taking in a Key, and returning a QualifiedKey will be more helpful, generally speaking
     *
     * @return the equivalent key, which may or may not be used to look up a reference in a book.
     */
    public String mapToQualifiedKey(final String key) throws NoSuchKeyException {
        List<QualifiedKey> qualifiedKeys = mapToQualifiedKey(getRange(nonKjv, key).getKey());
        StringBuilder representation = new StringBuilder(128);
        for (int i = 0; i < qualifiedKeys.size(); i++) {
            final QualifiedKey qk = qualifiedKeys.get(i);
            representation.append(qk.getAbsentType() == QualifiedKey.Qualifier.ABSENT_IN_KJV ? qk.getSectionName() : qk.getKey().getOsisRef());

            if(qk.getPart() != null) {
                representation.append(qk.getPart());
            }

            if (i < qualifiedKeys.size() - 1) {
                representation.append(' ');
            }
        }
        return representation.toString();
    }

    /**
     * @return the qualified keys associated with the input key.
     */
    public List<QualifiedKey> mapToQualifiedKey(final Key leftKey) {
        return this.toKJVMappings.get(leftKey);
    }

    /**
     * Converts the input to the KJV versification
     *
     * @return the equivalent key
     */
    public Key map(final Key leftKey) throws NoSuchKeyException {
        //we drop the part when going from left-to-kjv
        List<QualifiedKey> qualifiedKeys = mapToQualifiedKey(leftKey);

        RocketPassage keyList = new RocketPassage(KJV);
        if (qualifiedKeys == null) {
            //then we found no mapping, so we're essentially going to return the same key back...
            return leftKey;
        }

        //if we've found a qualified key, we will either have a absent in kjv, or a key to the kjv
        for (QualifiedKey qualifiedKey : qualifiedKeys) {
            keyList.addAll(qualifiedKey.getKey());
        }

        return keyList;
    }

    /**
     * Converts a KJV verse to the target versification
     *
     * @return the key in the left-hand versification
     */
    public String unmap(final String kjvVerse) throws NoSuchKeyException {
        return unmap(getRange(KJV, kjvVerse).getKey()).getOsisRef();
    }

    /**
     * Converts a KJV verse to the target versification, from a qualified key, rather than a real key
     *
     * @return the key in the left-hand versification
     */
    public Key unmap(final QualifiedKey kjvVerse) {
        //TODO: cope for parts?
        Key left = this.fromKJVMappings.get(kjvVerse);

        //if we have no mapping, then we are in 1 of two scenarios
        //the verse is either totally absent, or the verse is not part of the mappings, meaning it is a straight map
        if (left == null) {
            return this.absentVerses.contains(kjvVerse.getKey()) ? PassageKeyFactory.instance().createEmptyKeyList(KJV) : kjvVerse.getKey();
        }
        return left;
    }

    /**
     * Converts a KJV verse to the target versification
     *
     * @return the key in the left-hand versification
     */
    public Key unmap(final Key kjvVerse) {
        return unmap(new QualifiedKey(kjvVerse));
    }

    /**
     * Outputs the mappings for debug purposes...
     */
    public void trace() {
        if (!LOGGER.isTraceEnabled()) {
            return;
        }

        LOGGER.trace("******************************");
        LOGGER.trace("Forward mappings towards KJV");
        LOGGER.trace("******************************");
        for (Map.Entry<Key, List<QualifiedKey>> entry : this.toKJVMappings.entrySet()) {
            List<QualifiedKey> kjvVerses = entry.getValue();
            for (QualifiedKey q : kjvVerses) {
                LOGGER.trace("\t({}) {} => {}{}{} (KJV)",
                        this.nonKjv.getName(),
                        entry.getKey().getOsisRef(),
                        q.getKey() != null ? q.getKey().getOsisRef() : "",
                        q.getPart() != null ? q.getPart() : "",
                        getStringAbsentType(q));
            }
        }

        LOGGER.trace("Absent verses in left versification: [{}]", this.absentVerses.getOsisRef());
        LOGGER.trace("******************************");
        LOGGER.trace("Backwards mappings from KJV");
        LOGGER.trace("******************************");
        for (Map.Entry<QualifiedKey, Key> entry : this.fromKJVMappings.entrySet()) {
            LOGGER.trace("(KJV): {}{} => {} ({})",
                    entry.getKey().getKey() != null ? entry.getKey().getKey().getOsisRef() : "",
                    getStringAbsentType(entry.getKey()),
                    entry.getValue().getOsisRef(),
                    this.nonKjv.getName());
        }
        LOGGER.trace("##############################");
    }

    /**
     * A string printable version of absent type held in the qualified key
     *
     * @param q the qualified key
     * @return the printable form of the absent type.
     */
    private String getStringAbsentType(QualifiedKey q) {
        String absentType;
        switch (q.getAbsentType()) {
            case ABSENT_IN_KJV:
                absentType = q.getSectionName();
                break;
            case ABSENT_IN_LEFT:
                absentType = "Absent in Left";
                break;
            default:
                absentType = "";
                break;
        }
        return absentType;
    }
}
