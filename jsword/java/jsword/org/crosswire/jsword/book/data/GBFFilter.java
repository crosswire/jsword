
package org.crosswire.jsword.book.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;

/**
 * Filter to convert GBF data to OSIS format.
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
public class GBFFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.Filter#toOSIS(org.crosswire.jsword.book.data.BookDataListener, java.lang.String)
     */
    public void toOSIS(BookDataListener li, String plain) throws FilterException
    {
        try
        {
            List tokens = tokenize(plain);
            parseTokens(li, tokens);
        }
        catch (JAXBException ex)
        {
            throw new FilterException(Msg.GBF_JAXB, ex);
        }
    }

    /**
     * Go through a list of tokens and add them to the listener
     */
    public void parseTokens(BookDataListener li, List tokens) throws JAXBException, FilterException
    {
        // For notes
        int marker = 1;

        // go through the token working out what to do with them all
        for (Iterator it = tokens.iterator(); it.hasNext();)
        {
            Object token = it.next();
            if (token instanceof String)
            {
                li.addText((String) token);
            }
            else if (token instanceof Tag)
            {
                Tag tag = (Tag) token;
        
                // skip over the rest of the footnote
                if (tag.equals(FOOTNOTE_START))
                {
                    List footnote = getTokensUntil(it, FOOTNOTE_STOP);
                    String content = filterText(footnote);
                    
                    // This could be a marker or it could be the body of the note
                    // We tell which by string length. <= 1 is a marker which we
                    // ignore for simplicity
                    if (content.length() > 1)
                    {
                        li.addNote(""+(marker++), content);
                    }
                }
                else if (tag.equals(PARAGRAPH))
                {
                    // ignore paragraph markers
                }                
                else if (tag.equals(ITALICS_START))
                {
                    li.startSegment();
                }                
                else if (tag.equals(ITALICS_STOP))
                {
                    li.endSegment();
                }                
                else
                {
                    // unknown tags
                    log.warn("Ignoring tag of "+tag.getTag());
                }
            }
            else
            {
                throw new FilterException(Msg.GBF_BADTOKEN, new Object[] { token });
            }
        }
    }

    /**
     * Strip all the Tags from a List and return just the text
     */
    private String filterText(List list)
    {
        StringBuffer buffer = new StringBuffer();

        // go through the token working out what to do with them all
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Object token = it.next();
            if (token instanceof String)
            {
                buffer.append((String) token);
            }
        }
        
        return buffer.toString();
    }

    /**
     * Get a list for the footnote
     */
    private List getTokensUntil(Iterator it, Tag end) throws JAXBException
    {
        // take tokens off the list until end of list or FOOTNOTE_END
        List ignored = new ArrayList();

        while (true)
        {
            if (!it.hasNext())
            {
                break;
            }
        
            Object token = it.next();
            if (token instanceof String)
            {
                ignored.add(token);
            }
            else if (token instanceof Tag)
            {
                Tag tag = (Tag) token;
                if (tag.equals(end))
                {
                    break;
                }
                else
                {
                    ignored.add(token);
                }
            }
            else
            {
                throw new JAXBException("Failed to parse: "+token);
            }
        }

        return ignored;
    }
    
    /**
     * Create a list of strings and tags
     * @param plain
     * @return List
     */
    private List tokenize(String plain)
    {
        List retval = new ArrayList();
        String remains = plain;

        while (true)
        {
            int ltpos = remains.indexOf('<');
            int gtpos = remains.indexOf('>');

            if (ltpos == -1 && gtpos == -1)
            {
                // no more tags to decode
                retval.add(remains);
                break;
            }

            // check that we don't have unmatched tags
            if (ltpos == -1 || gtpos == -1)
            {
                log.warn("ignoring unmatched '<' or '>' in gbf: "+remains);
                retval.add(remains);
                break;
            }
            
            // check that the tags are in a sensible order
            if (ltpos > gtpos)
            {
                log.warn("ignoring unmatched '<' or '>' in gbf: "+remains);
                retval.add(remains);
                break;
            }

            String start = remains.substring(0, ltpos);
            retval.add(start);

            String tag = remains.substring(ltpos+1, gtpos);
            retval.add(new Tag(tag));
            
            remains = remains.substring(gtpos+1);
        }

        return retval;
    }

    /**
     * A GBF Tag
     */
    private static class Tag
    {
        public Tag(String tag)
        {
            this.tag = tag;
        }

        public String getTag()
        {
            return tag;
        }

        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;

            if (obj.getClass() != this.getClass())
                return false;

            Tag that = (Tag) obj;
            return this.tag.equals(that.tag);
        }
        
        public int hashCode()
        {
            return tag.hashCode();
        }

        private String tag;
    }
    
    private static final Tag PARAGRAPH = new Tag("CM");
    private static final Tag FOOTNOTE_START = new Tag("RF");
    private static final Tag FOOTNOTE_STOP = new Tag("Rf");
    private static final Tag ITALICS_START = new Tag("FI");
    private static final Tag ITALICS_STOP = new Tag("Fi");

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(GBFFilter.class);
}
