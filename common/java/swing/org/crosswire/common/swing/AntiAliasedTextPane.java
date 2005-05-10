/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.common.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextPane;

/**
 * An extension of JTextPane that does Anti-Aliasing.
 * J2SE5(joe): we will need to take a bit of care not clashing with J2SE5 AA
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
