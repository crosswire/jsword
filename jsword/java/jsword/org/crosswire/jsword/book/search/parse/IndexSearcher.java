package org.crosswire.jsword.book.search.parse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.Searcher;
import org.crosswire.jsword.passage.Key;

/**
 * The central interface to all searching.
 * 
 * Functionality the I invisage includes:<ul>
 * <li>A simple search syntax that goes something like this.<ul>
 *     <li>aaron, moses     (verses containing aaron and moses. Can also use & or +)
 *     <li>aaron/moses      (verses containing aaron or moses. Can also use |)
 *     <li>aaron - moses    (verses containing aaron but not moses)
 *     <li>aaron ~5 , moses (verses with aaron within 5 verses of moses)
 *     <li>soundslike aaron (verses with words that sound like aaron. Can also use sl ...)
 *     <li>thesaurus happy  (verses with words that mean happy. Can also use th ...)
 *     <li>grammar have     (words like has have had and so on. Can also use gr ...)</ul>
 * <li>The ability to add soundslike type extensions.</ul>
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
public class IndexSearcher implements Searcher
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Searcher#init(org.crosswire.jsword.book.search.Index)
     */
    public void init(Index newindex)
    {
        this.index = newindex;
        this.commands = getWordMap();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Searcher#search(java.lang.String, org.crosswire.jsword.passage.Key)
     */
    public Key search(String search) throws BookException
    {
        output = CustomTokenizer.tokenize(search, commands);
        return search(output);
    }

    /**
     * Take a search string and decipher it into a Passage.
     * @param sought The string to be searched for
     * @return The matching verses
     */
    protected Key wordSearch(String sought) throws BookException
    {
        return index.findWord(sought);
    }

    /**
     * Take a search string and decipher it into a Passage.
     * @return The matching verses
     */
    protected Key search(List matches) throws BookException
    {
        Key key = index.findWord(null);

        // Need a CommandWord, but a ParamWord we can deal with using an
        // AddCommandWord chucked on the front
        if (matches.get(0) instanceof ParamWord)
        {
            // Add a default AddCommandWord to the front it there is
            matches.add(0, new AddCommandWord());
        }

        wit = matches.iterator();
        while (wit.hasNext())
        {
            Object temp = wit.next();

            try
            {
                CommandWord command = (CommandWord) temp;
                command.updatePassage(this, key);
            }
            catch (ClassCastException ex)
            {
                ex.printStackTrace();
                throw new BookException(Msg.ENGINE_SYNTAX, new Object[] { temp });
            }
        }

        // Set these to null so that people can't play around
        // with them once they're done with, and to save memory.
        matches = null;
        wit = null;

        return key;
    }

    /**
     * A basic version of getPassage(String[]) simply calls getPassage(String)
     * in a loop for each word, adding the Verses to an Passage that is returned
     * @param words The words to search for
     * @return The Passage
     * @throws BookException If anything goes wrong with this method
     */
    protected Key getPassage(String[] words) throws BookException
    {
        Key ref = index.findWord(null);

        for (int i = 0; i < words.length; i++)
        {
            ref.addAll(wordSearch(words[i]));
        }

        return ref;
    }

    /**
     * Accessor for the Bible to search.
     * @return The current Bible
     */
    protected Index getIndex()
    {
        return index;
    }

    /**
     * Accessor for the available SearchWords. This is probably
     * the same as from Options.getSearchHashtable() but just in
     * case anyone has been playing around with it...
     * @return The Word Hashtable
     */
    protected Map getSearchMap()
    {
        return commands;
    }

    /**
     * Accessor for the available SearchWords. This is probably
     * the same as from Options.getSearchHashtable() but just in
     * case anyone has been playing around with it...
     */
    protected void setSearchMap(Map commands)
    {
        this.commands = commands;
    }

    /**
     * Most Words need to access parameters, this method allows them access to
     * the Searcher's own Enumerator. Use with care, and only if you are a Word
     * taking part in the current search.
     * @return The current Iterator
     */
    protected Iterator iterator()
    {
        return wit;
    }

    /**
     * @throws BookException
     */
    public Key iteratePassage() throws BookException
    {
        if (!iterator().hasNext())
        {
            throw new BookException(Msg.RETAIN_BLANK);
        }

        Object next = iterator().next();
        if (!(next instanceof ParamWord))
        {
            log.error("next=" + next); //$NON-NLS-1$
        }

        ParamWord param = (ParamWord) next;
        Key ref = param.getKeyList(this);

        return ref;
    }

    /**
     * @throws BookException
     */
    public String iterateWord() throws BookException
    {
        if (!iterator().hasNext())
        {
            throw new BookException(Msg.RETAIN_BLANK);
        }

        Object next = iterator().next();
        if (!(next instanceof ParamWord))
        {
            log.error("next=" + next); //$NON-NLS-1$
        }

        ParamWord param = (ParamWord) next;
        String word = param.getWord(this);

        return word;
    }

    /**
     * Accessor for the cached list of known special lookup words
     */
    public static Map getWordMap()
    {
        if (wordMap == null)
        {
            try
            {
                Properties prop = ResourceUtil.getProperties(Word.class);

                wordMap = new HashMap();
                preferredMap = new HashMap();
    
                for (Iterator it = prop.keySet().iterator(); it.hasNext(); )
                {
                    String key = (String) it.next();
                    String value = prop.getProperty(key);

                    if (key.startsWith(PACKAGE_NAME))
                    {
                        try
                        {
                            Class clazz = Class.forName(key);
                            preferredMap.put(clazz, value);
                        }
                        catch (Exception ex)
                        {
                            log.error("can't add CommandWord: key=" + key + " Class=" + value, ex); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                    else
                    {
                        try
                        {
                            Class clazz = Class.forName(value);
                            wordMap.put(key, clazz.newInstance());
                        }
                        catch (Exception ex)
                        {
                            log.error("can't add CommandWord: key=" + key + " Class=" + value, ex); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }
            }
            catch (IOException ex)
            {
                log.fatal("Missing search words", ex); //$NON-NLS-1$
            }
        }

        return wordMap;
    }

    /**
     * Accessor for the cached list of known special lookup words
     */
    public static String getPreferredSyntax(Class command)
    {
        // Check the maps have been created
        getWordMap();
        return (String) preferredMap.get(command);
    }

    /**
     * To distinguish command mappings from preferred mappings in Word.properties
     */
    private static final String PACKAGE_NAME = "org.crosswire.jsword.book.search.parse"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(IndexSearcher.class);

    /**
     * The cache of known words
     */
    private static Map wordMap = null;

    /**
     * The cache of preferred symbols for the words
     */
    private static Map preferredMap = null;

    /**
     * The parsed version of the current string
     */
    private List output = null;

    /**
     * The commands that we know about
     */
    private Map commands = null;

    /**
     * While the answer is being worked out ...
     */
    private Iterator wit = null;

    /**
     * The index
     */
    private Index index = null;
}
