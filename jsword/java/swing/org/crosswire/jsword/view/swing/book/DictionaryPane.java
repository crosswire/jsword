package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;
import org.crosswire.jsword.view.swing.display.BookDataDisplayFactory;
import org.crosswire.jsword.view.swing.display.FocusablePart;
import org.crosswire.jsword.view.swing.passage.KeyListListModel;

/**
 * Builds a panel on which all the Dictionaries and their entries are visible.
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
public class DictionaryPane extends JPanel implements FocusablePart
{
    /**
     * Setup the GUI 
     */
    public DictionaryPane()
    {
        jbInit();
    }

    /**
     * GUI initialiser
     */
    private void jbInit()
    {
        lstdicts.setVisibleRowCount(4);
        lstdicts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstdicts.setModel(mdldicts);
        lstdicts.setCellRenderer(new BookListCellRenderer());
        lstdicts.setPrototypeCellValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        lstdicts.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newDictionary();
            }
        });
        scrdicts.setViewportView(lstdicts);

        lstentries.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newEntry();
            }
        });
        screntries.setViewportView(lstentries);

        scrdisplay.setViewportView(txtdisplay.getComponent());

        sptmain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        sptmain.setTopComponent(screntries);
        sptmain.setBottomComponent(scrdisplay);

        this.setLayout(new BorderLayout(5, 5));
        this.add(scrdicts, BorderLayout.NORTH);
        this.add(sptmain, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
        try
        {
            Key key = (Key) lstentries.getSelectedValue();
            if (key == null)
            {
                return "";
            }

            BookData bdata = dict.getData(key);
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
        return (Key) lstentries.getSelectedValue();
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
     * See if the current dictionary has a mention of the word in question.
     * LATER(joe): add a background task to highlight other dictionaries that have the word.
     */
    public void setWord(String data)
    {
        try
        {
            if (dict == null)
            {
                return;
            }

            Key key = dict.getKey(data);
            if (key != null)
            {
                lstentries.setSelectedValue(key, true);
            }
        }
        catch (NoSuchKeyException ex)
        {
            // ignore
        }
    }

    /*
        // Code to search for a word
        for (Iterator it = Books.getBooks(filter).iterator(); it.hasNext();)
        {
            DictionaryMetaData dmd = (DictionaryMetaData) it.next();
            Dictionary tempdict = dmd.getDictionary();
            try
            {
                Key key = tempdict.getKey(data);
                lstdicts.setSelectedValue(tempdict, true);
                lstentries.setSelectedValue(key, true);
                return;
            }
            catch (BookException ex)
            {
                // ignore - we only wanted to see if it could be done.
            }
        }     
     */

    /**
     * Called when someone selects a new Dictionary
     */
    protected void newDictionary()
    {
        Object selected = lstdicts.getSelectedValue();
        if (selected != null)
        {
            BookMetaData dmd = (BookMetaData) selected;
            dict = dmd.getBook();
            KeyList set = dict.getGlobalKeyList();

            KeyListListModel model = new KeyListListModel(set);
            lstentries.setModel(model);
        }
    }

    /**
     * Called when someone selects a new entry from the current dictionary
     */
    protected void newEntry()
    {
        try
        {
            Key key = (Key) lstentries.getSelectedValue();
            if (key != null)
            {
                BookData bdata = dict.getData(key);
                txtdisplay.setBookData(bdata);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The display of OSIS data
     */
    private BookDataDisplay txtdisplay = BookDataDisplayFactory.createBookDataDisplay();

    private BookFilter filter = BookFilters.getDictionaries();
    private BooksComboBoxModel mdldicts = new BooksComboBoxModel(filter);
    private Book dict = null;

    private JScrollPane scrdicts = new JScrollPane();
    private JList lstdicts = new JList();
    private JSplitPane sptmain = new JSplitPane();
    private JScrollPane screntries = new JScrollPane();
    private JScrollPane scrdisplay =new JScrollPane();
    private JList lstentries = new JList();
}
