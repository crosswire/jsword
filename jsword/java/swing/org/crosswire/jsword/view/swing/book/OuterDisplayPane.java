package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.view.swing.passage.PassageGuiUtil;
import org.crosswire.jsword.view.swing.passage.PassageListModel;

/**
 * A quick Swing Bible display pane.
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
public class OuterDisplayPane extends JPanel implements DisplayArea
{
    /**
     * Initialize the OuterDisplayPane
     */
    public OuterDisplayPane()
    {
        try
        {
            List booklist = Books.getBookMetaDatas();
            if (booklist.size() == 0)
            {
                return;
            }

            Book book = ((BookMetaData) booklist.get(0)).getBook();
            txt_passg.setBook(book);
        }
        catch (Throwable ex)
        {
            log.error("Failed to set default book", ex);
        }

        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        mdl_passg.setMode(PassageListModel.LIST_RANGES);
        mdl_passg.setRestriction(PassageConstants.RESTRICT_CHAPTER);

        lst_passg.setModel(mdl_passg);
        lst_passg.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                selection();
            }
        });

        spt_passg.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        spt_passg.add(scr_passg, JSplitPane.LEFT);
        spt_passg.add(txt_passg, JSplitPane.RIGHT);
        spt_passg.setOneTouchExpandable(true);
        spt_passg.setDividerLocation(0.0D);

        scr_passg.getViewport().add(lst_passg);

        this.setLayout(new BorderLayout());
        this.add(spt_passg, BorderLayout.CENTER);
    }

    /**
     * Accessor for a notifier from the DisplaySelectPane
     */
    public DisplaySelectListener getDisplaySelectListener()
    {
        return dsli;
    }

    /**
     * Set the passage to be displayed
     */
    public void setPassage(Passage ref)
    {
        this.ref = ref;

        try
        {
            mdl_passg.setPassage(ref);
            txt_passg.setPassage(ref);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Get the passage being displayed.
     */
    public Passage getPassage()
    {
        return mdl_passg.getPassage();
    }

    /**
     * Delete the selected verses
     */
    public void deleteSelected(BibleViewPane view)
    {
        PassageGuiUtil.deleteSelectedVersesFromList(lst_passg);

        // Update the text box
        ref = mdl_passg.getPassage();
        DisplaySelectPane psel = view.getSelectPane();
        psel.setPassage(ref);
    }

    /*
     * Accessor for the current InnerDisplayPane
     *
    public InnerDisplayPane getSelectedInnerDisplayPane()
    {
        return txt_passg.getInnerDisplayPane();
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#cut()
     */
    public void cut()
    {
        txt_passg.cut();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#copy()
     */
    public void copy()
    {
        txt_passg.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#paste()
     */
    public void paste()
    {
        txt_passg.paste();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txt_passg.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txt_passg.removeHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getOSISSource()
     */
    public String getOSISSource()
    {
        return txt_passg.getOSISSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return txt_passg.getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getKey()
     */
    public Key getKey()
    {
        return txt_passg.getKey();
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            Object[] ranges = lst_passg.getSelectedValues();

            Passage local = null;
            if (ranges.length == 0)
            {
                local = ref;
            }
            else
            {
                local = PassageFactory.createPassage();
                for (int i=0; i<ranges.length; i++)
                {
                    local.add((VerseRange) ranges[i]);
                }

                // if there was a single selection then show the whole chapter
                if (ranges.length == 1)
                {
                    local.blur(1000, PassageConstants.RESTRICT_CHAPTER);
                }
            }

            txt_passg.setPassage(local);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The whole passage that we are viewing
     */
    private Passage ref;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(OuterDisplayPane.class);

    /*
     * GUI Components
     */
    private JSplitPane spt_passg = new JSplitPane();
    private JScrollPane scr_passg = new JScrollPane();
    protected TabbedDisplayPane txt_passg = new TabbedDisplayPane();
    private JList lst_passg = new JList();
    private PassageListModel mdl_passg = new PassageListModel();
    private DisplaySelectListener dsli = new CustomDisplaySelectListener();

    /**
     * Update the display whenever the version or passage changes
     */
    private class CustomDisplaySelectListener implements DisplaySelectListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.view.swing.book.DisplaySelectListener#bookChosen(org.crosswire.jsword.view.swing.book.DisplaySelectEvent)
         */
        public void bookChosen(DisplaySelectEvent ev)
        {
            log.debug("new bible chosen: "+ev.getBook());

            Book book = ev.getBook();
            txt_passg.setBook(book);

            // The following way to refresh the view is a little harsh because
            // resets any list selections. It would be nice if we could get
            // away with calling selection(), however it doesn't seem to work.
            setPassage(ev.getPassage());
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.view.swing.book.DisplaySelectListener#passageSelected(org.crosswire.jsword.view.swing.book.DisplaySelectEvent)
         */
        public void passageSelected(DisplaySelectEvent ev)
        {
            log.debug("new passage chosen: "+ev.getPassage().getName());
            setPassage(ev.getPassage());
        }
    }
}
