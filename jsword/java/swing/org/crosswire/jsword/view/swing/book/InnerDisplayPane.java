package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.TransformerException;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.util.SimpleSwingConverter;
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class InnerDisplayPane extends JPanel implements DisplayArea
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
        final Thread worker = new Thread("DisplayPreLoader")
        {
            public void run()
            {
                try
                {
                    URL predicturl = Project.instance().getWritablePropertiesURL("display");
                    Job job = JobManager.createJob("Display Pre-load", predicturl, this, true);

                    job.setProgress("Setup");
                    Passage gen11 = PassageFactory.createPassage("Gen 1:1");
                    Book deftbible = Defaults.getBibleMetaData().getBook();
                    if (interrupted())
                    {
                        return;
                    }

                    job.setProgress("Getting initial data");
                    BookData data = deftbible.getData(gen11);
                    if (interrupted())
                    {
                        return;
                    }

                    job.setProgress("Getting event provider");
                    SAXEventProvider provider = data.getSAXEventProvider();
                    if (interrupted())
                    {
                        return;
                    }

                    job.setProgress("Compiling stylesheet");
                    Converter swing = new SimpleSwingConverter();
                    swing.convert(provider);
                    if (interrupted())
                    {
                        return;
                    }
                    
                    job.done();

                    log.debug("View pre-load finished");
                }
                catch (Exception ex)
                {
                    log.error("View pre-load failed", ex);
                }
            }
        };

        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    /**
     * Gui creation
     */
    private void jbInit()
    {
        txt_view.setEditable(false);
        txt_view.setEditorKit(new HTMLEditorKit());

        scr_view.getViewport().setPreferredSize(new Dimension(500, 400));
        scr_view.getViewport().add(txt_view, null);

        this.setLayout(new BorderLayout());
        this.add(scr_view, BorderLayout.CENTER);
    }

    /**
     * Set the version used for lookup
     */
    public void setBook(Book book)
    {
        this.book = book;
    }

    /**
     * Set the passage being viewed
     */
    public void setPassage(Passage ref) throws BookException, SAXException, TransformerException
    {
        this.ref = ref;

        if (ref == null || book == null)
        {
            txt_view.setText("");
            return;
        }

        BookData data = book.getData(ref);
        SAXEventProvider osissep = data.getSAXEventProvider();
        SAXEventProvider htmlsep = style.convert(osissep);
        String text = XMLUtil.writeToString(htmlsep);

        txt_view.setText(text);
        txt_view.select(0, 0);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return txt_view.getText();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getOSISSource()
     */
    public String getOSISSource()
    {
        if (ref == null || book == null)
        {
            return "";
        }

        try
        {
            BookData data = book.getData(ref);
            SAXEventProvider provider = data.getSAXEventProvider();
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

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getKey()
     */
    public Key getKey()
    {
        return ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#cut()
     */
    public void cut()
    {
        txt_view.cut();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#copy()
     */
    public void copy()
    {
        txt_view.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#paste()
     */
    public void paste()
    {
        txt_view.paste();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txt_view.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txt_view.removeHyperlinkListener(li);
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

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(InnerDisplayPane.class);

    /**
     * What version is currently being used for display
     */
    private Book book = null;

    /**
     * What was the last passage to be viewed
     */
    private Passage ref = null;

    private Converter style = new SimpleSwingConverter();
    private JScrollPane scr_view = new JScrollPane();
    private JEditorPane txt_view = new JEditorPane();
}
