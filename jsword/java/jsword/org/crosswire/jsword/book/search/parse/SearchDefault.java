package org.crosswire.jsword.book.search.parse;

import java.util.HashMap;
import java.util.Map;

/**
 * A SearchDefault is a utility class for the Search package. 
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
public class SearchDefault
{
    /**
     * Ensure that we can not be instansiated
     */
    private SearchDefault()
    {
    }

    /**
     * The Search Hashtable
     * @return The default search hash
     */
    public static Map getMap()
    {
        return command;
    }

    /**
     * The Search Hashtable
     */
    public static void setMap(Map value)
    {
        command = value;
    }

    /**
     * The Search Hashtable
     */
    private static Map command = new HashMap();

    /**
     * Initialiser
     */
    static
    {
        // "/ | & + , - ~ ( ) [ ] sw startswith gr grammar"
        command.put("/", new AddCommandWord()); //$NON-NLS-1$
        command.put("|", new AddCommandWord()); //$NON-NLS-1$
        command.put("&", new RetainCommandWord()); //$NON-NLS-1$
        command.put("+", new RetainCommandWord()); //$NON-NLS-1$
        command.put(",", new RetainCommandWord()); //$NON-NLS-1$
        command.put("-", new RemoveCommandWord()); //$NON-NLS-1$
        command.put("~", new BlurCommandWord()); //$NON-NLS-1$
        command.put("(", new SubLeftParamWord()); //$NON-NLS-1$
        command.put(")", new SubRightParamWord()); //$NON-NLS-1$
        command.put("[", new PassageLeftParamWord()); //$NON-NLS-1$
        command.put("]", new PassageRightParamWord()); //$NON-NLS-1$
        command.put("sw", new StartsParamWord()); //$NON-NLS-1$
        command.put("startswith", new StartsParamWord()); //$NON-NLS-1$
        command.put("gr", new GrammarParamWord()); //$NON-NLS-1$
        command.put("grammar", new GrammarParamWord()); //$NON-NLS-1$
    }
}
