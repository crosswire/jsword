
package org.crosswire.jsword.book.data;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.Validator;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.xml.JAXBSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.util.Project;
import org.xml.sax.SAXException;

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
public class OSISUtil
{
    /**
     * The package into which JAXB generates its stuff
     */
    public static final String OSIS_PACKAGE = "org.crosswire.jsword.osis";

    /**
     * The JAXB worker factory
     */
    protected static JAXBContext jc = null;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(OSISUtil.class);

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
        }
        catch (IOException ex)
        {
            log.error("Failed to test JAXB", ex);
        }

        try
        {
            jc = JAXBContext.newInstance(OSISUtil.OSIS_PACKAGE);
        }
        catch (JAXBException ex)
        {
            log.error("Failed to start JAXB", ex);
            initex = ex;
        }
    }

    /**
     * Create a BibleData from a SAXEventProvider
     * @param doc
     */
    public static BookData createBibleData(SAXEventProvider provider) throws SAXException
    {
        checkJAXBContext();

        try
        {
            BookData bdata = new BookData();

            Unmarshaller unm = jc.createUnmarshaller();
            UnmarshallerHandler unmh = unm.getUnmarshallerHandler();
            provider.provideSAXEvents(unmh);
            bdata.osis = (Osis) unmh.getResult();

            return bdata;
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Check that a BibleData is valid. Currently (probably as a result of a bug
     * in JAXB) this method will always fail.
     * @param bdata The BibleData to check
     * @throws JAXBException
     */
    public static void validate(BookData bdata) throws JAXBException
    {
        checkJAXBContext();

        Validator val = jc.createValidator();
        val.setEventHandler(new ValidationEventHandler()
        {
            public boolean handleEvent(ValidationEvent ev)
            {
                return false;
            }
        });
        val.validateRoot(bdata.osis);
    }

    /**
     * Output the current data as a SAX stream.
     * @param handler The Place to post SAX events
     */
    public static SAXEventProvider getSAXEventProvider(BookData bdata)
    {
        checkJAXBContext();

        return new JAXBSAXEventProvider(jc, bdata.osis);
    }

    /**
     * This is an enumeration through all the sections in this Document.
     * Each of the sections will be able to give a list of the Verses
     * that it contains.
     * @return The list of sections
     */
    public static Iterator getSectionDatas(BookData bdata)
    {
        List reply = new ArrayList();
        
        for (Iterator dit = bdata.osis.getOsisText().getDiv().iterator(); dit.hasNext();)
        {
            SectionData sdata = new SectionData();
            sdata.div = (Div) dit.next();
            reply.add(sdata);
        }

        return reply.iterator();
    }

    /**
     * This is an accessor for the list of references (verses) that we
     * hold
     * @return The list of RefDatas
     */
    public static Iterator getRefDatas(SectionData sdata)
    {
        List reply = new ArrayList();

        for (Iterator it = sdata.div.getContent().iterator(); it.hasNext();)
        {
            Object element = it.next();
            if (element instanceof org.crosswire.jsword.osis.Verse)
            {
                VerseData rdata = new VerseData();
                rdata.everse = (org.crosswire.jsword.osis.Verse) element;
                reply.add(rdata);
            }
        }

        return reply.iterator();
    }

    /**
     * A simplified plain text version of the data in this verse with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public static String getPlainText(BookData bdata)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator it = OSISUtil.getSectionDatas(bdata);
        while (it.hasNext())
        {
            SectionData sdata = (SectionData) it.next();
            buffer.append(OSISUtil.getPlainText(sdata));
        }

        return buffer.toString().trim();
    }

    /**
     * A simplified plain text version of the data in this section with all the
     * markup stripped out.
     * @return The Bible text without markup
     */
    public static String getPlainText(SectionData sdata)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator it = OSISUtil.getRefDatas(sdata);
        while (it.hasNext())
        {
            VerseData vel = (VerseData) it.next();
            buffer.append(OSISUtil.getPlainText(vel));
        }

        return buffer.toString();
    }

    /**
     * A simplified plain text version of the data in this verse with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public static String getPlainText(VerseData rdata)
    {
        StringBuffer buffer = new StringBuffer();

        List content = rdata.everse.getContent();
        for (Iterator it = content.iterator(); it.hasNext();)
        {
            Object ele = it.next();
            recurseElement(ele, buffer);
        }

        return buffer.toString();
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

    /**
     * Check that the JAXBContext has been setup properly and throw if not.
     * @throws SAXException
     */
    private static void checkJAXBContext()
    {
        if (jc == null)
            throw new NullPointerException("jc is null");
    }
}
