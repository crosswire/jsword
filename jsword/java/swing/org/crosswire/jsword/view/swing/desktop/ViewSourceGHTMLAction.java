package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;

import org.crosswire.common.swing.TextViewPanel;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.StringSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.view.swing.display.FocusablePart;
import org.crosswire.jsword.view.swing.util.SimpleSwingConverter;

/**
 * View the HTML source to the current window.
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
public class ViewSourceGHTMLAction extends DesktopAbstractAction
{
    /**
     * Setup configuration
     */
    public ViewSourceGHTMLAction(Desktop tools)
    {
        super(tools,
              "View G-HTML Source",
              null,
              null,
              "View Generated HTML Source", "View the generated HTML source to the current window",
              'H', null);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            FocusablePart da = getDesktop().getDisplayArea();
            String osis = da.getOSISSource();
            Key ref = da.getKey();

            if (osis == null || osis.equals("") || ref == null)
            {
                Reporter.informUser(this, "No Generated HTML source to view.");
                return;
            }

            SAXEventProvider osissep = new StringSAXEventProvider(osis);
            SAXEventProvider htmlsep = style.convert(osissep);
            String html = XMLUtil.writeToString(htmlsep);
            
            TextViewPanel viewer = new TextViewPanel(html, "Generated source to " + ref.getName());
            viewer.setEditable(true);
            viewer.showInFrame(getDesktop().getJFrame());
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The stylizer
     */
    private Converter style = new SimpleSwingConverter();
}
