
package org.crosswire.jsword.book.data;

import org.crosswire.jsword.book.data.jaxb.*;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

/**
 * A listener for events that can affect a JAXBVerseData.
 * 
 * The OSIS (1.1.1) doumentation had these notes:
 * <p>The &lt;verse&gt; element is used to make the standard verse divisions in
 * Bibles, although obviously a later imposition on the text. It is a standard
 * method for referencing biblical materials.</p>
 * 
 * <p>Note that verses often cross the boundaries of other elements and that
 * raises the question of how to deal with elements that overlap. Normally the
 * verse identifier will be its osisID, exampe &quot;Matt.1.1&quot; and the like.
 * When a verse is segmented, that is split into two or more parts to cross a
 * boundary, like a quotation, the ID should be used to indicate the various
 * parts.</p>
 * 
 * <p>The &lt;verse&gt; element in OSIS does not have a counterpart in the TEI
 * Guidelines.</p>
 * 
 * <p>NOTE(joe): Outstanding OSIS Questions ...
 * <li>What to do with paragraph boundaries?</li>
 * <li>What are the following OSIS attriibutes on the word element for: POS, morph, lemmua, gloss, src, xlit?</li>
 * </p>
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface BookDataListener
{
    /**
     * Start a Book Document
     */
    public void startDocument(String initials);
    public JAXBBookData endDocument();

    /**
     * Start a Section
     */
    public void startSection(String title);
    public void endSection();

    /**
     * Start a Verse
     */
    public void startVerse(Verse verse);
    public void endVerse();

    /**
     * Add a text string to the contents of the Verse
     */
    public void addText(String text);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;note&gt; element abounds in Bible translations, commentaries
     * and other works about biblical literature. The actual text of the
     * &lt;note&gt; is recorded inline, where it applies to the text, but that
     * is not necessarily where it is displayed. If it is more convenient to
     * record notes at the end of a text, care should be taken to point to the
     * proper reference for the note using the work and cite attributes.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, catchWord, date, hi, figure, foreign, index,
     * inscription, lg, list, mentioned, milestone, milestoneEnd,
     * milestoneStart, name, p, q, rdg, table, verse.</p>
     * <p>Also ignored after thought: divineName, reference, seg, title, w
     * and the noteType and osisRef attrs</p>
     */
    public void addNote(String marker, String addition);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;divineName&gt; element is to be used to mark the name of the
     * diety in biblical and other texts. Traditions vary on the writing of the
     * divine name and this is our response to satisfy the need for marking the
     * divine name in the text and yet not make any presumptions about various
     * traditions concerning the writing of the name. This would be a most
     * appropriate element to record a typology of the divine name as found or
     * translated in the text.</p>
     * <p>There is no equivalent TEI element, other than perhaps &lt;name&gt;,
     * since the TEI Guidelines did not specifically address issues related to
     * the encoding of biblical materials.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, foreign, index, w.</p>
     * <p>Also ignored after thought: note, reference, seg</p>
     */
    public void addDivineName(String name);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;q&gt; element is used for quotes, block quotes, embedded
     * quotes, and (quotes within quotes). There is no real difference between
     * a quote and a block quote other than formatting so both of those are
     * treated with this single element. Note that  this element does not
     * contain the element &lt;verse&gt; such that is a speaker is citing a
     * verse in a speech, the proper way to encode that would be as a
     * &lt;reference&gt; within the larger &lt;q&gt; element.</p>
     * <p>When segmenting quotes, use the same qID, and increment the segID to
     * allow retrieval of the entire quotation.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, closer, date, figure, foreign, hi, index,
     * inscription, lg, list, mentioned, milestone, milestoneEnd,
     * milestoneStart, name, p, q, salute, signed, speech, table, verse.</p>
     */
    public void startQuote(String who, String level);
    public void endQuote();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;reference&gt; element will be the subject of extension to
     * include XLink/XPointer syntax in a later OSIS release. At present it
     * marks the location of a reference in one text to another, whether direct
     * (like citation/quotation) or indirect (such as an allusion), along with
     * the reference/pointing mechanism in this release.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, date, divineName, hi, foreign, index, milestone
     * milestoneEnd, milestoneStart, name.</p>
     */
    public void startReference(Passage ref);
    public void endReference();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;seg&gt; element is a generic phrase container element.
     * Its primary use should be for phrase level markup that was omitted in
     * this release (please send a note to the project with your requirements),
     * representation of line breaks in a translation (or original text), or for
     * some other purpose for which no other element suffices.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, date, divineName, hi, foreign, index, milestone
     * milestoneEnd, milestoneStart, name, q, title.</p>
     */
    public void startSegment();
    public void endSegment();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;speaker&gt; element can be used to enclose the name of a
     * speaker in the text (when reported) but can also bear speaker information
     * when not apparent in the text, such as the shifts of speaker in the Song
     * of Solomon.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, divineName, hi, foreign, index, name.</p>
     */
    public void startSpeaker(String who);
    public void endSpeaker();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;title&gt; element is used for titles both in the sense of
     * those of divisions in a work, i.e., chapters, books, but also for titles
     * of other works that occur in notes or even the text. The &lt;title&gt;
     * element can occur within itself so users can have multiple sub-titles if
     * desired.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, date, divineName, hi, figure, foreign, index,
     * inscription, lg, milestone, name, q, title.</p>
     */
    public void startTitle();
    public void endTitle();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;transChange&gt; element was formulated to deal with cases
     * where a literal translation has added words to clarify the translation.
     * The Amplified Bible is one example of where this element would be useful
     * but certainly not the only one. Changing the tense of a verb to agree
     * with modern language usage and yet wanting to preserve some indication
     * that the original text had been changed is another.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, date, divineName, hi, foreign, index, milestone,
     * milestoneEnd, milestoneStart, name.</p>
     */
    public void startTransChange(String type);
    public void endTransChange();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;w&gt; element is used to mark tokens separated by whitespace,
     * which is probably an inadequate definition of word. It is provided to
     * allow users to attach a variety of other information to such tokens.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, index, seg.</p>
     */
    public void startWord();
    public void endWord();

/*
    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;a&gt; element is a clone of &lt;reference&gt; and was added to
     * allow simple links to be built with the current OSIS release.
     * This will continue in future releases but expect to see full
     * XLink/XPointer syntax in later releases. Both the standard HTML links and
     * XLinks will be supported for OSIS texts for the foreseeable future.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the index element.</p>
     *
    public void addA(String href);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;abbr&gt; element contains abbreviations and the expansion of
     * abbreviations is placed in the expansion attribute. To illustrate:
     * &lt;abbr expansion=&quot;Journal of Biblical Literature&quot;&gt;JBL&lt;/abbr&gt;
     * This will be particularly helpful for users who are not professional
     * biblical scholars or to make sure references to lesser known publications
     * are easy to find (both by researchers and librarians).</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the short
     * version can unclide the folowing elements a, divineName, foreign, index,
     * name, note, reference, w.</p>
     *
    public void addAbbr(String expansion, String shortened);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;date&gt; element is used to record the type of date found in a
     * text. It bears an optional calendarType attribute which will allow the
     * user to note what sort of date is being recorded. Permissible values for
     * the calendarType attribute are: Chinese, Gregorian, Islamic, ISO, Jewish,
     * Julian. As with other attribute values in this schema, the user can
     * insert their own value for this attribute by appending &quot;x-&quot;
     * before the value they wish to use.
     * Thus, calendarType=&quot;x-DisneyLand&quot; would be one possible value,
     * although probably not a useful one.</p>
     * <p>The &lt;note&gt; element may occur within &lt;date&gt; as commentators
     * may wish to record additional information about the date.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, index, note and w.</p>
     *
    public void addDate(String calendarType, String date);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;foreign&gt; element is used to mark foreign words or phrases
     * that occur in a text. In some cases that may be for purposes of special
     * display of such words, such as displaying &quot;foreign&quot; words in an
     * English text in italics. In others, it may be to allow the use of a
     * special font to insure properly rendering of the text. Foreign in this
     * sense means different from the text being encoded and not foreign from a
     * particular language. The quotation of Armaic words in a New Testament
     * translation (whether the translation is English, Spanish or German) is an
     * example of foreign words in a text.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a abbr, date, divineName, hi, foreign, index, milestone,
     * name, note, reference, seg, title, w.</p>
     *
    public void addForeign(String quoted);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;hi&gt; element is a generic element that can be used to record
     * emphasis and the type of emphasis for an authored text. It should not be
     * used with primary source materials or translations to mark portions of
     * texts that have been rendered differently from the surrounding text.
     * The words of Jesus in a red-letter edition for example, should not be
     * encoded using the &lt;hi&gt; element. To merely record that the words
     * appear in the color red is to miss the reason why they are marked in red,
     * a fact that will probably be of interest to others reading the text.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include a, abbr, date, divineName, hi, milestone, milestoneEnd,
     * milestoneStart, name, w.</p>
     *
    public void addHi(String highlighted);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;index&gt; element is an empty element that is used to mark
     * index locations in a text. It follows the TEI Guidelines in most
     * respects, with the addition of a &quot;see&quot; attribute which is
     * explained below.
     * Note that the level attributes (level1 - level4) correspond to a main
     * entry (level1) and nested entries under that entry. Thus, the levels
     * link the attribute values together in hierarchy of topics. If you want a
     * separate index entry for a particular location, use another index
     * element.</p>
     * <p>The &quot;see&quot; attribute was added to the TEI syntax to enable
     * the recording of a see or see also entry in the index. The value of that
     * attribute should be a level1 entry that occurs in the document instance.
     * Automatic processing should generate a link to the appropriate location
     * in the text but use of the string provides a useful fallback to the user
     * by specifying the index entry that should be a pointer to the appropriate
     * location.</p>
     * <p>If it is desired to have see or see also entries at other levels of
     * the index, separate index tags should be inserted with the appropriate
     * see attributes.</p>
     * <p>Examples:
     * Standard usage of this element:
     * &lt;index id=&quot;1234&quot; index=&quot;subject&quot; level1=&quot;Job&quot; level2=&quot;theodicy&quot; level3=&quot;parallel literature&quot; level4=&quot;Babylonian sufferer&quot; see=&quot;Suffering - Theodicy&quot;/&gt;
     * would result in an entry in the subject index that looks like this:
     * <pre>
     * Job
     *  theodicy
     *    parallel literature
     *      Babylonian sufferer (pointer to location in text [id])
     * see: Suffering - Theodicy</pre>
     * </p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes.</p>
     *
    public void addIndex(String index, String level1, String level2, String level3, String level4, String see);

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;list&gt; element is used for common lists of items, as well as
     * simple glossaries and definition lists. The &lt;list&gt; element can
     * contain embedded lists, thereby allowing embedded sublists.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addList();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;milestone&gt; element is a true empty element that is used to
     * mark locations in a text. It carries not semantics other than a location
     * in the text stream. It can be used along with its type attribute to
     * record elements that otherwise would overlap. The milestone_Pt attribute
     * is used to indicate the type of attribute, such as a screen break - sb
     * (also known as a shadow milestone), pb (page break) and others.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addMilestone();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;milestoneEnd&gt; element is used with the
     * &lt;milestoneStart&gt; element to carry a semantic of containership for
     * an enumerated list of elements. That element type is specified on the
     * milestone_SE (Start/End) attribute. Milestones of this type are linked by
     * having identical osisID and splitID values.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addMilestoneEnd();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;milestoneStart&gt; element element is used with the
     * &lt;milestoneStart&gt; element to carry a semantic of containership.
     * That element being emulated should be specified with the type attribute.
     * Milestones of this type are linked by the end attribute of the
     * milestoneStart and the start attribute of milestoneEnd. In cases where
     * emulated containers have special attributes, such as who or level for
     * quote, those attributes should be added to milestoneStart.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addMilestoneStart();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;inscription&gt; element should not be used for quotations but
     * only in cases where an actual physical inscription is being reported or
     * recorded.</p>
     * <p>The TEI has no equivalent of this element.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addInscription();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;mentioned&gt; element is used to mark words (or phrases) that
     * are mentioned but not used. When illustrating a grammatical point, a
     * commentary may insert a word as an example of a particular usage. This is
     * more important for use in notes or commentaries than original texts, but
     * there are cases where it would be appropriate there as well.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addMentioned();

    /**
     * Add to a JAXBVerseData.
     * 
     * <p>OSIS v1.1.1 has this to say:
     * 
     * <p>The &lt;name&gt; element is useful as it allows the user to declare a
     * regular form for a name that may be written in different forms in the
     * text. For example, to recover all the instances of the name Susan, it
     * might be necessary to recover all instances of Susan, Susie (as a
     * nickname), Susie-Q (another nickname), as well as Suzanne (which might be
     * the person's full legal name. The regular attribute on &lt;name&gt;
     * allows the use of a single form of the name for indexing and searching
     * purposes.</p>
     * 
     * <p><b>Implementation details</b></p>
     * <p>Ignored a host of optional attributes and the fact that the contents
     * can include.</p>
     *
    public void addName();

*/
}
