
package org.crosswire.jsword.book;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.W;
import org.crosswire.jsword.passage.Passage;

/**
 * StudyTool is-an extension to Bible that knows about the original
 * Greek/Hebrew in the form of Strongs numbers.
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
public class StudyTool
{
    /**
     * StudyTool: For a given word find a list words it is translated from
     * @param word The text to search for
     * @return The source numbers of that word
     */
    public Collection getTranslations(Bible bible, String word) throws BookException
    {
        Search search = new Search(word, false);
        Passage ref = bible.findPassage(search);
        BookData data = bible.getData(ref);
        ParentLocator loc = new ParentLocator(data.getOsis());

        Map reply = new HashMap();

        // Loop through all the divs in this BookData
        Iterator oit = data.getOsis().getOsisText().getDiv().iterator();
        while (oit.hasNext())
        {
            Div div = (Div) oit.next();

            // And loop over the content in this div
            Iterator dit = JAXBUtil.getDeepContent(div, W.class).iterator();
            while (dit.hasNext())
            {
                // NOTE: This only looks at level 1 content, what we need is a
                // deep scan for all 'W's.
                Object ele = dit.next();
                W w = (W) ele;
                String content = JAXBUtil.getPlainText(w);

                // There will be many words in the passage in question,
                // but not all of them will be translations of our word
                if (content.indexOf(word) != -1)
                {
                    Strongs strongs = new Strongs(w);

                    Translation trans = (Translation) reply.get(strongs);
                    if (trans == null)
                    {
                        trans = new Translation(word, strongs);
                        reply.put(strongs, trans);
                    }

                    trans.getRef().add(JAXBUtil.getVerse(w, loc));
                }
            }
        }

        return reply.values();
    }

    /**
     * StudyTool: For a given number find a list of ways it is translated
     * @param number The strongs number to search for
     * @return The words that the number is translated to
     */
    public Collection getTranslations(Bible bible, Strongs number) throws BookException
    {
        Search search = new Search(number, false);
        Passage ref = bible.findPassage(search);
        BookData data = bible.getData(ref);
        ParentLocator loc = new ParentLocator(data.getOsis());

        Map reply = new HashMap();

        // Loop through all the divs in this BookData
        Iterator oit = data.getOsis().getOsisText().getDiv().iterator();
        while (oit.hasNext())
        {
            Div div = (Div) oit.next();

            // And loop over the content in this div        
            Iterator dit = JAXBUtil.getDeepContent(div, W.class).iterator();
            while (dit.hasNext())
            {
                // NOTE: This only looks at level 1 content, what we need is a
                // deep scan for all 'W's.
                Object ele = dit.next();
                W w = (W) ele;
                Strongs strongs = new Strongs(w);

                // There will be many strongs number in the passage in
                // question, but not all of them will be translations of our
                // strongs number
                if (strongs.equals(number))
                {
                    String translated = JAXBUtil.getPlainText(w);

                    Translation trans = (Translation) reply.get(translated);
                    if (trans == null)
                    {
                        trans = new Translation(translated, number);
                        reply.put(translated, trans);
                    }

                    trans.getRef().add(JAXBUtil.getVerse(w, loc));
                }
            }
        }

        return reply.values();
    }
}
