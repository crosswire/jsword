
package org.crosswire.jsword.control.search;

import java.util.Hashtable;

/**
* A SearchDefault is a utility class for the Search package. 
* 
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
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
    public static Hashtable getHashtable()
    {
        return command;
    }

    /**
    * The Search Hashtable
    * @return The default search hash
    */
    public static void setHashtable(Hashtable value)
    {
        command = value;
    }

    /**
    * The Search Hashtable
    */
    private static Hashtable command = new Hashtable();
    static
    {
        // "/ | & + , - ~ ( ) [ ] sw startswith gr grammar"
        command.put("/", new AddCommandWord());
        command.put("|", new AddCommandWord());
        command.put("&", new RetainCommandWord());
        command.put("+", new RetainCommandWord());
        command.put(",", new RetainCommandWord());
        command.put("-", new RemoveCommandWord());
        command.put("~", new BlurCommandWord());
        command.put("(", new SubLeftParamWord());
        command.put(")", new SubRightParamWord());
        command.put("[", new PassageLeftParamWord());
        command.put("]", new PassageRightParamWord());
        command.put("sw", new StartsParamWord());
        command.put("startswith", new StartsParamWord());
        command.put("gr", new GrammarParamWord());
        command.put("grammar", new GrammarParamWord());
    }
}
