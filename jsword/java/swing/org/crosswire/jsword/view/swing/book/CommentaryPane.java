
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.CommentaryMetaData;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.OSISUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.util.Style;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class CommentaryPane extends JPanel
{
    /**
     * Simple constructor that uses all the Books
     */
    public CommentaryPane()
    {
        cmds = Books.getBooks(filter);

        jbInit();
    }

    /**
     * Initialise the GUI
     */
    private void jbInit()
    {
        set.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        set.setBookComboBox(cbo_books);
        set.setChapterComboBox(cbo_chaps);
        set.setVerseComboBox(cbo_verse);

        cbo_books.setToolTipText("Select a book");
        cbo_chaps.setToolTipText("Select a chapter");
        cbo_verse.setToolTipText("Select a verse");

        pnl_select.setLayout(new FlowLayout());
        pnl_select.add(cbo_books, null);
        pnl_select.add(cbo_chaps, null);
        pnl_select.add(cbo_verse, null);

        cbo_comments.setModel(mdl_comments);
        cbo_comments.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        pnl_top.setLayout(new BorderLayout());
        pnl_top.add(pnl_select, BorderLayout.NORTH);
        pnl_top.add(cbo_comments, BorderLayout.SOUTH);

        txt_display.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent ev)
            {
                link(ev);
            }
        });
        txt_display.setEditable(false);
        txt_display.setEditorKit(new HTMLEditorKit());

        scr_display.getViewport().add(txt_display, null);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(pnl_top, BorderLayout.NORTH);
        this.add(scr_display, BorderLayout.CENTER);
    }

    /**
     * 
     */
    protected void updateDisplay()
    {
        try
        {
            int index = cbo_comments.getSelectedIndex();
            CommentaryMetaData cmd = (CommentaryMetaData) cmds.get(index);
            
            Verse verse = set.getVerse();
            Passage ref = PassageFactory.createPassage();
            ref.add(verse);
            BookData bdata = cmd.getCommentary().getComments(ref);
            SAXEventProvider provider = OSISUtil.getSAXEventProvider(bdata);
            String text = style.applyStyleToString(provider, "simple.xsl");
                
            txt_display.setText(text);
            txt_display.select(0, 0);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * @param ev
     */
    protected void link(HyperlinkEvent ev)
    {
        log.warn("No listener for "+ev.getURL());
    }

    /**
     * The CommentaryMetaDatas
     */
    protected List cmds;

    /**
     * The stylizer
     */
    protected Style style = new Style("swing");

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(CommentaryPane.class);

    private BookFilter filter = BookFilters.getCommentaries();
    private BooksComboBoxModel mdl_comments = new BooksComboBoxModel(filter);

    /*
     * GUI components
     */
    protected BibleComboBoxModelSet set = new BibleComboBoxModelSet();
    protected JComboBox cbo_comments = new JComboBox();
    private JComboBox cbo_books = new JComboBox();
    private JComboBox cbo_chaps = new JComboBox();
    private JComboBox cbo_verse = new JComboBox();
    private JPanel pnl_select = new JPanel();
    private JPanel pnl_top = new JPanel();
    protected JEditorPane txt_display = new JEditorPane();
    private JScrollPane scr_display = new JScrollPane();
}
