/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.crosswire.jsword.view.swing.display;

import java.awt.Component;
import java.awt.event.MouseListener;
import javax.swing.event.HyperlinkListener;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;

/**
 * @author Joe
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface BookDataDisplay
{
    /**
     * @param data
     */
    public void setBookData(BookData data) throws BookException
    ;

    /**
     * Accessor for the Swing component
     */
    public Component getComponent()
    ;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#copy()
     */public void copy()
    ;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */public void addHyperlinkListener(HyperlinkListener li)
    ;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */public void removeHyperlinkListener(HyperlinkListener li)
    ;

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    ;

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    ;

    /**
     * TODO: get rid of this method
     */
    public String getHTMLSource()
    ;
}