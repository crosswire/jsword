package org.crosswire.common.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextPane;

/**
 * An extension of JTextPane that does Anti-Aliasing.
 * J2SE5(joe): we will need to take a bit of care not clashing with J2SE5 AA
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
public class AntiAliasedTextPane extends JTextPane
{
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        if (antiAliasing)
        {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        super.paintComponent(g2);
    }

    /**
     * @return Returns the anti aliasing status.
     */
    public static boolean isAntiAliasing()
    {
        return antiAliasing;
    }

    /**
     * @param antiAliasing The new anti aliasing status.
     */
    public static void setAntiAliasing(boolean antiAliasing)
    {
        AntiAliasedTextPane.antiAliasing = antiAliasing;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256728398477734965L;

    /**
     * Do we anti-alias the text box?
     */
    private static boolean antiAliasing;
}
