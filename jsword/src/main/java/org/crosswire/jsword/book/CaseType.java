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
package org.crosswire.jsword.book;



/**
 * Types of Sentence Case.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum CaseType
{
    LOWER //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.CaseType#setCase(java.lang.String)
         */
        @Override
        public String setCase(String word)
        {
            return word.toLowerCase();
        }
    },

    SENTENCE //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.CaseType#setCase(java.lang.String)
         */
        @Override
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
    },

    UPPER //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.CaseType#setCase(java.lang.String)
         */
        @Override
        public String setCase(String word)
        {
            return word.toUpperCase();
        }
    };

    public abstract String setCase(String word);

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

        // So ...
        return SENTENCE;
    }

    public static CaseType fromInteger(int i)
    {
        for (CaseType t : CaseType.values())
        {
            if (t.ordinal() == i)
            {
                return t;
            }
        }
        return SENTENCE;
    }

}
