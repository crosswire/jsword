package org.crosswire.jsword.view.swing.display.textpane;

import java.awt.Component;
import java.awt.event.MouseListener;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;

/**
 * A JDK JTextPane implementation of an OSIS displayer.
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
public class TextPaneBookDataDisplay implements BookDataDisplay
{
    /**
     * Simple ctor
     */
    public TextPaneBookDataDisplay()
    {
        txtView.setEditable(false);
        txtView.setEditorKit(new HTMLEditorKit());
    }

    /**
     * @param data
     */
    public void setBookData(BookData data) throws BookException
    {
        try
        {
            if (data == null)
            {
                txtView.setText(""); //$NON-NLS-1$
                return;
            }

            SAXEventProvider osissep = data.getSAXEventProvider();
            SAXEventProvider htmlsep = converter.convert(osissep);
            String text = XMLUtil.writeToString(htmlsep);

            txtView.setText(text);
            txtView.select(0, 0);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.TRANSFORM_FAIL, ex);
        }
    }

    /**
     * Accessor for the Swing component
     */
    public Component getComponent()
    {
        return txtView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#copy()
     */
    public void copy()
    {
        txtView.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txtView.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txtView.removeHyperlinkListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    {
        txtView.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    {
        txtView.addMouseListener(li);
    }

    /**
     * TODO: get rid of this method
     */
    public String getHTMLSource()
    {
        return txtView.getText();
    }

    /**
     * To convert OSIS to HTML
     */
    private Converter converter = ConverterFactory.getConverter();

    /**
     * The display component
     */
    private JTextPane txtView = new JTextPane();
}
