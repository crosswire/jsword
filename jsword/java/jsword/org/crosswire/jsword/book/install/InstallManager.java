package org.crosswire.jsword.book.install;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.util.Project;

/**
 * A manager to abstract out the non-view specific module installation tasks.
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class InstallManager
{
    /**
     * Simple ctor
     */
    public InstallManager() throws InstallException
    {
        try
        {
            Properties sitemap = Project.instance().getProperties(getClass().getName());
            Map entries = Project.instance().getImplementorsMap(Installer.class);

            for (Iterator it = sitemap.keySet().iterator(); it.hasNext();)
            {
                String name = (String) it.next();
                String url = sitemap.getProperty(name);

                try
                {
                    String[] parts = url.split(":");
                    String type = parts[0];

                    Class clazz = (Class) entries.get(type);
                    Installer installer = (Installer) clazz.newInstance();
                    installer.init(name, url);

                    installers.add(installer);
                }
                catch (Exception ex)
                {
                    Reporter.informUser(this, ex);
                }
            }
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.INIT, ex);
        }
    }

    /**
     * Accessor for the known installers
     */
    public Installer[] getInstallers()
    {
        return (Installer[]) installers.toArray(new Installer[installers.size()]);
    }

    /**
     * The list of discovered installers
     */
    private List installers = new ArrayList();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(InstallManager.class);
}
