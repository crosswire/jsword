/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.jsword.book.filter.gbf;

import java.util.LinkedList;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.OSISUtil.ObjectFactory;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

/**
 * A holder of all of the GBF Tag Handler classes.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class GBFTags
{
    /**
     *
     */
    public static final class DefaultEndTag extends AbstractTag
    {
        /**
         * @param name
         */
        public DefaultEndTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            stack.removeFirst();
        }
    }

    /**
     *
     */
    public static final class BoldStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public BoldStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element hi = OSIS_FACTORY.createHI();
            hi.setAttribute(OSISUtil.ATTRIBUTE_HI_TYPE, OSISUtil.HI_BOLD);

            Element current = (Element) stack.get(0);
            current.addContent(hi);
            stack.addFirst(hi);
        }
    }

    /**
     *
     */
    public static final class CrossRefStartTag extends AbstractTag
    {
        public CrossRefStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element seg = OSIS_FACTORY.createReference();

            String refstr = getName().substring(2);
            try
            {
                Passage ref = (Passage) KEY_FACTORY.getKey(refstr);
                seg.setAttribute(OSISUtil.ATTRIBUTE_REFERENCE_OSISREF, ref.getOSISName());
            }
            catch (NoSuchKeyException ex)
            {
                DataPolice.report("unable to parse reference: " + refstr); //$NON-NLS-1$
            }

            Element current = (Element) stack.get(0);
            current.addContent(seg);
            stack.addFirst(seg);
        }
    }

    /**
     *
     */
    public static final class EOLTag extends AbstractTag
    {
        /**
         * @param name
         */
        public EOLTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {

            if (stack.size() == 0)
            {
                Element p = OSIS_FACTORY.createLB();
                stack.addFirst(p);
                // log.warn("failing to add to element on empty stack");
            }
            else
            {
                Element p = OSIS_FACTORY.createP();
                Element ele = (Element) stack.get(0);
                ele.addContent(p);
            }
        }
    }

    /**
     *
     */
    public static final class FootnoteStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public FootnoteStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element current = (Element) stack.get(0);
            Element note = OSIS_FACTORY.createNote();
            note.setAttribute(OSISUtil.ATTRIBUTE_NOTE_TYPE, OSISUtil.NOTETYPE_STUDY);

            current.addContent(note);
            stack.addFirst(note);
        }
    }

    /**
     *
     */
    public static final class FootnoteEndTag extends AbstractTag
    {
        /**
         * @param name
         */
        public FootnoteEndTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Object pop = stack.removeFirst();
            if (pop instanceof Element)
            {
                Element note = (Element) pop;

                if (note.getContentSize() < 1)
                {
                    Element ele = (Element) stack.get(0);
                    ele.removeContent(note);
                }
            }
            else
            {
                DataPolice.report("expected to pop a Note, but found " + ClassUtil.getShortClassName(pop.getClass())); //$NON-NLS-1$
            }
        }
    }

    /**
     *
     */
    public static final class HeaderStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public HeaderStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element title = OSIS_FACTORY.createTitle();

            Element current = (Element) stack.get(0);
            current.addContent(title);
            stack.addFirst(title);
        }
    }

    /**
     *
     */
    public static final class IgnoredTag extends AbstractTag
    {
        /**
         * @param name
         */
        public IgnoredTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
        }
    }

    /**
     *
     */
    public static final class ItalicStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public ItalicStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element hi = OSIS_FACTORY.createHI();
            hi.setAttribute(OSISUtil.ATTRIBUTE_HI_TYPE, OSISUtil.HI_ITALIC);

            Element current = (Element) stack.get(0);
            current.addContent(hi);
            stack.addFirst(hi);
        }
    }

    /**
     *
     */
    public static final class JustifyRightTag extends AbstractTag
    {
        /**
         * @param name
         */
        public JustifyRightTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            // LATER(joe): is div the right thing?
            Element seg = OSIS_FACTORY.createSeg();
            seg.setAttribute(OSISUtil.ATTRIBUTE_SEG_TYPE, OSISUtil.SEG_JUSTIFYRIGHT);

            Element current = (Element) stack.get(0);
            current.addContent(seg);
            stack.addFirst(seg);
        }
    }

    /**
     *
     */
    public static final class JustifyLeftTag extends AbstractTag
    {
        /**
         * @param name
         */
        public JustifyLeftTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
        }
    }

    /**
     *
     */
    public static final class OTQuoteStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public OTQuoteStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element q = OSIS_FACTORY.createQ();

            Element current = (Element) stack.get(0);
            current.addContent(q);
            stack.addFirst(q);
        }
    }

    /**
     *
     */
    public static final class ParagraphTag extends AbstractTag
    {
        /**
         * @param name
         */
        public ParagraphTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {

            if (stack.size() == 0)
            {
                Element p = OSIS_FACTORY.createLB();
                stack.addFirst(p);
            }
            else
            {
                Element p = OSIS_FACTORY.createP();
                Element ele = (Element) stack.get(0);
                ele.addContent(p);
            }
        }
    }

    /**
     *
     */
    public static final class PoetryStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public PoetryStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            // LATER(joe): is speech the right thing?
            Element speech = OSIS_FACTORY.createLG();

            Element current = (Element) stack.get(0);
            current.addContent(speech);
            stack.addFirst(speech);
        }
    }

    /**
     *
     */
    public static final class PsalmStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public PsalmStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element title = OSIS_FACTORY.createTitle();

            Element current = (Element) stack.get(0);
            current.addContent(title);
            stack.addFirst(title);
        }
    }

    /**
     *
     */
    public static final class RedLetterStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public RedLetterStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element speaker = OSIS_FACTORY.createSpeaker();
            speaker.setAttribute(OSISUtil.ATTRIBUTE_SPEAKER_WHO, Msg.NAME_JESUS.toString());

            Element current = (Element) stack.get(0);
            current.addContent(speaker);
            stack.addFirst(speaker);
        }
    }

    /**
     *
     */
    public static final class StrongsMorphTag extends AbstractTag
    {
        public StrongsMorphTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            String name = getName().trim();

            Element ele = (Element) stack.get(0);
            int size = ele.getContentSize();
            if (size == 0)
            {
                DataPolice.report("No content to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            int lastIndex = size - 1;
            Content prevObj = ele.getContent(lastIndex);
            Element word = null;

            if (prevObj instanceof Text)
            {
                word = OSIS_FACTORY.createW();
                ele.removeContent(prevObj);
                word.addContent(prevObj);
                ele.addContent(word);
            }
            else if (prevObj instanceof Element)
            {
                word = (Element) prevObj;
            }
            else
            {
                DataPolice.report("No words to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            String existingMorph = word.getAttributeValue(OSISUtil.ATTRIBUTE_W_MORPH);
            StringBuffer newMorph = new StringBuffer();

            if (existingMorph != null && existingMorph.length() > 0)
            {
                newMorph.append(existingMorph).append('|');
            }
            newMorph.append(OSISUtil.MORPH_STRONGS).append(name.substring(2));
            word.setAttribute(OSISUtil.ATTRIBUTE_W_MORPH, newMorph.toString());
        }
    }

    /**
     *
     */
    public static final class StrongsWordTag extends AbstractTag
    {
        /**
         * @param name
         */
        public StrongsWordTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            String name = getName().trim();

            Element ele = (Element) stack.get(0);
            int size = ele.getContentSize();
            if (size == 0)
            {
                DataPolice.report("No content to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            int lastIndex = size - 1;
            Content prevObj = ele.getContent(lastIndex);
            Element word = null;

            if (prevObj instanceof Text)
            {
                Text textItem = (Text) prevObj;
                word = OSIS_FACTORY.createW();
                ele.removeContent(textItem);
                word.addContent(textItem);
                ele.addContent(word);
            }
            else if (prevObj instanceof Element)
            {
                word = (Element) prevObj;
            }
            else
            {
                DataPolice.report("No words to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            String existingLemma = word.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);
            StringBuffer newLemma = new StringBuffer();

            if (existingLemma != null && existingLemma.length() > 0)
            {
                newLemma.append(existingLemma).append('|');
            }

            newLemma.append(OSISUtil.LEMMA_STRONGS).append(name.substring(2)); //$NON-NLS-1$
            word.setAttribute(OSISUtil.ATTRIBUTE_W_LEMMA, newLemma.toString());
        }
    }

    /**
     *
     */
    public static final class TextFootnoteTag extends AbstractTag
    {
        /**
         * @param name
         */
        public TextFootnoteTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element note = OSIS_FACTORY.createNote();
            note.setAttribute(OSISUtil.ATTRIBUTE_NOTE_TYPE, OSISUtil.NOTETYPE_STUDY);

            Element current = (Element) stack.get(0);
            current.addContent(note);
            stack.addFirst(note);
        }
    }

    /**
     *
     */
    public static final class TextTag extends AbstractTag
    {
        /**
         * @param name
         */
        public TextTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            if (stack.size() == 0)
            {
                stack.addFirst(getName());
            }
            else
            {
                Element ele = (Element) stack.get(0);
                ele.addContent(getName());
            }
        }
    }

    /**
     *
     */
    public static final class TitleStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public TitleStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element title = OSIS_FACTORY.createTitle();

            Element current = (Element) stack.get(0);
            current.addContent(title);
            stack.addFirst(title);
        }
    }

    /**
     *
     */
    public static final class UnderlineStartTag extends AbstractTag
    {
        /**
         * @param name
         */
        public UnderlineStartTag(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.filter.gbf.Tag#updateOsisStack(java.util.LinkedList)
         */
        public void updateOsisStack(LinkedList stack)
        {
            Element hi = OSIS_FACTORY.createHI();
            hi.setAttribute(OSISUtil.ATTRIBUTE_HI_TYPE, OSISUtil.HI_UNDERLINE);

            Element current = (Element) stack.get(0);
            current.addContent(hi);
            stack.addFirst(hi);
        }
    }

    /**
     * To convert strings into Biblical keys.
     */
    protected static final KeyFactory KEY_FACTORY = PassageKeyFactory.instance();

    /**
     * To create OSIS DOM nodes.
     */
    protected static final ObjectFactory OSIS_FACTORY = OSISUtil.factory();
}
