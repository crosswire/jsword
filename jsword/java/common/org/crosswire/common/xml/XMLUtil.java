package org.crosswire.common.xml;

import org.crosswire.common.util.Logger;
import org.xml.sax.Attributes;

/**
 * Utilities for working with SAX XML parsing.
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
public class XMLUtil
{
    /**
     * Prevent Instansiation
     */
    private XMLUtil()
    {
    }

    /**
     * Show the attributes of an element as debug
     */
    public static void debugSAXAttributes(Attributes attrs)
    {
        for (int i=0; i<attrs.getLength(); i++)
        {
            log.debug("attr["+i+"]: "+attrs.getQName(i)+"="+attrs.getValue(i));
        }
    }

    /**
     * A parse has failed so we can try to kill the entities and then have
     * another go.
     */
    public static String guessKillEntities(String broken)
    {
        String working = broken;

        allEntities:
        while (true)
        {
            int amp = working.indexOf('&');

            // If there are no more amps then we are done
            if (amp == -1)
            {
                break allEntities;
            }

            // Check for chars that should not be in an entity name
            int i = amp;
            singleEntity:
            while (true)
            {
                if (i >= working.length())
                {
                    break singleEntity;
                }

                char c = working.charAt(i);

                if (c == ';')
                {
                    log.warn("disguarding potentially valid entity: "+working.substring(amp, i));
                    working = working.substring(0, amp)+working.substring(i);
                    break singleEntity;
                }

                if (!Character.isLetterOrDigit(c) && c != '-')
                {
                    log.debug("disguarding invalid entity: "+working.substring(amp, i));
                    working = working.substring(0, amp)+working.substring(i);
                    break singleEntity;
                }

                i++;
            }
        }
        
        return working;
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(XMLUtil.class);
}
