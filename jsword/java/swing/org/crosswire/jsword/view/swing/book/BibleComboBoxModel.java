
package org.crosswire.jsword.view.swing.book;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * A ComboBoxModel for selecting book/chapter/verse.
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
public class BibleComboBoxModel extends AbstractListModel implements ComboBoxModel
{
    /**
     * Simple ctor for choosing verses
     */
    protected BibleComboBoxModel(BibleComboBoxModelSet set, int level)
    {
        this.set = set;
        this.level = level;

        switch (level)
        {
        case LEVEL_BOOK:
            try
            {
                selected = BibleInfo.getLongBookName(set.getVerse().getBook());
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
            break;

        case LEVEL_CHAPTER:
            selected = new Integer(set.getVerse().getChapter());
            break;

        case LEVEL_VERSE:
            selected = new Integer(set.getVerse().getVerse());
            break;

        default:
            throw new LogicError();
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selected)
    {
        log.debug("setSelectedItem("+selected+") level="+level);

        try
        {
            switch (level)
            {
            case LEVEL_BOOK:
                String bsel = (String) selected;
                int book = BibleInfo.getBookNumber(bsel);
                setBook(book);
                break;
    
            case LEVEL_CHAPTER:
                Integer csel = (Integer) selected;
                setChapter(csel.intValue());
                break;
    
            case LEVEL_VERSE:
                Integer vsel = (Integer) selected;
                setVerse(vsel.intValue());
                break;
    
            default:
                throw new LogicError();
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }

        this.selected = selected;
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem()
    {
        return selected;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        try
        {
            switch (level)
            {
            case LEVEL_BOOK:
                return BibleInfo.booksInBible();
            
            case LEVEL_CHAPTER:
                return BibleInfo.chaptersInBook(set.getVerse().getBook());
            
            case LEVEL_VERSE:
                return BibleInfo.versesInChapter(set.getVerse().getBook(), set.getVerse().getChapter());
            
            default:
                throw new LogicError();
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        try
        {
            switch (level)
            {
            case LEVEL_BOOK:
                return BibleInfo.getLongBookName(index+1);

            case LEVEL_CHAPTER:
            case LEVEL_VERSE:
                return new Integer(index+1);

            default:
                throw new LogicError();
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Accessor for the book
     */
    public void setBook(int book) throws NoSuchVerseException
    {
        Verse old = set.getVerse();
        Verse update = new Verse(book, old.getChapter(), old.getVerse(), true);
        set.setVerse(update);
    }

    /**
     * Accessor for the chapter
     */
    public void setChapter(int chapter) throws NoSuchVerseException
    {
        Verse old = set.getVerse();
        Verse update = new Verse(old.getChapter(), chapter, old.getVerse(), true);
        set.setVerse(update);
    }

    /**
     * Accessor for the chapter
     */
    public void setVerse(int verse)
    {
        Verse old = set.getVerse();
        Verse update = new Verse(old.getChapter(), old.getChapter(), verse, true);
        set.setVerse(update);
    }

    /* (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireContentsChanged(java.lang.Object, int, int)
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
        super.fireContentsChanged(source, index0, index1);
    }
    
    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(BibleComboBoxModel.class);

    /**
     * For when the we are a book level combo
     */
    public static final int LEVEL_BOOK = 0;

    /**
     * For when the we are a chapter level combo
     */
    public static final int LEVEL_CHAPTER = 1;

    /**
     * For when the we are a verse level combo
     */
    public static final int LEVEL_VERSE = 2;

    /**
     * Shared settings
     */
    private BibleComboBoxModelSet set;

    /**
     * What is currently selected?
     */
    private Object selected;

    /**
     * Are we a book, chapter or verse selector
     */
    protected int level;
}
