
package org.crosswire.jsword.book.search.ser;

import org.crosswire.common.util.I18NBase;

/**
 * Compile safe I18N resource settings.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class I18N extends I18NBase
{
    public static final I18N ADD_BLANK = new I18N("Syntax Error: No word to search for.");

    public static final I18N MISSED = new I18N("Search Failed.");
    public static final I18N RIGHT_PARAM = new I18N("Can't get a word from a sub-expression.");
    public static final I18N RIGHT_BRACKETS = new I18N("Syntax Error: Can't use brackets as a command.");
    public static final I18N LEFT_PARAM = new I18N("Can't get a word from a sub-expression.");
    public static final I18N LEFT_BRACKETS = new I18N("Syntax Error: Unmatching brackets.");
    public static final I18N STARTS_WORD = new I18N("Can't get a word from a startswith command.");
    public static final I18N STARTS_BLANK = new I18N("Syntax Error: No word to search for.");
    public static final I18N STARTS_OTHER = new I18N("Search Error.");
    public static final I18N RETAIN_BLANK = new I18N("Syntax Error: No word to search for.");
    public static final I18N REMOVE_BLANK = new I18N("Syntax Error: No word to search for.");
    public static final I18N GRAMMAR_WORD = new I18N("Can't get a word from a grammar command.");
    public static final I18N GRAMMAR_BLANK = new I18N("Syntax Error: No word to search for.");
    public static final I18N GRAMMER_OTHER = new I18N("Search failed.");
    public static final I18N DEFAULT_OTHER = new I18N("An error occured whilst searching for \"{0}\".");
    public static final I18N BLUR_BLANK = new I18N("Syntax Error: Missing number, nothing to blur by.");
    public static final I18N BLUR_FORMAT = new I18N("Can't understand \"{0}\" as a number.");
    public static final I18N ENGINE_SYNTAX = new I18N("Syntax Error: Invalid command \"{0}\".");
    public static final I18N ILLEGAL_PASSAGE = new I18N("Syntax Error: Invalid passage \"{0}\"");
    public static final I18N UNMATCHED_ESCAPE = new I18N("Syntax Error: Unmatched brackets - [ and ]");

    public static final I18N INITIALIZE = new I18N("Error initializing.");
    public static final I18N READ_ERROR = new I18N("Read Error.");
    public static final I18N WRITE_ERROR = new I18N("Write Error.");

    /** Initialise any resource bundles */
    static
    {
        init(I18N.class.getName());
    }

    /** Passthrough ctor */
    private I18N(String name)
    {
        super(name);
    }
}
