
package org.crosswire.jsword.book.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.osis.Note;
import org.crosswire.jsword.osis.P;
import org.crosswire.jsword.osis.Seg;
import org.crosswire.jsword.osis.W;

/**
 * Filter to convert GBF data to OSIS format.
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
public class GBFFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.Filter#toOSIS(org.crosswire.jsword.book.data.BookDataListener, java.lang.String)
     */
    public void toOSIS(Element ele, String plain) throws DataException
    {
        try
        {
            Stack stack = new Stack();
            stack.push(ele);

            TagGenerator generator = new TagGenerator(plain);
            Tag tag = generator.getNextTag();
            while (tag != null)
            {
                tag.updateOsisStack(stack);
                tag = generator.getNextTag();
            }

            stack.pop();
        }
        catch (JAXBException ex)
        {
            throw new DataException(Msg.GBF_JAXB, ex);
        }
    }

    /**
     * 
     */
    private static class TagGenerator
    {
        /**
         * 
         */
        public TagGenerator(String plain)
        {
            int lastIndex = plain.length() - 1;
            if (lastIndex >= 0 && plain.charAt(lastIndex) == ((char) 13))
            {
                plain = plain.substring(0, lastIndex);
            }
            remains = plain;
        }

        /**
         * Get Next tags in the string
         */
        public Tag getNextTag()
        {
            if (retval.isEmpty())
            {
                if (remains == null)
                {
                    return null;
                }

                parseNextTag();
            }
            return (Tag) retval.remove(0);
        }

        /**
         * 
         */
        private void parseNextTag()
        {
            if (remains == null)
            {
                return;
            }

            int ltpos = remains.indexOf('<');
            int gtpos = remains.indexOf('>');

            if (ltpos == -1 && gtpos == -1)
            {
                // no more tags to decode
                retval.add(createText(remains));
                remains = null;
                return;
            }

            // check that we don't have unmatched tags
            if (ltpos == -1 || gtpos == -1)
            {
                log.warn("ignoring unmatched '<' or '>' in gbf: " + remains);
                retval.add(createText(remains));
                remains = null;
                return;
            }

            // check that the tags are in a sensible order
            if (ltpos > gtpos)
            {
                log.warn("ignoring unmatched '<' or '>' in gbf: " + remains);
                retval.add(createText(remains));
                remains = null;
                return;
            }

            // generate tags
            String start = remains.substring(0, ltpos);
            int strLen = start.length();
            if (strLen > 0)
            {
                int beginIndex = 0;
                boolean inSepStr = isSeperator(start.charAt(0));
                // split words from seperators...
                // e.g., "a b c? e g." -> "a b c", "? ", "e g."
                //       "a b c<tag> e g." -> "a b c", tag, " ", "e g."
                for (int i = 1; inSepStr && i < strLen; i++)
                {
                    char currentChar = start.charAt(i);
                    if (!isSeperator(currentChar))
                    {
                        retval.add(createText(start.substring(beginIndex, i)));
                        beginIndex = i;
                        inSepStr = false;
                    }
                }

                if (beginIndex < strLen)
                {
                    retval.add(createText(start.substring(beginIndex)));
                }
            }

            String tag = remains.substring(ltpos + 1, gtpos);
            if (tag.length() > 0)
            {
                retval.add(createTag(tag));
            }

            remains = remains.substring(gtpos + 1);
        }

        /**
         * 
         */
        private boolean isSeperator(char c)
        {
            final String seperators = " ,:;.?!";
            return seperators.indexOf(c) >= 0;
        }

        /**
         * 
         */
        private Tag createTag(String tag)
        {
            if (tag.equals("RB"))
            {
                return new TextWithEmbeddedFootnote();
            }
            if (tag.equals("RF"))
            {
                return new FootnoteStartTag();
            }
            if (tag.equals("Rf"))
            {
                return new FootnoteEndTag();
            }
            if (tag.equals("FI"))
            {
                return new ItalicStartTag();
            }
            if (tag.equals("Fi"))
            {
                return new ItalicEndTag();
            }
            if (tag.equals("CM"))
            {
                return new ParagraphTag();
            }
            if (tag.startsWith("WT"))
            {
                return new StrongsMorphRefTag(tag);
            }
            if (tag.startsWith("WH") || tag.startsWith("WG"))
            {
                return new StrongsWordRefTag(tag);
            }
            return new UnknownTag(tag);
        }

        private String remains;
        private List retval = new ArrayList();
    }

    /**
     * Create a text tag which might involve some fancy parsing
     */
    protected static TextTag createText(String text)
    {
        return new TextTag(text);
    }

    /**
     * GBF Tag interface
     * Now the number of supported tags are small.
     * If the number become large, refactor...
     * <li>1. refactor Tag to public abstract class GBFTag</li>
     * <li>2. move createTag() to GBFTag</li>
     * <li>3. move tag classes to GBFTag.java so that adding tags updates only GBFTag.java</li>
     * On adding new tags, implements new tag classes and update createTag()
     */
    private static interface Tag
    {
        /**
         * Sub-classes should implement this method to generate OSIS Object
         */
        public void updateOsisStack(Stack osisStack) throws JAXBException;
    }

    /**
     * Tag syntax: <RB>Words<RF>note<Rf>
     */
    private static class TextWithEmbeddedFootnote implements Tag    
    {
        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Note note = JAXBUtil.factory().createNote();
            note.setNoteType("x-StudyNote");
            Element current = (Element) stack.peek();

            List list = JAXBUtil.getList(current);
            list.add(note);
            stack.push(note);
        }
    }

    /**
     * Tag syntax: <RF>note<Rf>
     */
    private static class FootnoteStartTag implements Tag
    {
        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Element current = (Element) stack.peek();
            if (!(current instanceof Note))
            {
                Note note = JAXBUtil.factory().createNote();
                note.setNoteType("x-StudyNote");

                List list = JAXBUtil.getList(current);
                list.add(note);
                stack.push(note);
            }
        }
    }

    /**
     * Tag syntax: <RF>note<Rf>
     */
    private static class FootnoteEndTag implements Tag
    {
        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Note note = (Note) stack.pop();
            List list = JAXBUtil.getList(note);

            if (list.size() < 1)
            {
                JAXBUtil.getList((Element) stack.peek()).remove(note);
            }
        }
    }

    /**
     * Tag syntax: <FI>note<Fi>
     */
    private static class ItalicStartTag implements Tag
    {
        public void updateOsisStack(Stack stack) throws JAXBException
        {
            // remarked, for the XSL does not present it correctly
            // The XSL should translate it to <I>...</I> but now it translated
            //  to <div>...</div>

            // Lets try to fix the XSL ...
            Seg seg = JAXBUtil.factory().createSeg();
            seg.setType(SEG_ITALIC);
            Element current = (Element) stack.peek();

            List list = JAXBUtil.getList(current); 
            list.add(seg);
            
            stack.push(seg);
        }
    }

    /**
     * Tag syntax: <FI>note<Fi>
     */
    private static class ItalicEndTag implements Tag
    {
        public void updateOsisStack(Stack stack)
        {
            // remarked, for the XSL does not translate it correctly

            // Lets try to fix the XSL ...
            stack.pop();
        }
    }

    /**
     * Tag syntax: Words<CM>
     */
    private static class ParagraphTag implements Tag
    {
        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Element ele = (Element) stack.peek();
            P p = JAXBUtil.factory().createP();
            JAXBUtil.getList(ele).add(p);
        }
    }

    /**
     * Tag syntax: word<WHxxxx> or word<WGxxxx>
     */
    private static class StrongsWordRefTag implements Tag
    {
        public StrongsWordRefTag(String tagName)
        {
            tag = tagName.trim();
        }

        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Element ele = (Element) stack.peek();
            List list = JAXBUtil.getList(ele);
            if (list.isEmpty())
            {
                log.error("Source has problem for tag <" + tag + ">.");
                return;
            }
            int lastIndex = list.size() - 1;
            Object prevObj = list.get(lastIndex);
            W word = null;

            if (prevObj instanceof String)
            {
                word = JAXBUtil.factory().createW();
                word.getContent().add(prevObj);
                list.set(lastIndex, word);
            }
            else if (prevObj instanceof W)
            {
                word = (W) prevObj;
            }
            else
            {
                log.error("Source has problem for tag <" + tag + ">.");
                return;
            }

            String existingLemma = word.getLemma();
            StringBuffer newLemma = new StringBuffer();

            if (existingLemma != null && existingLemma.length() > 0)
            {
                newLemma.append(existingLemma).append('|');
            }
            newLemma.append("x-Strongs:").append(tag.substring(2));
            word.setLemma(newLemma.toString());
        }

        private String tag;
    }

    /**
     * Tag syntax: word<WTxxxx>
     */
    private static class StrongsMorphRefTag implements Tag
    {
        public StrongsMorphRefTag(String tagName)
        {
            tag = tagName.trim();
        }

        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Element ele = (Element) stack.peek();
            List list = JAXBUtil.getList(ele);
            if (list.isEmpty())
            {
                log.error("Source has problem for tag <" + tag + ">.");
                return;
            }

            int lastIndex = list.size() - 1;
            Object prevObj = list.get(lastIndex);
            W word = null;

            if (prevObj instanceof String)
            {
                word = JAXBUtil.factory().createW();
                word.getContent().add(prevObj);
                list.set(lastIndex, word);
            }
            else if (prevObj instanceof W)
            {
                word = (W) prevObj;
            }
            else
            {
                log.error("Source has problem for tag <" + tag + ">.");
                return;
            }

            String existingMorph = word.getMorph();
            StringBuffer newMorph = new StringBuffer();

            if (existingMorph != null && existingMorph.length() > 0)
            {
                newMorph.append(existingMorph).append('|');
            }
            newMorph.append("x-StrongsMorph:T").append(tag.substring(2));
            word.setMorph(newMorph.toString());
        }

        private String tag;
    }

    /**
     * Represent a trunc of bible text without any tags
     */
    private static class TextTag implements Tag
    {
        public TextTag(String textData)
        {
            text = textData;
        }

        public void updateOsisStack(Stack stack) throws JAXBException
        {
            Element ele = (Element) stack.peek();
            List list = JAXBUtil.getList(ele);
            list.add(text);
        }

        private String text;
    }

    /**
     * Unknown Tag. Either not supported tag or tag not defined in GBF specification
     */
    private static class UnknownTag implements Tag
    {
        public UnknownTag(String tagName)
        {
            tag = tagName;
        }

        public void updateOsisStack(Stack stack)
        {
            // unknown tags
            log.warn("Ignoring tag of " + tag);
        }

        private String tag;
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(GBFFilter.class);
}
