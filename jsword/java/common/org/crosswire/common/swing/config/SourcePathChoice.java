
package org.crosswire.common.swing.config;

import java.io.File;

import org.crosswire.common.config.choices.StringArrayChoice;
import org.crosswire.common.swing.DetailedExceptionPane;
import org.crosswire.common.util.UserLevel;

/**
* The SourcePathChoice creates a Choice that
* controls the paths that source is stored in.
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
* @see DetailedExceptionPane
* @author Joe Walker
*/
public class SourcePathChoice extends StringArrayChoice
{
    /** Setup the separator to be used */
    public SourcePathChoice()
    {
        super(File.pathSeparator);
    }

    /** Read the save setting from DetailedExceptionPane */
    public String[] getArray()
    {
        return DetailedExceptionPane.getSourcePath();
    }

    /** Save the save setting to UserLevel */
    public void setArray(String[] value)
    {
        DetailedExceptionPane.setSourcePath(value);
    }

    /** Some help text */
    public String getHelpText()
    {
        return "The directories to search for source code in when investigating an exception.";
    }

    /** This is an advanced level option */
    public int getUserLevel()
    {
        return UserLevel.LEVEL_ADVANCED;
    }
}

