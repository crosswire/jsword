package org.crosswire.jsword.book.basic;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Msg extends MsgBase
{
    static final Msg DRIVER_READONLY = new Msg("This Book is read-only."); //$NON-NLS-1$
    static final Msg DELETE_NOTIMPL = new Msg("Sorry delete is not implemented yet.\n Some Bible names are simply the names of the directories in which they live.\n So you can manually delete the directory \"JSWORD/versions/{0}\""); //$NON-NLS-1$
    static final Msg NO_VERSE = new Msg("Invalid reference."); //$NON-NLS-1$
    static final Msg FILTER_FAIL = new Msg("Filtering input data failed."); //$NON-NLS-1$
    static final Msg VERIFY_START = new Msg("Copying Bible data to new driver"); //$NON-NLS-1$
    static final Msg VERIFY_VERSES = new Msg("Checking Verses"); //$NON-NLS-1$
    static final Msg VERIFY_VERSE = new Msg("Verse: "); //$NON-NLS-1$
    static final Msg VERIFY_PASSAGES = new Msg("Checking Passages"); //$NON-NLS-1$
    static final Msg VERIFY_WORDS = new Msg("Checking Words"); //$NON-NLS-1$
    static final Msg WORD = new Msg("Word: "); //$NON-NLS-1$

    /* From LocalURL*
    static final Msg DRIVER_READONLY = new Msg("This Book is read-only.");
    static final Msg CREATE_NOBIBLE = new Msg("Can't create a Bible from a non-Bible source");
    static final Msg DELETE_FAIL = new Msg("Failed to delete Book '{0}'");
    static final Msg FLUSH_FAIL = new Msg("Failed to write data.");
    static final Msg CREATE_FAIL = new Msg("Failed to create Book.");
    static final Msg IO_FAIL = new Msg("IO Failure.");
    */

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
