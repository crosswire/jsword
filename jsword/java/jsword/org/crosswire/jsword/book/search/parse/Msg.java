
package org.crosswire.jsword.book.search.parse;

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
    protected static final Msg ADD_BLANK = new Msg("Syntax Error: No word to search for.");

    protected static final Msg MISSED = new Msg("Search Failed.");
    protected static final Msg RIGHT_PARAM = new Msg("Can't get a word from a sub-expression (processing ')').");
    protected static final Msg RIGHT_BRACKETS = new Msg("Syntax Error: Can't use brackets as a command.");
    protected static final Msg LEFT_PARAM = new Msg("Can't get a word from a sub-expression (processing '(').");
    protected static final Msg LEFT_BRACKETS = new Msg("Syntax Error: Unmatching brackets.");
    protected static final Msg STARTS_WORD = new Msg("Can't get a word from a startswith command.");
    protected static final Msg STARTS_BLANK = new Msg("Syntax Error: No word to search for. (processing 'blank')");
    protected static final Msg STARTS_OTHER = new Msg("Search Error.");
    protected static final Msg RETAIN_BLANK = new Msg("Syntax Error: No word to search for. (processing 'retains')");
    protected static final Msg REMOVE_BLANK = new Msg("Syntax Error: No word to search for. (processing 'remove')");
    protected static final Msg GRAMMAR_WORD = new Msg("Can't get a word from a grammar command.");
    protected static final Msg GRAMMAR_BLANK = new Msg("Syntax Error: No word to search for. (processing 'grammar')");
    protected static final Msg GRAMMER_OTHER = new Msg("Search failed.");
    protected static final Msg DEFAULT_OTHER = new Msg("An error occured whilst searching for \"{0}\".");
    protected static final Msg BLUR_BLANK = new Msg("Syntax Error: Missing number, nothing to blur by.");
    protected static final Msg BLUR_FORMAT = new Msg("Can't understand \"{0}\" as a number.");
    protected static final Msg ENGINE_SYNTAX = new Msg("Syntax Error: Invalid command \"{0}\".");
    protected static final Msg ILLEGAL_PASSAGE = new Msg("Syntax Error: Invalid passage \"{0}\"");
    protected static final Msg UNMATCHED_ESCAPE = new Msg("Syntax Error: Unmatched brackets - [ and ]");

    /** Initialise any resource bundles */
    static
    {
        init(Msg.class.getName());
    }

    /** Passthrough ctor */
    private Msg(String name)
    {
        super(name);
    }
}
