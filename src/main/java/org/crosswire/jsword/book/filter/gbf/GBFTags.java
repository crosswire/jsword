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
package org.crosswire.jsword.book.filter.gbf;

import java.util.LinkedList;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.OSISUtil.OSISFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;

/**
 * A holder of all of the GBF Tag Handler classes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            if (stack.isEmpty()) {
                DataPolice.report(book, key, "Ignoring end tag without corresponding start tag: " + getName());
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            Element ele = OSIS_FACTORY.createHI();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_BOLD);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /**
     *
     */
    public static final class BookTitleStartTag extends AbstractTag {
       /**
        * @param name
        */
       public BookTitleStartTag(String name) {
           super(name);
       }

       public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
           Element ele = OSIS_FACTORY.createTitle();
           ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, "main");
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            Element ele = OSIS_FACTORY.createReference();

            String refstr = getName().substring(2);
            try {
                Passage ref = (Passage) book.getKey(refstr);
                ele.setAttribute(OSISUtil.OSIS_ATTR_REF, ref.getOsisRef());
            } catch (NoSuchKeyException ex) {
                DataPolice.report(book, key, "unable to parse reference: " + refstr);
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {

            Element p = OSIS_FACTORY.createLB();
            if (stack.isEmpty()) {
                stack.addFirst(p);
            } else {
                Content top = stack.get(0);
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            if (stack.isEmpty()) {
                DataPolice.report(book, key, "Ignoring end tag without corresponding start tag: " + getName());
                return;
            }

            Object pop = stack.removeFirst();
            if (!(pop instanceof Element)) {
                DataPolice.report(book, key, "expected to pop a Note, but found " + ClassUtil.getShortClassName(pop.getClass()));
                return;
            }

            Element note = (Element) pop;
            if (note.getContentSize() < 1) {
                Content top = stack.get(0);
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {

            if (stack.isEmpty()) {
                Element p = OSIS_FACTORY.createLB();
                stack.addFirst(p);
            } else {
                Element p = OSIS_FACTORY.createP();
                Content top = stack.get(0);
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            // In OSIS Psalm titles are canonical
            Element title = OSIS_FACTORY.createTitle();
            title.setAttribute(OSISUtil.OSIS_ATTR_TYPE, "psalm");
            title.setAttribute(OSISUtil.OSIS_ATTR_SUBTYPE, "x-preverse");
            title.setAttribute(OSISUtil.OSIS_ATTR_CANONICAL, "true");
            GBFTags.updateOsisStack(stack, title);
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            Element ele = OSIS_FACTORY.createQ();
            ele.setAttribute(OSISUtil.ATTRIBUTE_Q_WHO, "Jesus");
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            String name = getName().trim();

            Content top = stack.get(0);
            if (top instanceof Element) {
                Element ele = (Element) top;
                int size = ele.getContentSize();
                if (size == 0) {
                    DataPolice.report(book, key, "No content to attach Strong's Morph tag to: <" + name + ">.");
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
                    DataPolice.report(book, key, "No words to attach Strong's Morph tag to: <" + name + ">.");
                    return;
                }

                String existingMorph = word.getAttributeValue(OSISUtil.ATTRIBUTE_W_MORPH);
                StringBuilder newMorph = new StringBuilder();

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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            boolean empty = false;
            String name = getName().trim();
            Element word = null;

            Content top = stack.get(0);
            if (top instanceof Element) {
                Element ele = (Element) top;
                int size = ele.getContentSize();
                if (size > 0) {
                    int lastIndex = size - 1;
                    Content prevObj = ele.getContent(lastIndex);

                    if (prevObj instanceof Text) {
                        Text textItem = (Text) prevObj;
                        word = OSIS_FACTORY.createW();
                        ele.removeContent(textItem);
                        word.addContent(textItem);
                        ele.addContent(word);
                    } else if (prevObj instanceof Element) {
                        word = (Element) prevObj;
                    }
                }
            }

            if (word == null) {
                word = OSIS_FACTORY.createW();
                empty = true;
            }

            String existingLemma = word.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);
            StringBuilder newLemma = new StringBuilder();

            // Strong's numbers are separated by spaces w/in the attribute
            if (existingLemma != null && existingLemma.length() > 0) {
                newLemma.append(existingLemma).append(' ');
            }

            // Grab the G or H and the number that follows
            newLemma.append(OSISUtil.LEMMA_STRONGS).append(name.substring(1));
            word.setAttribute(OSISUtil.ATTRIBUTE_W_LEMMA, newLemma.toString());

            if (empty) {
                // The last element of the stack is the wrapping div.
                // Empty elements are merely appended to the parent container
                top = stack.getLast();
                if (top instanceof Element) {
                    Element ele = (Element) top;
                    ele.addContent(word);
                }
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            // Make sure that characters that XML requires to be escaped are.
            String text = XMLUtil.escape(getName());
            if (stack.isEmpty()) {
                stack.addFirst(new Text(text));
            } else {
                Content top = stack.get(0);
                if (top instanceof Element) {
                    Element ele = (Element) top;
                    // Don't make this text the child of a preceding <w>
                    if (OSISUtil.OSIS_ELEMENT_W.equals(ele.getName())) {
                        // The last element of the stack is the wrapping div.
                        top = stack.getLast();
                        if (top instanceof Element) {
                            ele = (Element) top;
                            ele.addContent(new Text(text));
                        }
                    } else {
                        ele.addContent(text);
                    }
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
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

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack) {
            Element ele = OSIS_FACTORY.createHI();
            ele.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_UNDERLINE);
            GBFTags.updateOsisStack(stack, ele);
        }
    }

    /* private */static void updateOsisStack(LinkedList<Content> stack, Content content) {
        Content top = stack.get(0);
        if (top instanceof Element) {
            Element current = (Element) top;
            current.addContent(content);
            stack.addFirst(content);
        }
    }

    /**
     * To create OSIS DOM nodes.
     */
    static final OSISFactory OSIS_FACTORY = OSISUtil.factory();
}
