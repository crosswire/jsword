
package org.crosswire.jsword.control.search;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
* The central interface to all searching.
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
public class Engine
{
    /**
    * Create a new search engine. Bible should probably be Book however
    * Book does not yet have a well defined interface.
    * @param bible The book to search
    */
    public Engine(Bible bible)
    {
        this.bible = bible;
        this.commands = SearchDefault.getHashtable();
    }

    /**
    * Create a new search engine. Bible should probably be Book however
    * Book does not yet have a well defined interface.
    * @param bible The book to search
    * @param commands The commands to make available to the search engine
    */
    public Engine(Bible bible, Hashtable commands)
    {
        this.bible = bible;
        this.commands = commands;
    }

    /**
    * Take a search string and decipher it into a Passage.
    * @param ref The Passage to alter
    * @param sought The string to be searched for
    * @return The matching verses
    */
    public Passage search(Passage ref, String sought) throws SearchException
    {
        output = CustomTokenizer.tokenize(sought, commands);
        return search(ref, output);
    }

    /**
    * Take a search string and decipher it into a Passage.
    * @param sought The string to be searched for
    * @return The matching verses
    */
    public Passage search(String sought) throws SearchException
    {
        output = CustomTokenizer.tokenize(sought, commands);
        return search(output);
    }

    /**
    * Take a search string and decipher it into a Passage.
    * @param output The string to be searched for as a Vector of SearchWords
    * @return The matching verses
    */
    public Passage search(Vector output) throws SearchException
    {
        Passage ref = PassageFactory.createPassage();

        return search(ref, output);
    }

    /**
    * Take a search string and decipher it into a Passage.
    * @param ref The Passage to alter
    * @param output The string to be searched for as a Vector of SearchWords
    * @return The Passage passed in
    */
    public Passage search(Passage ref, Vector output) throws SearchException
    {
        // Check that there is a CommandWord first
        if (!(output.elementAt(0) instanceof CommandWord))
        {
            // Add a default AddCommandWord if not
            output.insertElementAt(new AddCommandWord(), 0);
        }

        en = output.elements();
        while (en.hasMoreElements())
        {
            Object temp = en.nextElement();
            try
            {
                CommandWord command = (CommandWord) temp;
                command.updatePassage(this, ref);
            }
            catch (ClassCastException ex)
            {
                throw new SearchException("search_engine_syntax", new Object[] { temp });
            }
        }

        // Set these to null so that people can't play around
        // with them once they're done with, and to save memory.
        output = null;
        en = null;

        return ref;
    }

    /**
    * Accessor for the Bible to search.
    * @return The current Bible
    */
    public Bible getBible()
    {
        return bible;
    }

    /**
    * Accessor for the available SearchWords. This is probably
    * the same as from Options.getSearchHashtable() but just in
    * case anyone has been playing around with it...
    * @return The SearchWord Hashtable
    */
    public Hashtable getSearchHashtable()
    {
        return commands;
    }

    /**
    * Accessor for the whole array of SearchWords. I'm not sure
    * why anyone would want this, but here it is anyway.
    * @return The SearchWord Vector
    */
    public Vector getSearchWords()
    {
        return output;
    }

    /**
    * Most SearchWords (Almost all except the DefaultParamWord) need
    * to access parameters, this method allows them access to the
    * Engine's own Enumerator. Use with care, and only if you are a
    * SearchWord taking part in the current search.
    * @return The current Enumerator
    */
    public Enumeration elements()
    {
        return en;
    }

    /** The book to search */
    private Bible bible;

    /** The parsed version of the current string */
    private Vector output = null;

    /** The commands that we know about */
    private Hashtable commands = null;

    /** While the answer is being worked out ... */
    private Enumeration en = null;
}
