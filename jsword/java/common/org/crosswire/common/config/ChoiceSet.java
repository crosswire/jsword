
package org.crosswire.common.config;

import java.util.*;

/**
* A ConfigExtender represents a set of Choices that fit together
* in a sensible group.
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
public abstract class ChoiceSet
{
    /**
    * Accessor for the set of Choices
    */
    public abstract Hashtable getChoices();

    /**
    *
    */
    public void setConfig(Config config)
    {
        this.config = config;
    }

    /**
    * Add all the key, model pairs in this hashtable
    */
    public void add()
    {
        Hashtable hash = getChoices();

        Enumeration en = hash.keys();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            Choice model = (Choice) hash.get(key);
            config.add(key, model);
        }
    }

    /**
    * Add all the key, model pairs in this hashtable
    */
    public void remove()
    {
        Hashtable hash = getChoices();

        Enumeration en = hash.keys();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            config.remove(key);
        }
    }

    /** The config that we are talking to */
    protected Config config;
}
