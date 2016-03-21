/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.diff.Difference;
import org.crosswire.common.diff.EditType;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.Versification;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some simple utilities to help working with OSIS classes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class OSISUtil {
    private static final char SPACE_SEPARATOR = ' ';
    private static final char MORPH_INFO_SEPARATOR = '@';

    /**
     * The following are values for the type attribute on the hi element.
     */
    /**
     * Constant for acrostic highlighting
     */
    public static final String HI_ACROSTIC = "acrostic";

    /**
     * Constant for rendering bold text
     */
    public static final String HI_BOLD = "bold";

    /**
     * Constant for rendering emphatic text
     */
    public static final String HI_EMPHASIS = "emphasis";

    /**
     * Constant for rendering illuminated text.
     */
    public static final String HI_ILLUMINATED = "illuminated";

    /**
     * Constant for rendering italic text.
     */
    public static final String HI_ITALIC = "italic";

    /**
     * Constant for rendering strike-through text
     */
    public static final String HI_LINETHROUGH = "line-through";

    /**
     * Constant for rendering normal text.
     */
    public static final String HI_NORMAL = "normal";

    /**
     * Constant for rendering small caps
     */
    public static final String HI_SMALL_CAPS = "small-caps";

    /**
     * Constant for rendering subscripts
     */
    public static final String HI_SUB = "sub";

    /**
     * Constant for rendering superscripts
     */
    public static final String HI_SUPER = "super";

    /**
     * Constant for rendering underlined text
     */
    public static final String HI_UNDERLINE = "underline";

    /**
     * Constant for rendering upper case text
     */
    public static final String HI_X_CAPS = "x-caps";

    /**
     * Constant for rendering big text
     */
    public static final String HI_X_BIG = "x-big";

    /**
     * Constant for rendering small text
     */
    public static final String HI_X_SMALL = "x-small";

    /**
     * Constant for rendering tt text
     */
    public static final String HI_X_TT = "x-tt";

    /**
     * Constant to help narrow down what we use seg for. In this case the
     * justify right tag
     */
    public static final String SEG_JUSTIFYRIGHT = "text-align: right;";

    /**
     * Constant to help narrow down what we use seg for. In this case the
     * justify right tag
     */
    public static final String SEG_JUSTIFYLEFT = "text-align: left;";

    /**
     * Constant to help narrow down what we use seg for. In this case the thml
     * center tag
     */
    public static final String SEG_CENTER = "text-align: center;";

    /**
     * Constant to help narrow down what we use div for. In this case the thml
     * pre tag
     */
    public static final String DIV_PRE = "x-pre";

    /**
     * Constant to help narrow down what we use seg for. In this case the color
     * tag
     */
    public static final String SEG_COLORPREFIX = "color: ";

    /**
     * Constant to help narrow down what we use seg for. In this case the
     * font-size tag
     */
    public static final String SEG_SIZEPREFIX = "font-size: ";

    /**
     * Constant for x- types
     */
    public static final String TYPE_X_PREFIX = "x-";

    /**
     * Constant for the study note type
     */
    public static final String NOTETYPE_STUDY = "x-StudyNote";

    /**
     * Constant for the cross reference note type
     */
    public static final String NOTETYPE_REFERENCE = "crossReference";

    /**
     * Constant for the variant type segment
     */
    public static final String VARIANT_TYPE = "x-variant";
    public static final String VARIANT_CLASS = "x-";

    /**
     * Constant for JSword generated content. Used for type or subType.
     */
    public static final String GENERATED_CONTENT = "x-gen";

    /**
     * Constant for the pos (part of speech) type.
     */
    public static final String POS_TYPE = "x-pos";

    /**
     * Constant for the def (dictionary definition) type
     */
    public static final String DEF_TYPE = "x-def";

    /**
     * Constant for a Strong's numbering lemma
     */
    public static final String LEMMA_STRONGS = "strong:";
    public static final String LEMMA_MISC = "lemma:";
    public static final String MORPH_ROBINSONS = "robinson:";

    /**
     * Constant for Strong's numbering morphology
     */
    public static final String MORPH_STRONGS = "x-StrongsMorph:T";

    /**
     * Constant to help narrow down what we use "q" for. In this case:
     * blockquote
     */
    public static final String Q_BLOCK = "blockquote";

    /**
     * Constant to help narrow down what we use "q" for. In this case: citation
     */
    public static final String Q_CITATION = "citation";

    /**
     * Constant to help narrow down what we use "q" for. In this case: embedded
     */
    public static final String Q_EMBEDDED = "embedded";

    /**
     * Constant to help narrow down what we use "list" for.
     */
    public static final String LIST_ORDERED = "x-ordered";
    public static final String LIST_UNORDERED = "x-unordered";

    /**
     * Table roles (on table, row and cell elements) can be "data", the default,
     * or label.
     */
    public static final String TABLE_ROLE_LABEL = "label";

    /**
     * Possible cell alignments
     */
    public static final String CELL_ALIGN_LEFT = "left";
    public static final String CELL_ALIGN_RIGHT = "right";
    public static final String CELL_ALIGN_CENTER = "center";
    public static final String CELL_ALIGN_JUSTIFY = "justify";
    public static final String CELL_ALIGN_START = "start";
    public static final String CELL_ALIGN_END = "end";

    public static final String OSIS_ELEMENT_ABBR = "abbr";
    public static final String OSIS_ELEMENT_TITLE = "title";
    public static final String OSIS_ELEMENT_TABLE = "table";
    public static final String OSIS_ELEMENT_SPEECH = "speech";
    public static final String OSIS_ELEMENT_SPEAKER = "speaker";
    public static final String OSIS_ELEMENT_ROW = "row";
    public static final String OSIS_ELEMENT_REFERENCE = "reference";
    public static final String OSIS_ELEMENT_NOTE = "note";
    public static final String OSIS_ELEMENT_NAME = "name";
    public static final String OSIS_ELEMENT_Q = "q";
    public static final String OSIS_ELEMENT_LIST = "list";
    public static final String OSIS_ELEMENT_P = "p";
    public static final String OSIS_ELEMENT_ITEM = "item";
    public static final String OSIS_ELEMENT_FIGURE = "figure";
    public static final String OSIS_ELEMENT_FOREIGN = "foreign";
    public static final String OSIS_ELEMENT_W = "w";
    public static final String OSIS_ELEMENT_CHAPTER = "chapter";
    public static final String OSIS_ELEMENT_VERSE = "verse";
    public static final String OSIS_ELEMENT_CELL = "cell";
    public static final String OSIS_ELEMENT_DIV = "div";
    public static final String OSIS_ELEMENT_OSIS = "osis";
    public static final String OSIS_ELEMENT_WORK = "work";
    public static final String OSIS_ELEMENT_HEADER = "header";
    public static final String OSIS_ELEMENT_OSISTEXT = "osisText";
    public static final String OSIS_ELEMENT_SEG = "seg";
    public static final String OSIS_ELEMENT_LG = "lg";
    public static final String OSIS_ELEMENT_L = "l";
    public static final String OSIS_ELEMENT_LB = "lb";
    public static final String OSIS_ELEMENT_HI = "hi";

    public static final String ATTRIBUTE_TEXT_OSISIDWORK = "osisIDWork";
    public static final String ATTRIBUTE_WORK_OSISWORK = "osisWork";
    public static final String OSIS_ATTR_OSISID = "osisID";
    public static final String OSIS_ATTR_SID = "sID";
    public static final String OSIS_ATTR_EID = "eID";
    public static final String ATTRIBUTE_W_LEMMA = "lemma";
    public static final String ATTRIBUTE_FIGURE_SRC = "src";
    public static final String ATTRIBUTE_TABLE_BORDER = "border";
    public static final String ATTRIBUTE_TABLE_ROLE = "role";
    public static final String ATTRIBUTE_CELL_ALIGN = "align";
    public static final String ATTRIBUTE_CELL_ROWS = "rows";
    public static final String ATTRIBUTE_CELL_COLS = "cols";
    public static final String OSIS_ATTR_TYPE = "type";
    public static final String OSIS_ATTR_CANONICAL = "canonical";
    public static final String OSIS_ATTR_SUBTYPE = "subType";
    public static final String OSIS_ATTR_REF = "osisRef";
    public static final String OSIS_ATTR_LEVEL = "level";
    public static final String ATTRIBUTE_SPEAKER_WHO = "who";
    public static final String ATTRIBUTE_Q_WHO = "who";
    public static final String ATTRIBUTE_W_MORPH = "morph";
    public static final String ATTRIBUTE_OSISTEXT_OSISIDWORK = "osisIDWork";
    // OSIS defines the lang attribute as the one from the xml namespace
    // Typical usage element.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang,
    // Namespace.XML_NAMESPACE);
    public static final String OSIS_ATTR_LANG = "lang";
    public static final String ATTRIBUTE_DIV_BOOK = "book";

    /**
     * Prefix for OSIS IDs that refer to Bibles
     */
    private static final String OSISID_PREFIX_BIBLE = "Bible.";

    private static final Set<String> EXTRA_BIBLICAL_ELEMENTS = new HashSet<String>(Arrays.asList(new String[] {
            OSIS_ELEMENT_NOTE, OSIS_ELEMENT_TITLE, OSIS_ELEMENT_REFERENCE
    }));

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(OSISUtil.class);


    /**
     * Prevent instantiation
     */
    private OSISUtil() {
    }

    private static OSISFactory factory = new OSISFactory();

    /**
     * An accessor for the OSISFactory that creates OSIS objects
     * 
     * @return the singleton OSISFactory
     */
    public static OSISFactory factory() {
        return factory;
    }

    /**
     * A generic way of creating empty Elements of various types
     */
    public static class OSISFactory {
        /**
        * @return an abbr element
        */
        public Element createAbbr() {
            return new Element(OSIS_ELEMENT_ABBR);
        }

        /**
         * @return a seg element
         */
        public Element createSeg() {
            return new Element(OSIS_ELEMENT_SEG);
        }

        /**
         * @return an osisText element
         */
        public Element createOsisText() {
            return new Element(OSIS_ELEMENT_OSISTEXT);
        }

        /**
         * @return a header element
         */
        public Element createHeader() {
            return new Element(OSIS_ELEMENT_HEADER);
        }

        /**
         * @return a work element
         */
        public Element createWork() {
            return new Element(OSIS_ELEMENT_WORK);
        }

        /**
         * @return an osis element
         */
        public Element createOsis() {
            return new Element(OSIS_ELEMENT_OSIS);
        }

        /**
         * @return a div element
         */
        public Element createDiv() {
            return new Element(OSIS_ELEMENT_DIV);
        }

        /**
         * @return a cell element
         */
        public Element createCell() {
            return new Element(OSIS_ELEMENT_CELL);
        }

        /**
         * @return a header cell element (akin to HTML's TH)
         */
        public Element createHeaderCell() {
            Element ele = new Element(OSIS_ELEMENT_CELL);
            ele.setAttribute(ATTRIBUTE_TABLE_ROLE, TABLE_ROLE_LABEL);
            ele.setAttribute(ATTRIBUTE_CELL_ALIGN, CELL_ALIGN_CENTER);
            return ele;
        }

        /**
         * @return a verse element
         */
        public Element createVerse() {
            return new Element(OSIS_ELEMENT_VERSE);
        }

        /**
         * @return a w element
         */
        public Element createW() {
            return new Element(OSIS_ELEMENT_W);
        }

        /**
         * @return a figure element
         */
        public Element createFigure() {
            return new Element(OSIS_ELEMENT_FIGURE);
        }

        /**
         * @return a foreign element
         */
        public Element createForeign() {
            return new Element(OSIS_ELEMENT_FOREIGN);
        }

        /**
         * @return an item element
         */
        public Element createItem() {
            return new Element(OSIS_ELEMENT_ITEM);
        }

        /**
         * @return a p element
         */
        public Element createP() {
            return new Element(OSIS_ELEMENT_P);
        }

        /**
         * @return a list element
         */
        public Element createList() {
            return new Element(OSIS_ELEMENT_LIST);
        }

        /**
         * @return a q element
         */
        public Element createQ() {
            return new Element(OSIS_ELEMENT_Q);
        }

        /**
         * @return a name element
         */
        public Element createName() {
            return new Element(OSIS_ELEMENT_NAME);
        }

        /**
         * @return a note element
         */
        public Element createNote() {
            return new Element(OSIS_ELEMENT_NOTE);
        }

        /**
         * @return a reference element
         */
        public Element createReference() {
            return new Element(OSIS_ELEMENT_REFERENCE);
        }

        /**
         * @return a row element
         */
        public Element createRow() {
            return new Element(OSIS_ELEMENT_ROW);
        }

        /**
         * @return a speaker element
         */
        public Element createSpeaker() {
            return new Element(OSIS_ELEMENT_SPEAKER);
        }

        /**
         * @return a speech element
         */
        public Element createSpeech() {
            return new Element(OSIS_ELEMENT_SPEECH);
        }

        /**
         * @return a table element
         */
        public Element createTable() {
            return new Element(OSIS_ELEMENT_TABLE);
        }

       /**
        * @return a title element
        */
       public Element createTitle() {
           return new Element(OSIS_ELEMENT_TITLE);
       }

        /**
         * Create a title marked as generated.
         * 
         * @return a generated title element
         */
        public Element createGeneratedTitle() {
            Element title = new Element(OSIS_ELEMENT_TITLE);
            title.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.GENERATED_CONTENT);
            return title;
        }

        /**
         * Line Group
         * 
         * @return a lg element
         */
        public Element createLG() {
            return new Element(OSIS_ELEMENT_LG);
        }

        /**
         * Line
         * 
         * @return a l element
         */
        public Element createL() {
            return new Element(OSIS_ELEMENT_L);
        }

        /**
         * Line Break
         * 
         * @return a lb element
         */
        public Element createLB() {
            return new Element(OSIS_ELEMENT_LB);
        }

        /**
         * Highlight
         * 
         * @return a hi element
         */
        public Element createHI() {
            return new Element(OSIS_ELEMENT_HI);
        }

        /**
         * Text
         * 
         * @param text the text for this element
         * @return a text element
         */
        public Text createText(String text) {
            return new Text(text);
        }
    }

    /**
     * Dig past the osis and osisText element, if present, to get the meaningful
     * content of the document.
     * 
     * @param root the element from which to get a fragment
     * @return a fragment
     */
    public static List<Content> getFragment(Element root) {
        if (root != null) {
            Element content = root;
            if (OSISUtil.OSIS_ELEMENT_OSIS.equals(root.getName())) {
                content = root.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
            }

            if (OSISUtil.OSIS_ELEMENT_OSISTEXT.equals(root.getName())) {
                content = root.getChild(OSISUtil.OSIS_ELEMENT_DIV);
            }

            // At this point we are at something interesting, possibly null.
            // If this was a semantically valid OSIS document then it is a div.
            // As long as this node has one child dig deeper.
            if (content != null && content.getContentSize() == 1) {
                Content firstChild = content.getContent(0);
                if (firstChild instanceof Element && OSISUtil.OSIS_ELEMENT_DIV.equals(((Element) firstChild).getName())) {
                    content = (Element) firstChild;
                }
            }

            if (content != null) {
                return content.getContent();
            }
        }
        return new ArrayList<Content>();
    }

    /**
     * Get the canonical text from an osis document consisting of a single
     * fragment. The document is assumed to be valid OSIS2.0 XML. While xml
     * valid is rigidly defined as meaning that an xml parser can validate the
     * document, it does not mean that the document is valid OSIS. This is a
     * semantic problem that is not validated. This method assumes that the root
     * element is also semantically valid.
     * 
     * <p>
     * This means that the top level element's tagname is osis. This can contain
     * either a osisText or an osisCorpus. If it is an osisCorpus, then it
     * contains an osisText. However, as a simplification, since JSword
     * constructs the whole doc for the fragment, osisCorpus can be ignored.
     * <p>
     * The osisText element contains a div element that is either a container or
     * a milestone. Again, JSword is providing the div element and it will be
     * provided as a container. It is this div that "contains" the actual
     * fragment.
     * </p>
     * <p>
     * A verse element may either be a container or a milestone. Sword OSIS
     * books differ in whether they provide the verse element. Most do not. The
     * few that do are using the container model, but it has been proposed that
     * milestones are the best practice.
     * </p>
     * 
     * <p>
     * The fragment may contain elements that are not a part of the original
     * text. These are things such as notes.
     * </p>
     * 
     * <p>
     * Milestones require special handling. Beginning milestones elements have
     * an sID attribute, while ending milestones have an eID with the same value
     * as the opening. So everything between the start and the corresponding end
     * is the content of the element. Also, for a given element, say div, they
     * have to be properly nested as if they were container elements.
     * </p>
     * 
     * @param root
     *            the whole osis document.
     * @return The canonical text without markup
     */
    public static String getCanonicalText(Element root) {
        // if someone passes a root element which has text in, we need to check whether it's worth processing.
        // For example. where you have a non-canonical title being passed in, we deal with this here.
        if (!isCanonical(root)) {
            //no point in continuing...
            return "";
        }

        StringBuilder buffer = new StringBuilder();

        // Dig past osis, osisText, if present, to get to the real content.
        List<Content> frag = OSISUtil.getFragment(root);

        Iterator<Content> dit = frag.iterator();
        String sID = null;
        Content data = null;
        Element ele = null;
        while (dit.hasNext()) {
            data = dit.next();
            if (data instanceof Element) {
                ele = (Element) data;
                if (!isCanonical(ele)) {
                    continue;
                }

                if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
                    sID = ele.getAttributeValue(OSISUtil.OSIS_ATTR_SID);
                }

                if (sID != null) {
                    getCanonicalContent(ele, sID, dit, buffer);
                } else {
                    getCanonicalContent(ele, null, ele.getContent().iterator(), buffer);
                }
            } else if (data instanceof Text) {
                // make sure that adjacent text elements are separated by
                // whitespace
                // TODO(dms): verify that the xml parser does not split words
                // containing entities.
                int lastIndex = buffer.length() - 1;
                String text = ((Text) data).getText();
                // Ignore empty text nodes and do not add 
                if (text.length() != 0) {
                    //do not add spaces when within a OSIS seg
                    if (lastIndex >= 0 && !Character.isWhitespace(buffer.charAt(lastIndex)) && !Character.isWhitespace(text.charAt(0))) {
                        buffer.append(' ');
                    }
                    buffer.append(text);
                }
            }
        }

        return buffer.toString().trim();
    }

    /**
     * A simplified plain text version of the data in this Element with all the
     * markup stripped out.
     * 
     * @param root
     *            the whole osis document.
     * @return The Bible text without markup
     */
    public static String getPlainText(Element root) {
        // Dig past osis, osisText, if present, to get to the real content.
        return getTextContent(OSISUtil.getFragment(root));
    }

    /**
     * A space separate string containing Strong's numbers.
     * 
     * @param root
     *            the whole osis document.
     * @return The Strong's numbers in the text
     */
    public static String getStrongsNumbers(Element root) {
        return getLexicalInformation(root, false);
    }

    /**
     * A '@' separated list of morphologies and strong numbers
     * 
     * @param root the osis element in question
     * @return the string
     */
    public static String getMorphologiesWithStrong(Element root) {
        return getLexicalInformation(root, true);
    }

    /**
     * concatenates strong and morphology information together
     * 
     * @param root the osis element in question
     * @param includeMorphology whether to include morphology
     * @return root of the element
     */
    public static String getLexicalInformation(Element root, boolean includeMorphology) {
        StringBuilder buffer = new StringBuilder();

        for (Content content : getDeepContent(root, OSISUtil.OSIS_ELEMENT_W)) {
            Element ele = (Element) content;
            String attr = ele.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);
            if (attr != null) {
                Matcher matcher = strongsNumberPattern.matcher(attr);
                while (matcher.find()) {
                    String strongsNum = matcher.group(1);
                    if (buffer.length() > 0) {
                        buffer.append(' ');
                    }

                    if (includeMorphology) {
                        //if including morphology, we want 1 big field, separated with '@'
                        strongsNum = strongsNum.replace(SPACE_SEPARATOR, MORPH_INFO_SEPARATOR);
                    }
                    buffer.append(strongsNum);

                    if (includeMorphology) {
                        //also include morphology if available
                        String morph = ele.getAttributeValue(OSISUtil.ATTRIBUTE_W_MORPH);
                        if (morph != null && morph.length() != 0) {
                            buffer.append(MORPH_INFO_SEPARATOR);
                            buffer.append(morph.replace(SPACE_SEPARATOR, MORPH_INFO_SEPARATOR));
                        }
                    }
                }
            }
        }

        return buffer.toString().trim();
    }

    /**
     * A space separate string containing osisID from the reference element.
     * We pass book and key because the xref may not be valid and it needs to be reported.
     *
     * @param book the book to which the references refer
     * @param key the verse containing the cross references
     * @param v11n the versification
     * @param root the osis element in question
     * @return The references in the text
     */
    public static String getReferences(Book book, Key key, Versification v11n, Element root) {
        PassageKeyFactory keyf = PassageKeyFactory.instance();
        Key collector = keyf.createEmptyKeyList(v11n);

        for (Content content : getDeepContent(root, OSISUtil.OSIS_ELEMENT_REFERENCE)) {
            Element ele = (Element) content;
            String attr = ele.getAttributeValue(OSISUtil.OSIS_ATTR_REF);
            if (attr != null) {
                try {
                    collector.addAll(keyf.getKey(v11n, attr));
                } catch (NoSuchKeyException e) {
                    DataPolice.report(book, key, "Unable to parse: " + attr + " - No such reference:" + e.getMessage());
                }
            }
        }

        return collector.getOsisID();
    }

    /**
     * The text of non-reference notes.
     * 
     * @param root the whole OSIS document
     * @return The references in the text
     */
    public static String getNotes(Element root) {
        StringBuilder buffer = new StringBuilder();

        for (Content content : getDeepContent(root, OSISUtil.OSIS_ELEMENT_NOTE)) {
            Element ele = (Element) content;
            String attr = ele.getAttributeValue(OSISUtil.OSIS_ATTR_TYPE);
            if (attr == null || !attr.equals(NOTETYPE_REFERENCE)) {
                if (buffer.length() > 0) {
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
     * @param root the whole OSIS document
     * @return The references in the text
     */
    public static String getHeadings(Element root) {
        StringBuilder buffer = new StringBuilder();

        for (Content content : getDeepContent(root, OSISUtil.OSIS_ELEMENT_TITLE)) {
            Element ele = (Element) content;

            if (buffer.length() > 0) {
                buffer.append(' ');
            }
            buffer.append(OSISUtil.getTextContent(ele.getContent()));
        }

        return buffer.toString();
    }

    private static void getCanonicalContent(Element parent, String sID, Iterator<Content> iter, StringBuilder buffer) {
        if (!isCanonical(parent)) {
            return;
        }

        Content data = null;
        Element ele = null;
        String eleName = null;
        String eID = null;
        while (iter.hasNext()) {
            data = iter.next();
            if (data instanceof Element) {
                ele = (Element) data;
                // If the milestoned element is done then quit.
                // This should be a eID=, that matches sID, from the same
                // element.
                eleName = ele.getName();
                eID = ele.getAttributeValue(OSISUtil.OSIS_ATTR_SID);
                if (eID != null && eID.equals(sID) && eleName.equals(parent.getName())) {
                    break;
                }
                OSISUtil.getCanonicalContent(ele, sID, ele.getContent().iterator(), buffer);
            } else if (data instanceof Text) {
                // make sure that adjacent text elements are separated by
                // whitespace
                // Empty elements also produce whitespace.
                // TODO(dms): verify that the xml parser does not split words
                // containing entities.
                int lastIndex = buffer.length() - 1;
                String text = ((Text) data).getText();
                if (lastIndex >= 0 && !Character.isWhitespace(buffer.charAt(lastIndex)) && (text.length() == 0 || !Character.isWhitespace(text.charAt(0)))  && !OSIS_ELEMENT_SEG.equals(parent.getName())) {
                    buffer.append(' ');
                }
                buffer.append(text);
            }
        }
    }

    private static boolean isCanonical(Content content) {
        boolean result = true;
        if (content instanceof Element) {
            Element element = (Element) content;

            // Ignore extra-biblical text
            if (EXTRA_BIBLICAL_ELEMENTS.contains(element.getName())) {
                String canonical = element.getAttributeValue(OSISUtil.OSIS_ATTR_CANONICAL);
                result = Boolean.valueOf(canonical).booleanValue();
            }
        }

        return result;
    }

    private static String getTextContent(List<Content> fragment) {
        StringBuilder buffer = new StringBuilder();

        for (Content next : fragment) {
            recurseElement(next, buffer);
        }

        return buffer.toString();
    }

    /**
     * Find all the instances of elements of type <code>find</code> under the
     * element <code>div</code>.
     * 
     * @param div the element to trawl
     * @param name the element name to search
     * @return the collection of matching content
     */
    public static Collection<Content> getDeepContent(Element div, String name) {
        List<Content> reply = new ArrayList<Content>();
        recurseDeepContent(div, name, reply);
        return reply;
    }

    /**
     * Walk up the tree from the W to find out what verse we are in.
     * 
     * @param v11n the versification
     * @param ele
     *            The start point for our verse hunt.
     * @return The verse we are in
     * @throws BookException 
     */
    public static Verse getVerse(Versification v11n, Element ele) throws BookException {
        if (ele.getName().equals(OSIS_ELEMENT_VERSE)) {
            // If the element is an OSIS Verse then this is fairly easy
            String osisid = ele.getAttributeValue(OSIS_ATTR_OSISID);

            try {
                return VerseFactory.fromString(v11n, osisid);
            } catch (NoSuchVerseException ex) {
                throw new BookException(JSOtherMsg.lookupText("OsisID not valid: {0}", osisid), ex);
            }
        }

        // So we just walk up the tree trying to find a verse
        Parent parent = ele.getParent();
        if (parent instanceof Element) {
            return getVerse(v11n, (Element) parent);
        }

        throw new BookException(JSOtherMsg.lookupText("Verse element could not be found"));
    }

    /**
     * Helper method to create the boilerplate headers in an OSIS document from
     * the current metadata object
     * 
     * @param bmd the book's meta data
     * @return the root of an OSIS document
     */
    public static Element createOsisFramework(BookMetaData bmd) {
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
     * 
     * @param diffs
     *            List of Difference objects
     * @return HTML representation
     */
    public static List<Content> diffToOsis(List<Difference> diffs) {
        Element div = factory().createDiv();

        for (int x = 0; x < diffs.size(); x++) {
            Difference diff = diffs.get(x);
            EditType editType = diff.getEditType(); // Mode (delete, equal,
                                                    // insert)
            Text text = factory.createText(diff.getText()); // Text of change.

            if (EditType.DELETE.equals(editType)) {
                Element hi = factory().createHI();
                hi.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_LINETHROUGH);
                hi.addContent(text);
                div.addContent(hi);
            } else if (EditType.INSERT.equals(editType)) {
                Element hi = factory().createHI();
                hi.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_UNDERLINE);
                hi.addContent(text);
                div.addContent(hi);
            } else {
                div.addContent(text);
            }
        }
        return div.cloneContent();
    }

    public static List<Content> rtfToOsis(String rtf) {
        Element div = factory().createDiv();
        Stack<Content> stack = new Stack<Content>();
        stack.push(div);

        int strlen = rtf.length();

        StringBuilder text = new StringBuilder(strlen);

        int i = 0;
        for (i = 0; i < strlen; i++) {
            char curChar = rtf.charAt(i);
            if (curChar != '\\') {
                text.append(curChar);
                continue;
            }

            // The following are ordered from most to least common
            // and when one is a prefix of another, it follows.

            // Used to end all open attributes. Only \qc in our implementation.
            if (rtf.startsWith("\\pard", i)) {
                Element currentElement = (Element) stack.pop();
                currentElement.addContent(text.toString());
                text.delete(0, text.length());
                stack.clear();
                stack.push(div);
                i += (i + 5 < strlen && rtf.charAt(i + 5) == ' ') ? 5 : 4;
                continue;
            }

            // Simulate a paragraph break.
            if (rtf.startsWith("\\par", i)) {
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                text.delete(0, text.length());
                currentElement.addContent(OSISUtil.factory.createLB());
                i += (i + 4 < strlen && rtf.charAt(i + 4) == ' ') ? 4 : 3;
                continue;
            }

            // OSIS does not have the notion of centered text.
            // So we define our own
            if (rtf.startsWith("\\qc", i)) {
                Element centerDiv = OSISUtil.factory.createDiv();
                centerDiv.setAttribute(OSIS_ATTR_TYPE, "x-center");
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                text.delete(0, text.length());
                currentElement.addContent(centerDiv);
                stack.push(centerDiv);
                // skip following space, if any
                i += (i + 3 < strlen && rtf.charAt(i + 3) == ' ') ? 3 : 2;
                continue;
            }

            // convert Unicode representations to Unicode
            if (rtf.startsWith("\\u", i)) {
                StringBuilder buf = new StringBuilder();
                i += 2;
                while (i < strlen) {
                    char curDigit = rtf.charAt(i);
                    if (curDigit != '-' && !Character.isDigit(curDigit)) {
                        break;
                    }
                    buf.append(curDigit);
                    i++;
                }
                // At this point:
                // buf contains the numeric representation of the number, 16-bit
                // signed
                // charAt(i) is the substitution character if Unicode is not
                // supported
                int value = Integer.parseInt(buf.toString());
                if (value < 0) {
                    value += 65536;
                }
                text.append((char) value);
                // don't advance since i is on the substitute character.
                continue;
            }

            // close italic and bold
            if (rtf.startsWith("\\i0", i) || rtf.startsWith("\\b0", i)) {
                Element currentElement = (Element) stack.pop();
                currentElement.addContent(text.toString());
                text.delete(0, text.length());
                i += (i + 3 < strlen && rtf.charAt(i + 3) == ' ') ? 3 : 2;
                continue;
            }

            // Skip escaped whitespace
            if (rtf.startsWith(" ", i) || rtf.startsWith("\n", i)) {
                i += 1;
                continue;
            }

            // start italic
            if (rtf.startsWith("\\i", i)) {
                Element hiElement = OSISUtil.factory.createHI();
                hiElement.setAttribute(OSIS_ATTR_TYPE, HI_ITALIC);
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                text.delete(0, text.length());
                currentElement.addContent(hiElement);
                stack.push(hiElement);
                i += (i + 2 < strlen && rtf.charAt(i + 2) == ' ') ? 2 : 1;
                continue;
            }

            // start bold
            if (rtf.startsWith("\\b", i)) {
                Element hiElement = OSISUtil.factory.createHI();
                hiElement.setAttribute(OSIS_ATTR_TYPE, HI_BOLD);
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                text.delete(0, text.length());
                currentElement.addContent(hiElement);
                stack.push(hiElement);
                i += (i + 2 < strlen && rtf.charAt(i + 2) == ' ') ? 2 : 1;
                continue;
            }

        }

        // If there is any text that has not been consumed
        if (text.length() > 0) {
            div.addContent(text.toString());
        }
        // div.addContent(text.toString());
        // // If the fragment is already in a document, then use that.
        // Document doc = div.getDocument();
        // if (doc == null)
        // {
        // doc = new Document(div);
        // }
        // SAXEventProvider ep = new JDOMSAXEventProvider(doc);
        // ContentHandler osis = new
        // PrettySerializingContentHandler(FormatType.CLASSIC_INDENT);
        // try
        // {
        // ep.provideSAXEvents(osis);
        // }
        // catch (SAXException e)
        // {
        // e.printStackTrace();
        // }
        // System.err.println(osis.toString());
        return div.cloneContent();
    }

    /**
     * Find all the instances of elements of type <code>find</code> under the
     * element <code>div</code>. For internal use only.
     * 
     * @param start the node under which searches occur
     * @param name element name to search
     * @param reply the list to modify with matching content
     */
    private static void recurseDeepContent(Element start, String name, List<Content> reply) {
        if (start.getName().equals(name)) {
            reply.add(start);
        }

//        Content data = null;
        Element ele = null;
        for (Content data : start.getContent()) {
            if (data instanceof Element) {
                ele = (Element) data;
                recurseDeepContent(ele, name, reply);
            }
        }
    }

    /**
     * If we have a String just add it to the buffer, but if we have an Element
     * then try to dig the strings out of it.
     * 
     * @param sub a sub element or text node
     * @param buffer the buffer to build on match
     */
    private static void recurseElement(Object sub, StringBuilder buffer) {
        if (sub instanceof Text) {
            buffer.append(((Text) sub).getText());
        } else if (sub instanceof Element) {
            recurseChildren((Element) sub, buffer);
        } else {
            log.error("unknown type: {}", sub.getClass().getName());
        }
    }

    /**
     * Helper to extract the Strings from a nest of JDOM elements
     * 
     * @param ele
     *            The JDOM Element to dig into
     * @param buffer
     *            The place we accumulate strings.
     */
    private static void recurseChildren(Element ele, StringBuilder buffer) {
        // ele is a JDOM Element that might have a getContent() method
        for (Content sub : ele.getContent()) {
            recurseElement(sub, buffer);
        }
    }

    private static String strongsNumber = "strong:([GgHh][0-9]+!?[A-Za-z]*)";
    private static Pattern strongsNumberPattern = Pattern.compile(strongsNumber);
}
