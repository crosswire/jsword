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
package org.crosswire.jsword.book.filter.gbf;

import java.util.LinkedList;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.OSISUtil.OSISFactory;
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
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class GBFTags {
    /**
     * Prevent instantiation.
     */
    private GBFTags() {
    }

    /**
     *
     */
    public static final class DefaultEndTag extends AbstractTag {
        /**
         * @param name
         */
        public DefaultEndTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            if (stack.isEmpty()) {
                DataPolice.report("Ignoring end tag without corresponding start tag: " + getName()); //$NON-NLS-1$
                return;
            }
            stack.removeFirst();
        }
    }

    /**
     *
     */
    public static final class BoldStartTag extends AbstractTag {
        /**
         * @param name
         */
        public BoldStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createHI();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_BOLD);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class CrossRefStartTag extends AbstractTag {
        public CrossRefStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createReference();

            String refstr = getName().substring(2);
            try {
                Passage ref = (Passage) KEY_FACTORY.getKey(refstr);
                ele.setAttribute(OSISUtil.OSIS_ATTR_REF, ref.getOsisRef());
            } catch (NoSuchKeyException ex) {
                DataPolice.report("unable to parse reference: " + refstr); //$NON-NLS-1$
            }
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class EOLTag extends AbstractTag {
        /**
         * @param name
         */
        public EOLTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {

            Element p = OSIS_FACTORY.createLB();
            if (stack.isEmpty()) {
                stack.addFirst(p);
            } else {
                Content top = (Content) stack.get(0);
                if (top instanceof Element) {
                    Element current = (Element) top;
                    current.addContent(p);
                }
            }
        }
    }

    /**
     *
     */
    public static final class FootnoteStartTag extends AbstractTag {
        /**
         * @param name
         */
        public FootnoteStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createNote();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.NOTETYPE_STUDY);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class FootnoteEndTag extends AbstractTag {
        /**
         * @param name
         */
        public FootnoteEndTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            if (stack.isEmpty()) {
                DataPolice.report("Ignoring end tag without corresponding start tag: " + getName()); //$NON-NLS-1$
                return;
            }

            Object pop = stack.removeFirst();
            if (!(pop instanceof Element)) {
                DataPolice.report("expected to pop a Note, but found " + ClassUtil.getShortClassName(pop.getClass())); //$NON-NLS-1$
                return;
            }

            Element note = (Element) pop;
            if (note.getContentSize() < 1) {
                Content top = (Content) stack.get(0);
                if (top instanceof Element) {
                    Element ele = (Element) top;
                    ele.removeContent(note);
                }
            }
        }
    }

    /**
     *
     */
    public static final class HeaderStartTag extends AbstractTag {
        /**
         * @param name
         */
        public HeaderStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            GBFTags.updateOsisStack(stack, OSIS_FACTORY.createTitle());
        }
    }

    /**
     *
     */
    public static final class IgnoredTag extends AbstractTag {
        /**
         * @param name
         */
        public IgnoredTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
        }
    }

    /**
     *
     */
    public static final class ItalicStartTag extends AbstractTag {
        /**
         * @param name
         */
        public ItalicStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createHI();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_ITALIC);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class JustifyRightTag extends AbstractTag {
        /**
         * @param name
         */
        public JustifyRightTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            // LATER(joe): is seg the right thing?
            Element ele = OSIS_FACTORY.createSeg();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.SEG_JUSTIFYRIGHT);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class JustifyLeftTag extends AbstractTag {
        /**
         * @param name
         */
        public JustifyLeftTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createSeg();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.SEG_JUSTIFYLEFT);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class OTQuoteStartTag extends AbstractTag {
        /**
         * @param name
         */
        public OTQuoteStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            GBFTags.updateOsisStack(stack, OSIS_FACTORY.createQ());
        }
    }

    /**
     *
     */
    public static final class ParagraphTag extends AbstractTag {
        /**
         * @param name
         */
        public ParagraphTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {

            if (stack.isEmpty()) {
                Element p = OSIS_FACTORY.createLB();
                stack.addFirst(p);
            } else {
                Element p = OSIS_FACTORY.createP();
                Content top = (Content) stack.get(0);
                if (top instanceof Element) {
                    Element current = (Element) top;
                    current.addContent(p);
                }
            }
        }
    }

    /**
     *
     */
    public static final class PoetryStartTag extends AbstractTag {
        /**
         * @param name
         */
        public PoetryStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            GBFTags.updateOsisStack(stack, OSIS_FACTORY.createLG());
        }
    }

    /**
     *
     */
    public static final class PsalmStartTag extends AbstractTag {
        /**
         * @param name
         */
        public PsalmStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            GBFTags.updateOsisStack(stack, OSIS_FACTORY.createTitle());
        }
    }

    /**
     *
     */
    public static final class RedLetterStartTag extends AbstractTag {
        /**
         * @param name
         */
        public RedLetterStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createQ();
            ele.setAttribute(OSISUtil.ATTRIBUTE_Q_WHO, Msg.NAME_JESUS.toString());
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class StrongsMorphTag extends AbstractTag {
        public StrongsMorphTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            String name = getName().trim();

            Content top = (Content) stack.get(0);
            if (top instanceof Element) {
                Element ele = (Element) top;
                int size = ele.getContentSize();
                if (size == 0) {
                    DataPolice.report("No content to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                int lastIndex = size - 1;
                Content prevObj = ele.getContent(lastIndex);
                Element word = null;

                if (prevObj instanceof Text) {
                    word = OSIS_FACTORY.createW();
                    ele.removeContent(prevObj);
                    word.addContent(prevObj);
                    ele.addContent(word);
                } else if (prevObj instanceof Element) {
                    word = (Element) prevObj;
                } else {
                    DataPolice.report("No words to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String existingMorph = word.getAttributeValue(OSISUtil.ATTRIBUTE_W_MORPH);
                StringBuffer newMorph = new StringBuffer();

                if (existingMorph != null && existingMorph.length() > 0) {
                    newMorph.append(existingMorph).append('|');
                }
                newMorph.append(OSISUtil.MORPH_STRONGS).append(name.substring(2));
                word.setAttribute(OSISUtil.ATTRIBUTE_W_MORPH, newMorph.toString());
            }
        }
    }

    /**
     *
     */
    public static final class StrongsWordTag extends AbstractTag {
        /**
         * @param name
         */
        public StrongsWordTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            String name = getName().trim();

            Content top = (Content) stack.get(0);
            if (top instanceof Element) {
                Element ele = (Element) top;
                int size = ele.getContentSize();
                if (size == 0) {
                    DataPolice.report("No content to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                int lastIndex = size - 1;
                Content prevObj = ele.getContent(lastIndex);
                Element word = null;

                if (prevObj instanceof Text) {
                    Text textItem = (Text) prevObj;
                    word = OSIS_FACTORY.createW();
                    ele.removeContent(textItem);
                    word.addContent(textItem);
                    ele.addContent(word);
                } else if (prevObj instanceof Element) {
                    word = (Element) prevObj;
                } else {
                    DataPolice.report("No words to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String existingLemma = word.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);
                StringBuffer newLemma = new StringBuffer();

                if (existingLemma != null && existingLemma.length() > 0) {
                    newLemma.append(existingLemma).append('|');
                }

                newLemma.append(OSISUtil.LEMMA_STRONGS).append(name.substring(2));
                word.setAttribute(OSISUtil.ATTRIBUTE_W_LEMMA, newLemma.toString());
            }
        }
    }

    /**
     *
     */
    public static final class TextFootnoteTag extends AbstractTag {
        /**
         * @param name
         */
        public TextFootnoteTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createNote();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.NOTETYPE_STUDY);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class TextTag extends AbstractTag {
        /**
         * @param name
         */
        public TextTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            // Make sure that characters that XML requires to be escaped are.
            String text = XMLUtil.escape(getName());
            if (stack.isEmpty()) {
                stack.addFirst(new Text(text));
            } else {
                Content top = (Content) stack.get(0);
                if (top instanceof Element) {
                    Element ele = (Element) top;
                    ele.addContent(text);
                }
            }
        }
    }

    /**
     *
     */
    public static final class TitleStartTag extends AbstractTag {
        /**
         * @param name
         */
        public TitleStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            GBFTags.updateOsisStack(stack, OSIS_FACTORY.createTitle());
        }
    }

    /**
     *
     */
    public static final class UnderlineStartTag extends AbstractTag {
        /**
         * @param name
         */
        public UnderlineStartTag(String name) {
            super(name);
        }

        public void updateOsisStack(LinkedList stack) {
            Element ele = OSIS_FACTORY.createHI();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_UNDERLINE);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /* private */static void updateOsisStack(LinkedList stack, Content content) {
        Content top = (Content) stack.get(0);
        if (top instanceof Element) {
            Element current = (Element) top;
            current.addContent(content);
            stack.addFirst(content);
        }
    }

    /**
     * To convert strings into Biblical keys.
     */
    static final KeyFactory KEY_FACTORY = PassageKeyFactory.instance();

    /**
     * To create OSIS DOM nodes.
     */
    static final OSISFactory OSIS_FACTORY = OSISUtil.factory();
}
