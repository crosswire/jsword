
package org.crosswire.common.util;

/**
 * This package looks after Exceptions as they happen.
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
public class LogUtil
{
    /**
     * Simple ctor for use only with getLogger(String)
     */
    private LogUtil()
    {
    }

    /**
     * Gets a short HTML description of an Exception for display in a
     * window
     */
    public static String getHTMLDescription(Throwable ex)
    {
        StringBuffer retcode = new StringBuffer();

        // The message in the exception
        String message = ex.getMessage();
        if (message == null || message.equals(""))
            message = "No description available";
        message = StringUtil.swap(message, "\n", "<br>");

        // The name of the exception
        String classname = ex.getClass().getName();
        int lastdot = classname.lastIndexOf('.');
        if (lastdot != -1)
            classname = classname.substring(lastdot+1);
        if (classname.endsWith("Exception") && classname.length() > "Exception".length())
            classname = classname.substring(0, classname.length() - "Exception".length());
        if (classname.endsWith("Error") && classname.length() > "Error".length())
            classname = classname.substring(0, classname.length() - "Error".length());
        classname = StringUtil.createTitle(classname);
        if (classname.equals("IO")) classname = "Input / Output";

        retcode.append("<font size=\"-1\"><strong>");
        retcode.append(classname);
        retcode.append("</strong></font><br>");
        retcode.append(message);

        // If this is a LucidException with a nested Exception
        if (ex instanceof LucidException)
        {
            Throwable nex = ((LucidException) ex).getCause();
            if (nex != null)
            {
                retcode.append("<p><br><font size=\"-1\">This was caused by: </font>");
                retcode.append(getHTMLDescription(nex));
            }
        }

        return retcode.toString();
    }
}
