package org.crosswire.common.swing;

import java.awt.Rectangle;
import java.lang.reflect.Method;

import javax.swing.JEditorPane;
import javax.swing.JToolBar;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.crosswire.common.util.Logger;

/**
 * JDK: Make up for problems with 1_3.
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
public class BackportUtil
{
    /**
     * Prevent Instansiation
     */
    private BackportUtil()
    {
    }

    /**
     * Scrolls the view to the given reference
     * @param reference the named location to scroll to
     * @param pane The JEditorPane to work on
     * @see JEditorPane#scrollToReference(java.lang.String)
     */
    public static void scrollToReference(String reference, JEditorPane pane)
    {
        // 1_4: just do -
        // pane.scrollToReference(url);

        Document doc = pane.getDocument();
        if (doc instanceof HTMLDocument)
        {
            HTMLDocument hdoc = (HTMLDocument) doc;
            HTMLDocument.Iterator it = hdoc.getIterator(HTML.Tag.A);
            while (it.isValid())
            {
                AttributeSet attrs = it.getAttributes();
                String name = (String) attrs.getAttribute(HTML.Attribute.NAME);
                if ((name != null) && name.equals(reference))
                {
                    scrollToOffset(pane, it.getStartOffset());
                    return;
                }

                it.next();
            }

            log.warn("missing reference looking for "+reference+". Found:"); //$NON-NLS-1$ //$NON-NLS-2$
            /*
            it = hdoc.getIterator(HTML.Tag.A);
            while (it.isValid())
            {
                AttributeSet attrs = it.getAttributes();
                log.debug("  "+attrs.toString());
                it.next();
            }
            */
        }
    }

    /**
     * Helper to scroll to an offset within a Document
     */
    private static void scrollToOffset(JEditorPane pane, int offset)
    {
        // found a matching reference in the document.
        try
        {
            Rectangle rect = pane.modelToView(offset);
            if (rect != null)
            {
                // the view is visible, scroll it to the
                // center of the current visible area.
                Rectangle vis = pane.getVisibleRect();

                //r.y -= (vis.height / 2);
                rect.height = vis.height;
                pane.scrollRectToVisible(rect);

                log.debug("scrolling to: "+rect); //$NON-NLS-1$
            }
            else
            {
                log.warn("rect == null"); //$NON-NLS-1$
            }
        }
        catch (BadLocationException ex)
        {
            log.warn("Bad location", ex); //$NON-NLS-1$
        }
    }

    /**
     * JToolBar.setRollover(boolean) is not supported in 1.3, instead we use reflection
     * to find out whether the method is available, if so call it.
     */
    public static void setRollover(JToolBar pnl_tbar, boolean value)
    {
        try
        {
            Class cl = pnl_tbar.getClass();
            Method method = cl.getMethod("setRollover", new Class[] { Boolean.TYPE }); //$NON-NLS-1$
            method.invoke(pnl_tbar, new Object[] { value ? Boolean.TRUE : Boolean.FALSE });
        }
        catch (NoSuchMethodException ex)
        {
            // ignore
        }
        catch (Exception ex)
        {
            // we don't expect this one, print a stack trace
            log.warn("unexpected", ex); //$NON-NLS-1$
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BackportUtil.class);
}
