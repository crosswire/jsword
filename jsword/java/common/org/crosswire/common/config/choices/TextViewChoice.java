
package org.crosswire.common.config.choices;

import java.net.URL;

import org.crosswire.common.config.*;

/**
* The Log file stylizer.
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
*/
public class TextViewChoice extends AbstractChoice
{
    /**
    * Create a TextViewChoice with a URL to view
    */
    public TextViewChoice(URL url)
    {
        this.url = url;
    }

    /**
    * There is a good change that this will be a read-only option
    * so the default is to do nothing with any values
    */
    public void setString(String value)
    {
    }

    /**
    * There is a good change that this will be a read-only option
    * so the default is to do nothing with any values
    */
    public String getString()
    {
        return "";
    }

    /**
    * Point at a TextViewPanel if possible
    */
    public String getType()
    {
        return "file";
    }

    /**
    * This method is used to configure a the type selected above.
    * @return a configuration parameter for the type
    */
    public Object getTypeOptions()
    {
        return url;
    }

    /** The URL for the text file to view */
    private URL url;
}
