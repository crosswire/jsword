
package org.crosswire.jsword.util;

import org.apache.log4j.Logger;

/**
 * The Project class looks after the source of project files.
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
 * @version D0.I0.T0
 */
public class Project
{
    /**
     * This is a simple method that all code should call as it begins. We
     * can put various bits of necessary setup in here. It should not
     * matter if this method is called twice, although in case this
     * requirement becomes to hard we also should not write code that
     * calls it more than once.
     */
    public static void init()
    {
        init("");
    }

    /**
     * This is a simple method that all code should call as it begins.
     * We can put various bits of necessary setup in here. It should not
     * matter if this method is called twice, although in case this
     * requirement becomes to hard we also should not write code that
     * calls it more than once.
     * <p>The biggest job is trying to work out which resource bundle to
     * load to work out where the config and data files are stored.
     * We construct a name from the projectname, hostname and any other
     * info and then try to use that.
     */
    public static void init(String base)
    {
        resource = new Resource(base);

        // Some defaults while we get started
        // StdOutCaptureListener.setHelpDeskInformListener(true);
        // StdOutCaptureListener.setHelpDeskLogListener(true);
    }

    /**
     * I want this to become a proper singleton. This is the accessor.
     */
    public static Resource resource()
    {
        if (resource == null)
            log.error("attempt to use Resource before project is init()ed");
            
        return resource;
    }

    /**
     * The name of this project.
     */
    public static String getName()
    {
        return "JSword";
    }

    /**
     * The name of this project.
     */
    public static String getVersion()
    {
        return "0.9.2";
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(Project.class);

    /**
     * The filesystem resources
     */
    private static Resource resource = null;
}
