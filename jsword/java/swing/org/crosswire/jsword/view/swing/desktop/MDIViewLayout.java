
package org.crosswire.jsword.view.swing.desktop;

import java.awt.Component;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.log4j.Logger;
import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * MDI manager of how we layout views.
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
public class MDIViewLayout extends ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public MDIViewLayout(Desktop tools)
    {
        super(tools);
    }

    /**
     * What should the desktop add to the parent?
     */
    public Component getRootComponent()
    {
        return mdi_main;
    }

    /**
     * Add a view to the set while visible
     */
    public boolean add(BibleViewPane view)
    {
        String name = view.getTitle();

        JInternalFrame iframe = new JInternalFrame(name, true, true, true, true);
        iframe.getContentPane().add(view);

        mdi_main.add(iframe/*, JLayeredPane.PALETTE_LAYER*/);

        iframe.addInternalFrameListener(new CustomInternalFrameAdapter());

        iframe.setVisible(true);
        iframe.pack();

        return true;
    }

    /**
     * Remove a view from the set while visible
     */
    public boolean remove(BibleViewPane view)
    {
        JInternalFrame iframe = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, view);
        iframe.dispose();

        return true;
    }

    /**
     * Remove a view from the set while visible
     */
    public void updateTitle(BibleViewPane view)
    {
        JInternalFrame iframe = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, view);
        iframe.setTitle(view.getTitle());
    }

    /**
     * While visible, which is the current pane
     */
    public BibleViewPane getSelected()
    {
        JInternalFrame frame = mdi_main.getSelectedFrame();

        if (frame == null)
        {
            // none of the frames are selected, but things like cut/copy/paste
            // rely on there being a 'current' BibleViewPane so we just use the
            // first one we find, which might be the top one?
            Component[] comps = mdi_main.getComponents();
            for (int i=0; i<comps.length; i++)
            {
                if (comps[i] instanceof JInternalFrame)
                {
                    frame = (JInternalFrame) comps[i];
                    break;
                }
            }
        }

        Component comp = frame.getContentPane().getComponent(0);
        return (BibleViewPane) comp;
    }

    /** The log stream */
    private static Logger log = Logger.getLogger(Desktop.class);

    private JDesktopPane mdi_main = new JDesktopPane();

    /**
     * So we can tidy things up when a window is closed
     */
    private class CustomInternalFrameAdapter extends InternalFrameAdapter
    {
        public void internalFrameClosed(InternalFrameEvent ev)
        {
            JInternalFrame iframe = ev.getInternalFrame();
            BibleViewPane view = (BibleViewPane) iframe.getContentPane().getComponent(0);

            // calling remove() will can result in the window being closed as
            // a result of it being dispose()ed and we don't want to get into an
            // infinite loop so we nned to stop listening
            iframe.removeInternalFrameListener(this);

            remove(view);
        }
    }
}
