
package org.crosswire.jsword.book;

import java.util.Iterator;

import org.crosswire.jsword.book.data.BookData;

/**
 * Book is the most basic store of textual data - It can retrieve data
 * either as an XML document or as plain text - It uses Bookmarks to refer
 * to parts of itself, and can search for words (returning Bookmarks).
 *
 * <p>All Books should have an no-arg constructor. This restriction is
 * important because without this the config package can not create an
 * instance of this class from a string in an options file.</p>
 *
 * <p>What type should we use to describe a part of a book that we want to
 * wiew? This question is made more complex because a find() operation
 * needs to be able to return a collection of pointers.</p>
 * <ul>
 * <li>String: This is simple, however the requirement to allow
 *     transfer of several pointers in a single String means using
 *     delimitters, which make Books un-necessarily more complex.</li>
 * <li>String[]: This may be a simpler solution, however it does not
 *     work well with Passage, and it specifies the collection method</li>
 * <li>Collection: Enforces JDK 1.2 dependancy, and is not clearly typed
 *     which means that to work with Passage we would need somthing like
 *     <code>Vector find()</code> and <code>Passage findRef()</code>.</li>
 * <li>Bookmark[]: See problems for String[]</li>
 * <li>Bookmarks: Works well with Passage (Passage extends Bookmarks and
 *     Verse and VerseRange extend Bookmark) and it lets the implementor
 *     choose the implementation. This is the best method</li>
 * </ul>
 * <p>What type should we use to pass around DOM Documents? The options
 * seem to be:
 * <ul>
 * <li>Document: Just use the basic <code>org.w3c.dom.Document</code> type
 *     This is fairly simple, although it increases dependacies on outside
 *     code (slightly) It is effectively a weak type, since a Document
 *     could contain anything.</ul>
 * <li>BlahDOM: This biggest problem with this is that it makes access to
 *     the data more complex. It could allow is to store a 'current node'
 *     pointer which could be useful. This is the best method</ul>
 * </ul>
 *
 * <p>Each Book needs a GUI to select a Bookmark for viewing. The GUI
 * layout is very Book dependant however, we do not want to put GUI
 * dependancies into non-GUI code, so there is no Component getSelector()
 * interface. Maybe a Class getGUISelector() interface would be better? or
 * we could have a convention like <book_class_path>.swing.Selector</p>
 *
 * <p>We also need to consider handling DOMs
 * <pre>
 * interface BaseDOM
 * {
 *     public Document getDom();
 *
 *     public DTD getDTD();
 *     public void setDTD(DTD dtd);
 * }
 *
 * // Consider THML when thinking about the DTD
 * class BookDOM extends BaseDOM
 * {
 *     public String getText();
 * }
 * </pre></p>
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
 * @version $Id$
 */
public interface Book
{
    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getMetaData();

    /**
     * Retrieval: Add to the given document some mark-up for the specified
     * Verses.
     * @param ref The verses to search for
     * @return The found Book data
     * @throws BookException If anything goes wrong with this method
     */
    public BookData getData(Key ref) throws BookException;

    /**
     * Retrieval: For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     * @throws BookException If anything goes wrong with this method
     */
    public Key find(String word) throws BookException;

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * This is only needed to make your this driver play well
     * in searches it is not vital for normal display. To save yourself
     * the bother of implementing this properly you could do:
     *   <code>return new String[] { base };</code>
     * The Iterator can be converted into a String[] easily using the
     * toStringArray() method in BookUtil.
     * @param base The word to base your word array on
     * @see BookUtil#toStringArray(Iterator)
     * @return An array of words starting with the base
     * @throws BookException If anything goes wrong with this method
     */
    public Iterator getStartsWith(String base) throws BookException;

    /*
    * Create an String for the specified Verses. There is some debate in
    * my mind as to whether this should be more like:
    * <code>String getText(Verse v)</code>. The problem with this version
    * is that it doesn't tell you about where the verse ends. It is just
    * raw text, so if you want to know about verse endings you will need
    * to call this several times (as you would have to with the
    * alternative) however means there is more Object creation to be done
    * @param range The verses to search for
    * @return The Bible text
    *
    public String getText(String bookmark) throws BookException;
    */

    /*
    * Create an XML document for the specified Verses
    * @param doc The XML document
    * @param ele The Element to start adding at. null if the doc is empty
    * @param ref The verses to search for
    *
    public void getDocument(Document doc, Element ele, String bookmark) throws BookException;
    */

    /*
    * For a given word find a list of references to it
    * @param word The text to search for
    * @return The references to the word
    *
    public String find(String word) throws BookException;
    */
}
