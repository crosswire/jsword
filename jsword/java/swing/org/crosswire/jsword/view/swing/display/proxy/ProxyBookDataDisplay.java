package org.crosswire.jsword.view.swing.display.proxy;

import java.awt.Component;
import java.awt.event.MouseListener;

import javax.swing.event.HyperlinkListener;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;

/**
 * An implementation of BookDataDisplay that simply proxies all requests to an
 * underlying BookDataDisplay.
 * <p>Useful for chaining a few BookDataDisplays together to add functionallity
 * component by component.</p>
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
public class ProxyBookDataDisplay implements BookDataDisplay
{
    /**
     * Setup the proxy
     */
    public ProxyBookDataDisplay(BookDataDisplay proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Accessor for the proxy
     * @return Returns the proxy.
     */
    public BookDataDisplay getProxy()
    {
        return proxy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        proxy.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#addMouseListener(java.awt.event.MouseListener)
     */
    public void addMouseListener(MouseListener li)
    {
        proxy.addMouseListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#copy()
     */
    public void copy()
    {
        proxy.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return proxy.getComponent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return proxy.getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        proxy.removeHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#removeMouseListener(java.awt.event.MouseListener)
     */
    public void removeMouseListener(MouseListener li)
    {
        proxy.removeMouseListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.BookData)
     */
    public void setBookData(BookData data) throws BookException
    {
        proxy.setBookData(data);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return proxy.toString();
    }

    /**
     * The component to which we proxy
     */
    private BookDataDisplay proxy = null;
}
