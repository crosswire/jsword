
package org.crosswire.jsword.map.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.LogicError;

/**
 * A map is an array of Nodes (verses with position).
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
public class Map implements Serializable
{
    /**
     * Basic constructor
     */
    public Map(int dimensions)
    {
        this.dimensions = dimensions;
        this.nodes = new Position[Books.versesInBible()+1][];

        // Create the array of Nodes
        try
        {
            for (int b=1; b<=Books.booksInBible(); b++)
            {
                nodes[b] = new Position[Books.chaptersInBook(b)+1];
                for (int c=1; c<=Books.chaptersInBook(b); c++)
                {
                    nodes[b][c] = new Position(new float[dimensions]);
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            ex.printStackTrace();
            throw new LogicError(ex);
        }
    }

    /**
     * Get the number of dimensions in the nodes in this map
     * @return The number of dimensions
     */
    public int getDimensions()
    {
        return dimensions;
    }

    /**
     * Get the position (as a float array) of a node by the ordinal number
     * of the verse that it contains
     * @param ord The verse ordinal number
     * @return The requested node position
     */
    public float[] getPosition(int book, int chapter)
    {
        return (float[]) nodes[book][chapter].pos.clone();
    }

    /**
     * Get the position (as a float array) of a node by the ordinal number
     * of the verse that it contains
     * @param ord The verse ordinal number
     * @param idx The index into the position array for the given verse
     * @return The requested node position
     */
    public float getPositionDimension(int book, int chapter, int idx)
    {
        return nodes[book][chapter].pos[idx];
    }

    /**
     * Get the position of a node by the ordinal number of the verse that
     * it contains
     * @param ord The verse ordinal number
     * @return The requested node position
     */
    public void setPosition(int book, int chapter, float[] pos)
    {
        nodes[book][chapter].pos = pos;

        fireMapChanged(book, chapter);
    }

    /**
     * Get the position of a node by the ordinal number of the verse that
     * it contains
     * @param ord The verse ordinal number
     * @param idx The index into the position array for the given verse
     * @param f The new position
     * @return The requested node position
     */
    public void setPositionDimension(int book, int chapter, int idx, float f)
    {
        nodes[book][chapter].pos[idx] = f;

        fireMapChanged(book, chapter);
    }

    /**
     * Fix the layout to a fairly random one
     */
    public void setLayoutRandom()
    {
        try
        {
            for (int b=1; b<=Books.booksInBible(); b++)
            {
                for (int c=1; c<=Books.chaptersInBook(b); c++)
                {
                    nodes[b][c] = new Position(new float[] { (float) Math.random(), (float) Math.random() });
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Fix the layout to a simple book/chapter line default
     */
    public void setLayoutSimple()
    {
        if (dimensions != 2)
            throw new IllegalArgumentException("Can't set simple layout for maps with "+dimensions+" dimensions.");

        try
        {
            int bie = Books.booksInBible();
            for (int b=1; b<=bie; b++)
            {
                int cib = Books.chaptersInBook(b);
                for (int c=1; c<=cib; c++)
                {
                    float x = ((float) (c - 1)) / (cib - 1);
                    float y = ((float) (b - 1)) / (bie - 1);

                    nodes[b][c] = new Position(new float[] { x, y });
                }
            }

            fireMapRewritten();
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Apply the rules to the map.
     * @param map The set of nodes to move around
     * @param rules The rules to apply
     */
    public void applyRules(Rule[] rules)
    {
        try
        {
            // For each verse
            for (int b=1; b<=Books.versesInBible(); b++)
            {
                for (int c=1; c<=Books.chaptersInBook(b); c++)
                {
                    Position[][] dar = new Position[rules.length][];
                    for (int j=0; j<rules.length; j++)
                    {
                        dar[j] = rules[j].getDesiredPosition(this, b, c);
                    }
                
                    Position[] total = cat(dar);
                    nodes[b][c] = average(total);
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }

        fireMapRewritten();
    }

    /**
     * Add a map listener to the list of things wanting
     * to know whenever we make some changes to the map
     */
    public void addMapListener(MapListener li)
    {
        listeners.add(MapListener.class, li);
    }

    /**
     * Remove a progress listener from the list of things wanting
     * to know whenever we make some progress
     */
    public void removeMapListener(MapListener li)
    {
        listeners.remove(MapListener.class, li);
    }

    /**
     * Before we save/load something to/from disk we want to ensure that
     * we don't loose the list of things that have registered to recieve
     * map change events.
     */
    public EventListenerList getEventListenerList()
    {
        return listeners;
    }

    /**
     * Before we save/load something to/from disk we want to ensure that
     * we don't loose the list of things that have registered to recieve
     * map change events.
     */
    public void setEventListenerList(EventListenerList listeners)
    {
        this.listeners = listeners;
    }

    /**
     * What is the average position of all the nodes in this map
     * @return The center of gravity
     */
    public Position getCenterOfGravity()
    {
        // to cheat ...
        // return new Position(new float[] { 0.5F, 0.5F });

        if (cog == null || replies > MAX_REPLIES)
        {
            cog = average(nodes);
            replies = 0;
        }

        replies++;
        return cog;
    }

    /**
     * Called to fire a MapEvent to all the Listeners, when a single node
     * has changed position.
     * @param percent The percentage of the way through that we are now
     */
    protected void fireMapChanged(int book, int chapter)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        MapEvent ev = null;
        for (int i=contents.length-2; i>=0; i-=2)
        {
            if (contents[i] == MapListener.class)
            {
                if (ev == null)
                    ev = new MapEvent(this, book, chapter);

                ((MapListener) contents[i+1]).mapChanged(ev);
            }
        }
    }

    /**
     * Called to fire a MapEvent to all the Listeners, when a single node
     * has changed position.
     * @param percent The percentage of the way through that we are now
     */
    protected void fireMapRewritten()
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        MapEvent ev = null;
        for (int i=contents.length-2; i>=0; i-=2)
        {
            if (contents[i] == MapListener.class)
            {
                if (ev == null)
                    ev = new MapEvent(this);

                ((MapListener) contents[i+1]).mapRewritten(ev);
            }
        }
    }

    /**
     * Take an array of Position arrays can cat them all together to make
     * a single array containing all of them.
     * @param The array of Position arrays
     * @return The single big array
     */
    public static Position[] cat(Position[][] dar)
    {
        int size = 0;
        for (int i=0; i<dar.length; i++)
        {
            size += dar[i].length;
        }

        Position[] total = new Position[size];

        int offset = 0;
        for (int i=0; i<dar.length; i++)
        {
            System.arraycopy(dar[i], 0, total, offset, dar[i].length);
            offset += dar[i].length;
        }

        return total;
    }

    /**
     * Find the average position of an array of Positions
     */
    public static Position average(Position[][] array)
    {
        int dimensions = array[1][1].pos.length;
        double[] tot = new double[dimensions];

        for (int b=0; b<array.length; b++)
        {
            for (int c=0; c<array[b].length; c++)
            {
                for (int j=0; j<dimensions; j++)
                {
                    tot[j] += array[b][c].pos[j];
                }
            }
        }

        float[] retcode = new float[dimensions];

        for (int j=0; j<dimensions; j++)
        {
            retcode[j] = (float) (tot[j] / array.length);
        }

        return new Position(retcode);
    }

    /**
     * Find the average position of an array of Positions
     */
    public static Position average(Position[] array)
    {
        int dimensions = array[1].pos.length;
        double[] tot = new double[dimensions];

        // for all the array members and all the dimensions
        // add up the positions
        for (int i=0; i<array.length; i++)
        {
            for (int j=0; j<dimensions; j++)
            {
                tot[j] += array[i].pos[j];
            }
        }

        // divide by the number of members
        float[] retcode = new float[dimensions];
        for (int j=0; j<dimensions; j++)
        {
            retcode[j] = (float) (tot[j] / array.length);
        }

        return new Position(retcode);
    }

    /**
     * Initialize the transient fields
     * @param in The stream to read our state from
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        listeners = new EventListenerList();
    }

    /** What is the maximum calculations between re-calcing the CoG */
    private static final int MAX_REPLIES = 1000;

    /** The current center of gravity */
    private Position cog = null;

    /** How long until we next calculate the center of gravity */
    private int replies = 0;

    /** The array of verse nodes */
    private Position[][] nodes;

    /** The number of dimensions in the display */
    private int dimensions;

    /** The number of links that we track for a node */
    public static final int LINKS_PER_NODE = 20;

    /** The list of listeners */
    protected transient EventListenerList listeners = new EventListenerList();

    /** Serialization ID - a serialization of nodes and dimensions */
    static final long serialVersionUID = -193572391252539071L;
}
