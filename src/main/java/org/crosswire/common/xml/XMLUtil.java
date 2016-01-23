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
package org.crosswire.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Utilities for working with SAX XML parsing.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public final class XMLUtil {
    /**
     * Prevent instantiation
     */
    private XMLUtil() {
    }

    /**
     * Get and load an XML file from the classpath and a few other places into a
     * JDOM Document object.
     * 
     * @param subject
     *            The name of the desired resource (without any extension)
     * @return The requested resource
     * @throws IOException
     *             if there is a problem reading the file
     * @throws JDOMException
     *             If the resource is not valid XML
     */
    public static Document getDocument(String subject) throws JDOMException, IOException {
        String resource = subject + FileUtil.EXTENSION_XML;
        InputStream in = ResourceUtil.getResourceAsStream(resource);

        log.debug("Loading {}.xml from classpath: [OK]", subject);
        // With JDom 1.x this passed true
        SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
        return builder.build(in);
    }

    /**
     * Serialize a SAXEventProvider into an XML String
     * 
     * @param provider
     *            The source of SAX events
     * @return a serialized string
     * @throws SAXException 
     */
    public static String writeToString(SAXEventProvider provider) throws SAXException {
        ContentHandler ser = new PrettySerializingContentHandler();
        provider.provideSAXEvents(ser);
        return ser.toString();
    }

    /**
     * Get the full name of the attribute, including the namespace if any.
     * 
     * @param attrs
     *            the collection of attributes
     * @param index
     *            the index of the desired attribute
     * @return the requested attribute
     */
    public static String getAttributeName(Attributes attrs, int index) {
        String qName = attrs.getQName(index);
        if (qName != null) {
            return qName;
        }
        return attrs.getLocalName(index);
    }

    /**
     * Show the attributes of an element as debug
     * @param attrs 
     */
    public static void debugSAXAttributes(Attributes attrs) {
        for (int i = 0; i < attrs.getLength(); i++) {
            log.debug("attr[{}]: {}={}", Integer.toString(i), attrs.getQName(i), attrs.getValue(i));
        }
    }

    /**
     * Normalizes the given string
     * @param s 
     * @return the escaped string
     */
    public static String escape(String s) {
        if (s == null) {
            return s;
        }
        int len = s.length();
        StringBuilder str = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
            case '<':
                str.append("&lt;");
                break;

            case '>':
                str.append("&gt;");
                break;

            case '&':
                str.append("&amp;");
                break;

            case '"':
                str.append("&quot;");
                break;

            default:
                str.append(ch);
            }
        }

        return str.toString();
    }

    /**
     * For each entity in the input that is not allowed in XML, replace the
     * entity with its unicode equivalent or remove it. For each instance of a
     * bare &, replace it with &amp;<br>
     * XML only allows 4 entities: &amp;amp;, &amp;quot;, &amp;lt; and &amp;gt;.
     * 
     * @param broken
     *            the string to handle entities
     * @return the string with entities appropriately fixed up
     */
    public static String cleanAllEntities(String broken) {
        if (broken == null) {
            return null;
        }

        String working = broken;
        int cleanfrom = 0;

        while (true) {
            int amp = working.indexOf('&', cleanfrom);

            // If there are no more amps then we are done
            if (amp == -1) {
                break;
            }

            // Skip references of the kind &#ddd;
            if (validCharacterEntityPattern.matcher(working.substring(amp)).find()) {
                cleanfrom = working.indexOf(';', amp) + 1;
                continue;
            }

            int i = amp + 1;
            while (true) {
                // if we are at the end of the string then just escape the '&';
                if (i >= working.length()) {
                    // String entity = working.substring(amp);
                    // String replace = guessEntity(entity);
                    // DataPolice.report("replacing unterminated entity: '" +
                    // entity + "' with: '" + replace + "'");

                    return working.substring(0, amp) + "&amp;" + working.substring(amp + 1);
                }

                // if we have come to a ; then we have an entity
                // If it is something that xml can't handle then replace it.
                char c = working.charAt(i);
                if (c == ';') {
                    String entity = working.substring(amp, i + 1);
                    String replace = handleEntity(entity);
                    // log.warn("replacing entity: '{}' with: '{}'", entity, replace);

                    working = working.substring(0, amp) + replace + working.substring(i + 1);
                    break;
                }

                // Did we end an entity without finding a closing ;
                // Then treat it as an '&' that needs to be replaced with &amp;
                if (!Character.isLetterOrDigit(c)) {
                    // String entity = working.substring(amp, i);
                    // String replace = "&amp;" + working.substring(amp + 1, i);
                    // log.warn("replacing invalid entity: '{}' with: '{}': {}", entity, replace, broken);

                    working = working.substring(0, amp) + "&amp;" + working.substring(amp + 1);
                    amp = i + 4; // account for the 4 extra characters
                    break;
                }

                i++;
            }

            cleanfrom = amp + 1;
        }

        return working;
    }

    /**
     * Remove all invalid characters in the input, replacing them with a space. XML has stringent
     * requirements as to which characters are or are not allowed. The set of
     * allowable characters are:<br>
     * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]<br>
     * Note: Java handles to \uFFFF
     * 
     * @param broken
     *            the string to be cleaned
     * @return the cleaned string
     */
    public static String cleanAllCharacters(String broken) {
        return invalidCharacterPattern.matcher(broken).replaceAll(" ");
    }

    /**
     * Strip all closing tags from the end of the XML fragment, and then
     * re-close all tags that are open at the end of the string.
     * 
     * @param broken
     *            the string to be cleaned.
     * @return cleaned string, or {@code null} if the string could not be
     *         cleaned due to more broken XML
     */
    public static String recloseTags(String broken) {
        String result = broken;
        // remove closing tags from the end
        while (result.matches(".*</[a-zA-Z]+>[ \t\r\n]*")) {
            result = result.substring(0, result.lastIndexOf('<'));
        }
        // close tags again
        List<String> openTags = new ArrayList<String>();
        Matcher m = Pattern.compile("</?[a-zA-Z]+").matcher(result);
        boolean lTagFound = false;
        boolean lgTagFound = false;
        while (m.find()) {
            String match = m.group();
            if (match.startsWith("</")) {
                if (openTags.size() == 0 && "</l".equals(match) && !lTagFound) {
                    return recloseTags("<l>" + broken);
                }
                if (openTags.size() == 0 && "</lg".equals(match) && !lgTagFound) {
                    return recloseTags("<lg>" + broken);
                }
                if (openTags.size() == 0) {
                    return null;
                }
                String lastTag = openTags.remove(openTags.size() - 1);
                if (!("</" + lastTag).equals(match)) {
                    return null;
                }
            } else {
                int closePos = result.indexOf('>', m.end());
                if (closePos == -1) {
                    return null;
                }
                while (Character.isWhitespace(result.charAt(closePos - 1))) {
                    --closePos;
                }
                if (result.charAt(closePos - 1) != '/') {
                    if ("<l".equals(match)) {
                        lTagFound = true;
                    }
                    if ("<lg".equals(match)) {
                        lgTagFound = true;
                    }
                    openTags.add(match.substring(1));
                }
            }
        }
        Collections.reverse(openTags);
        for (String openTag : openTags) {
            result += "</" + openTag + ">";
        }
        return result;
    }

    /**
     * Common HTML tags such as &lt;br&gt;,&lt;hr&gt; and &lt;img&gt; may be
     * left open causing XML parsing to fail. This method closes these tags.
     * 
     * @param broken
     *            the string to be cleaned
     * @return the cleaned string
     */
    public static String closeEmptyTags(String broken) {
        if (broken == null) {
            return null;
        }

        return openHTMLTagPattern.matcher(broken).replaceAll("<$1$2/>");
    }

    /**
     * XML parse failed, so we can try getting rid of all the tags and having
     * another go. We define a tag to start at a &lt; and end at the end of the
     * next word (where a word is what comes in between spaces) that does not
     * contain an = sign, or at a >, whichever is earlier.
     * @param broken 
     * @return the string without any tags
     */
    public static String cleanAllTags(String broken) {
        if (broken == null) {
            return null;
        }

        String working = broken;

        allTags: while (true) {
            int lt = working.indexOf('<');

            // If there are no more amps then we are done
            if (lt == -1) {
                break allTags;
            }

            // loop to find the end of this tag
            int i = lt;
            int startattr = -1;

            singletag: while (true) {
                i++;

                // the tag can't exist past the end of the string
                if (i >= working.length()) {
                    // go back one so we can safely chop
                    i--;
                    break singletag;
                }

                char c = working.charAt(i);

                // normal end of tag
                if (c == '>') {
                    break singletag;
                }

                // we declare end-of-tag if this 'word' is not an attribute
                if (c == ' ') {
                    if (startattr == -1) {
                        // NOTE(joe): should we skip over consecutive spaces?
                        startattr = i;
                    } else {
                        // so we've already had a space indicating start of
                        // attribute, so this must be the beginning of the next
                        // NOTE(joe): no - spaces can exist in attr values
                        String value = working.substring(startattr, i);
                        if (value.indexOf('=') == -1) {
                            // this 'attribute' does not contain an equals so
                            // we call it a word and end the parse
                            break singletag;
                        }
                    }
                }
            }

            // So we have the end of the tag, delete it, but leave a space in it's place
            // DataPolice.report("discarding tag: " + working.substring(lt, i + 1));
            working = working.substring(0, lt) + " " + working.substring(i + 1);
        }

        return working;
    }

    /**
     * Replace entity with its unicode equivalent, if it is not a valid XML
     * entity. Otherwise strip it out. XML only allows 4 entities: &amp;amp;,
     * &amp;quot;, &amp;lt; and &amp;gt;.
     * 
     * @param entity
     *            the entity to be replaced
     * @return the substitution for the entity, either itself, the unicode
     *         equivalent or an empty string.
     */
    private static String handleEntity(String entity) {
        if (goodEntities.contains(entity)) {
            return entity;
        }

        String replace = badEntities.get(entity);
        if (replace != null) {
            return replace;
        }

        // replace unknown entities with a space
        return " ";
    }

    // Map entities to their unicode equivalent
    private static Set<String> goodEntities = new HashSet<String>();
    private static PropertyMap badEntities = new PropertyMap();
    static {
        // pre-defined XML entities
        goodEntities.add("&quot;"); // quotation mark
        goodEntities.add("&amp;"); // ampersand
        goodEntities.add("&lt;"); // less-than sign
        goodEntities.add("&gt;"); // greater-than sign

        // misc entities
        badEntities.put("&euro;", "\u20AC"); // euro
        badEntities.put("&lsquo;", "\u2018"); // left single quotation mark
        badEntities.put("&rsquo;", "\u2019"); // right single quotation mark

        // Latin 1 entities
        badEntities.put("&nbsp;", "\u00A0"); // no-break space
        badEntities.put("&iexcl;", "\u00A1"); // inverted exclamation mark
        badEntities.put("&cent;", "\u00A2"); // cent sign
        badEntities.put("&pound;", "\u00A3"); // pound sign
        badEntities.put("&curren;", "\u00A4"); // currency sign
        badEntities.put("&yen;", "\u00A5"); // yen sign
        badEntities.put("&brvbar;", "\u00A6"); // broken vertical bar
        badEntities.put("&sect;", "\u00A7"); // section sign
        badEntities.put("&uml;", "\u00A8"); // diaeresis
        badEntities.put("&copy;", "\u00A9"); // copyright sign
        badEntities.put("&ordf;", "\u00AA"); // feminine ordinal indicator
        badEntities.put("&laquo;", "\u00AB"); // left-pointing double angle quotation mark
        badEntities.put("&not;", "\u00AC"); // not sign
        badEntities.put("&shy;", "\u00AD"); // soft hyphen
        badEntities.put("&reg;", "\u00AE"); // registered sign
        badEntities.put("&macr;", "\u00AF"); // macron
        badEntities.put("&deg;", "\u00B0"); // degree sign
        badEntities.put("&plusmn;", "\u00B1"); // plus-minus sign
        badEntities.put("&sup2;", "\u00B2"); // superscript two
        badEntities.put("&sup3;", "\u00B3"); // superscript three
        badEntities.put("&acute;", "\u00B4"); // acute accent
        badEntities.put("&micro;", "\u00B5"); // micro sign
        badEntities.put("&para;", "\u00B6"); // pilcrow sign
        badEntities.put("&middot;", "\u00B7"); // middle dot
        badEntities.put("&cedil;", "\u00B8"); // cedilla
        badEntities.put("&sup1;", "\u00B9"); // superscript one
        badEntities.put("&ordm;", "\u00BA"); // masculine ordinal indicator
        badEntities.put("&raquo;", "\u00BB"); // right-pointing double angle quotation mark
        badEntities.put("&frac14;", "\u00BC"); // vulgar fraction one quarter
        badEntities.put("&frac12;", "\u00BD"); // vulgar fraction one half
        badEntities.put("&frac34;", "\u00BE"); // vulgar fraction three quarters
        badEntities.put("&iquest;", "\u00BF"); // inverted question mark
        badEntities.put("&Agrave;", "\u00C0"); // latin capital letter A with grave
        badEntities.put("&Aacute;", "\u00C1"); // latin capital letter A with acute
        badEntities.put("&Acirc;", "\u00C2"); // latin capital letter A with circumflex
        badEntities.put("&Atilde;", "\u00C3"); // latin capital letter A with tilde
        badEntities.put("&Auml;", "\u00C4"); // latin capital letter A with diaeresis
        badEntities.put("&Aring;", "\u00C5"); // latin capital letter A with ring above
        badEntities.put("&AElig;", "\u00C6"); // latin capital letter AE
        badEntities.put("&Ccedil;", "\u00C7"); // latin capital letter C with cedilla
        badEntities.put("&Egrave;", "\u00C8"); // latin capital letter E with grave
        badEntities.put("&Eacute;", "\u00C9"); // latin capital letter E with acute
        badEntities.put("&Ecirc;", "\u00CA"); // latin capital letter E with circumflex
        badEntities.put("&Euml;", "\u00CB"); // latin capital letter E with diaeresis
        badEntities.put("&Igrave;", "\u00CC"); // latin capital letter I with grave
        badEntities.put("&Iacute;", "\u00CD"); // latin capital letter I with acute
        badEntities.put("&Icirc;", "\u00CE"); // latin capital letter I with circumflex
        badEntities.put("&Iuml;", "\u00CF"); // latin capital letter I with diaeresis
        badEntities.put("&ETH;", "\u00D0"); // latin capital letter ETH
        badEntities.put("&Ntilde;", "\u00D1"); // latin capital letter N with tilde
        badEntities.put("&Ograve;", "\u00D2"); // latin capital letter O with grave
        badEntities.put("&Oacute;", "\u00D3"); // latin capital letter O with acute
        badEntities.put("&Ocirc;", "\u00D4"); // latin capital letter O with circumflex
        badEntities.put("&Otilde;", "\u00D5"); // latin capital letter O with tilde
        badEntities.put("&Ouml;", "\u00D6"); // latin capital letter O with diaeresis
        badEntities.put("&times;", "\u00D7"); // multiplication sign
        badEntities.put("&Oslash;", "\u00D8"); // latin capital letter O with stroke
        badEntities.put("&Ugrave;", "\u00D9"); // latin capital letter U with grave
        badEntities.put("&Uacute;", "\u00DA"); // latin capital letter U with acute
        badEntities.put("&Ucirc;", "\u00DB"); // latin capital letter U with circumflex
        badEntities.put("&Uuml;", "\u00DC"); // latin capital letter U with diaeresis
        badEntities.put("&Yacute;", "\u00DD"); // latin capital letter Y with acute
        badEntities.put("&THORN;", "\u00DE"); // latin capital letter THORN
        badEntities.put("&szlig;", "\u00DF"); // latin small letter sharp s
        badEntities.put("&agrave;", "\u00E0"); // latin small letter a with grave
        badEntities.put("&aacute;", "\u00E1"); // latin small letter a with acute
        badEntities.put("&acirc;", "\u00E2"); // latin small letter a with circumflex
        badEntities.put("&atilde;", "\u00E3"); // latin small letter a with tilde
        badEntities.put("&auml;", "\u00E4"); // latin small letter a with diaeresis
        badEntities.put("&aring;", "\u00E5"); // latin small letter a with ring above
        badEntities.put("&aelig;", "\u00E6"); // latin small letter ae
        badEntities.put("&ccedil;", "\u00E7"); // latin small letter c with cedilla
        badEntities.put("&egrave;", "\u00E8"); // latin small letter e with grave
        badEntities.put("&eacute;", "\u00E9"); // latin small letter e with acute
        badEntities.put("&ecirc;", "\u00EA"); // latin small letter e with circumflex
        badEntities.put("&euml;", "\u00EB"); // latin small letter e with diaeresis
        badEntities.put("&igrave;", "\u00EC"); // latin small letter i with grave
        badEntities.put("&iacute;", "\u00ED"); // latin small letter i with acute
        badEntities.put("&icirc;", "\u00EE"); // latin small letter i with circumflex
        badEntities.put("&iuml;", "\u00EF"); // latin small letter i with diaeresis
        badEntities.put("&eth;", "\u00F0"); // latin small letter eth
        badEntities.put("&ntilde;", "\u00F1"); // latin small letter n with tilde
        badEntities.put("&ograve;", "\u00F2"); // latin small letter o with grave
        badEntities.put("&oacute;", "\u00F3"); // latin small letter o with acute
        badEntities.put("&ocirc;", "\u00F4"); // latin small letter o with circumflex
        badEntities.put("&otilde;", "\u00F5"); // latin small letter o with tilde
        badEntities.put("&ouml;", "\u00F6"); // latin small letter o with diaeresis
        badEntities.put("&divide;", "\u00F7"); // division sign
        badEntities.put("&oslash;", "\u00F8"); // latin small letter o with stroke
        badEntities.put("&ugrave;", "\u00F9"); // latin small letter u with grave
        badEntities.put("&uacute;", "\u00FA"); // latin small letter u with acute
        badEntities.put("&ucirc;", "\u00FB"); // latin small letter u with circumflex
        badEntities.put("&uuml;", "\u00FC"); // latin small letter u with diaeresis
        badEntities.put("&yacute;", "\u00FD"); // latin small letter y with acute
        badEntities.put("&thorn;", "\u00FE"); // latin small letter thorn
        badEntities.put("&yuml;", "\u00FF"); // latin small letter y with diaeresis
    }

    /**
     * Pattern for numeric entities.
     */
    private static Pattern validCharacterEntityPattern = Pattern.compile("^&#x?\\d{2,4};");

    /**
     * Pattern that negates the allowable XML 4 byte unicode characters. Valid
     * are: #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
     * [#x10000-#x10FFFF]
     */
    private static Pattern invalidCharacterPattern = Pattern.compile("[^\t\r\n\u0020-\uD7FF\uE000-\uFFFD]");

    /**
     * Pattern that matches open &lt;br&gt;,&lt;hr&gt; and &lt;img&gt; tags.
     */
    private static Pattern openHTMLTagPattern = Pattern.compile("<(img|hr|br)([^>]*)(?<!/)>");

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);
}
