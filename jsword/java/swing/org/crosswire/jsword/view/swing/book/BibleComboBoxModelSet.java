package org.crosswire.jsword.view.swing.book;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.event.EventListenerList;

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
    public void setBookComboBox(JComboBox cboBook)
    {
        this.cboBook = cboBook;

        cboBook.setModel(mdlBook);
        cboBook.addItemListener(cil);
    }

    /**
     * The chapter combo box
     */
    public void setChapterComboBox(JComboBox cboChapter)
    {
        this.cboChapter = cboChapter;

        cboChapter.setModel(mdlChapter);
        // There are over 100 chapters in some books
        cboChapter.setPrototypeDisplayValue(new Integer(999));
        cboChapter.addItemListener(cil);
    }

    /**
     * The verse combo box
     */
    public void setVerseComboBox(JComboBox cboVerse)
    {
        this.cboVerse = cboVerse;

        cboVerse.setModel(mdlVerse);
        // There are over 100 verses in some chapters
        cboChapter.setPrototypeDisplayValue(new Integer(999));
        cboVerse.addItemListener(cil);
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
     * @param newverse The verse to set
     */
    protected void setViewedVerse(Verse newverse)
    {
        setVerse(newverse);
    }

    /**
     * Set the combo-boxes to a new verse
     */
    public void setVerse(Verse newverse)
    {
        if (verse.equals(newverse))
        {
            return;
        }

        try
        {
            Verse oldverse = verse;
            verse = newverse;
            int bookval = newverse.getBook();
            String book = BibleInfo.getLongBookName(bookval);
            if (oldverse.getBook() != bookval || !cboBook.getSelectedItem().equals(book))
            {
                cboBook.setSelectedItem(book);
            }

            int chapterval = newverse.getChapter();
            Integer chapternum = new Integer(chapterval);
            if (oldverse.getChapter() != chapterval || !cboChapter.getSelectedItem().equals(chapternum))
            {
                cboChapter.setSelectedItem(chapternum);
            }

            int verseval = newverse.getVerse();
            Integer versenum = new Integer(verseval);
            if (oldverse.getVerse() != verseval || !cboVerse.getSelectedItem().equals(versenum))
            {
                cboVerse.setSelectedItem(versenum);
            }

            fireContentsChanged();
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
        }
    }

    private Verse verse = new Verse();

    protected JComboBox cboBook = null;
    protected JComboBox cboChapter = null;
    private JComboBox cboVerse = null;

    protected BibleComboBoxModel mdlBook = new BibleComboBoxModel(this, BibleComboBoxModel.LEVEL_BOOK);
    protected BibleComboBoxModel mdlChapter = new BibleComboBoxModel(this, BibleComboBoxModel.LEVEL_CHAPTER);
    protected BibleComboBoxModel mdlVerse = new BibleComboBoxModel(this, BibleComboBoxModel.LEVEL_VERSE);

    protected EventListenerList listeners = new EventListenerList();
    private ItemListener cil = new CustomItemListener();

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
    private final class CustomItemListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent ev)
        {
            if (ev.getStateChange() == ItemEvent.SELECTED)
            {
                // If the book changes we need to change both the chapter and verse list
                // If the chapter changes we need to change the verse list
                Object source = ev.getSource();
                if (source.equals(cboBook))
                {
                    mdlChapter.fireContentsChanged(this, 0, mdlChapter.getSize());
                }

                if (source.equals(cboBook) || source.equals(cboChapter))
                {
                    mdlVerse.fireContentsChanged(this, 0, mdlVerse.getSize());
                }
            }
        }
    }
}
