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

/**
 * Make up for problems with JDK1.3
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class BackportUtil
{
    /**
     * Scrolls the view to the given reference location
     * (that is, the value returned by the <code>UL.getRef</code>
     * method for the URL being displayed).  By default, this
     * method only knows how to locate a reference in an
     * HTMLDocument.  The implementation calls the
     * <code>scrollRectToVisible</code> method to
     * accomplish the actual scrolling.  If scrolling to a
     * reference location is needed for document types other
     * than HTML, this method should be reimplemented.
     * This method will have no effect if the component
     * is not visible.
     * @param reference the named location to scroll to
     * @param pane The JEditorPane to work on
     * @see JEditorPane#scrollToReference(java.lang.String)
     */
    public static void scrollToReference(String reference, JEditorPane pane)
    {
        // JDK14: just do -
        // pane.scrollToReference(url);

        Document d = pane.getDocument();
        if (d instanceof HTMLDocument)
        {
            HTMLDocument doc = (HTMLDocument) d;
            HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A);
            for (; iter.isValid(); iter.next())
            {
                AttributeSet a = iter.getAttributes();
                String nm = (String) a.getAttribute(HTML.Attribute.NAME);
                if ((nm != null) && nm.equals(reference))
                {
                    // found a matching reference in the document.
                    try
                    {
                        Rectangle r = pane.modelToView(iter.getStartOffset());
                        if (r != null)
                        {
                            // the view is visible, scroll it to the 
                            // center of the current visible area.
                            Rectangle vis = pane.getVisibleRect();

                            //r.y -= (vis.height / 2);
                            r.height = vis.height;
                            pane.scrollRectToVisible(r);
                        }
                    }
                    catch (BadLocationException ble)
                    {
                        ble.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * JToolBar.setRollover(boolean) is not supported in JDK1.3, instead we use reflection
     * to find out whether the method is available, if so call it.
     */
    public static void setRollover(JToolBar pnl_tbar, boolean value)
    {
        try
        {
            Class cl = pnl_tbar.getClass();
            Method method = cl.getMethod("setRollover", new Class[] { Boolean.TYPE });
            method.invoke(pnl_tbar, new Object[] { value ? Boolean.TRUE : Boolean.FALSE });
        }
        catch (NoSuchMethodException ex)
        {
            // ignore
        }
        catch (Exception ex)
        {
            // we don't expect this one, print a stack trace
            ex.printStackTrace();
        }
    }
}
