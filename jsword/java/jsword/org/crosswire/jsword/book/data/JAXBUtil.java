
package org.crosswire.jsword.book.data;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBContext;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Note;
import org.crosswire.jsword.osis.ObjectFactory;
import org.crosswire.jsword.osis.Seg;
import org.crosswire.jsword.osis.Verse;
import org.crosswire.jsword.osis.W;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JAXBUtil
{
    /**
     * The package into which JAXB generates its stuff
     */
    protected static final String OSIS_PACKAGE = "org.crosswire.jsword.osis";

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
            Properties test = Project.resource().getProperties("org/crosswire/jsword/osis/jaxb");
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
     * A simplified plain text version of the data in this verse with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public static String getPlainText(Verse overse)
    {
        StringBuffer buffer = new StringBuffer();
    
        List content = overse.getContent();
        for (Iterator it = content.iterator(); it.hasNext();)
        {
            Object ele = it.next();
            JAXBUtil.recurseElement(ele, buffer);
        }
    
        return buffer.toString();
    }

    /**
     * Many of the OSIS elements have lists with content, but the accessors are
     * not accoring to any interface, (or even with consistent names) so this
     * method extracts a content List from a JAXB element.
     */
    public static List getList(Element current)
    {
        if (current instanceof Verse)
        {
            return ((Verse) current).getContent();
        }
        else if (current instanceof Seg)
        {
            return ((Seg) current).getContent();
        }
        else if (current instanceof Div)
        {
            return ((Div) current).getContent();
        }
        else if (current instanceof Note)
        {
            return ((Note) current).getContent();
        }
        else if (current instanceof W)
        {
            return ((W) current).getContent();
        }
        
        log.error("unknown element: "+current.getClass().getName());
        
        throw new LogicError();
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
        else
        {
            recurseChildren(sub, buffer);
        }
    }

    /**
     * Helper to extract the Strings from a nest of JAXB elements
     * @param ele The JAXB Element to dig into
     * @param buffer The place we accumulate strings.
     */
    private static void recurseChildren(Object ele, StringBuffer buffer)
    {
        // ele is a JAXBElement that might have a getContent() method
        Class clazz = ele.getClass();
        try
        {
            Method method = clazz.getMethod("getContent", new Class[0]);
            if (method.getReturnType() == List.class)
            {
                List content = (List) method.invoke(ele, new Object[0]);
                for (Iterator it = content.iterator(); it.hasNext();)
                {
                    Object sub = it.next();
                    recurseElement(sub, buffer);
                }
            }
        }
        catch (Exception ex)
        {
            // We can continue, but we should report a problem
            log.error("Error interrogating: "+ele.getClass().getName(), ex);
        }
    }
}
