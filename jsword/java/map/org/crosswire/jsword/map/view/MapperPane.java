
package org.crosswire.jsword.map.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.crosswire.jsword.map.model.Map;
import org.crosswire.jsword.map.model.MapEvent;
import org.crosswire.jsword.map.model.MapListener;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.common.util.LogicError;

/**
 * This is an Swing GUI interface to the BMap project.
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
public class MapperPane extends JPanel implements Scrollable
{
    /**
     * Basic Constructor
     */
    public MapperPane(Map map)
    {
        setMap(map);
        setSize(new Dimension(500, 500));
    }

    /**
     * Basic Constructor
     */
    public MapperPane()
    {
        setMap(null);
        setSize(new Dimension(500, 500));
    }

    /**
     * Setup a new map to view
     * @param map The new map to model
     */
    public void setMap(Map map)
    {
        if (map != null)
            map.removeMapListener(cml);

        this.map = map;

        if (map != null)
            map.addMapListener(cml);

        repaint();
    }

    /**
     * Get the map being viewed
     * @return The current map
     */
    public Map getMap()
    {
        return map;
    }

    /**
     * Set the way in which we color the map
     * @param versecolor The new map colorizer
     */
    public void setVerseColor(VerseColor versecolor)
    {
        this.versecolor = versecolor;
        repaint();
    }

    /**
     * Get the way in which we color the map
     * @return The current map colorizer
     */
    public VerseColor getVerseColor()
    {
        return versecolor;
    }

    /**
     * Do we force the height and width of this panel to be the same
     * @param lock_aspect The new aspect locking state
     */
    public void setLockAspectRation(boolean lock_aspect)
    {
        this.lock_aspect = lock_aspect;
    }

    /**
     * Do we force the height and width of this panel to be the same
     * @return The current aspect locking state
     */
    public boolean getLockAspectRation()
    {
        return lock_aspect;
    }

    /**
     * Paint the map
     * @param g The graphics instance to paint with
     */
    public void paintComponent(Graphics g)
    {
        g.setColor(versecolor.getBackground());
        g.fillRect(translateX(0), translateY(0), translateX(getWidth()), translateY(getHeight()));

        if (map == null)
        {
            g.setColor(Color.red);
            g.drawRect(translateX(0), translateY(0), translateX(getWidth()), translateY(getHeight()));
            g.drawLine(translateX(0), translateY(0), translateX(getWidth()), translateY(getHeight()));
            g.drawLine(translateX(getWidth()), translateY(0), translateX(0), translateY(getHeight()));

            return;
        }

        try
        {
            int[] pos;
            int[] prev;

            for (int b=1; b<=Books.booksInBible(); b++)
            {
                pos = getPosition(b, 1);

                Color col = versecolor.getForeground();
                g.setColor(col);
                g.drawString(Books.getShortBookName(b), pos[X], pos[Y]);

                col = versecolor.getColor(b, 1, 1);
                g.setColor(col);

                int cib = Books.chaptersInBook(b);
                for (int c=1; c<=cib; c++)
                {
                    pos = getPosition(b, 1);
                    g.fillOval(pos[X]-1, pos[Y]-1, 3, 3);

                    if (c != 1)
                    {
                        prev = getPosition(b, c-1);
                        pos = getPosition(b, c);

                        g.drawLine(prev[X], prev[Y], pos[X], pos[Y]);
                    }
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Get a (2D) position based on a verse ordinal
     * @param ord The verse id
     * @return A position array
     */
    private int[] getPosition(int book, int chapter)
    {
        float[] fpos = map.getPositionArrayCopy(book, chapter);
        return translate(fpos);
    }

    /**
     * Map a (0-1) float coordinate into the int based coordinate that we
     * can paint with
     * @param orig The (x,y) float array
     * @return A int position array
     */
    private final int[] translate(float[] orig)
    {
        int[] pos = new int[2];
        pos[X] = translateX(orig[X]);
        pos[Y] = translateX(orig[Y]);
        return pos;
    }

    /**
     * Map a (0-1) float width into the int based coordinate that we
     * can paint with
     * @param orig The width as a float
     * @return The width as an int
     */
    private final int translateX(float orig)
    {
        return (int) (orig*getWidth());
    }

    /**
     * Map a (0-1) float height into the int based coordinate that we
     * can paint with
     * @param orig The height as a float
     * @return The height as an int
     */
    private final int translateY(float orig)
    {
        return (int) (orig*getHeight());
    }

    /**
     * The size of this map
     * public void reshape(int x, int y, int w, int h)
     */
    public void setSize(Dimension x)
    {
        if (lock_aspect)
        {
            int max = Math.max(x.width, x.height);
            super.setSize(new Dimension(max, max));
        }
        else
        {
            super.setSize(x);
        }

        Dimension dim = getSize();

        super.setSize(dim);
        super.setMinimumSize(dim);
        super.setMaximumSize(dim);
        super.setPreferredSize(dim);

        revalidate();
    }

    /**
     * Get the unit or block increment in pixels. The Rectangle parameter
     * is the bounds of the currently visible rectangle. The first int
     * parameter is either SwingConstants.HORIZONTAL or
     * SwingConstants.VERTICAL depending on what scroll bar the user
     * clicked on. The second int parameter indicates which direction to
     * scroll. A value less than 0 indicates up or left. A value greater
     * than 0 indicates down or right.
     */
    public int getScrollableUnitIncrement(Rectangle rec, int bar, int dir)
    {
        if (bar == SwingConstants.HORIZONTAL)
            return getWidth() / 60;
        else
            return getHeight() / 60;
    }

    /**
     * Set the unit or block increment in pixels. The Rectangle parameter
     * is the bounds of the currently visible rectangle. The first int
     * parameter is either SwingConstants.HORIZONTAL or
     * SwingConstants.VERTICAL depending on what scroll bar the user
     * clicked on. The second int parameter indicates which direction to
     * scroll. A value less than 0 indicates up or left. A value greater
     * than 0 indicates down or right.
     */
    public int getScrollableBlockIncrement(Rectangle rec, int bar, int dir)
    {
        if (bar == SwingConstants.HORIZONTAL)
            return getWidth() / 6;
        else
            return getHeight() / 6;
    }

    /**
     * Get the preferred size of the viewport. This allows the client to
     * influence the size of the viewport in which it will be displayed.
     * If the viewport size is unimportant, implement this method to
     * return getPreferredSize.
     */
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    /**
     * If we are is displayed in a JViewport, don't change its width
     * when the viewports width changes.  This allows horizontal
     * scrolling if the JViewport is itself embedded in a JScrollPane.
     * @return False - don't track the viewports width.
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportWidth()
    {
        /*
        // Code to automatically grow the map if the window is too big
        if (getParent() instanceof JViewport)
        {
            return ((JViewport) getParent()).getWidth() > getPreferredSize().width;
        }
        */

        return false;
    }

    /**
     * If we are is displayed in a JViewport, don't change its height
     * when the viewports height changes.  This allows vertical
     * scrolling if the JViewport is itself embedded in a JScrollPane.
     * @return False - don't track the viewports width.
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportHeight()
    {
        /*
        // Code to automatically grow the map if the window is too big
        if (getParent() instanceof JViewport)
        {
            return ((JViewport) getParent()).getHeight() > getPreferredSize().height;
        }
        */

        return false;
    }

    /** constant for the X part */
    private static final int X = 0;

    /** constant for the Y part */
    private static final int Y = 1;

    /** The Map that we are viewing */
    private Map map;

    /** Do we force height and width to be the same */
    private boolean lock_aspect = true;

    /** The map listener */
    private CustomMapListener cml = new CustomMapListener();

    /** The VerseColorizer */
    private VerseColor versecolor = new RainbowVerseColor();

    /**
    * Sync the map and the table
    */
    class CustomMapListener implements MapListener
    {
        /**
        * This method is called to indicate that a node on the map has
        * moved.
        * @param ev Describes the change
        */
        public void mapChanged(final MapEvent ev)
        {
            if (!SwingUtilities.isEventDispatchThread())
            {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() { mapChanged(ev); }
                });
                return;
            }

            repaint();
        }

        /**
        * This method is called to indicate that the whole map has changed
        * @param ev Describes the change
        */
        public void mapRewritten(final MapEvent ev)
        {
            if (!SwingUtilities.isEventDispatchThread())
            {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() { mapRewritten(ev); }
                });
                return;
            }

            repaint();
        }
    }
}
