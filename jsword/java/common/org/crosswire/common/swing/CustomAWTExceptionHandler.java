
package org.crosswire.common.swing;

import java.awt.Component;
import java.util.Properties;

import org.crosswire.common.util.Reporter;

/**
 * Handle AWT exceptions that reach the event thread.
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
public class CustomAWTExceptionHandler
{
    /**
     * Its important that we have a no-arg ctor to make this work
     */
    public CustomAWTExceptionHandler()
    {
    }

    /**
     * Handle AWT exceptions
     */
    public void handle(final Throwable ex)
    {
        Reporter.informUser(this, ex);

        /* This is done by the above
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ExceptionPane.showExceptionDialog(comp, ex);
            }
        });
        */
    }
    
    /**
     * Sets the parent of any exception windows.
     * @param comp The comp to set
     */
    public static void setParentComponent(Component comp)
    {
        if (comp != null)
        {
            // register ourselves
            System.setProperty(AWT_HANDLER_PROPERTY, OUR_NAME);
        }
        else
        {
            // deregister ourselves
            String current = System.getProperty(AWT_HANDLER_PROPERTY);
            if (current.equals(OUR_NAME))
            {
                Properties prop = System.getProperties();
                prop.remove(AWT_HANDLER_PROPERTY);
            }
        }

        CustomAWTExceptionHandler.comp = comp;
    }

    private static final String AWT_HANDLER_PROPERTY = "sun.awt.exception.handler";
    private static final String OUR_NAME = CustomAWTExceptionHandler.class.getName();

    protected static Component comp = null;
}
