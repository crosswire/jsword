package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.Key;

/**
 * Wraps around a key, and allows a specified to distinguish between different mappings
 *
 * @author chrisburrell
 */
public class QualifiedKey {
    public static enum Qualifier {DEFAULT, ABSENT_IN_LEFT, ABSENT_IN_KJV}
    private String sectionName;
    private String part;
    private Key key;
    //we use the null character here to avoid boxing/unboxing a Character all the time. A slightly smaller
    //memory foot-print.
    private Qualifier absentType = Qualifier.DEFAULT;

    /**
     * Constructs the Qualified key, leaving the qualifier as set to the null character.
     *
     * @param key the key to be wrapped
     */
    public QualifiedKey(Key key) {
        this.key = key;
    }

    /**
     * Constructs the Qualified key, leaving the qualifier as set to the null character, but specifying the part.
     *
     * @param key the key to be wrapped
     * @param part the part associated with a key, often null ; used for patching across the KJV versification which not
     *             support the breakdown.
     */
    public QualifiedKey(Key key, String part) {
        this.key = key;
        this.part = part;
    }

    /**
     * @param sectionName with a given section name, we assume absent in KJV
     */
    public QualifiedKey(String sectionName) {
        this.sectionName = sectionName;
        this.absentType = Qualifier.ABSENT_IN_KJV;
    }

    /**
     * Constructs the Qualified key, leaving the qualifier as set to the null character.
     *
     * @param absentType the qualifier indicating if it is absent from the left text, or the KJV text.
     */
    public QualifiedKey(Qualifier absentType) {
        this.absentType = absentType;
    }

    /**
     * @return * The internal key
     */
    public Key getKey() {
        return key;
    }

    /**
     * @return the type of the unknown qualifier
     */
    public Qualifier getAbsentType() {
        return absentType;
    }

    /**
     * @return the name (any name) of the section represented within the KJV
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @return the part associated with a verse
     */
    public String getPart() {
        return part;
    }

    /**
     * Allow override of the key, particular useful if we're constructing in 2 stages like the offset mechanism
     * @param key the new key
     */
    public void setKey(final Key key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        //use a prime number in case one of the values is not around
        return (this.key == null ? 17 : key.hashCode()) +
                (this.absentType == null ? 13 : this.absentType.ordinal()) +
                (this.sectionName == null ? 19 : this.sectionName.hashCode()) +
                (this.getPart() == null ? 23 : this.getPart().hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof QualifiedKey) {
            final QualifiedKey otherKey = (QualifiedKey) obj;

            //purposefully inlining the various checks, because we do want to avoid doing too many comparisons
            //when using QualifiedKey in a hash map/set, so placing the expensive equals() nearer the end.
            return this.getAbsentType() == otherKey.getAbsentType() &&
                    (this.part == null ? otherKey.part == null : this.part.equals(otherKey.part)) &&
                    (sectionName == null ? otherKey.sectionName == null : sectionName.equals(otherKey.sectionName)) &&
                    (this.key == null ? otherKey.key == null : this.key.equals(otherKey.key));
        }
        return false;
    }
}
