
package org.crosswire.common.swing;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JFrame;

/**
 * This class simplifies running Applets as applications
 * It mirrors the actions of a Browser in an application.
 * The methods without specific JavaDoc comments mirror the methods
 * of AppletStub and AppletContext, returning null, true, this or ""
 * where appropriate.
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
public class AppletFrame extends JFrame
{
    /**
     * Creates a Frame and runs an Applet in the frame.
     * Mirrors the actions of a Browser in an application.
     * @param name The text that should appear in the title bar.
     * @param app The Applet that we should run in the new frame.
     * @param width The horizontal size of the frame.
     * @param height The vertical size of the frame.
     */
    public AppletFrame(String name, Applet app, int width, int height)
    {
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });

        app.setStub(new AppletFrameStub());
        app.init();
        app.start();

        /*getContentPane().setLayout(new BorderLayout()); */
        getContentPane().add("Center", app);

        setTitle(name);
        setSize(width, height);
        setVisible(true);
    }

    /**
     * Creates a Frame and runs an Applet in the frame.
     * The frame is given the default name of the Applet Class.
     * @param app The Applet that we should run in the new frame.
     * @param width The horizontal size of the frame.
     * @param height The vertical size of the frame.
     */
    public AppletFrame(Applet app, int width, int height)
    {
        this(app.getClass().getName(), app, width, height);
    }

    /**
     * Creates a Frame and runs an Applet in the frame.
     * The frame is given the default name of the Applet Class.
     * @param name The text that should appear in the title bar.
     * @param app The Applet that we should run in the new frame.
     */
    public AppletFrame(String name, Applet app)
    {
        this(name, app, 100, 100);

        Dimension x = app.getPreferredSize();
        setSize(x.width, x.height);
    }

    /**
     * Creates a Frame and runs an Applet in the frame.
     * The frame is given the default name of the Applet Class.
     * @param app The Applet that we should run in the new frame.
     */
    public AppletFrame(Applet app)
    {
        this(app.getClass().getName(), app);
    }

    /**
     * Close everything down and exit from the JVM
     */
    public void close()
    {
        dispose();
        System.exit(0);
    }

    /**
     * So that we can be an AppletStub
     */
    public class AppletFrameStub implements AppletStub
    {
        /* (non-Javadoc)
         * @see java.applet.AppletStub#isActive()
         */
        public boolean isActive()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see java.applet.AppletStub#getDocumentBase()
         */
        public URL getDocumentBase()
        {
            return null;
        }

        /* (non-Javadoc)
         * @see java.applet.AppletStub#getCodeBase()
         */
        public URL getCodeBase()
        {
            return null;
        }

        /* (non-Javadoc)
         * @see java.applet.AppletStub#getParameter(java.lang.String)
         */
        public String getParameter(String name)
        {
            return "";
        }

        /* (non-Javadoc)
         * @see java.applet.AppletStub#getAppletContext()
         */
        public AppletContext getAppletContext()
        {
            return null;
        }

        /* (non-Javadoc)
         * @see java.applet.AppletStub#appletResize(int, int)
         */
        public void appletResize(int w, int h)
        {
        }
    }
}
