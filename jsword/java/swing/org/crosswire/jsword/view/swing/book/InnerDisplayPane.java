
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
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.TransformerException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.Style;
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
public class InnerDisplayPane extends JPanel
{
    /**
     * Simple Constructor
     */
    public InnerDisplayPane()
    {
        jbInit();
    }

    /**
     * Makes the second invocation much faster
     */
    public static void preload()
    {
        try
        {
            Passage ref = PassageFactory.createPassage("Gen 1:1");
            Bible version = Defaults.getBibleMetaData().getBible();

            BookData data = version.getData(ref);
            SAXEventProvider provider = data.getSAXEventProvider();
            
            Style style = new Style("swing");
            style.applyStyleToString(provider, "simple.xsl");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
            public void hyperlinkUpdate(HyperlinkEvent ev)
            {
                link(ev);
            }
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

        BookData data = version.getData(ref);
        SAXEventProvider provider = data.getSAXEventProvider();
        String text = style.applyStyleToString(provider, "simple.xsl");

        txt_view.setText(text);
        
        // The following *ought* to scroll to the top but it doesn't ...
        //txt_view.scrollRectToVisible(new Rectangle());
        txt_view.select(0, 0);
    }

    /**
     * Accessor for the current TextComponent
     */
    public JTextComponent getJTextComponent()
    {
        return txt_view;
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
    private Bible version = null;
    private Style style = new Style("swing");

    /** The log stream */
    private static Logger log = Logger.getLogger(InnerDisplayPane.class);

    private JScrollPane scr_view = new JScrollPane();
    private JEditorPane txt_view = new JEditorPane();
}
