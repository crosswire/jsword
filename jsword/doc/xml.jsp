<%@ page %>

<html>
<head>
  <title>The J-SWORD Project - XML</title>
  <META NAME="keywords" CONTENT="bible, free, software, christian, study, gpl, bible software,">
  <META http-equiv="keywords" CONTENT="bible, free, software, christian, study, gpl, bible software,">
  <META NAME="distribution" CONTENT="global">
  <link rel="stylesheet" href="sword.css" type="text/css">
</head>

<body>

<%@ include file="header.jsp" %>

<h1>JSword - XML</h1>

This is some example XML that could have been produced by the Book
package.

<pre>
&lt;?xml version="1.0"?>
&lt;!DOCTYPE bible SYSTEM "bible.dtd">
&lt;bible>
  &lt;title level='0'>Gen 1:1-4&lt;/title>
  &lt;section version="AV">
    &lt;ref b='1' c='1' v='1' para='true'>
      &lt;it>In the beginning God created the heaven and the earth.&lt;/it>
    &lt;/ref>
    &lt;ref b='1' c='1' v='2'>
      &lt;it>And the earth was without form, and void; and darkness was upon the face of the deep.
      And the Spirit of God moved upon the face of the waters.&lt;/it>
    &lt;/ref>
    &lt;ref b='1' c='1' v='3' para='true'>
      &lt;it>And God said, Let there be light: and there was light.&lt;/it>
    &lt;/ref>
    &lt;ref b='1' c='1' v='4'>
      &lt;it>And God saw the light, that it was good: and God divided the light from the darkness.&lt;/it>
      &lt;ut type="note">the light from...: Heb. between the light and between the darkness&lt;/ut_note>
    &lt;/ref>
  &lt;/section>
&lt;/bible>
</pre>

<h2>DTD</h2>
This is what the DTD could look like. I currently do not specify any DTD
in my XML documents. For speed reasons I specifically do not want a DTD
most of the time, however it could be useful for testing purposes.

<pre>

&lt;!-- The document is some biblical text, contining a series of sections. -->
&lt;!ELEMENT bible (section)+>
&lt;!ATTLIST bible
  version CDATA ''
>

&lt;!-- A section contains a list of references, and a note that describes them.
We can also override the version settting on the bible element here. -->
&lt;!ELEMENT section (ref)+>
&lt;!ATTLIST section
  title CDATA #REQUIRED
  version CDATA ''
>

&lt;!-- A reference has a book, chapter and verse numbers (all counting from 1)
and it might be a new paragraph. Inside a reference is a set of inspired text
or uninspired text elements. We can also override the version settting on the
bible element here. Internally this is referred to as a Verse since it only
ever applies to one verse. Here calling it a Verse is confusing with the v
attribute, to ref remains. -->
&lt;!ELEMENT ref (it|ut)*>
&lt;!ATTLIST ref
  b CDATA #REQUIRED
  c CDATA #REQUIRED
  v CDATA #REQUIRED
  para (true|false) 'false'
  version CDATA ''
>

&lt;!-- Inspired text is simply characters, but can include empasis (enspired or
otherwise), and paragraph markers. -->
&lt;!ELEMENT it (#PCDATA|ue|ie|p)*>

&lt;!-- Inspired emphasis means the heading notes in the psalms -->
&lt;!ELEMENT ie (#PCDATA)*>
&lt;!ATTLIST ie
  type (psnote) 'psnote'
>

&lt;!-- Uninspired text is notes (usually textual clarifications) and headings -->
&lt;!ELEMENT ut (#PCDATA|ue|p)*>
&lt;!ATTLIST ut
  type (note|head) 'note'
>

&lt;!-- Uninspired emphasis. Not sure which of these should exist. The currently
defined ones are:
  small - indicates 'less important' text (some RSVs)
  christ - red letter editions
  poetry - all modern versions change format for poetry
  clarify - KJV and NKJV use italics for clarifying text
  quote - It could be useful to have all spoken words separated
-->
&lt;!ELEMENT ue (#PCDATA)*>
&lt;!ATTLIST ue
  type (small|christ|poetry|clarify|quote) 'clarify'
>

&lt;!-- New paragraph marker. Not that these are only for mid verse new
paragraphs. End of verse new paragraphs are as an attribute to the ref
element. -->
&lt;!ELEMENT p EMPTY>

</pre>

<%@ include file="footer.jsp" %>

</body>
</html>
