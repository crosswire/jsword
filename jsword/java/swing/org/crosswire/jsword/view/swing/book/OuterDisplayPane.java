
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.view.swing.event.DisplaySelectEvent;
import org.crosswire.jsword.view.swing.event.DisplaySelectListener;
import org.crosswire.jsword.view.swing.passage.PassageList;

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
 * @see docs.Licence
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
            Bible version = Defaults.getBibleMetaData().getBible();
            txt_passg.setVersion(version);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }

        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
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
        try
        {
            lst_passg.setPassage(ref);
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
        return lst_passg.getPassage();
    }

    /**
     * 
     * @param view
     */
    public void deleteSelected(BibleViewPane view)
    {
        lst_passg.deleteSelected();
        
        // Update the text box
        Passage ref = lst_passg.getPassage();
        DisplaySelectPane psel = view.getSelectPane();
        psel.setPassage(ref);
    }

    /**
     * Accessor for the current InnerDisplayPane
     *
    public InnerDisplayPane getSelectedInnerDisplayPane()
    {
        return txt_passg.getInnerDisplayPane();
    }

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

            Passage ref = PassageFactory.createPassage();
            for (int i=0; i<ranges.length; i++)
            {
                ref.add((VerseRange) ranges[i]);
            }

            // if there was a single selection then show the whole chapter
            if (ranges.length == 1)
            {
                ref.blur(1000, PassageConstants.RESTRICT_CHAPTER);
            }

            txt_passg.setPassage(ref);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(OuterDisplayPane.class);

    private JSplitPane spt_passg = new JSplitPane();
    private JScrollPane scr_passg = new JScrollPane();
    protected TabbedDisplayPane txt_passg = new TabbedDisplayPane();
    private PassageList lst_passg = new PassageList();

    private DisplaySelectListener dsli = new CustomDisplaySelectListener();

    /**
     * Update the display whenever the version or passage changes
     */
    private class CustomDisplaySelectListener implements DisplaySelectListener
    {
        /**
         * Someone wants us to show in a new version
         * @see org.crosswire.jsword.view.swing.event.DisplaySelectListener#bookChosen(DisplaySelectEvent)
         */
        public void bookChosen(DisplaySelectEvent ev)
        {
            log.debug("new bible chosen: "+ev.getBook().getBookMetaData().getFullName());

            Bible version = (Bible) ev.getBook();
            txt_passg.setVersion(version);
    
            // The following way to refresh the view is a little harsh because
            // resets any list selections. It would be nice if we could get
            // away with calling selection(), however it doesn't seem to work.
            setPassage(ev.getPassage());
        }

        /**
         * Someone wants us to display a new passage
         * @see org.crosswire.jsword.view.swing.event.DisplaySelectListener#passageSelected(DisplaySelectEvent)
         */
        public void passageSelected(DisplaySelectEvent ev)
        {
            log.debug("new passage chosen: "+ev.getPassage().getName());

            setPassage(ev.getPassage());
        }
    }
}
