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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.diff.Difference;
import org.crosswire.common.diff.EditType;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.jdom.Content;
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
     * The following are values for the type attribute on the hi element.
     */
    /**
     * Constant for acrostic highlighting
     */
    public static final String HI_ACROSTIC = "acrostic"; //$NON-NLS-1$

    /**
     * Constant for rendering bold text
     */
    public static final String HI_BOLD = "bold"; //$NON-NLS-1$

    /**
     * Constant for rendering emphatic text
     */
    public static final String HI_EMPHASIS = "emphasis"; //$NON-NLS-1$

    /**
     * Constant for rendering illuminated text.
     */
    public static final String HI_ILLUMINATED = "illuminated"; //$NON-NLS-1$

    /**
     * Constant for rendering italic text.
     */
    public static final String HI_ITALIC = "italic"; //$NON-NLS-1$

    /**
     * Constant for rendering strike-through text
     */
    public static final String HI_LINETHROUGH = "line-through"; //$NON-NLS-1$

    /**
     * Constant for rendering normal text.
     */
    public static final String HI_NORMAL = "normal"; //$NON-NLS-1$

    /**
     * Constant for rendering small caps
     */
    public static final String HI_SMALL_CAPS = "small-caps"; //$NON-NLS-1$

    /**
     * Constant for rendering subscripts
     */
    public static final String HI_SUB = "sub"; //$NON-NLS-1$

    /**
     * Constant for rendering superscripts
     */
    public static final String HI_SUPER = "super"; //$NON-NLS-1$

    /**
     * Constant for rendering underlined text
     */
    public static final String HI_UNDERLINE = "underline"; //$NON-NLS-1$

    /**
     * Constant for rendering uppercase text
     */
    public static final String HI_X_CAPS = "x-caps"; //$NON-NLS-1$

    /**
     * Constant for rendering big text
     */
    public static final String HI_X_BIG = "x-big"; //$NON-NLS-1$

    /**
     * Constant for rendering small text
     */
    public static final String HI_X_SMALL = "x-small"; //$NON-NLS-1$

    /**
     * Constant for rendering tt text
     */
    public static final String HI_X_TT = "x-tt"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the justify right tag
     */
    public static final String SEG_JUSTIFYRIGHT = "text-align: right;"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the thml center tag
     */
    public static final String SEG_CENTER = "text-align: center;"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use div for. In this case the thml pre tag
     */
    public static final String DIV_PRE = "x-pre"; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the color tag
     */
    public static final String SEG_COLORPREFIX = "color: "; //$NON-NLS-1$

    /**
     * Constant to help narrow down what we use seg for. In this case the font-size tag
     */
    public static final String SEG_SIZEPREFIX = "font-size: "; //$NON-NLS-1$

    /**
     * Constant for x- types
     */
    public static final String TYPE_X_PREFIX = "x-"; //$NON-NLS-1$

    /**
     * Constant for the study note type
     */
    public static final String NOTETYPE_STUDY = "x-StudyNote"; //$NON-NLS-1$

    /**
     * Constant for the cross reference note type
     */
    public static final String NOTETYPE_REFERENCE = "crossReference"; //$NON-NLS-1$

    /**
     * Constant for the variant type segment
     */
    public static final String VARIANT_TYPE = "x-variant"; //$NON-NLS-1$
    public static final String VARIANT_CLASS = "x-class"; //$NON-NLS-1$

    /**
     * Constant for the pos (part of speech) type.
     */
    public static final String POS_TYPE = "x-pos"; //$NON-NLS-1$

    /**
     * Constant for the def (dictionary definition) type
     */
    public static final String DEF_TYPE = "x-def"; //$NON-NLS-1$

    /**
     * Constant for a Strong's numbering lemma
     */
    public static final String LEMMA_STRONGS = "strong:"; //$NON-NLS-1$
    public static final String MORPH_ROBINSONS = "robinson:"; //$NON-NLS-1$

    /**
     * Constant for Strong's numbering morphology
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

    /**
     * Table roles (on table, row and cell elements) can be "data", the default, or label.
     */
    public static final String TABLE_ROLE_LABEL = "label"; //$NON-NLS-1$

    /**
     * Possible cell alignments
     */
    public static final String CELL_ALIGN_LEFT = "left"; //$NON-NLS-1$
    public static final String CELL_ALIGN_RIGHT = "right"; //$NON-NLS-1$
    public static final String CELL_ALIGN_CENTER = "center"; //$NON-NLS-1$
    public static final String CELL_ALIGN_JUSTIFY = "justify"; //$NON-NLS-1$
    public static final String CELL_ALIGN_START = "start"; //$NON-NLS-1$
    public static final String CELL_ALIGN_END = "end"; //$NON-NLS-1$

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
    public static final String OSIS_ELEMENT_FIGURE = "figure"; //$NON-NLS-1$
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
    public static final String ATTRIBUTE_FIGURE_SRC = "src"; //$NON-NLS-1$
    public static final String ATTRIBUTE_TABLE_ROLE = "role"; //$NON-NLS-1$
    public static final String ATTRIBUTE_CELL_ALIGN = "align"; //$NON-NLS-1$
    public static final String OSIS_ATTR_TYPE = "type"; //$NON-NLS-1$
    public static final String OSIS_ATTR_CANONICAL = "canonical"; //$NON-NLS-1$
    public static final String OSIS_ATTR_SUBTYPE = "subType"; //$NON-NLS-1$
    public static final String OSIS_ATTR_REF = "osisRef"; //$NON-NLS-1$
    public static final String OSIS_ATTR_LEVEL = "level"; //$NON-NLS-1$
    public static final String ATTRIBUTE_SPEAKER_WHO = "who"; //$NON-NLS-1$
    public static final String ATTRIBUTE_W_MORPH = "morph"; //$NON-NLS-1$
    public static final String ATTRIBUTE_OSISTEXT_OSISIDWORK = "osisIDWork"; //$NON-NLS-1$
    // OSIS defines the lang attribute as the one from the xml namespace
    // Typical usage element.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang, Namespace.XML_NAMESPACE);
    public static final String OSIS_ATTR_LANG = "lang"; //$NON-NLS-1$
    public static final String ATTRIBUTE_DIV_BOOK = "book"; //$NON-NLS-1$

    /**
     * Prefix for OSIS IDs that refer to Bibles
     */
    private static final String OSISID_PREFIX_BIBLE = "Bible."; //$NON-NLS-1$

    private static final Set EXTRA_BIBLICAL_ELEMENTS = new HashSet(Arrays.asList(new String[]
    {
        OSIS_ELEMENT_NOTE, OSIS_ELEMENT_TITLE, OSIS_ELEMENT_REFERENCE
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
        public Element createHeaderCell()
        {
            Element ele = new Element(OSIS_ELEMENT_CELL);
            ele.setAttribute(ATTRIBUTE_TABLE_ROLE, TABLE_ROLE_LABEL);
            ele.setAttribute(ATTRIBUTE_CELL_ALIGN, CELL_ALIGN_CENTER);
            return ele;
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
        public Element createFigure()
        {
            return new Element(OSIS_ELEMENT_FIGURE);
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

        /**
         * Text
         */
        public Text createText(String text)
        {
            return new Text(text);
        }
    }

    /**
     * Dig past the osis and osisText element, if present, to get the meaningful content of the document.
     *
     * @return a fragment
     */
    public static List getFragment(Element root)
    {
        Element content = root;
        if (OSISUtil.OSIS_ELEMENT_OSIS.equals(root.getName()))
        {
            content = root.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
        }

        if (OSISUtil.OSIS_ELEMENT_OSISTEXT.equals(root.getName()))
        {
            content = root.getChild(OSISUtil.OSIS_ELEMENT_DIV);
        }

        // At this point we are at something interesting, possibly null.
        // If this was a semantically valid OSIS document then it is a div.
        // As long as this node has one child dig deeper.
        while (content != null && content.getContentSize() == 1)
        {
            Content firstChild = content.getContent(0);
            if (firstChild instanceof Element && OSISUtil.OSIS_ELEMENT_DIV.equals(((Element) firstChild).getName()))
            {
                content = (Element) firstChild;
            }
            break;
        }

        assert content != null;
        return content.getContent();
    }

    /**
     * Get the canonical text from an osis document consisting of a single fragment.
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
     * that "contains" the actual fragment.</p>
     * <p>A verse element may either be
     * a container or a milestone. Sword OSIS books differ in whether
     * they provide the verse element. Most do not. The few that do are
     * using the container model, but it has been proposed that milestones
     * are the best practice.</p>
     *
     * <p>The fragment may contain elements that are not a part of the
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
     * @return The canonical text without markup
     */
    public static String getCanonicalText(Element root)
    {
        StringBuffer buffer = new StringBuffer();

        // Dig past osis, osisText, if present, to get to the real content.
        List frag = OSISUtil.getFragment(root);

        Iterator dit = frag.iterator();
        String sID = null;
        Object data = null;
        Element ele = null;
        while (dit.hasNext())
        {
            data = dit.next();
            if (data instanceof Element)
            {
                ele = (Element) data;
                if (!isCanonical(ele))
                {
                    continue;
                }

                if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE))
                {
                    sID = ele.getAttributeValue(OSISUtil.OSIS_ATTR_SID);
                }

                if (sID != null)
                {
                    getCanonicalContent(ele, sID, dit, buffer);
                }
                else
                {
                    getCanonicalContent(ele, null, ele.getContent().iterator(), buffer);
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
        // Dig past osis, osisText, if present, to get to the real content.
        return getTextContent(OSISUtil.getFragment(root));
    }

    /**
     * A space separate string containing Strong's numbers.
     * @return The Strong's numbers in the text
     */
    public static String getStrongsNumbers(Element root)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator contentIter = getDeepContent(root, OSISUtil.OSIS_ELEMENT_W).iterator();
        while (contentIter.hasNext())
        {
            Element ele = (Element) contentIter.next();
            String attr = ele.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);
            if (attr != null)
            {
                if (buffer.length() > 0)
                {
                    buffer.append(' ');
                }

                buffer.append(attr);
            }
        }

        String lemmas = buffer.toString();

        // Clear out the buffer for re-use
        int len = buffer.length();
        if (len > 0)
        {
            buffer.delete(0, len);
        }

        Matcher matcher = strongsNumberPattern.matcher(lemmas);
        while (matcher.find())
        {
            String strongType = matcher.group(1);
            String strongsNum = matcher.group(2);
            if (buffer.length() > 0)
            {
                buffer.append(' ');
            }
            buffer.append(strongType);
            buffer.append(strongsNum);
        }

        return buffer.toString().trim();
    }

    /**
     * A space separate string containing osisID from the reference element.
     * @return The references in the text
     */
    public static String getReferences(Element root)
    {
        KeyFactory keyf = PassageKeyFactory.instance();
        Key collector = keyf.createEmptyKeyList();

        Iterator contentIter = getDeepContent(root, OSISUtil.OSIS_ELEMENT_REFERENCE).iterator();
        while (contentIter.hasNext())
        {
            Element ele = (Element) contentIter.next();
            String attr = ele.getAttributeValue(OSISUtil.OSIS_ATTR_REF);
            if (attr != null)
            {
                try
                {
                    Key key = keyf.getKey(attr);
                    collector.addAll(key);
                }
                catch (NoSuchKeyException e)
                {
                    log.warn("Unable to parse: " + attr, e); //$NON-NLS-1$
                }
            }
        }

        return collector.getOsisID();
    }

    /**
     * The text of non-reference notes.
     *
     * @return The references in the text
     */
    public static String getNotes(Element root)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator contentIter = getDeepContent(root, OSISUtil.OSIS_ELEMENT_NOTE).iterator();
        while (contentIter.hasNext())
        {
            Element ele = (Element) contentIter.next();
            String attr = ele.getAttributeValue(OSISUtil.OSIS_ATTR_TYPE);
            if (attr == null || !attr.equals(NOTETYPE_REFERENCE))
            {
                if (buffer.length() > 0)
                {
                    buffer.append(' ');
                }
                buffer.append(OSISUtil.getTextContent(ele.getContent()));
            }
        }

        return buffer.toString();
    }

    /**
     * The text of non-reference notes.
     *
     * @return The references in the text
     */
    public static String getHeadings(Element root)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator contentIter = getDeepContent(root, OSISUtil.OSIS_ELEMENT_TITLE).iterator();
        while (contentIter.hasNext())
        {
            Element ele = (Element) contentIter.next();
            getCanonicalContent(ele, null, ele.getContent().iterator(), buffer);
        }

        return buffer.toString();
    }

    private static void getCanonicalContent(Element parent, String sID, Iterator iter, StringBuffer buffer)
    {
        if (!isCanonical(parent))
        {
            return;
        }

        Object data = null;
        Element ele = null;
        String eleName = null;
        String eID = null;
        while (iter.hasNext())
        {
            data = iter.next();
            if (data instanceof Element)
            {
                ele = (Element) data;
                // If the milestoned element is done then quit.
                // This should be a eID=, that matches sID, from the same element.
                eleName = ele.getName();
                eID = ele.getAttributeValue(OSISUtil.OSIS_ATTR_SID);
                if (eID != null && eID.equals(sID) && eleName.equals(parent.getName()))
                {
                    break;
                }
                OSISUtil.getCanonicalContent(ele, sID, ele.getContent().iterator(), buffer);
            }
            else if (data instanceof Text)
            {
                buffer.append(((Text) data).getText());
            }
        }
    }

    private static boolean isCanonical(Content content)
    {
        boolean result = true;
        if (content instanceof Element)
        {
            Element element = (Element) content;

            // Ignore extra-biblical text
            if (EXTRA_BIBLICAL_ELEMENTS.contains(element.getName()))
            {
                String canonical = element.getAttributeValue(OSISUtil.OSIS_ATTR_CANONICAL);
                result = Boolean.valueOf(canonical).booleanValue();
            }
        }

        return result;
    }

    private static String getTextContent(List fragment)
    {
        StringBuffer buffer = new StringBuffer();

        Iterator contentIter = fragment.iterator();
        while (contentIter.hasNext())
        {
            Content next = (Content) contentIter.next();
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
        if (parent instanceof Element)
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
     * Convert a Difference list into a pretty HTML report.
     * @param diffs List of Difference objects
     * @return HTML representation
     */
    public static List diffToOsis(List diffs)
    {
        Element div = factory().createDiv();

        for (int x = 0; x < diffs.size(); x++)
        {
            Difference diff = (Difference) diffs.get(x);
            EditType editType = diff.getEditType(); // Mode (delete, equal, insert)
            Text text = factory.createText(diff.getText()); // Text of change.

            if (EditType.DELETE.equals(editType))
            {
                Element hi = factory().createHI();
                hi.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_LINETHROUGH);
                hi.addContent(text);
                div.addContent(hi);
            }
            else if (EditType.INSERT.equals(editType))
            {
                Element hi = factory().createHI();
                hi.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_UNDERLINE);
                hi.addContent(text);
                div.addContent(hi);
            }
            else
            {
                div.addContent(text);
            }
        }
        return div.cloneContent();
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

        Object data = null;
        Element ele = null;
        Iterator contentIter = start.getContent().iterator();
        while (contentIter.hasNext())
        {
            data = contentIter.next();
            if (data instanceof Element)
            {
                ele = (Element) data;
                recurseDeepContent(ele, name, reply);
            }
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
        Iterator contentIter = ele.getContent().iterator();
        while (contentIter.hasNext())
        {
            Object sub = contentIter.next();
            recurseElement(sub, buffer);
        }
    }

    private static String strongsNumber = "strong:([GH])0*([0-9]+)"; //$NON-NLS-1$
    private static Pattern strongsNumberPattern = Pattern.compile(strongsNumber);
}
