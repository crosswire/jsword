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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.filter.gbf;

import java.util.HashMap;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                builder = BUILDERS.get(name);
            } else {
                builder = BUILDERS.get(name.substring(0, 2));
            }

            if (builder != null) {
                tag = builder.createTag(name);
            }

            if (tag == null) {
                // I'm not confident enough that we handle all the GBF tags
                // that I will blame the book instead of the program
                log.warn("In {}({}) ignoring tag of <{}>", book.getInitials(), key.getName(), name);
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
        public Tag createTag(String name) {
            return new BoldStartTag(name);
        }
    }

    /**
     *
     */
    static final class CrossRefStartTagBuilder implements TagBuilder {
        public Tag createTag(final String name) {
            return new CrossRefStartTag(name);
        }
    }

    /**
     *
     */
    static final class DefaultEndTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new DefaultEndTag(name);
        }
    }

    /**
     *
     */
    static final class EndOfLineTagBuilder implements TagBuilder {
        public Tag createTag(final String name) {
            return new EOLTag(name);
        }

    }

    /**
    *
    */
    static final class EscapeTagBuilder implements TagBuilder {
        public Tag createTag(final String name) {
            if ("CG".equals(name)) {
                return new TextTag("&gt;");
            }

            // else "CT"
            return new TextTag("&lt;");
        }

    }

    /**
     *
     */
    static final class FootnoteStartTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new FootnoteStartTag(name);
        }
    }

    /**
     *
     */
    static final class FootnoteEndTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new FootnoteEndTag(name);
        }
    }

    /**
     *
     */
    static final class HeaderStartTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new HeaderStartTag(name);
        }
    }

    /**
     *
     */
    static final class IgnoredTagBuilder implements TagBuilder {
        public Tag createTag(final String name) {
            return new IgnoredTag(name);
        }
    }

    /**
     *
     */
    static final class ItalicStartTagBuilder implements TagBuilder {
       public Tag createTag(String name) {
            return new ItalicStartTag(name);
        }
    }

    /**
     *
     */
    static final class JustifyRightTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new JustifyRightTag(name);
        }
    }

    /**
     *
     */
    static final class OTQuoteStartTagBuilder implements TagBuilder {
       public Tag createTag(String name) {
            return new OTQuoteStartTag(name);
        }
    }

    /**
     *
     */
    static final class ParagraphTagBuilder implements TagBuilder {
       public Tag createTag(String name) {
            return new ParagraphTag(name);
        }
    }

    /**
     *
     */
    static final class PoetryStartTagBuilder implements TagBuilder {
       public Tag createTag(String name) {
            return new PoetryStartTag(name);
        }

    }

    /**
     *
     */
    static final class PsalmTitleStartTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new PsalmStartTag(name);
        }

    }

    /**
     *
     */
    static final class RedLetterStartTagBuilder implements TagBuilder {
       public Tag createTag(String name) {
            return new RedLetterStartTag(name);
        }
    }

    /**
     *
     */
    static final class StrongsMorphTagBuilder implements TagBuilder {
       public Tag createTag(final String name) {
            return new StrongsMorphTag(name);
        }
    }

    /**
     *
     */
    static final class StrongsWordTagBuilder implements TagBuilder {
        public Tag createTag(final String name) {
            return new StrongsWordTag(name);
        }
    }

    /**
     *
     */
    static final class TextFootnoteTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new TextFootnoteTag(name);
        }
    }

    /**
     *
     */
    static final class TitleStartTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new TitleStartTag(name);
        }
    }

    /**
     *
     */
    static final class UnderlineStartTagBuilder implements TagBuilder {
        public Tag createTag(String name) {
            return new UnderlineStartTag(name);
        }
    }

    /**
     * The <code>BUILDERS</code> maps the 2 letter GBF tag to a class that
     * proxies for the tag.
     */
    private static final Map<String, TagBuilder> BUILDERS = new HashMap<String, TagBuilder>();
    static {
        TagBuilder defaultEndTagBuilder = new DefaultEndTagBuilder();
        TagBuilder ignoreTagBuilder = new IgnoredTagBuilder();

        BUILDERS.put("FB", new BoldStartTagBuilder());
        BUILDERS.put("Fb", defaultEndTagBuilder);

        BUILDERS.put("FI", new ItalicStartTagBuilder());
        BUILDERS.put("Fi", defaultEndTagBuilder);

        BUILDERS.put("FR", new RedLetterStartTagBuilder());
        BUILDERS.put("Fr", defaultEndTagBuilder);

        BUILDERS.put("FU", new UnderlineStartTagBuilder());
        BUILDERS.put("Fu", defaultEndTagBuilder);

        BUILDERS.put("RX", new CrossRefStartTagBuilder());
        BUILDERS.put("Rx", defaultEndTagBuilder);

        BUILDERS.put("CL", new EndOfLineTagBuilder());
        BUILDERS.put("CM", new ParagraphTagBuilder());

        BUILDERS.put("RF", new FootnoteStartTagBuilder());
        BUILDERS.put("Rf", new FootnoteEndTagBuilder());
        BUILDERS.put("RB", new TextFootnoteTagBuilder());

        BUILDERS.put("TS", new HeaderStartTagBuilder());
        BUILDERS.put("Ts", defaultEndTagBuilder);

        BUILDERS.put("TB", new PsalmTitleStartTagBuilder());
        BUILDERS.put("Tb", defaultEndTagBuilder);

        BUILDERS.put("TH", new TitleStartTagBuilder());
        BUILDERS.put("Th", defaultEndTagBuilder);

        BUILDERS.put("BA", ignoreTagBuilder);
        BUILDERS.put("BC", ignoreTagBuilder);
        BUILDERS.put("BI", ignoreTagBuilder);
        BUILDERS.put("BN", ignoreTagBuilder);
        BUILDERS.put("BO", ignoreTagBuilder);
        BUILDERS.put("BP", ignoreTagBuilder);

        BUILDERS.put("JR", new JustifyRightTagBuilder());
        BUILDERS.put("JL", ignoreTagBuilder);

        BUILDERS.put("FO", new OTQuoteStartTagBuilder());
        BUILDERS.put("Fo", defaultEndTagBuilder);

        BUILDERS.put("PP", new PoetryStartTagBuilder());
        BUILDERS.put("Pp", defaultEndTagBuilder);

        TagBuilder builder = new StrongsWordTagBuilder();
        BUILDERS.put("WH", builder);
        BUILDERS.put("WG", builder);
        BUILDERS.put("WT", new StrongsMorphTagBuilder());

        BUILDERS.put("CG", new EscapeTagBuilder());
        BUILDERS.put("CT", new EscapeTagBuilder());
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(GBFTagBuilders.class);
}
