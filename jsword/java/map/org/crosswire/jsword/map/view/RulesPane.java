
package org.crosswire.jsword.map.view;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.map.model.Rule;

/**
 * RulesPane displays an array of Rules and allows the user to select the
 * scale used for each of them. I wanted to make this a JScrollPAne, but it
 * doesn't like being of any size so I swapped back to JPanel for the time being.
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
public class RulesPane extends JPanel
{
    /**
     * Basic Constructor
     */
    public RulesPane(Rule[] rules)
    {
        add(pnl_main);

        pnl_main.setLayout(new BoxLayout(pnl_main, BoxLayout.Y_AXIS));

        for (int i=0; i<rules.length; i++)
        {
            pnl_main.add(new RuleSlider(rules[i]));
        }
    }

    /**
     * Method setRules.
     * @param rules
     */
    public void setRules(Rule[] rules)
    {
        pnl_main.removeAll();

        for (int i=0; i<rules.length; i++)
        {
            pnl_main.add(new RuleSlider(rules[i]));
        }

        GuiUtil.restrainedRePack(GuiUtil.getWindow(this));
    }
    
    private JPanel pnl_main = new JPanel();
}
