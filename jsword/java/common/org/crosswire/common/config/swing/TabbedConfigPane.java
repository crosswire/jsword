
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.crosswire.common.config.Config;
import org.crosswire.common.swing.FormPane;

/**
* Some static methods for using the Config package.
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
public class TabbedConfigPane extends PanelConfigPane
{
    /**
    * Create a Config base with the set of Fields that it will
    * display.
    */
    public TabbedConfigPane(Config config)
    {
        super(config);
    }

    /**
    * Now this wasn't created with JBuilder but maybe, just maybe, by
    * calling my method this, JBuilder may grok it.
    */
    protected void jbInit()
    {
        JComponent bar = getButtonPane();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add("South", bar);
    }

    /**
    * Update the tree structure
    */
    protected void updateTree()
    {
        if (tab != null) remove(tab);
        tab = new JTabbedPane();

        Enumeration en = config.getPaths();
        while (en.hasMoreElements())
        {
            String path = (String) en.nextElement();
            // log.fine("TAB: path="+path);

            JTabbedPane nest = tab;
            StringTokenizer st = new StringTokenizer(path, ".");
            while (st.hasMoreTokens())
            {
                String name = st.nextToken();
                int index = nest.indexOfTab(name);
                // log.fine("  name="+name+" index="+index+" hasMoreTokens="+st.hasMoreTokens());

                // We don't want to create a tab for the last branch
                if (st.hasMoreTokens())
                {
                    if (index == -1)
                    {
                        JTabbedPane sub = new JTabbedPane();
                        sub.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                        nest.addTab(name, tab_icon, sub);
                        // log.fine("  Added named tab");
                        nest = sub;
                    }
                    else
                    {
                        Component comp = nest.getComponentAt(index);
                        if (comp instanceof JTabbedPane)
                        {
                            nest = (JTabbedPane) comp;
                            // log.fine("  Drilling to tab");
                        }
                        else
                        {
                            // log.fine("  Downgrading and adding tab");
                            JTabbedPane sub = new JTabbedPane();
                            sub.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                            nest.setComponentAt(index, sub);
                            nest = sub;
                            nest.addTab("Basic", tab_icon, comp);
                        }
                    }
                }
                else
                {
                    if (index == -1)
                    {
                        // log.fine("  Adding named panel");
                        FormPane card = (FormPane) decks.get(path);
                        if (card != null)
                        {
                            card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                            nest.addTab(name, tab_icon, card);
                        }
                    }
                    else
                    {
                        // log.fine("  Adding Basic panel");
                        Component comp = nest.getComponentAt(index);
                        if (comp instanceof JTabbedPane)
                        {
                            nest = (JTabbedPane) comp;
                            FormPane card = (FormPane) decks.get(path);
                            if (card != null)
                            {
                                card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                                nest.addTab("Basic", tab_icon, card);
                            }
                        }
                        // else log.fine("  Warning skipping comp="+comp.getClass().getName());
                    }
                }
            }
        }

        add("Center", tab);
    }

    /** The tabbed pane */
    private JTabbedPane tab;

    /** Set this to task_small to get icons on the tabs. I don't like it */
    protected static ImageIcon tab_icon;
}
