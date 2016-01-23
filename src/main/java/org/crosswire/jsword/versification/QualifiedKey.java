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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * A QualifiedKey represents the various left and right sides of a map entry.
 * <p>
 * The QualifiedKey is Qualified:
 * </p>
 * <ul>
 * <li><strong>DEFAULT</strong> - This QualifiedKey is either a Verse or a VerseRange.</li>
 * <li><strong>ABSENT_IN_KJV</strong> - This QualifiedKey has a section name for what is absent in the KJV (the right hand of the map entry).</li>
 * <li><strong>ABSENT_IN_LEFT</strong> - This QualifiedKey has no other content.</li>
 * </ul>
 * <p>
 * The mapping can indicate a part of a verse. This is an internal implementation detail of the Versification mapping code.
 * Here it is used to distinguish one QualifiedKey from another in equality tests and in containers.
 * </p>
 *
 * @author Chris Burrell
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
public final class QualifiedKey {
    /**
     * A Qualifier indicates whether the verse is numbered the same in both the KJV and the other, is missing in the KJV or the other.
     */
    enum Qualifier {
        /**
         * The DEFAULT Qualifier indicates a Verse or a VerseRange.
         */
        DEFAULT {
            @Override
            public String getDescription(QualifiedKey q) {
                return "";
            }
        },
        /**
         * The ABSENT_IN_LEFT Qualifier indicates that the left side of the map has no equivalent on the right (KJV).
         */
        ABSENT_IN_LEFT {
            @Override
            public String getDescription(QualifiedKey q) {
                return "Absent in Left";
            }
        },
        /**
         * The ABSENT_IN_KJV Qualifier indicates that the right (KJV) side of the map has no equivalent on the left.
         */
        ABSENT_IN_KJV {
            @Override
            public String getDescription(QualifiedKey q) {
                return q != null && q.getSectionName() != null ? q.getSectionName() : "Missing section name";
            }
        };

        /**
         * @param q the QualifiedKey that this describes
         * @return The description for the qualified key
         */
        public abstract String getDescription(QualifiedKey q);

    }

    /**
     * Construct a QualifiedKey from a Verse.
     *
     * @param key the verse from which to create this QualifiedKey
     */
    protected QualifiedKey(Verse key) {
        setKey(key);
        this.absentType = Qualifier.DEFAULT;
    }

    /**
     * Construct a QualifiedKey from a Verse.
     *
     * @param key the verse range from which to create this QualifiedKey
     */
    public QualifiedKey(VerseRange key) {
        setKey(key);
        this.absentType = Qualifier.DEFAULT;
    }

    /**
     * @param sectionName with a given section name, we assume absent in KJV
     */
    public QualifiedKey(String sectionName) {
        this.sectionName = sectionName;
        this.absentType = Qualifier.ABSENT_IN_KJV;
    }

    /**
     * Constructs the QualifiedKey with the ABSENT_IN_LEFT qualifier.
     * This really means that there are no fields in this QualifiedKey.
     */
    public QualifiedKey() {
        this.absentType = Qualifier.ABSENT_IN_LEFT;
    }

    /**
     * Create a QualifiedKey from a Verse or a VerseRange.
     *
     * @param k the Verse or VerseRange
     * @return the created QualifiedKey
     * @throws ClassCastException
     */
    public static QualifiedKey create(VerseKey k) {
        return k instanceof Verse ? new QualifiedKey((Verse) k) : new QualifiedKey((VerseRange) k);
    }

    /**
     * @return * The internal key which is either a Verse or VerseRange
     */
    public VerseKey getKey() {
        return wholeKey;
    }

    /**
     * @return * The internal key cast as a Verse
     * @throws ClassCastException
     */
    public Verse getVerse() {
        return (Verse) wholeKey;
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
     * A QualifiedKey is whole if it does not split part of a reference.
     *
     * @return whether this QualifiedKey has a whole reference
     */
    public boolean isWhole() {
        // If the reference is null, then it cannot be part or whole.
        // But we say it is whole because the calls to this are really testing
        // to see if it is a part.
        return qualifiedKey == null || qualifiedKey.isWhole();
    }

    /**
     * Convert this QualifiedKey from one Versification to another.
     * This is a potentially dangerous operation that does no mapping
     * from one versification to another. Use it only when it is known
     * to be safe.
     *
     * @param target The target versification
     * @return The reversified QualifiedKey
     */
    public QualifiedKey reversify(Versification target) {
        // Only if it has a qualified key can it be reversified
        if (this.qualifiedKey == null) {
            return this;
        }

        final VerseKey reversifiedKey = qualifiedKey.reversify(target);
        if (reversifiedKey != null) {
            return create(reversifiedKey);
        }

        if (target.getName().equals(Versifications.DEFAULT_V11N)) {
            //then we're absent in KJV
            return new QualifiedKey(qualifiedKey.getOsisID());
        }
        return new QualifiedKey();

    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (wholeKey != null) {
            buf.append(qualifiedKey.getOsisRef());
        }
        String desc = absentType.getDescription(this);
        if (desc.length() > 0) {
            if (buf.length() > 0) {
                buf.append(": ");
            }
            buf.append(absentType.getDescription(this));
        }
        return buf.toString();
    }

    @Override
    public int hashCode() {
        // Use a prime number in case one of the values is not around
        return (this.qualifiedKey == null ? 17 : qualifiedKey.hashCode())
                + (this.absentType == null ? 13 : this.absentType.ordinal())
                + (this.sectionName == null ? 19 : this.sectionName.hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof QualifiedKey) {
            final QualifiedKey otherKey = (QualifiedKey) obj;
            return this.getAbsentType() == otherKey.getAbsentType()
                    && bothNullOrEqual(this.sectionName, otherKey.sectionName)
                    && bothNullOrEqual(this.qualifiedKey, otherKey.qualifiedKey);
        }
        return false;
    }

    /**
     * Allow override of the key, particular useful if we're constructing in 2 stages like the offset mechanism
     *
     * @param key the new key
     */
    private void setKey(final Verse key) {
        this.qualifiedKey = key;
        this.wholeKey = key.getWhole();
    }

    /**
     * Allow override of the key, particular useful if we're constructing in 2 stages like the offset mechanism
     *
     * @param key the new key
     */
    private void setKey(final VerseRange key) {
        if (key.getCardinality() == 1) {
            this.qualifiedKey = key.getStart();
        } else {
            this.qualifiedKey = key;
        }
        this.wholeKey = this.qualifiedKey.getWhole();
    }

    /**
     * Determine whether two objects are equal, allowing nulls
     *
     * @param x
     * @param y
     * @return true if both are null or the two are equal
     */
    private static boolean bothNullOrEqual(Object x, Object y) {
        return x == y || (x != null && x.equals(y));
    }

    private VerseKey qualifiedKey;
    private VerseKey wholeKey;
    private String sectionName;
    private Qualifier absentType;

}
