/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book;

import java.io.Serializable;

/**
 * Types of Sentence Case.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class CaseType implements Serializable
{
    public static final CaseType LOWER = new CaseType("LOWER") //$NON-NLS-1$
    {
        public String setCase(String word)
        {
            return word.toLowerCase();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3546637707360286256L;
    };

    public static final CaseType SENTENCE = new CaseType("SENTENCE") //$NON-NLS-1$
    {
        public String setCase(String word)
        {
            int index = word.indexOf('-');
            if (index == -1)
            {
                return toSentenceCase(word);
            }

            // So there is a "-", however first some exceptions
            if (word.equalsIgnoreCase("maher-shalal-hash-baz")) //$NON-NLS-1$
            {
                return "Maher-Shalal-Hash-Baz"; //$NON-NLS-1$
            }

            if (word.equalsIgnoreCase("no-one")) //$NON-NLS-1$
            {
                return "No-one"; //$NON-NLS-1$
            }

            if (word.substring(0, 4).equalsIgnoreCase("god-")) //$NON-NLS-1$
            {
                return toSentenceCase(word);
            }

            // So cut by the -
            return toSentenceCase(word.substring(0, index))
                   + "-" + toSentenceCase(word.substring(index + 1)); //$NON-NLS-1$
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3905520510312985138L;
    };

    public static final CaseType UPPER = new CaseType("UPPER") //$NON-NLS-1$
    {
        public String setCase(String word)
        {
            return word.toUpperCase();
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257002163871035698L;
    };

    public static final CaseType MIXED = new CaseType("MIXED") //$NON-NLS-1$
    {
        public String setCase(String word)
        {
            if (word.equalsIgnoreCase("lord's")) //$NON-NLS-1$
            {
                return "LORD's"; //$NON-NLS-1$
            }
            // This should not happen
            throw new IllegalArgumentException(Msg.ERROR_MIXED.toString());
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3546925766555547956L;
    };

    public abstract String setCase(String word);

    /**
     * Simple ctor
     */
    public CaseType(String name)
    {
        this.name = name;
    }

    /**
     * Change to sentence case - ie first character in caps, the rest in lower.
     * @param word The word to be manipulated
     * @return The altered word
     */
    protected static String toSentenceCase(String word)
    {
        assert word != null;

        if (word.equals("")) //$NON-NLS-1$
        {
            return ""; //$NON-NLS-1$
        }

        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    /**
     * What case is the specified word?. A blank word is LOWER, a
     * word with a single upper case letter is SENTENCE and not
     * UPPER - Simply because this is more likely, however TO BE
     * SURE I WOULD NEED TO THE CONTEXT. I could not tell otherwise.
     * <p>The issue here is that getCase("FreD") is undefined. Telling
     * if this is SENTENCE (Tubal-Cain) or MIXED (really the case)
     * is complex and would slow things down for a case that I don't
     * believe happens with Bible text.</p>
     * @param word The word to be tested
     * @return LOWER, SENTENCE, UPPER or MIXED
     * @exception IllegalArgumentException is the word is null
     */
    public static CaseType getCase(String word)
    {
        assert word != null;

        // Blank word
        if (word.equals("")) //$NON-NLS-1$
        {
            return LOWER;
        }

        // Lower case?
        if (word.equals(word.toLowerCase()))
        {
            return LOWER;
        }

        // Upper case?
        // A string length of 1 is no good ('I' or 'A' is sentence case)
        if (word.equals(word.toUpperCase()) && word.length() != 1)
        {
            return UPPER;
        }

        // If initial is lower then it must be mixed
        if (Character.isLowerCase(word.charAt(0)))
        {
            return MIXED;
        }

        // Hack the only real caseMixed is LORD's
        // And we don't want to bother sorting out Tubal-Cain
        // as SENTENCE, so for now ...
        if (word.equals("LORD's")) //$NON-NLS-1$
        {
            return MIXED;
        }

        // So ...
        return SENTENCE;
    }

    /**
     * Get an integer representation for this RestrictionType
     */
    public int toInteger()
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            if (equals(VALUES[i]))
            {
                return i;
            }
        }
        // cannot get here
        assert false;
        return -1;
    }

    /**
     * Lookup method to convert from a String
     */
    public static CaseType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            CaseType o = VALUES[i];
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
    public static CaseType fromInteger(int i)
    {
        return VALUES[i];
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
     * The name of the type
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final CaseType[] VALUES =
    {
        LOWER,
        SENTENCE,
        UPPER,
        MIXED
    };
}
