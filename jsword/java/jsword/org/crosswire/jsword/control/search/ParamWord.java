
package org.crosswire.jsword.control.search;

import org.crosswire.jsword.passage.Passage;

/**
* A ParamWord extends SearchWord to provide more information
* to a CommandWord. This will either be in the form of a String
* or in the form of a Passage (from a search)
* ParamWords are used by CommandWords that alter the final
* Passage.
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
public interface ParamWord extends SearchWord
{
    /**
    * Get a word for something else to word on.
    * @param engine The controller that can provide access to the search
    *               string or a default Bible.
    * @return The requested text
    * @exception SearchException If this action is not appropriate
    */
    public String getWord(Engine engine) throws SearchException;

    /**
    * Get a Passage or throw-up if that is not appropriate
    * for this Word.
    * @param engine The controller that can provide access to the search
    *               string or a default Bible.
    * @return A Passage relevant to this command
    * @exception SearchException If this action is not appropriate
    */
    public Passage getPassage(Engine engine) throws SearchException;
}
