
package docs;

/**
* XML. 

<h2>DTD</h2>
This is some example XML that could have been produced by the Book
package. It really needs some work on Strongs numbers.

<pre>

<?xml version="1.0"?>
<!DOCTYPE bible SYSTEM "../notes/bible.dtd">
<bible>
  <title level='0'>Gen 1:1-4</title>
  <section version="AV">
    <ref b='1' c='1' v='1' para='true'>
      <it>In the beginning God created the heaven and the earth.</it>
    </ref>
    <ref b='1' c='1' v='2'>
      <it>And the earth was without form, and void; and darkness was upon the face of the deep.
      And the Spirit of God moved upon the face of the waters.</it>
    </ref>
    <ref b='1' c='1' v='3' para='true'>
      <it>And God said, Let there be light: and there was light.</it>
    </ref>
    <ref b='1' c='1' v='4'>
      <it>And God saw the light, that it was good: and God divided the light from the darkness.</it>
      <ut type="note">the light from...: Heb. between the light and between the darkness</ut_note>
    </ref>
  </section>
</bible>

</pre>

<h2>DTD</h2>
This is what the DTD could look like. I currently do not specify any DTD
in my XML documents. For speed reasons I specifically do not want a DTD
most of the time, however it could be useful for testing purposes.

<pre>

<!-- The document is some biblical text, contining a series of sections. -->
<!ELEMENT bible (section)+>
<!ATTLIST bible
  version CDATA ''
>

<!-- A section contains a list of references, and a note that describes them.
We can also override the version settting on the bible element here. -->
<!ELEMENT section (ref)+>
<!ATTLIST section
  title CDATA #REQUIRED
  version CDATA ''
>

<!-- A reference has a book, chapter and verse numbers (all counting from 1)
and it might be a new paragraph. Inside a reference is a set of inspired text
or uninspired text elements. We can also override the version settting on the
bible element here. Internally this is referred to as a Verse since it only
ever applies to one verse. Here calling it a Verse is confusing with the v
attribute, to ref remains. -->
<!ELEMENT ref (it|ut)*>
<!ATTLIST ref
  b CDATA #REQUIRED
  c CDATA #REQUIRED
  v CDATA #REQUIRED
  para (true|false) 'false'
  version CDATA ''
>

<!-- Inspired text is simply characters, but can include empasis (enspired or
otherwise), and paragraph markers. -->
<!ELEMENT it (#PCDATA|ue|ie|p)*>

<!-- Inspired emphasis means the heading notes in the psalms -->
<!ELEMENT ie (#PCDATA)*>
<!ATTLIST ie
  type (psnote) 'psnote'
>

<!-- Uninspired text is notes (usually textual clarifications) and headings -->
<!ELEMENT ut (#PCDATA|ue|p)*>
<!ATTLIST ut
  type (note|head) 'note'
>

<!-- Uninspired emphasis. Not sure which of these should exist. The currently
defined ones are:
  small - indicates 'less important' text (some RSVs)
  christ - red letter editions
  poetry - all modern versions change format for poetry
  clarify - KJV and NKJV use italics for clarifying text
  quote - It could be useful to have all spoken words separated
-->
<!ELEMENT ue (#PCDATA)*>
<!ATTLIST ue
  type (small|christ|poetry|clarify|quote) 'clarify'
>

<!-- New paragraph marker. Not that these are only for mid verse new
paragraphs. End of verse new paragraphs are as an attribute to the ref
element. -->
<!ELEMENT p EMPTY>

</pre>

* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
* @version D0.I0.T0
*/
public class XML
{
}
