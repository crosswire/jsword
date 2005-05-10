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
 * ID: $Id$
 */
package org.crosswire.jsword.book.raw;

/**
 * Various constants for RawBooks.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawConstants
{
    static final String SIG_PARA_INST = "RAW:AI"; //$NON-NLS-1$
    static final String SIG_PUNC_INST = "RAW:PI"; //$NON-NLS-1$
    static final String SIG_CASE_INST = "RAW:CI"; //$NON-NLS-1$
    static final String SIG_WORD_INST = "RAW:WI"; //$NON-NLS-1$

    static final String SIG_PUNC_ITEM = "RAW:PR"; //$NON-NLS-1$
    static final String SIG_WORD_ITEM = "RAW:WR"; //$NON-NLS-1$

    static final String FILE_PARA_INST = "parainst.idx"; //$NON-NLS-1$
    static final String FILE_PUNC_INST = "puncinst.idx"; //$NON-NLS-1$
    static final String FILE_CASE_INST = "caseinst.idx"; //$NON-NLS-1$
    static final String FILE_WORD_INST = "wordinst.idx"; //$NON-NLS-1$

    static final String FILE_PUNC_ITEM = "punc.idx"; //$NON-NLS-1$
    static final String FILE_WORD_ITEM = "word.idx"; //$NON-NLS-1$

    static final String FILE_BIBLE_PROPERTIES = "bible.properties"; //$NON-NLS-1$
    
    /**
     * Prevent instansiation
     */
    private RawConstants()
    {
    }
}
