package org.crosswire.jsword.view.swing.book;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Logger;

/**
 * This trivial singleton class is used as a means of finding
 * BookActionFactory.properties and calling methods in listening classes.
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
 * @author DM Smith [ dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public final class BookActionFactory extends ActionFactory
{
    private static final String UNEXPECTED_ERROR = "Stupid Programmer Error"; //$NON-NLS-1$
    private static final String METHOD_PREFIX = "do"; //$NON-NLS-1$

    private BookActionFactory()
    {
        super();
    }

    public static BookActionFactory instance()
    {
        if (instance == null)
        {
            instance = new BookActionFactory();
        }
        return instance;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e, Object caller)
    {
        String action = e.getActionCommand();

        assert action != null;
        assert action.length() != 0;

        // Instead of cascading if/then/else
        // use reflecton to do a direct lookup and call
        try
        {
            Method doMethod = caller.getClass().getDeclaredMethod(METHOD_PREFIX+action, new Class[] { });
            doMethod.invoke(caller, null);
        }
        catch (NoSuchMethodException e1)
        {
            // assume some other class is listening for it
        }
        catch (IllegalArgumentException e2)
        {
            log.error(UNEXPECTED_ERROR, e2);
        }
        catch (IllegalAccessException e3)
        {
            log.error(UNEXPECTED_ERROR, e3);
        }
        catch (InvocationTargetException e4)
        {
            log.error(UNEXPECTED_ERROR, e4);
        }
    }

    private static BookActionFactory instance;
    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BookActionFactory.class);

}
