package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.osis.Cell;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.DivineName;
import org.crosswire.jsword.osis.Foreign;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Hi;
import org.crosswire.jsword.osis.Item;
import org.crosswire.jsword.osis.Milestone;
import org.crosswire.jsword.osis.Note;
import org.crosswire.jsword.osis.ObjectFactory;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.P;
import org.crosswire.jsword.osis.Q;
import org.crosswire.jsword.osis.Reference;
import org.crosswire.jsword.osis.Row;
import org.crosswire.jsword.osis.Seg;
import org.crosswire.jsword.osis.Speaker;
import org.crosswire.jsword.osis.Speech;
import org.crosswire.jsword.osis.Table;
import org.crosswire.jsword.osis.Title;
import org.crosswire.jsword.osis.TransChange;
import org.crosswire.jsword.osis.Verse;
import org.crosswire.jsword.osis.W;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.util.Project;

/**
 * Some simple utilities to help working with OSIS classes.
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
public class JAXBUtil
{
    /**
     * Prevent Instansiation
     */
    private JAXBUtil()
    {
    }

    /**
     * The package into which JAXB generates its stuff
     */
    protected static final String OSIS_PACKAGE = "org.crosswire.jsword.osis";

    /**
     * Constant to help narrow down what we use seg for. In this case the bold tag
     */
    public static final String SEG_BOLD = "font-weight: bold;";

    /**
     * Constant to help narrow down what we use seg for. In this case the italic tag
     */
    public static final String SEG_ITALIC = "font-style: italic;";

    /**
     * Constant to help narrow down what we use seg for. In this case the underline tag
     */
    public static final String SEG_UNDERLINE = "text-decoration: underline;";

    /**
     * Constant to help narrow down what we use seg for. In this case the justify right tag
     */
    public static final String SEG_JUSTIFYRIGHT = "text-align: right;";

    /**
     * Constant to help narrow down what we use seg for. In this case the justify right tag
     */
    public static final String SEG_CENTER = "text-align: center;";

    /**
     * Constant to help narrow down what we use seg for. In this case the small tag
     */
    public static final String SEG_SMALL = "font-size: small;";

    /**
     * Constant to help narrow down what we use seg for. In this case the sup tag
     */
    public static final String SEG_SUPERSCRIPT = "vertical-align: super;";

    /**
     * Constant to help narrow down what we use seg for. In this case the color tag
     */
    public static final String SEG_COLORPREFIX = "color: ";

    /**
     * Constant to help narrow down what we use seg for. In this case the font-size tag
     */
    public static final String SEG_SIZEPREFIX = "font-size: ";

    /**
     * The JAXB worker factory
     */
    private static JAXBContext jc = null;

    /**
     * The JAXB object factory
     */
    protected static ObjectFactory factory = null;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JAXBUtil.class);

    /**
     * Something went wrong at startup
     */
    private static Exception initex;

    static
    {
        try
        {
            Properties test = Project.instance().getProperties("org/crosswire/jsword/osis/jaxb");
            for (Iterator it = test.keySet().iterator(); it.hasNext();)
            {
                String key = (String) it.next();
                String val = (String) test.get(key);
                log.debug("jaxb: "+key+"="+val);
            }

            jc = JAXBContext.newInstance(OSIS_PACKAGE);
            factory = new ObjectFactory();
        }
        catch (Exception ex)
        {
            log.fatal("Failed to test JAXB", ex);
            initex = ex;
        }
    }

    /**
     * Accessor for the JAXB context created at startup
     */
    public static JAXBContext getJAXBContext()
    {
        if (jc == null)
        {
            throw new NullPointerException("jc is null due to startup issue: "+initex.getMessage());
        }

        return jc;
    }

    /**
     * An accessor for the ObjectFactory that creates OSIS objects
     */
    public static ObjectFactory factory()
    {
        return factory;
    }

    /**
     * A simplified plain text version of the data in this Element with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public static String getPlainText(Element ele)
    {
        StringBuffer buffer = new StringBuffer();
    
        List content = getList(ele);
        for (Iterator it = content.iterator(); it.hasNext();)
        {
            Object next = it.next();
            recurseElement(next, buffer);
        }
    
        return buffer.toString();
    }

    /**
     * Many of the OSIS elements have lists with content, but the accessors are
     * not accoring to any interface, (or even with consistent names) so this
     * method extracts a content List from a JAXB element.
     */
    public static List getList(Element ele)
    {
        if (ele instanceof Verse)
        {
            return ((Verse) ele).getContent();
        }
        else if (ele instanceof Seg)
        {
            return ((Seg) ele).getContent();
        }
        else if (ele instanceof Div)
        {
            return ((Div) ele).getContent();
        }
        else if (ele instanceof Note)
        {
            return ((Note) ele).getContent();
        }
        else if (ele instanceof W)
        {
            return ((W) ele).getContent();
        }
        else if (ele instanceof P)
        {
            return ((P) ele).getContent();
        }
        else if (ele instanceof Q)
        {
            return ((Q) ele).getContent();
        }
        else if (ele instanceof TransChange)
        {
            return ((TransChange) ele).getContent();
        }
        else if (ele instanceof Speaker)
        {
            return ((Speaker) ele).getContent();
        }
        else if (ele instanceof Speech)
        {
            return ((Speech) ele).getContent();
        }
        else if (ele instanceof Reference)
        {
            return ((Reference) ele).getContent();
        }
        else if (ele instanceof DivineName)
        {
            return ((DivineName) ele).getContent();
        }
        else if (ele instanceof Title)
        {
            return ((Title) ele).getContent();
        }
        else if (ele instanceof Item)
        {
            return ((Item) ele).getContent();
        }
        else if (ele instanceof Foreign)
        {
            return ((Foreign) ele).getContent();
        }
        else if (ele instanceof org.crosswire.jsword.osis.List)
        {
            return ((org.crosswire.jsword.osis.List) ele).getContent();
        }
        else if (ele instanceof Table)
        {
            return ((Table) ele).getRow();
        }
        else if (ele instanceof Row)
        {
            return ((Row) ele).getCell();
        }
        else if (ele instanceof Cell)
        {
            return ((Cell) ele).getContent();
        }
        else if (ele instanceof Hi)
        {
            return ((Hi) ele).getContent();
        }
        else if (ele instanceof Milestone)
        {
            // NOTE(joe): Milestone does not have content, so why are we here?
            return new ArrayList();
        }
        
        log.error("unknown element: "+ele.getClass().getName());
        throw new LogicError();
    }

    /**
     * Find all the instances of elements of type <code>find</code> under
     * the element <code>div</code>.
     */
    public static Collection getDeepContent(Element div, Class find)
    {
        List reply = new ArrayList();
        recurseDeepContent(div, find, reply);
        return reply;
    }

    /**
     * Walk up the tree from the W to find out what verse we are in.
     * @param ele The start point for our verse hunt.
     * @return The verse we are in
     */
    public static org.crosswire.jsword.passage.Verse getVerse(Element ele, ParentLocator loc) throws BookException
    {
        if (ele instanceof Verse)
        {
            // If the element is an OSIS Verse then this is fairly easy
            Verse overse = (Verse) ele;
            String osisid = overse.getOsisID();

            try
            {
                return new org.crosswire.jsword.passage.Verse(osisid);
            }
            catch (NoSuchVerseException ex)
            {
                throw new BookException(Msg.OSIS_BADID, ex, new Object[] { osisid });
            }
        }
        else
        {
            // So we just walk up the tree trying to find a verse
            Element parent = loc.getParent(ele);
            if (parent != null)
            {
                return getVerse(parent, loc);
            }

            throw new BookException(Msg.MISSING_VERSE);
        }
    }

    /**
     * Helper method to create the boilerplate headers in an OSIS document from
     * the current metadata object
     */
    public static Osis createOsisFramework(BookMetaData bmd) throws JAXBException
    {
        Osis osis = factory().createOsis();
        String osisid = bmd.getInitials();
    
        Work work = factory().createWork();
        work.setOsisWork(osisid);
    
        Header header = factory().createHeader();
        header.getWork().add(work);
    
        OsisText text = factory().createOsisText();
        text.setOsisIDWork("Bible."+osisid);
        text.setHeader(header);
    
        osis.setOsisText(text);
    
        return osis;
    }

    /**
     * Split a BookData into a number of BookDatas where each one has a maximum
     * of <code>i</code> words on each page. 
     * @param data The BookData to cut up
     * @param i The max words on a page
     * @return List of cut up BookDatas
     */
    public static List pagenate(BookData data, int i)
    {
        List reply = new ArrayList();
        reply.add(data);
        return reply;
    }

    /**
     * Create a title based on the given <code>BookData</code> not more than
     * <code>length</code> characters in length
     * @param data The BookData to create a title from.
     * @param length The maximim String length
     * @return The BookData title
     */
    public static String getTitle(BookData data, int length)
    {
        return data.toString();
    }

    /**
     * Find all the instances of elements of type <code>find</code> under
     * the element <code>div</code>. For internal use only.
     */
    private static void recurseDeepContent(Element start, Class find, List reply)
    {
        if (find.isInstance(start))
        {
            reply.add(start);
        }

        Iterator it = getList(start).iterator();
        while (it.hasNext())
        {
            Element ele = (Element) it.next();
            recurseDeepContent(ele, find, reply);
        }
    }

    /**
     * If we have a String just add it to the buffer, but if we have an Element
     * then try to dig the strings out of it.
     */
    private static void recurseElement(Object sub, StringBuffer buffer)
    {
        if (sub instanceof String)
        {
            buffer.append((String) sub);
        }
        else if (sub instanceof Element)
        {
            recurseChildren((Element) sub, buffer);
        }
        else
        {
            log.error("unknown type: "+sub.getClass().getName());
        }
    }

    /**
     * Helper to extract the Strings from a nest of JAXB elements
     * @param ele The JAXB Element to dig into
     * @param buffer The place we accumulate strings.
     */
    private static void recurseChildren(Element ele, StringBuffer buffer)
    {
        // ele is a JAXBElement that might have a getContent() method
        try
        {
            List content = getList(ele);
            for (Iterator it = content.iterator(); it.hasNext();)
            {
                Object sub = it.next();
                recurseElement(sub, buffer);
            }
        }
        catch (Exception ex)
        {
            // We can continue, but we should report a problem
            log.error("Error interrogating: "+ele.getClass().getName(), ex);
        }
    }
}
