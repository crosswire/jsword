
package org.crosswire.jsword.map.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.crosswire.common.util.Logger;
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

        // Create the array of Nodes
        int bie = BibleInfo.booksInBible();
        this.nodes = new Position[bie+1][];
        try
        {
            for (int b=1; b<=bie; b++)
            {
                int cib = BibleInfo.chaptersInBook(b);
                nodes[b] = new Position[cib+1];
                for (int c=1; c<=cib; c++)
                {
                    float[] pos = new float[dimensions];

                    for (int d=0; d<dimensions; d++)
                    {
                        pos[d] = 0.0f;
                    }

                    nodes[b][c] = new Position(pos);
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
    public float[] getPositionArrayCopy(int book, int chapter)
    {
        try
        {
            return (float[]) nodes[book][chapter].pos.clone();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            log.error("getPosition() book="+book+" chapter="+chapter, ex);
            return new float[] { 0.0f, 0.0f };
        }
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
        try
        {
            return nodes[book][chapter].pos[idx];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            log.error("getPositionDimension() book="+book+" chapter="+chapter+" dim="+idx, ex);
            return 0.0f;
        }
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
            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
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

        float start = 0.05F;
        float end = 0.95F;
        float mid = (end - start) / 2;
        float scale = end - start;

        try
        {
            int bie = BibleInfo.booksInBible();
            for (int b=1; b<=bie; b++)
            {
                float y = (((float) (b - 1)) / (bie - 1)) * scale + start;

                int cib = BibleInfo.chaptersInBook(b);
                if (cib == 1)
                {
                    nodes[b][1] = new Position(new float[] { mid + start, y });
                }
                else
                {
                    for (int c=1; c<=cib; c++)
                    {
                        float x = (((float) (c - 1)) / (cib - 1)) * scale + start;

                        nodes[b][c] = new Position(new float[] { x, y });
                    }
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
            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
                {
                    Position[][] dar = new Position[rules.length][];
                    for (int j=0; j<rules.length; j++)
                    {
                        dar[j] = rules[j].getScaledPosition(this, b, c);

                        /*if (log.isDebugEnabled())
                        {
                            log.debug("Rule: "+j+" ("+rules[j].getClass().getName()+") scale="+rules[j].getScale());
                            StringBuffer out = new StringBuffer(" ");
                            for (int i=0; i<dar[j].length; i++)
                            {
                                out.append(" ("+i+"="+dar[j][i].pos[0]+","+dar[j][i].pos[1]+")");
                            }
                            log.debug(out);
                        }*/
                    }

                    Position[] total = cat(dar);
                    nodes[b][c] = PositionUtil.average(total, dimensions);
                    //log.debug("Total:");
                    //log.debug("  (t="+nodes[b][c].pos[0]+","+nodes[b][c].pos[1]+")");
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
     * Fix the layout to a fairly random one
     */
    public void debug(PrintWriter out)
    {
        try
        {
            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                log.debug("Book "+b);
                for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
                {
                    log.debug("  Chapter "+c+": Position=("+nodes[b][c].pos[0]+","+nodes[b][c].pos[1]+")");
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            ex.printStackTrace(out);
        }
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
            cog = PositionUtil.average(nodes, dimensions);
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
     * Save link data to XML as a stream.
     */
    public void load(Reader out) throws IOException
    {
        try
        {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(out);
            Element root = doc.getRootElement();
            fromXML(root);
        }
        catch (JDOMException ex)
        {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Save link data to XML as a stream.
     */
    public void save(Writer out) throws IOException
    {
        Element root = toXML();
        Document doc = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent("  ");
        outputter.setNewlines(true);
        outputter.output(doc, out);
    }

    /**
     * Generate links from an XML representation.
     * @param root The root 'links' element
     */
    public void fromXML(Element epos) throws JDOMException
    {
        if (!epos.getName().equals("positions"))
            throw new JDOMException("root element is not called 'links'");

        dimensions = Integer.parseInt(epos.getAttributeValue("dimensions"));

        List ebs = epos.getChildren("book");
        Iterator bit = ebs.iterator();
        while (bit.hasNext())
        {
            Element eb = (Element) bit.next();
            int b = Integer.parseInt(eb.getAttributeValue("num"));
            
            List ecs = eb.getChildren("chapter");
            Iterator cit = ecs.iterator();
            while (cit.hasNext())
            {
                Element ec = (Element) cit.next();
                int c = Integer.parseInt(ec.getAttributeValue("num"));

                float[] fa = new float[dimensions];
                for (int d=0; d<dimensions; d++)
                {
                    fa[d] = Float.parseFloat(ec.getAttributeValue("dim"+d));
                }

                nodes[b][c] = new Position(fa);
            }
        }
    }

    /**
     * Save link data to XML as a JDOM tree.
     */
    public Element toXML()
    {
        Element epos = new Element("positions");
        epos.setAttribute("dimensions", ""+dimensions);

        try
        {
            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                Element eb = new Element("book");
                eb.setAttribute("num", ""+b);
                eb.setAttribute("name", BibleInfo.getShortBookName(b));
                epos.addContent(eb);

                for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
                {
                    Position node = nodes[b][c];
                    Element ec = new Element("chapter");
                    ec.setAttribute("num", ""+c);

                    for (int d=0; d<dimensions; d++)
                    {
                        ec.setAttribute("dim"+d, ""+node.pos[d]);
                    }
                    eb.addContent(ec);
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }

        return epos;
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

    /** The log stream */
    private static final Logger log = Logger.getLogger(LinkArray.class);

    /** What is the maximum calculations between re-calcing the CoG */
    private static final int MAX_REPLIES = 1;

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
