
package org.crosswire.jsword.map.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.map.model.Rule;

/**
 * RuleSlider allows the user to edit the scale for a given rule.
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
public class RuleSlider extends JPanel
{
    /**
     * Basic constructor
     */
    public RuleSlider(Rule rule)
    {
        this.rule = rule;

        jbInit();

        String fullname = rule.getClass().getName();
        int last_dot = fullname.lastIndexOf('.');
        if (last_dot == -1) last_dot = 0;
        title = fullname.substring(last_dot+1);
        bdr_rule.setTitle(title);

        sdr_rule.setValue(rule.getScale());
        txt_rule.setText(""+rule.getScale());
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        bdr_rule = BorderFactory.createTitledBorder("Rule");

        sdr_rule.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                changed();
            }
        });
        sdr_rule.setPaintLabels(true);
        sdr_rule.setPaintTicks(true);
        sdr_rule.setMinorTickSpacing(16);
        sdr_rule.setMajorTickSpacing(32);
        sdr_rule.setMaximum(256);
        sdr_rule.setOrientation(SwingConstants.HORIZONTAL);
        sdr_rule.setValue(0);

        txt_rule.setText("256");
        txt_rule.setEditable(false);

        this.setLayout(new BorderLayout());
        this.setBorder(bdr_rule);
        this.add(sdr_rule, BorderLayout.CENTER);
        this.add(txt_rule, BorderLayout.EAST);
    }

    /**
     * When someone slides the slider
     */
    protected void changed()
    {
        rule.setScale(sdr_rule.getValue());

        int check = rule.getScale();
        if (check != sdr_rule.getValue())
            sdr_rule.setValue(check);

        txt_rule.setText(""+check);

        log.info(title+": "+check);
    }

    /** The rule that we notify of any changes */
    private Rule rule;

    /* GUI Components */
    private JSlider sdr_rule = new JSlider();
    private JTextField txt_rule = new JTextField(3);
    private String title = "-";

    private TitledBorder bdr_rule;

    /** The log stream */
    private static final Logger log = Logger.getLogger(RuleSlider.class);
}
