
package org.crosswire.jsword.view.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * This is a quick demo of the Passage understanding capabilities
 * of the passage package.
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
public class Resolve extends Applet
{
    /**
    * Construct the applet containing the text filed and label
    */
    public Resolve()
    {
        setLayout(new BorderLayout());
        add("North", text);
        add("Center", messages);

        text.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { updateText(); }
        });
    }

    /**
    * Update the text area by attempting to understand the
    * text in it
    */
    public void updateText()
    {
        try
        {
            String input = text.getText();

            Passage ref = PassageFactory.createPassage(input);
            text.setText(ref.getName());
            messages.setText("Passage contains: "+ref.getOverview());
        }
        catch (Exception ex)
        {
            messages.setText(ex.getMessage());
        }
    }

    /** The input text editor */
    private TextField text = new TextField();

    /** For any messages */
    private Label messages = new Label();
}
