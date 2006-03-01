/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.jdom.Element;
import org.jdom.Parent;
import org.jdom.Text;

/**
 * Some simple utilities to help working with OSIS classes.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class OSISUtil
{
    /**
     * Constant to help narrow down what we use "hi" for. In this case the bold tag
     */
    public static final String HI_BOLD = "bold"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use "hi" for. In this case the italic tag
     */
    public static final String HI_ITALIC = "italic"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use "hi" for. In this case the underline tag
     */
    public static final String HI_UNDERLINE = "underline"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the justify right tag
     */
    public static final String SEG_JUSTIFYRIGHT = "text-align: right;"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the justify right tag
     */
    public static final String SEG_CENTER = "text-align: center;"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the small tag
     */
    public static final String SEG_SMALL = "font-size: small;"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the sup tag
     */
    public static final String SEG_SUPERSCRIPT = "vertical-align: super;"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the color tag
     */
    public static final String SEG_COLORPREFIX = "color: "; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the font-size tag
     */
    public static final String SEG_SIZEPREFIX = "font-size: "; //$NON-NLS-1$

    /**
     * Constant for the study note type
     */
    public static final String NOTETYPE_STUDY = "x-StudyNote"; //$NON-NLS-1$

    /**
     * Constant for the variant type segment
     */
    public static final String VARIANT_TYPE = "x-variant"; //$NON-NLS-1$
    public static final String VARIANT_CLASS = "x-class"; //$NON-NLS-1$

    /**
     * Constant for a Strongs numbering lemma
     */
    public static final String LEMMA_STRONGS = "strong:"; //$NON-NLS-1$
    public static final String MORPH_ROBINSONS = "robinson:"; //$NON-NLS-1$

    /**
     * Constant for Strongs numbering morphology
     */
    public static final String MORPH_STRONGS = "x-StrongsMorph:T"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use "q" for. In this case: blockquote
     */
    public static final String Q_BLOCK = "blockquote"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use "q" for. In this case: citation
     */
    public static final String Q_CITATION = "citation"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use "q" for. In this case: embedded
     */
    public static final String Q_EMBEDDED = "embedded"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use "list" for.
     */
    public static final String LIST_ORDERED = "x-ordered"; //$NON-NLS-1$
    public static final String LIST_UNORDERED = "x-unordered"; //$NON-NLS-1$

    public static final String OSIS_ELEMENT_TITLE = "title"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_TABLE = "table"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_SPEECH = "speech"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_SPEAKER = "speaker"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_ROW = "row"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_REFERENCE = "reference"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_NOTE = "note"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_NAME = "name"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_Q = "q"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_LIST = "list"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_P = "p"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_ITEM = "item"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_FOREIGN = "foreign"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_W = "w"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_CHAPTER = "chapter"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_VERSE = "verse"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_CELL = "cell"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_DIV = "div"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_OSIS = "osis"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_WORK = "work"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_HEADER = "header"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_OSISTEXT = "osisText"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_SEG = "seg"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_LG = "lg"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_L = "l"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_LB = "lb"; //$NON-NLS-1$
    public static final String OSIS_ELEMENT_HI = "hi"; //$NON-NLS-1$

    public static final String ATTRIBUTE_TEXT_OSISIDWORK = "osisIDWork"; //$NON-NLS-1$
    public static final String ATTRIBUTE_WORK_OSISWORK = "osisWork"; //$NON-NLS-1$
    public static final String OSIS_ATTR_OSISID = "osisID"; //$NON-NLS-1$
    public static final String OSIS_ATTR_SID = "sID"; //$NON-NLS-1$
    public static final String OSIS_ATTR_EID = "eID"; //$NON-NLS-1$
    public static final String ATTRIBUTE_W_LEMMA = "lemma"; //$NON-NLS-1$
    public static final String OSIS_ATTR_TYPE = "type"; //$NON-NLS-1$
    public static final String OSIS_ATTR_SUBTYPE = "subType"; //$NON-NLS-1$
    public static final String OSIS_ATTR_REF = "osisRef"; //$NON-NLS-1$
    public static final String ATTRIBUTE_SPEAKER_WHO = "who"; //$NON-NLS-1$
    public static final String ATTRIBUTE_W_MORPH = "morph"; //$NON-NLS-1$
    public static final String ATTRIBUTE_OSISTEXT_OSISIDWORK = "osisIDWork"; //$NON-NLS-1$
    public static final String OSIS_ATTR_LANG = "xml:lang"; //$NON-NLS-1$
    public static final String ATTRIBUTE_DIV_BOOK = "book"; //$NON-NLS-1$

    /**
     * Prefix for OSIS IDs that refer to Bibles
     */
    private static final String OSISID_PREFIX_BIBLE = "Bible."; //$NON-NLS-1$

    private static final Set EXTRA_BIBLICAL_ELEMENTS = new HashSet(Arrays.asList(new String[]
    {
        OSIS_ELEMENT_NOTE,
    }));

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(OSISUtil.class);

    /**
     * Prevent Instansiation
     */
    private OSISUtil()
    {
    }

    private static OSISFactory factory = new OSISFactory();

    /**
     * An accessor for the OSISFactory that creates OSIS objects
     */
    public static OSISFactory factory()
    {
        return factory;
    }

    /**
     * A generic way of creating empty Elements of various types
     */
    public static class OSISFactory
    {
        /**
         * 
         */
        public Element createSeg()
        {
            return new Element(OSIS_ELEMENT_SEG);
        }

        /**
         * 
         */
        public Element createOsisText()
        {
            return new Element(OSIS_ELEMENT_OSISTEXT);
        }

        /**
         * 
         */
        public Element createHeader()
        {
            return new Element(OSIS_ELEMENT_HEADER);
        }

        /**
         * 
         */
        public Element createWork()
        {
            return new Element(OSIS_ELEMENT_WORK);
        }

        /**
         * 
         */
        public Element createOsis()
        {
            return new Element(OSIS_ELEMENT_OSIS);
        }

        /**
         * 
         */
        public Element createDiv()
        {
            return new Element(OSIS_ELEMENT_DIV);
        }

        /**
         * 
         */
        public Element createCell()
        {
            return new Element(OSIS_ELEMENT_CELL);
        }

        /**
         * 
         */
        public Element createVerse()
        {
            return new Element(OSIS_ELEMENT_VERSE);
        }

        /**
         * 
         */
        public Element createW()
        {
            return new Element(OSIS_ELEMENT_W);
        }

        /**
         * 
         */
        public Element createForeign()
        {
            return new Element(OSIS_ELEMENT_FOREIGN);
        }

        /**
         * 
         */
        public Element createItem()
        {
            return new Element(OSIS_ELEMENT_ITEM);
        }

        /**
         * 
         */
        public Element createP()
        {
            return new Element(OSIS_ELEMENT_P);
        }

        /**
         * 
         */
        public Element createList()
        {
            return new Element(OSIS_ELEMENT_LIST);
        }

        /**
         * 
         */
        public Element createQ()
        {
            return new Element(OSIS_ELEMENT_Q);
        }

        /**
         * 
         */
        public Element createName()
        {
            return new Element(OSIS_ELEMENT_NAME);
        }

        /**
         * 
         */
        public Element createNote()
        {
            return new Element(OSIS_ELEMENT_NOTE);
        }

        /**
         * 
         */
        public Element createReference()
        {
            return new Element(OSIS_ELEMENT_REFERENCE);
        }

        /**
         * 
         */
        public Element createRow()
        {
            return new Element(OSIS_ELEMENT_ROW);
        }

        /**
         * 
         */
        public Element createSpeaker()
        {
            return new Element(OSIS_ELEMENT_SPEAKER);
        }

        /**
         * 
         */
        public Element createSpeech()
        {
            return new Element(OSIS_ELEMENT_SPEECH);
        }

        /**
         * 
         */
        public Element createTable()
        {
            return new Element(OSIS_ELEMENT_TABLE);
        }

        /**
         * 
         */
        public Element createTitle()
        {
            return new Element(OSIS_ELEMENT_TITLE);
        }
        /**
         * Line Group
         */
        public Element createLG()
        {
            return new Element(OSIS_ELEMENT_LG);
        }
        /**
         * Line
         */
        public Element createL()
        {
            return new Element(OSIS_ELEMENT_L);
        }
        /**
         * Line Break
         */
        public Element createLB()
        {
            return new Element(OSIS_ELEMENT_LB);
        }
        /**
         * Highlight
         */
        public Element createHI()
        {
            return new Element(OSIS_ELEMENT_HI);
        }
    }

    /**
     * Get the verse text from an osis document consisting of a single verse.
     * The document is assumed to be valid OSIS2.0 XML. While xml valid
     * is rigidly defined as meaning that an xml parser can validate the document,
     * it does not mean that the document is valid OSIS. This is a semantic
     * problem that is not validated. This method assumes that the
     * root element is also semantically valid.
     * 
     * <p>This means that the top level element's tagname is osis.
     * This can contain either a osisText or an osisCorpus.
     * If it is an osisCorpus, then it contains an osisText.
     * However, as a simplification, since JSword constructs
     * the whole doc for the fragment, osisCorpus can be ignored.
     * <p>The osisText element contains a div element that is either
     * a container or a milestone. Again, JSword is providing the
     * div element and it will be provided as a container. It is this div
     * that "contains" the verse element.</p>
     * <p>The verse element may either be
     * a container or a milestone. Sword OSIS books differ in whether
     * they provide the verse element. Most do not. The few that do are
     * using the container model, but it has been proposed that milestones
     * are the best practice.</p>
     * 
     * <p>The verse may contain elements that are not a part of the
     * original text. These are things such as notes.</p>
     * 
     * <p>Milestones require special handling. Beginning milestones
     * elements have an sID attribute, while ending milestones have
     * an eID with the same value as the opening. So everything between
     * the start and the corresponding end is the content of the element.
     * Also, for a given element, say div, they have to be properly nested
     * as if they were container elements.</p>
     * 
     * @param root the whole osis document.
     * @return The Bible text without markup
     */
    public static String getVerseText(Element root)
    {
        StringBuffer buffer = new StringBuffer();

        Element osisText = root.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
        Element div = osisText.getChild(OSISUtil.OSIS_ELEMENT_DIV);

        Iterator dit = div.getContent().iterator();
        String sid = null;
        Object data = null;
        Element ele = null;
        while (dit.hasNext())
        {
            data = dit.next();
            if (data instanceof Element)
            {
                ele = (Element) data;
                if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE))
                {
                    sid = ele.getAttributeValue(OSISUtil.OSIS_ATTR_SID);
                    if (sid != null)
                    {
                        getVerseContent(dit, buffer);
                    }
                    else
                    {
                        getVerseContent(ele.getContent().iterator(), buffer);
                    }
                }
            }
            else if (data instanceof Text)
            {
                buffer.append(((Text) data).getText());
            }
        }

        return buffer.toString().trim();
    }

    /**
     * A simplified plain text version of the data in this Element with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public static String getPlainText(Element root)
    {
        StringBuffer buffer = new StringBuffer();

        Element osisText = root.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
        List divs = osisText.getChildren(OSISUtil.OSIS_ELEMENT_DIV);

        for (Iterator oit = divs.iterator(); oit.hasNext(); )
        {
            Element div = (Element) oit.next();

            Iterator dit = div.getContent().iterator();
            while (dit.hasNext())
            {
                Object data = dit.next();
                if (data instanceof Element)
                {
                    Element ele = (Element) data;
                    if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE))
                    {
                        String txt = OSISUtil.getTextContent((Element) data);
                        buffer.append(txt);
                    }
                }
            }
        }

        return buffer.toString().trim();
    }

    private static void getVerseContent(Iterator iter, StringBuffer buffer)
    {
        Object data = null;
        Element ele = null;
        String eleName = null;
        while (iter.hasNext())
        {
            data = iter.next();
            if (data instanceof Element)
            {
                ele = (Element) data;
                // If the verse is done then quit.
                // This should be a verse eID=, that matches sID, but it does not matter.
                // Since this gets the text of one verse, any verse element that follows
                // is the end of the previous verse.
                eleName = ele.getName();
                if (eleName.equals(OSISUtil.OSIS_ELEMENT_VERSE))
                {
                    break;
                }

                // Ignore extra-biblical text
                if (!EXTRA_BIBLICAL_ELEMENTS.contains(eleName))
                {
                    OSISUtil.getVerseContent(ele.getContent().iterator(), buffer);
                }
            }
            else if (data instanceof Text)
            {
                buffer.append(((Text) data).getText());
            }
        }
    }

    private static String getTextContent(Element ele)
    {
        StringBuffer buffer = new StringBuffer();

        List content = ele.getContent();
        for (Iterator it = content.iterator(); it.hasNext(); )
        {
            Object next = it.next();
            recurseElement(next, buffer);
        }

        return buffer.toString();
    }

    /**
     * Find all the instances of elements of type <code>find</code> under
     * the element <code>div</code>.
     */
    public static Collection getDeepContent(Element div, String name)
    {
        List reply = new ArrayList();
        recurseDeepContent(div, name, reply);
        return reply;
    }

    /**
     * Walk up the tree from the W to find out what verse we are in.
     * @param ele The start point for our verse hunt.
     * @return The verse we are in
     */
    public static Verse getVerse(Element ele) throws BookException
    {
        if (ele.getName().equals(OSIS_ELEMENT_VERSE))
        {
            // If the element is an OSIS Verse then this is fairly easy
            String osisid = ele.getAttributeValue(OSIS_ATTR_OSISID);

            try
            {
                return VerseFactory.fromString(osisid);
            }
            catch (NoSuchVerseException ex)
            {
                throw new BookException(Msg.OSIS_BADID, ex, new Object[] { osisid });
            }
        }
        // So we just walk up the tree trying to find a verse
        Parent parent = ele.getParent();
        if (parent != null && parent instanceof Element)
        {
            return getVerse((Element) parent);
        }

        throw new BookException(Msg.MISSING_VERSE);
    }

    /**
     * Helper method to create the boilerplate headers in an OSIS document from
     * the current metadata object
     */
    public static Element createOsisFramework(BookMetaData bmd)
    {
        Element osis = factory().createOsis();
        String osisid = bmd.getInitials();

        Element work = factory().createWork();
        work.setAttribute(ATTRIBUTE_WORK_OSISWORK, osisid);

        Element header = factory().createHeader();
        header.addContent(work);

        Element text = factory().createOsisText();
        text.setAttribute(ATTRIBUTE_TEXT_OSISIDWORK, OSISID_PREFIX_BIBLE + osisid);
        text.addContent(header);

        osis.addContent(text);

        return osis;
    }

    /**
     * Find all the instances of elements of type <code>find</code> under
     * the element <code>div</code>. For internal use only.
     */
    private static void recurseDeepContent(Element start, String name, List reply)
    {
        if (start.getName().equals(name))
        {
            reply.add(start);
        }

        Iterator it = start.getContent().iterator();
        while (it.hasNext())
        {
            Element ele = (Element) it.next();
            recurseDeepContent(ele, name, reply);
        }
    }

    /**
     * If we have a String just add it to the buffer, but if we have an Element
     * then try to dig the strings out of it.
     */
    private static void recurseElement(Object sub, StringBuffer buffer)
    {
        if (sub instanceof Text)
        {
            buffer.append(((Text) sub).getText());
        }
        else if (sub instanceof Element)
        {
            recurseChildren((Element) sub, buffer);
        }
        else
        {
            log.error("unknown type: " + sub.getClass().getName()); //$NON-NLS-1$
        }
    }

    /**
     * Helper to extract the Strings from a nest of JDOM elements
     * @param ele The JDOM Element to dig into
     * @param buffer The place we accumulate strings.
     */
    private static void recurseChildren(Element ele, StringBuffer buffer)
    {
        // ele is a JDOM Element that might have a getContent() method
        try
        {
            List content = ele.getContent();
            for (Iterator it = content.iterator(); it.hasNext(); )
            {
                Object sub = it.next();
                recurseElement(sub, buffer);
            }
        }
        catch (Exception ex)
        {
            // We can continue, but we should report a problem
            log.error("Error interrogating: " + ele.getClass().getName(), ex); //$NON-NLS-1$
        }
    }
}