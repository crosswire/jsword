
package org.crosswire.jsword.book;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.Element;

import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.osis.W;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;

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

        Map reply = new HashMap();
        Iterator it = iterateTranslations(data, word);
        while (it.hasNext())
        {
            W w = (W) it.next();

            // Yes, it doesn't have to be english, the point it it is not greek or hebrew
            Strongs strongs = new Strongs(w);

            Translation trans = null;
            trans = (Translation) reply.get(strongs);
            if (trans == null)
            {
                trans = new Translation();
                trans.word = word;
                trans.strongs = strongs;
                trans.ref = PassageFactory.createPassage();
                reply.put(strongs, trans);
            }

            trans.ref.add(JAXBUtil.getVerse(w));
        }

        // find words and counts
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

        Map reply = new HashMap();
        Iterator it = iterateTranslations(data, number);
        while (it.hasNext())
        {
            W w = (W) it.next();

            // Yes, it doesn't have to be english, the point it it is not greek or hebrew
            String english = JAXBUtil.getPlainText(w);

            Translation trans = null;
            trans = (Translation) reply.get(english);
            if (trans == null)
            {
                trans = new Translation();
                trans.word = english;
                trans.strongs = number;
                trans.ref = PassageFactory.createPassage();
                reply.put(english, trans);
            }

            trans.ref.add(JAXBUtil.getVerse(w));
        }

        // find words and counts
        return reply.values();
    }

    /**
     * @param data
     * @param word
     * @return
     */
    private Iterator iterateTranslations(BookData data, String word)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param data
     * @param number
     * @return
     */
    private Iterator iterateTranslations(BookData data, Strongs number)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public class Translation
    {
        public String word;
        public Strongs strongs;
        public Passage ref;
    }
}
