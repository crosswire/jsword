
package org.crosswire.common.swing.config;

import java.net.URL;

import org.crosswire.common.config.Config;
import org.crosswire.common.config.choices.AbstractChoice;
import org.crosswire.common.config.choices.BooleanChoice;
import org.crosswire.common.config.swing.FieldMap;
import org.crosswire.common.swing.Status;

/**
* A collection of things that could be configured for the Status window
* and the general monitor system.
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
public class MonitorChoices
{
    /**
    * Do we show the status window
    */
    public static class WindowChoice extends BooleanChoice
    {
        public WindowChoice(Config config, URL url) { this.config = config; this.url = url; }
        public boolean getBoolean()               { return Status.getWindowVisible(); }
        public void setBoolean(boolean value)     { Status.setWindowVisible(value, config, url); }
        public String getHelpText()               { return "Make the status window visible."; }
        private Config config;
        private URL url;
    }

    /**
    * The Log file stylizer
    */
    public static class LogStyleChoice extends AbstractChoice
    {
        public LogStyleChoice(int index)        { this.index = index; }
        public void setString(String value)     { Status.setLogStyle(index, value); }
        public String getString()               { return Status.getLogStyle(index); }
        public String getHelpText()             { return "The style associated with a match in the log reporter."; }
        public String getType()                 { return "style"; }
        private int index;
        static
        {
            FieldMap.getHashtable().put("style", StyleField.class);
        }
    }
}

