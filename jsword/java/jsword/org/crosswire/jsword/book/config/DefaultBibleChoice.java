
package org.crosswire.jsword.book.config;

import java.io.IOException;

import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.common.config.choices.AbstractChoice;

/**
 * The default Bible Choice.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version D5.I8.T0
 */
public class DefaultBibleChoice extends AbstractChoice
{
    /**
     * Get the name of the default version
     */
    public String getString()
    {
        try
        {
            return Bibles.getDefaultName();
        }
        catch (BookException ex)
        {
            return "#Error#";
        }
    }

    /**
     * Set the name of the default version
     */
    public void setString(String data) throws BookException, IOException
    {
        Bibles.setDefaultName(data);
    }

    /**
     * How should this Choice be edited
     */
    public String getType()
    {
        return "options";
    }

    /**
     * What can we tell the editor to help it present the user with a
     * good way of editing this choice
     */
    public Object getTypeOptions()
    {
        try
        {
            return Bibles.getBibleNames();
        }
        catch (BookException ex)
        {
            return new String[0];
        }
    }

    /**
     * Fetch some basic help text
     */
    public String getHelpText()
    {
        return "Which of the available Bibles is the default.";
    }
}
