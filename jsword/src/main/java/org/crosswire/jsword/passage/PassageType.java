/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import java.io.Serializable;

/**
 * Types of Passage optimizations.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class PassageType implements Serializable
{
    /**
     * Optimize the Passage for speed
     */
    public static final PassageType SPEED = new PassageType("SPEED") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createPassage(java.lang.String)
         */
        public Passage createPassage(String passage) throws NoSuchVerseException
        {
            if (passage == null || passage.length() == 0)
            {
                return createEmptyPassage();
            }
            return new RocketPassage(passage);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createEmptyPassage()
         */
        public Passage createEmptyPassage()
        {
            return new RocketPassage();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -5432599732858220775L;
    };

    /**
     * Optimize the Passage for write speed
     */
    public static final PassageType WRITE_SPEED = new PassageType("WRITE_SPEED") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createPassage(java.lang.String)
         */
        public Passage createPassage(String passage) throws NoSuchVerseException
        {
            if (passage == null || passage.length() == 0)
            {
                return createEmptyPassage();
            }
            return new BitwisePassage(passage);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createEmptyPassage()
         */
        public Passage createEmptyPassage()
        {
            return new BitwisePassage();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -8808127396341614058L;
    };

    /**
     * Optimize the Passage for size
     */
    public static final PassageType SIZE = new PassageType("SIZE") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createPassage(java.lang.String)
         */
        public Passage createPassage(String passage) throws NoSuchVerseException
        {
            if (passage == null || passage.length() == 0)
            {
                return createEmptyPassage();
            }
            return new DistinctPassage(passage);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createEmptyPassage()
         */
        public Passage createEmptyPassage()
        {
            return new DistinctPassage();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -1959355535575121168L;
    };

    /**
     * Optimize the Passage for a mix
     */
    public static final PassageType MIX = new PassageType("MIX") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createPassage(java.lang.String)
         */
        public Passage createPassage(String passage) throws NoSuchVerseException
        {
            if (passage == null || passage.length() == 0)
            {
                return createEmptyPassage();
            }
            return new PassageTally(passage);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createEmptyPassage()
         */
        public Passage createEmptyPassage()
        {
            return new PassageTally();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -8426713571411491868L;
    };

    /**
     * Optimize the Passage for tally operations
     */
    public static final PassageType TALLY = new PassageType("TALLY") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createPassage(java.lang.String)
         */
        public Passage createPassage(String passage) throws NoSuchVerseException
        {
            if (passage == null || passage.length() == 0)
            {
                return createEmptyPassage();
            }
            return new PassageTally(passage);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageType#createEmptyPassage()
         */
        public Passage createEmptyPassage()
        {
            return new PassageTally();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -4148688085074351220L;
    };

    /**
     * Simple ctor
     */
    public PassageType(String name)
    {
        this.name = name;
    }

    /**
     * Create an optimized passage
     * @param passage
     * @return the optimized passage
     * @throws NoSuchVerseException
     */
    public abstract Passage createPassage(String passage) throws NoSuchVerseException;

    /**
     * Create an empty, optimized passage
     * @return the optimized, empty passage
     * @throws NoSuchVerseException
     */
    public abstract Passage createEmptyPassage();

    /**
     * Lookup method to convert from a String
     */
    public static PassageType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            PassageType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static PassageType fromInteger(int i)
    {
        // on error return SPEED
        if (i < 0 || i >= VALUES.length)
        {
            return SPEED;
        }
        return VALUES[i];
    }

    /**
     * Lookup method to convert from an integer
     */
    public static int toInteger(PassageType type)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            PassageType o = VALUES[i];
            if (o.equals(type))
            {
                return i;
            }
        }
        // cannot get here
        assert false;
        return 0; // SPEED
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the PassageListType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final PassageType[] VALUES =
    {
        SPEED,
        WRITE_SPEED,
        SIZE,
        MIX,
        TALLY,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 1678142015407980515L;
}
