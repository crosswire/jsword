
package org.crosswire.jsword.view.swing.book;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.event.EventListenerList;

import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * A set of correctly constructed and linked BibleComboBoxModels.
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
public class BibleComboBoxModelSet
{
    /**
     * The book combo box
     */
    public void setBookComboBox(JComboBox cbo_book)
    {
        this.cbo_book = cbo_book;

        cbo_book.setModel(mdl_book);
        cbo_book.addActionListener(new CustomActionListener());
    }

    /**
     * The chapter combo box
     */
    public void setChapterComboBox(JComboBox cbo_chapter)
    {
        this.cbo_chapter = cbo_chapter;

        cbo_chapter.setModel(mdl_chapter);
    }

    /**
     * The verse combo box
     */
    public void setVerseComboBox(JComboBox cbo_verse)
    {
        this.cbo_verse = cbo_verse;

        cbo_verse.setModel(mdl_verse);
    }

    /**
     * @return Verse
     */
    public Verse getVerse()
    {
        return verse;
    }

    /**
     * Sets the verse.
     * @param verse The verse to set
     */
    protected void setViewedVerse(Verse verse)
    {
        this.verse = verse;
    }

    /**
     * Set the combo-boxes to a new verse
     */
    public void setVerse(Verse newverse)
    {
        try
        {
            String book = BibleInfo.getLongBookName(newverse.getBook());
            cbo_book.setSelectedItem(book);
            
            Integer chapternum = new Integer(newverse.getChapter());
            cbo_chapter.setSelectedItem(chapternum);
            
            Integer versenum = new Integer(newverse.getVerse());
            cbo_verse.setSelectedItem(versenum);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    private Verse verse = new Verse();

    private JComboBox cbo_book = null;
    private JComboBox cbo_chapter = null;
    private JComboBox cbo_verse = null;

    protected BibleComboBoxModel mdl_book = new BibleComboBoxModel(this, BibleComboBoxModel.LEVEL_BOOK);
    protected BibleComboBoxModel mdl_chapter = new BibleComboBoxModel(this, BibleComboBoxModel.LEVEL_CHAPTER);
    protected BibleComboBoxModel mdl_verse = new BibleComboBoxModel(this, BibleComboBoxModel.LEVEL_VERSE);

    protected EventListenerList listeners = new EventListenerList();

    /**
     * Add a listener to the list that's notified each time a change
     * to the data model occurs.
     * @param li the ListDataListener
     */
    public void addActionListener(ActionListener li)
    {
        listeners.add(ActionListener.class, li);
    }

    /**
     * Remove a listener from the list that's notified each time a 
     * change to the data model occurs.
     * @param li the ListDataListener
     */
    public void removeActionListener(ActionListener li)
    {
        listeners.remove(ActionListener.class, li);
    }

    /**
     * Called after the verse changes.
     * @see EventListenerList
     * @see javax.swing.DefaultListModel
     */
    protected void fireContentsChanged()
    {
        Object[] liarray = listeners.getListenerList();
        ActionEvent ev = null;

        for (int i = liarray.length - 2; i >= 0; i -= 2)
        {
            if (liarray[i] == ActionListener.class)
            {
                if (ev == null)
                {
                    ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, verse.getName());
                }

                ((ActionListener) liarray[i + 1]).actionPerformed(ev);
            }
        }
    }

    /**
     * For when a selection is made
     */
    private final class CustomActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent ev)
        {
            mdl_book.fireContentsChanged(this, 0, mdl_book.getSize());
            mdl_chapter.fireContentsChanged(this, 0, mdl_chapter.getSize());
            mdl_verse.fireContentsChanged(this, 0, mdl_verse.getSize());
            
            fireContentsChanged();
        }
    }
}
