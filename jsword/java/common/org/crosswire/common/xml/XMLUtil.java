package org.crosswire.common.xml;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.DataPolice;
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
    public static String cleanAllEntities(String broken)
    {
        if (broken == null)
        {
            return null;
        }

        String working = broken;

        allEntities: while (true)
        {
            int amp = working.indexOf('&');

            // If there are no more amps then we are done
            if (amp == -1)
            {
                break allEntities;
            }

            // Check for chars that should not be in an entity name
            int i = amp + 1;
            singleEntity: while (true)
            {
                // if we are at the end of the string the disgard from the & on
                if (i >= working.length())
                {
                    DataPolice.report("disguarding unterminated entity: "+working.substring(amp));
                    working = working.substring(0, amp);
                    break singleEntity;
                }

                // if we have come to an ; then we just have an entity that isn't
                // properly declared, (or maybe it is but something else is
                // broken) so disgard it
                char c = working.charAt(i);
                if (c == ';')
                {
                    DataPolice.report("disguarding entity: "+working.substring(amp, i+1));
                    working = working.substring(0, amp)+working.substring(i+1);
                    break singleEntity;
                }

                // XML entities are letters, numbers or -????
                // If we find something else then dump the entity
                if (!Character.isLetterOrDigit(c) && c != '-')
                {
                    DataPolice.report("disguarding invalid entity: "+working.substring(amp, i));
                    working = working.substring(0, amp)+working.substring(i);
                    break singleEntity;
                }

                i++;
            }
        }

        return working;
    }

    /**
     * XML parse failed, so we can try getting rid of all the tags and having
     * another go. We define a tag to start at a &lt; and end at the end of the
     * next word (where a word is what comes in between spaces) that does not
     * contain an = sign, or at a >, whichever is earlier.
     */
    public static String cleanAllTags(String broken)
    {
        if (broken == null)
        {
            return null;
        }

        String working = broken;

        allTags: while (true)
        {
            int lt = working.indexOf('<');

            // If there are no more amps then we are done
            if (lt == -1)
            {
                break allTags;
            }

            // where is the next > symbol?
            int gt = working.indexOf('>', lt);

            // Are there any "words" that can not be attributes first?
            int noeqword = -1;
            // The first attribute starts at the first space after the lt
            int nextspc = working.indexOf(' ', lt);
            if (nextspc != -1)
            {
                String lton = working.substring(nextspc);
                String[] parts = StringUtils.split(lton, ' ');
                for (int i = 0; i < parts.length; i++)
                {
                    // Check this contains an =
                    if (parts[i].indexOf('=') == -1)
                    {
                        noeqword = working.indexOf(parts[i], lt)-2;
                    }
                }
            }

            // so which is sooner gt, noeqword, or end-of-string?
            if (gt == -1)
            {
                gt = Integer.MAX_VALUE;
            }
            if (noeqword == -1)
            {
                noeqword = Integer.MAX_VALUE;
            }
            int min = gt;
            if (noeqword < min)
            {
                min = noeqword;
            }
            if (working.length() < min)
            {
                min = working.length()-1;
            }

            // So chop the string
            DataPolice.report("disguarding invalid tag: "+working.substring(lt, min+1));
            working = working.substring(0, lt)+working.substring(min+1);
        }
        
        return working;
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(XMLUtil.class);
}
