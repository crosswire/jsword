
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.view.style.Style;
import org.jdom.Document;
import org.xml.sax.SAXException;

/**
 * An inner component of Passage pane that can't show the list.
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
public class PassageInnerPane extends JPanel
{
    /**
     * Simple Constructor
     */
    public PassageInnerPane()
    {
        jbInit();
    }

    /**
     * Gui creation
     */
    private void jbInit()
    {
        txt_view.setEditable(false);
        txt_view.setEditorKit(new HTMLEditorKit());
        txt_view.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent ev) { link(ev); }
        });
        scr_view.getViewport().setPreferredSize(new Dimension(500, 400));
        scr_view.getViewport().add(txt_view, null);

        this.setLayout(new BorderLayout());
        this.add(scr_view, BorderLayout.CENTER);
    }

    /**
     * Set the version used for lookup
     */
    public void setVersion(Bible version)
    {
        this.version = version;
    }

    /**
     * Set the passage being viewed
     */
    public void setPassage(Passage ref) throws IOException, SAXException, BookException, TransformerException
    {
        if (ref == null || version == null)
        {
            txt_view.setText("");
            return;
        }

        BibleData data = version.getData(ref);
        Document xml = data.getDocument();

        Document output = style.applyStyle(xml, "Simple");
        String text = style.getString(output);

        // For some reason the new TRaX stuff leaves the
        // <?xml version="1.0" encoding="UTF-8"?> string in the result.
        if (text.startsWith("<?xml"))
        {
            int close = text.indexOf("?>");
            if (close != -1)
            {
                text = text.substring(close+2);
            }
        }

        txt_view.setText(text);
    }

    /**
     * When a hyperlink is clicked
     */
    public void link(HyperlinkEvent ev)
    {
        log.warn("No listener for "+ev.getURL());
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    {
        txt_view.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    {
        txt_view.addMouseListener(li);
    }

    /** What is being displayed */
    private Passage ref = null;
    private Bible version = null;
    private Style style = new Style("swing");

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.view");

    private JScrollPane scr_view = new JScrollPane();
    private JEditorPane txt_view = new JEditorPane();
}
