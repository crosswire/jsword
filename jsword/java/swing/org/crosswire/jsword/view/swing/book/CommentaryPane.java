package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;

import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;
import org.crosswire.jsword.view.swing.display.BookDataDisplayFactory;
import org.crosswire.jsword.view.swing.display.FocusablePart;

/**
 * Builds a set of tabs from the list of Books returned by a filtered list
 * of Books.
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
public class CommentaryPane extends JPanel implements FocusablePart
{
    /**
     * Simple constructor that uses all the Books
     */
    public CommentaryPane()
    {
        init();
    }

    /**
     * Initialise the GUI
     */
    private void init()
    {
        set.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        set.setBookComboBox(cbobooks);
        set.setChapterComboBox(cbochaps);
        set.setVerseComboBox(cboverse);

        cbobooks.setToolTipText("Select a book");
        cbochaps.setToolTipText("Select a chapter");
        cboverse.setToolTipText("Select a verse");

        pnlselect.setLayout(new FlowLayout());
        pnlselect.add(cbobooks, null);
        pnlselect.add(cbochaps, null);
        pnlselect.add(cboverse, null);

        cbocomments.setModel(mdlcomments);
        cbocomments.setRenderer(new BookListCellRenderer());
        cbocomments.setPrototypeDisplayValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        cbocomments.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        pnltop.setLayout(new BorderLayout());
        pnltop.add(pnlselect, BorderLayout.NORTH);
        pnltop.add(cbocomments, BorderLayout.SOUTH);

        scrdisplay.getViewport().add(txtdisplay.getComponent(), null);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(pnltop, BorderLayout.NORTH);
        this.add(scrdisplay, BorderLayout.CENTER);
    }

    /**
     * 
     */
    protected void updateDisplay()
    {
        BookMetaData bmd = (BookMetaData) cbocomments.getSelectedItem();
        if (bmd == null)
        {
            return;
        }

        try
        {
            Verse verse = set.getVerse();
            ref = PassageFactory.createPassage();
            ref.add(verse);

            BookData bdata = bmd.getBook().getData(ref);
            txtdisplay.setBookData(bdata);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#copy()
     */
    public void copy()
    {
        txtdisplay.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getOSISSource()
     */
    public String getOSISSource()
    {
        BookMetaData bmd = (BookMetaData) cbocomments.getSelectedItem();

        if (ref == null || bmd == null)
        {
            return "";
        }

        try
        {

            BookData bdata = bmd.getBook().getData(ref);
            SAXEventProvider provider = bdata.getSAXEventProvider();

            SerializingContentHandler handler = new SerializingContentHandler(true);
            provider.provideSAXEvents(handler);

            return handler.toString();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return "";
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return txtdisplay.getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return ref;
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return ref;
    }

    /**
     * Accessor for the current passage
     */
    public void setPassage(Passage ref)
    {
        this.ref = ref;

        if (ref != null && ref.countVerses() > 0)
        {
            set.setVerse(ref.getVerseAt(0));
            updateDisplay();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txtdisplay.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txtdisplay.removeHyperlinkListener(li);
    }

    /**
     * Last displayed
     */
    protected Passage ref = null;

    /**
     * To get us just the Commentaries
     */
    private BookFilter filter = BookFilters.getCommentaries();

    /**
     * The display of OSIS data
     */
    private BookDataDisplay txtdisplay = BookDataDisplayFactory.createBookDataDisplay();

    /*
     * GUI components
     */
    private BooksComboBoxModel mdlcomments = new BooksComboBoxModel(filter);
    protected BibleComboBoxModelSet set = new BibleComboBoxModelSet();
    protected JComboBox cbocomments = new JComboBox();
    private JComboBox cbobooks = new JComboBox();
    private JComboBox cbochaps = new JComboBox();
    private JComboBox cboverse = new JComboBox();
    private JPanel pnlselect = new JPanel();
    private JPanel pnltop = new JPanel();
    private JScrollPane scrdisplay = new JScrollPane();
}
