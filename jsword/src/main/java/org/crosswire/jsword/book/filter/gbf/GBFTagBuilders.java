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

import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.filter.gbf.GBFTags.BoldStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.CrossRefStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.DefaultEndTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.EOLTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.FootnoteEndTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.FootnoteStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.HeaderStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.IgnoredTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.ItalicStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.JustifyRightTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.OTQuoteStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.ParagraphTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.PoetryStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.PsalmStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.RedLetterStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.StrongsMorphTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.StrongsWordTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.TextFootnoteTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.TextTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.TitleStartTag;
import org.crosswire.jsword.book.filter.gbf.GBFTags.UnderlineStartTag;
import org.crosswire.jsword.passage.Key;

/**
 * This class is a convenience to get GBF Tags.
 * 
 * The best place to go for more information about the GBF spec that I have
 * found is: <a
 * href="http://ebible.org/bible/gbf.htm">http://ebible.org/bible/gbf.htm</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class GBFTagBuilders {
    /**
     *
     */
    private GBFTagBuilders() {
    }

    /**
     * @param name
     * @return return a GBF Tag for the given tag name
     */
    public static Tag getTag(Book book, Key key, String name) {
        Tag tag = null;
        int length = name.length();
        if (length > 0) {
            // Only the first two letters of the tag are indicative of the tag
            // The rest, if present, is data.
            TagBuilder builder = null;
            if (length == 2) {
                builder = (TagBuilder) BUILDERS.get(name);
            } else {
                builder = (TagBuilder) BUILDERS.get(name.substring(0, 2));
            }

            if (builder != null) {
                tag = builder.createTag(name);
            }

            if (tag == null) {
                // I'm not confident enough that we handle all the GBF tags
                // that I will blame the book instead of the program
                log.warn("In " + book.getInitials() + "(" + key.getName() + ") ignoring tag of <" + name + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                // DataPolice.report("Ignoring tag of <" + name + ">");
            }
        }
        return tag;
    }

    /**
     * @param text
     * @return get a Text Tag object containing the text
     */
    public static Tag getTextTag(String text) {
        return new TextTag(text);
    }

    /**
     *
     */
    static final class BoldStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new BoldStartTag(name);
        }
    }

    /**
     *
     */
    static final class CrossRefStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(final String name) {
            return new CrossRefStartTag(name);
        }
    }

    /**
     *
     */
    static final class DefaultEndTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new DefaultEndTag(name);
        }
    }

    /**
     *
     */
    static final class EndOfLineTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(final String name) {
            return new EOLTag(name);
        }

    }

    /**
    *
    */
    static final class EscapeTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(final String name) {
            if ("CG".equals(name)) //$NON-NLS-1$
            {
                return new TextTag("&gt;"); //$NON-NLS-1$
            }

            // else "CT"
            return new TextTag("&lt;"); //$NON-NLS-1$
        }

    }

    /**
     *
     */
    static final class FootnoteStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new FootnoteStartTag(name);
        }
    }

    /**
     *
     */
    static final class FootnoteEndTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new FootnoteEndTag(name);
        }
    }

    /**
     *
     */
    static final class HeaderStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new HeaderStartTag(name);
        }
    }

    /**
     *
     */
    static final class IgnoredTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(final String name) {
            return new IgnoredTag(name);
        }
    }

    /**
     *
     */
    static final class ItalicStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new ItalicStartTag(name);
        }
    }

    /**
     *
     */
    static final class JustifyRightTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new JustifyRightTag(name);
        }
    }

    /**
     *
     */
    static final class OTQuoteStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new OTQuoteStartTag(name);
        }
    }

    /**
     *
     */
    static final class ParagraphTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new ParagraphTag(name);
        }
    }

    /**
     *
     */
    static final class PoetryStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new PoetryStartTag(name);
        }

    }

    /**
     *
     */
    static final class PsalmTitleStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new PsalmStartTag(name);
        }

    }

    /**
     *
     */
    static final class RedLetterStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new RedLetterStartTag(name);
        }
    }

    /**
     *
     */
    static final class StrongsMorphTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(final String name) {
            return new StrongsMorphTag(name);
        }
    }

    /**
     *
     */
    static final class StrongsWordTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(final String name) {
            return new StrongsWordTag(name);
        }
    }

    /**
     *
     */
    static final class TextFootnoteTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new TextFootnoteTag(name);
        }
    }

    /**
     *
     */
    static final class TitleStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new TitleStartTag(name);
        }
    }

    /**
     *
     */
    static final class UnderlineStartTagBuilder implements TagBuilder {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang
         * .String)
         */
        public Tag createTag(String name) {
            return new UnderlineStartTag(name);
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(GBFTagBuilders.class);

    /**
     * The <code>BUILDERS</code> maps the 2 letter GBF tag to a class that
     * proxies for the tag.
     */
    private static final Map BUILDERS = new HashMap();
    static {
        TagBuilder defaultEndTagBuilder = new DefaultEndTagBuilder();
        TagBuilder ignoreTagBuilder = new IgnoredTagBuilder();

        BUILDERS.put("FB", new BoldStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Fb", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("FI", new ItalicStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Fi", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("FR", new RedLetterStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Fr", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("FU", new UnderlineStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Fu", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("RX", new CrossRefStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Rx", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("CL", new EndOfLineTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("CM", new ParagraphTagBuilder()); //$NON-NLS-1$

        BUILDERS.put("RF", new FootnoteStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Rf", new FootnoteEndTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("RB", new TextFootnoteTagBuilder()); //$NON-NLS-1$

        BUILDERS.put("TS", new HeaderStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Ts", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("TB", new PsalmTitleStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Tb", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("TH", new TitleStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Th", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("BA", ignoreTagBuilder); //$NON-NLS-1$
        BUILDERS.put("BC", ignoreTagBuilder); //$NON-NLS-1$
        BUILDERS.put("BI", ignoreTagBuilder); //$NON-NLS-1$
        BUILDERS.put("BN", ignoreTagBuilder); //$NON-NLS-1$
        BUILDERS.put("BO", ignoreTagBuilder); //$NON-NLS-1$
        BUILDERS.put("BP", ignoreTagBuilder); //$NON-NLS-1$

        BUILDERS.put("JR", new JustifyRightTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("JL", ignoreTagBuilder); //$NON-NLS-1$

        BUILDERS.put("FO", new OTQuoteStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Fo", defaultEndTagBuilder); //$NON-NLS-1$

        BUILDERS.put("PP", new PoetryStartTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("Pp", defaultEndTagBuilder); //$NON-NLS-1$

        TagBuilder builder = new StrongsWordTagBuilder();
        BUILDERS.put("WH", builder); //$NON-NLS-1$
        BUILDERS.put("WG", builder); //$NON-NLS-1$
        BUILDERS.put("WT", new StrongsMorphTagBuilder()); //$NON-NLS-1$

        BUILDERS.put("CG", new EscapeTagBuilder()); //$NON-NLS-1$
        BUILDERS.put("CT", new EscapeTagBuilder()); //$NON-NLS-1$
    }
}
