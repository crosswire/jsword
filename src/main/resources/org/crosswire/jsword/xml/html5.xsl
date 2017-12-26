<?xml version="1.0"?>
<!--
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
 * © CrossWire Bible Society, 2005 - 2017
 -->
 <!--
 * Transforms OSIS to HTML for viewing within JSword browsers.
 * Note: There are custom protocols which the browser must handle.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 -->
 <xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0"
  xmlns:jsword="http://xml.apache.org/xalan/java"
  extension-element-prefixes="jsword">

  <!--  Version 3.0 is necessary to get br to work correctly. -->
  <xsl:output method="html" version="3.0" omit-xml-declaration="yes" indent="no"/>

  <!-- Be very careful about introducing whitespace into the document.
       strip-space merely remove space between one tag and another tag.
       This may cause significant whitespace to be removed.
       
       It is easy to have apply-templates on a line to itself which if
       it encounters text before anything else will introduce whitespace.
       With the browser we are using, span will introduce whitespace
       but font does not. Therefore we use font as a span.
    -->
  <!-- gdef and hdef refer to hebrew and greek definitions keyed by strongs -->
  <xsl:param name="greek.def.protocol" select="'gdef:'"/>
  <xsl:param name="hebrew.def.protocol" select="'hdef:'"/>
  <xsl:param name="lex.def.protocol" select="'lex:'"/>
  <!-- currently these are not used, but they are for morphologic forms -->
  <xsl:param name="greek.morph.protocol" select="'gmorph:'"/>
  <xsl:param name="hebrew.morph.protocol" select="'hmorph:'"/>

  <!-- The absolute base for relative references. -->
  <xsl:param name="baseURL" select="''"/>

  <!-- Whether to show Strongs or not -->
  <xsl:param name="Strongs" select="'false'"/>

  <!-- Whether to show morphologic forms or not -->
  <xsl:param name="Morph" select="'false'"/>

  <!-- Whether to show other lemma forms or not -->
  <xsl:param name="Lemma" select="'false'"/>

  <!-- Whether to start each verse on an new line or not -->
  <xsl:param name="VLine" select="'false'"/>

  <!-- Whether to show non-canonical "headings" or not -->
  <xsl:param name="Headings" select="'true'"/>

  <!-- Whether to show notes or not -->
  <xsl:param name="Notes" select="'true'"/>

  <!-- Whether to have linking cross references or not -->
  <xsl:param name="XRef" select="'true'"/>

  <!-- Whether to output no Verse numbers -->
  <xsl:param name="NoVNum" select="'false'"/>

  <!-- Whether to output Verse numbers or not -->
  <xsl:param name="VNum" select="'true'"/>

  <!-- Whether to output Chapter and Verse numbers or not -->
  <xsl:param name="CVNum" select="'false'"/>

  <!-- Whether to output Book, Chapter and Verse numbers or not -->
  <xsl:param name="BCVNum" select="'false'"/>

  <!-- Whether to output superscript verse numbers or normal size ones -->
  <xsl:param name="TinyVNum" select="'true'"/>

  <!-- Whether to show glosses as ruby text. -->
  <xsl:param name="Gloss" select="'true'"/>

  <!-- Which variant to output -->
  <xsl:param name="Variant" select="'x-1'"/>

  <!-- The CSS stylesheet to use. The url must be absolute. -->
  <xsl:param name="css"/>
  
  <!-- The default v11n is KJV -->
  <xsl:param name="v11n" select="'KJV'"/>

  <!-- The order of display. Hebrew is rtl (right to left) -->
  <xsl:param name="direction" select="'ltr'"/>

  <!-- The font that is passed in is in one of two forms:
    FamilyName-STYLE-size, where STYLE is either PLAIN, BOLD, ITALIC or BOLDITALIC
    or
    FamilyName,style,size, where STYLE is 0 for PLAIN, 1 for BOLD, 2 for ITALIC or 3 for BOLDITALIC.
    This needs to be changed into a CSS style specification
  -->
  <xsl:param name="font" select="Serif"/>

  <xsl:variable name="fontspec">
      <xsl:call-template name="generateFontStyle">
        <xsl:with-param name="fontspec" select="$font"/>
      </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="page-flags">
    <!-- Each will be a class attribute and needs a leading space. -->
    <xsl:if test="$VLine    = 'true'" > vpl</xsl:if>
    <xsl:if test="$Notes    = 'false'"> no-notes</xsl:if>
    <xsl:if test="$NoVNum   = 'true'" > no-vm</xsl:if> 
    <xsl:if test="$TinyVNum = 'true'" > tiny-vm</xsl:if> 
    <xsl:if test="$Strongs  = 'false'"> no-strongs</xsl:if>
    <xsl:if test="$Lemma    = 'false'"> no-lemma</xsl:if>
    <xsl:if test="$Morph    = 'false'"> no-morph</xsl:if>
    <xsl:if test="$Headings = 'false'"> no-title</xsl:if>
    <xsl:if test="$Strongs  = 'true' or $Lemma = 'true' or $Morph = 'true'"> interlinear</xsl:if>
  </xsl:variable>

  <!-- Create a versification from which verse numbers are understood -->
  <xsl:variable name="v11nf" select="jsword:org.crosswire.jsword.versification.system.Versifications.instance()"/>
  <!-- Create a global key factory from which OSIS ids will be generated -->
  <xsl:variable name="keyf" select="jsword:org.crosswire.jsword.passage.PassageKeyFactory.instance()"/>
  <!-- Create a global number shaper that can transform 0-9 into other number systems. -->
  <xsl:variable name="shaper" select="jsword:org.crosswire.common.icu.NumberShaper.new()"/>

  <!--=======================================================================-->
  <xsl:template match="/">
    <html dir="{$direction}">
      <head>
        <base href="http://book/s"/>
        <style type="text/css">
          body { background:white; margin: 0; padding: 0; <xsl:value-of select="$fontspec" /> }
          div.page { display: flex; }

          <!-- Notes are in the "first" column -->
          div.notes { flex: 1 1; padding: 5px; background:#f4f4e8; font-size: .75em;}
          div.notes:empty {display:none;}
          <!-- Text is in the "second" or only column -->
          div.text { flex: 5 1; padding: 5px; }

          .versenotes { margin-top: 1em; }
          .versenotes dl { margin: 0; }
          .versenotes dt { display: inline-block; text-align: center; width: 1em; font-weight: bold; }
          .versenotes dd { margin: 0; display: inline; }

          a { text-decoration: none; }
          a:hover { text-decoration: underline; }
          .sup { font-size: .75em; vertical-align: super }
          .strongs { color: red; }
          .morph { color: blue; color : green;}
          .lemma { color: orange; }
          .vm { color: gray; margin-right: 0.25em; margin-left: 0.25em;}
          .verse { margin-right: 1em; }
          .note { color: green; }
          a.note { font-weight: bold;}
          a.note:not(.first):before { content: ", "; }
          .lex { color: orange; }
          .jesus { color: red; }
          .speech { color: blue; }
          .strike { text-decoration: line-through; }
          .small-caps { font-variant: small-caps; }
          .spaced-letters { letter-spacing: 0.1em; margin-right: -0.1em; }
          .inscription { font-weight: bold; font-variant: small-caps; }
          .divineName { font-variant: small-caps; }
          .inscription { font-variant: small-caps; }
          .normal { font-variant: normal; }
          .caps { text-transform: uppercase; }
          .catchword { font-weight: bold; font-style: italic; }
          .illuminated { font-weight: bold; font-style: italic; }
          .added { font-style: italic; }
          .rdg { font-style: italic; }
          /* Headings */
          h1 { font-size: 130%; font-weight: bold; }
          h2 { font-size: 110%; font-weight: bold; }
          h3 { font-size: 100%; font-weight: bold; }
          h4 { font-size:  90%; font-weight: bold; }
          h5 { font-size:  85%; font-weight: bold; }
          h6 { font-size:  80%; font-weight: bold; }
          .heading { color: #669966; text-align: center; }
          .canonical { color: #666699; text-align: center; }
          .gen { color: #996666; }
          /* Dictionary entries */
          .orth { font-weight: bold; }
          .pron { font-style: italic; }
          .def  { font-style: italic; }
          .usg  { font-style: plain; }
          /* Page Level Options */
          .interlinear span.verse           { display: inline-flex; }
          .interlinear span.box             { display: inline-flex; flex-flow: row wrap; }
          .interlinear span.box > *         { display: block; }
          :not(.interlinear) .annotate      { display: none; }
          .shim                             { display: none; }
          .interlinear .shim                { display: inline; }
          .interlinear span.w > *:not(.box) { display: block; }
 
          /* Spacing */
          /* To make the display of wrapped words pleasing, some space needs to be added between lines. */
          /* Use margin to add space between lines */
          .verse { margin-top: 0.25em; margin-bottom: 0.25em; }

          /* Put spacing between items. */
          /* By putting the same amount left and right of items a border will go right between them. */
          /* But don't put padding before the first. */
          .interlinear span.box > *:not(:first-child) { padding-left: 0.25em; }

          /* But don't put padding after the last. */
          .interlinear span.box > *:not(:last-child) { padding-right: 0.25em; }
 
          /* Text styles */
          .interlinear span.box { text-align: center; white-space: nowrap; }
          
          /* Borders */
          /* Borders go between padding and margin */
          /* Put a border between items */
          .interlinear span.box > *:not(:last-child) { border-right: 1px solid orange; }

          /* Underline the verse text which is text, punct or added */
          .interlinear span.text  { border-bottom: 3px double red; }
          .interlinear span.added { border-bottom: 3px double green; }

          .no-notes   .notes              { display: none; }
          .no-notes   .nm                 { display: none; }
          .no-strongs span.w .strongs     { display: none; }
          .no-strongs span.w a.strongs    { display: none; }
          .no-lemma   span.w .lemma       { display: none; }
          .no-lemma   span.w a.lemma      { display: none; }
          .no-morph   span.w .morph       { display: none; }
          .no-morph   span.w a.morph      { display: none; }
          .no-vm      .vm                 { display: none; }
          .no-title   h1:not(.canonical)  { display: none; }
          .no-title   h2:not(.canonical)  { display: none; }
          .no-title   h3:not(.canonical)  { display: none; } 
          .no-title   h4:not(.canonical)  { display: none; }
          .no-title   h5:not(.canonical)  { display: none; } 
          .no-title   h6:not(.canonical)  { display: none; }

          .vpl        .verse { display: flex; }
          .tiny-vm    .vm    { vertical-align: super; font-size: .75em; }
          </style>
      </head>
      <body>
        <div class="page{$page-flags}">
          <div class="notes"><xsl:apply-templates select="//verse" mode="print-notes"/></div>
          <div class="text"><xsl:apply-templates/></div>
        </div>
      </body>
    </html>
  </xsl:template>

  <!--=======================================================================-->
  <!--
    == A proper OSIS document has osis as it's root.
    == We dig deeper for it's content.
    -->
  <xsl:template match="osis">
    <xsl:apply-templates/>
  </xsl:template>

  <!--=======================================================================-->
  <!--
    == An OSIS document may contain more that one work.
    == Each work is held in an osisCorpus element.
    == If there is only one work, then this element will (should) be absent.
    == Process each document in turn.
    == It might be reasonable to dig into the header element of each work
    == and get its title.
    == Otherwise, we ignore the header and work elements and just process
    == the osisText elements.
    -->
  <xsl:template match="osisCorpus">
    <xsl:apply-templates select="osisText"/>
  </xsl:template>

  <!--=======================================================================-->
  <!--
    == Each work has an osisText element.
    == We ignore the header and work elements and process its div elements.
    == While divs can be milestoned, the osisText element requires container
    == divs.
    -->
  <xsl:template match="osisText">
    <xsl:apply-templates select="div"/>
  </xsl:template>
  
  <!-- Ignore headers and its elements -->
  <xsl:template match="header"/>
  <xsl:template match="revisionDesc"/>
  <xsl:template match="work"/>
   <!-- <xsl:template match="title"/> who's parent is work -->
  <xsl:template match="contributor"/>
  <xsl:template match="creator"/>
  <xsl:template match="subject"/>
  <!-- <xsl:template match="date"/> who's parent is work -->
  <xsl:template match="description"/>
  <xsl:template match="publisher"/>
  <xsl:template match="type"/>
  <xsl:template match="format"/>
  <xsl:template match="identifier"/>
  <xsl:template match="source"/>
  <xsl:template match="language"/>
  <xsl:template match="relation"/>
  <xsl:template match="coverage"/>
  <xsl:template match="rights"/>
  <xsl:template match="scope"/>
  <xsl:template match="workPrefix"/>
  <xsl:template match="castList"/>
  <xsl:template match="castGroup"/>
  <xsl:template match="castItem"/>
  <xsl:template match="actor"/>
  <xsl:template match="role"/>
  <xsl:template match="roleDesc"/>
  <xsl:template match="teiHeader"/>
  <xsl:template match="refSystem"/>

  <!-- Ignore titlePage -->
  <xsl:template match="titlePage"/>

  <!--=======================================================================-->
  <!-- 
    == Div provides the major containers for a work.
    == Divs are milestoneable.
    -->
  <!-- osis2mod creates pre-verse DIVs as milestones
    == to mark material that stands before a verse
    == These should be ignored.
    -->
  <xsl:template match="div[@type='x-milestone' and @subType='x-preverse']"/>

  <xsl:template match="div[@type='x-center']">
    <div align="center">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <!-- osis2mod transforms:
    == <p> into <div type="x-p" sID="xxx"/>
    == </p> into <div type="x-p" eID="xxx"/>
    == These need to produce vertical whitespace.
    -->
  <xsl:template match="div[@type='x-p' and @sID]">
    <br/><xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="div[@type='x-p' and @eID]">
    <xsl:apply-templates/><br/>
  </xsl:template>

  <xsl:template match="div[@type='x-p']">
    <br/><xsl:apply-templates/><br/>
  </xsl:template>

  <xsl:template match="div">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="div[@type='x-p' and @sID]" mode="jesus">
    <br/><xsl:apply-templates mode="jesus"/>
  </xsl:template>

  <xsl:template match="div[@type='x-p' and @eID]" mode="jesus">
    <xsl:apply-templates mode="jesus"/><br/>
  </xsl:template>

  <xsl:template match="div[@type='x-p']" mode="jesus">
    <br/><xsl:apply-templates mode="jesus"/><br/>
  </xsl:template>

  <xsl:template match="div" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Handle verses as containers and as a start verse.                     -->
  <xsl:template match="verse[not(@eID)]">
    <!-- Handle the KJV paragraph marker. -->
    <xsl:if test="milestone[@type = 'x-p']"><br/></xsl:if>
    <!-- Always output the verse -->
    <span class="verse"><xsl:call-template name="versenum"/><span class="box"><xsl:apply-templates/></span></span>
  </xsl:template>

  <xsl:template match="verse[not(@eID)]" mode="jesus">
    <!-- Handle the KJV paragraph marker. -->
    <xsl:if test="milestone[@type = 'x-p']"><br/></xsl:if>
    <!-- Always output the verse -->
    <span class="verse"><xsl:call-template name="versenum"/><span class="box"><xsl:apply-templates/></span></span>
  </xsl:template>

  <xsl:template match="verse" mode="print-notes">
    <xsl:if test=".//note[not(@type) or not(@type = 'x-strongsMarkup')]">
      <xsl:variable name="versification" select="jsword:getVersification($v11nf, $v11n)"/>
      <xsl:variable name="passage" select="jsword:getValidKey($keyf, $versification, @osisID)"/>
      <div class="versenotes">
        <a href="#{substring-before(concat(@osisID, ' '), ' ')}">
          <xsl:value-of select="jsword:getName($passage)"/>
        </a>
        <xsl:apply-templates select=".//note" mode="print-notes" />
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="versenum">
    <!-- An osisID can be a space separated list of them -->
    <xsl:variable name="firstOsisID" select="substring-before(concat(@osisID, ' '), ' ')"/>
    <xsl:variable name="book" select="substring-before($firstOsisID, '.')"/>
    <xsl:variable name="chapter" select="jsword:shape($shaper, substring-before(substring-after($firstOsisID, '.'), '.'))"/>
    <!-- If n is present use it for the number -->
    <xsl:variable name="verse">
      <xsl:choose>
        <xsl:when test="@n">
          <xsl:value-of select="jsword:shape($shaper, string(@n))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="jsword:shape($shaper, substring-after(substring-after($firstOsisID, '.'), '.'))"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="versenum">
      <xsl:choose>
        <xsl:when test="$BCVNum = 'true'">
          <xsl:variable name="versification" select="jsword:getVersification($v11nf, $v11n)"/>
          <xsl:variable name="passage" select="jsword:getValidKey($keyf, $versification, @osisID)"/>
          <xsl:value-of select="jsword:getName($passage)"/>
        </xsl:when>
        <xsl:when test="$CVNum = 'true'">
          <xsl:value-of select="concat($chapter, ':', $verse)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$verse"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--
      == Surround versenum with something that forces a proper bidi context in Java.
      == Sup does not.
      -->
    <span id="{@osisID}" class="vm"><xsl:value-of select="$versenum"/></span>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="a">
    <a href="{@href}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="a" mode="jesus">
    <a href="{@href}"><xsl:apply-templates mode="jesus"/></a>
  </xsl:template>

  <!--=======================================================================-->
  <!-- x-strongsMarkup is a note in the KJV that should not be shown. -->
  <xsl:template match="note[@type = 'x-strongsMarkup']"/>
  <xsl:template match="note[@type = 'x-strongsMarkup']" mode="jesus"/>
  <xsl:template match="note[@type = 'x-strongsMarkup']" mode="print-notes"/>

  <!-- When we encounter a note, we merely output a link to the note. -->
  <xsl:template match="note">
    <xsl:variable name="siblings" select="../child::node()"/>
    <xsl:variable name="prev-position" select="position() - 1"/>
    <xsl:variable name="first">
      <xsl:choose>
        <xsl:when test="$prev-position >= 0 and name($siblings[$prev-position]) = 'note'"><xsl:value-of select="''"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="' first'"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a href="#{generate-id(.)}" class="nm note sup{$first}"><xsl:call-template name="generateNoteXref"/></a>
  </xsl:template>

  <xsl:template match="note" mode="jesus">
    <xsl:variable name="siblings" select="../child::node()"/>
    <xsl:variable name="prev-position" select="position() - 1"/>
    <xsl:variable name="first">
      <xsl:choose>
        <xsl:when test="$prev-position >= 0 and name($siblings[$prev-position]) = 'note'"><xsl:value-of select="''"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="' first'"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a href="#{generate-id(.)}" class="nm note sup{$first}"><xsl:call-template name="generateNoteXref"/></a>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="note" mode="print-notes">
    <dl id="{generate-id(.)}"><dt><xsl:call-template name="generateNoteXref"/></dt><dd><xsl:apply-templates/></dd></dl>
  </xsl:template>

  <!--
    == If the n attribute is present then use that for the cross ref otherwise create a letter.
    == Note: numbering restarts with each verse.
    -->
  <xsl:template name="generateNoteXref">
    <xsl:choose>
      <xsl:when test="@n">
        <xsl:value-of select="@n"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:number level="any" from="/osis//verse" format="a"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="p">
    <p><xsl:apply-templates/></p>
  </xsl:template>
  
  <xsl:template match="p" mode="jesus">
    <p><xsl:apply-templates mode="jesus"/></p>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="p" mode="print-notes">
    <!-- FIXME: This ignores text in the note. -->
    <!-- don't put para's in notes -->
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="w[@gloss]">
    <xsl:choose>
      <xsl:when test="$Gloss = 'true'">
        <ruby><rb><xsl:apply-templates/></rb><rp>(</rp><rt><xsl:value-of select="@gloss"/></rt><rp>)</rp></ruby>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="w[@gloss]" mode="jesus">
    <xsl:choose>
      <xsl:when test="$Gloss = 'true'">
        <ruby><rb><xsl:apply-templates mode="jesus"/></rb><rp>(</rp><rt><xsl:value-of select="@gloss"/></rt><rp>)</rp></ruby>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates mode="jesus"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="w">
   <!-- lemma and strongs are values in the same lemma attribute. Order is meaningful, but they may be interleaved. -->
   <xsl:variable name="lemma">
      <xsl:call-template name="prefix-match">
        <xsl:with-param name="str" select="@lemma"/>
        <xsl:with-param name="prefix" select="'lemma'"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="strongs">
     <xsl:call-template name="prefix-match">
       <xsl:with-param name="str" select="@lemma"/>
       <xsl:with-param name="prefix" select="'strong'"/>
     </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="morph">
     <xsl:call-template name="prefix-match">
       <xsl:with-param name="str" select="@morph"/>
       <xsl:with-param name="prefix" select="'robinson'"/>
     </xsl:call-template>
   </xsl:variable>
    <xsl:variable name="text">
      <xsl:apply-templates/>
    </xsl:variable>
    <!-- Output the content followed by all the lemmas and then all the morphs. -->
    <span class="w">
      <span class="text"><xsl:choose><xsl:when test="string-length($text)"><xsl:value-of select="$text"/></xsl:when><xsl:otherwise><span class="shim">&#160;</span></xsl:otherwise></xsl:choose></span>
      <span class="annotate box">
        <xsl:call-template name="notate">
          <xsl:with-param name="pos"     select="@src"    />
          <xsl:with-param name="lemma"   select="$lemma"  />
          <xsl:with-param name="strongs" select="$strongs"/>
          <xsl:with-param name="morph"   select="$morph"  />
        </xsl:call-template>
     </span>
    </span>
    <!--
        except when followed by a text node or non-printing node.
        This is true whether the href is output or not.
    -->
    <xsl:variable name="siblings" select="../child::node()"/>
    <xsl:variable name="next-position" select="position() + 1"/>
    <xsl:if test="$siblings[$next-position] and name($siblings[$next-position]) != ''">
      <xsl:text> </xsl:text>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="w" mode="jesus">
    <xsl:variable name="lemma">
      <xsl:call-template name="prefix-match">
        <xsl:with-param name="str" select="@lemma"/>
        <xsl:with-param name="prefix" select="'lemma'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="strongs">
      <xsl:call-template name="prefix-match">
        <xsl:with-param name="str" select="@lemma"/>
        <xsl:with-param name="prefix" select="'strong'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="morph">
      <xsl:call-template name="prefix-match">
        <xsl:with-param name="str" select="@morph"/>
        <xsl:with-param name="prefix" select="'robinson'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="text">
      <xsl:apply-templates mode="jesus"/>
    </xsl:variable>
    <!-- Output the content followed by all the lemmas and then all the morphs. -->
    <span class="w">
      <span class="text"><xsl:choose><xsl:when test="string-length($text)"><xsl:value-of select="$text"/></xsl:when><xsl:otherwise><span class="shim">&#160;</span></xsl:otherwise></xsl:choose></span>
      <span class="annotate box">
        <xsl:call-template name="notate">
          <xsl:with-param name="pos"     select="@src"    />
          <xsl:with-param name="lemma"   select="$lemma"  />
          <xsl:with-param name="strongs" select="$strongs"/>
          <xsl:with-param name="morph"   select="$morph"  />
        </xsl:call-template>
     </span>
    </span>
    <!--
        except when followed by a text node or non-printing node.
        This is true whether the href is output or not.
    -->
    <xsl:variable name="siblings" select="../child::node()"/>
    <xsl:variable name="next-position" select="position() + 1"/>
    <xsl:if test="$siblings[$next-position] and name($siblings[$next-position]) != ''">
      <xsl:text> </xsl:text>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="prefix-match">
    <xsl:param name="str"/>
    <xsl:param name="prefix"/>
    <xsl:param name="found" select="0"/>
    <xsl:if test="string-length($str)">
      <xsl:if test="starts-with($str, $prefix)">
        <xsl:if test="$found = 1"><xsl:text> </xsl:text></xsl:if>
        <xsl:value-of select="substring-before(concat($str, ' '), ' ')"/>
      </xsl:if>
      <xsl:variable name="match">
        <xsl:choose>
          <xsl:when test="starts-with($str, $prefix)"><xsl:value-of select="1"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="$found"/></xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:call-template name="prefix-match">
        <xsl:with-param name="str" select="substring-after($str, ' ')"/>
        <xsl:with-param name="prefix" select="$prefix"/>
        <xsl:with-param name="found" select="$match"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="notate">
    <xsl:param name="pos"    />
    <xsl:param name="lemma"  />
    <xsl:param name="strongs"/>
    <xsl:param name="morph"  />
    <xsl:if test="string-length($strongs)">
      <span class="w">
        <span><xsl:value-of select="substring-before(concat($pos, ' '), ' ')"/></span>

        <xsl:variable name="lemma-work" select="substring-after(substring-before($lemma, ':'), '.')"/>
        <xsl:variable name="lemma-next" select="substring-after(substring-before(concat($lemma, ' '), ' '), ':')"/>
        <span class="lemma" onclick='app.uri("sword://{$lemma-work}/{$lemma-next}"); return false;'><xsl:value-of select="$lemma-next"/></span>

        <xsl:variable name="strong-work" select="substring-before($strongs, ':')"/>
        <xsl:variable name="strong-next" select="substring-after(substring-before(concat($strongs, ' '), ' '), ':')"/>
        <span class="strongs" onclick='app.uri("sword://{$strong-work}/{$strong-next}"); return false;'><xsl:value-of select="format-number(substring($strong-next,2),'#')"/></span>

        <xsl:variable name="morph-work" select="substring-before($morph, ':')"/>
        <xsl:variable name="morph-next" select="substring-after(substring-before(concat($morph, ' '), ' '), ':')"/>
        <span class="morph" onclick='app.uri("sword://{$morph-work}/{$morph-next}"); return false;'><xsl:value-of select="$morph-next"/></span>
      </span>
      <xsl:call-template name="notate">
        <xsl:with-param name="pos"     select="substring-after($pos,     ' ')"/>
        <xsl:with-param name="lemma"   select="substring-after($lemma,   ' ')"/>
        <xsl:with-param name="strongs" select="substring-after($strongs, ' ')"/>
        <xsl:with-param name="morph"   select="substring-after($morph,   ' ')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="pos">
    <xsl:param name="str"/>
    <xsl:if test="string-length($str)">
      <span><xsl:value-of select="substring-before(concat($str, ' '), ' ')"/></span>
      <xsl:call-template name="pos">
        <xsl:with-param name="str" select="substring-after($str, ' ')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="lemma">
    <xsl:param name="lemma"/>
    <xsl:if test="string-length($lemma)">
      <xsl:variable name="work" select="substring-after(substring-before($lemma, ':'), '.')"/>
      <xsl:variable name="next-lemma" select="substring-before(concat(substring-after($lemma, ':'), ' '), ' ')"/>
      <a class="lemma" href="sword://{$work}/{$next-lemma}"><xsl:value-of select="$next-lemma"/></a>
      <xsl:call-template name="lemma">
        <xsl:with-param name="lemma" select="substring-after($lemma, ' ')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="strongs">
    <xsl:param name="lemma"/>
    <xsl:param name="part" select="0"/>
    <xsl:if test="string-length($lemma)">
      <xsl:variable name="work" select="substring-before($lemma, ':')"/>
      <xsl:variable name="next-lemma" select="substring-before(concat(substring-after($lemma, ':'), ' '), ' ')"/>
      <a class="strongs" href="sword://{$work}/{$next-lemma}"><xsl:value-of select="format-number(substring($next-lemma,2),'#')"/></a>
      <xsl:call-template name="strongs">
        <xsl:with-param name="lemma" select="substring-after($lemma, ' ')"/>
        <xsl:with-param name="part" select="1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="morph">
    <xsl:param name="morph"/>
    <xsl:param name="part" select="0"/>
    <xsl:if test="string-length($morph)">
      <xsl:variable name="work" select="substring-before($morph, ':')"/>
      <xsl:variable name="next-morph" select="substring-before(concat(substring-after($morph, ':'), ' '), ' ')"/>
      <a class="morph" href="sword://{$work}/{$next-morph}"><xsl:value-of select="$next-morph"/></a>
      <xsl:call-template name="morph">
        <xsl:with-param name="morph" select="substring-after($morph, ' ')"/>
        <xsl:with-param name="part" select="1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>


  <!--=======================================================================-->
  <xsl:template match="seg">
    <xsl:choose>
      <xsl:when test="starts-with(@type, 'color:')">
        <font color="{substring-before(substring-after(@type, 'color: '), ';')}"><xsl:apply-templates/></font>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'font-size:')">
        <font size="{substring-before(substring-after(@type, 'font-size: '), ';')}"><xsl:apply-templates/></font>
      </xsl:when>
      <xsl:when test="@type = 'x-variant'">
        <xsl:if test="@subType = $Variant">
          <xsl:apply-templates/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="seg" mode="jesus">
    <xsl:choose>
      <xsl:when test="starts-with(@type, 'color:')">
        <font color="{substring-before(substring-after(@type, 'color: '), ';')}"><xsl:apply-templates mode="jesus"/></font>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'font-size:')">
        <font size="{substring-before(substring-after(@type, 'font-size: '), ';')}"><xsl:apply-templates mode="jesus"/></font>
      </xsl:when>
      <xsl:when test="@type = 'x-variant'">
        <xsl:if test="@subType = $Variant">
          <xsl:apply-templates mode="jesus"/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates mode="jesus"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <!-- expansion is OSIS, expan is TEI -->
  <xsl:template match="abbr">
    <span class="abbr">
      <xsl:if test="@expansion">
        <xsl:attribute name="title">
          <xsl:value-of select="@expansion"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@expan">
        <xsl:attribute name="title">
          <xsl:value-of select="@expan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="abbr" mode="jesus">
    <span class="abbr">
      <xsl:if test="@expansion">
        <xsl:attribute name="title">
          <xsl:value-of select="@expansion"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@expan">
        <xsl:attribute name="title">
          <xsl:value-of select="@expan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates mode="jesus"/>
    </span>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="speaker[@who = 'Jesus']">
    <span class="jesus"><xsl:apply-templates mode="jesus"/></span>
  </xsl:template>

  <xsl:template match="speaker">
    <span class="speech"><xsl:apply-templates/></span>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="title">
    <xsl:call-template name="render-title"/>
  </xsl:template>

  <xsl:template match="title" mode="jesus">
    <xsl:call-template name="render-title"/>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template name="render-title">
    <xsl:variable name="heading">
      <xsl:choose>
        <xsl:when test="@type      = 'x-gen'">gen</xsl:when>
        <xsl:when test="@canonical = 'true'" >canonical</xsl:when>
        <xsl:otherwise>heading</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="@level = '1'"                   ><h1 class="{$heading}"><xsl:apply-templates/></h1></xsl:when>
      <xsl:when test="@level = '2'"                   ><h2 class="{$heading}"><xsl:apply-templates/></h2></xsl:when>
      <xsl:when test="@level = '3'"                   ><h3 class="{$heading}"><xsl:apply-templates/></h3></xsl:when>
      <xsl:when test="@level = '4' or @type = 'x-gen'"><h4 class="{$heading}"><xsl:apply-templates/></h4></xsl:when>
      <xsl:when test="@level = '5'"                   ><h5 class="{$heading}"><xsl:apply-templates/></h5></xsl:when>
      <xsl:when test="@level = '6'"                   ><h6 class="{$heading}"><xsl:apply-templates/></h6></xsl:when>
      <!-- Level supercedes type -->
      <xsl:when test="@type = 'main'"                 ><h1 class="{$heading}"><xsl:apply-templates/></h1></xsl:when>
      <xsl:when test="@type = 'chapter'"              ><h2 class="{$heading}"><xsl:apply-templates/></h2></xsl:when>
      <xsl:otherwise                                  ><h3 class="{$heading}"><xsl:apply-templates/></h3></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="reference">
    <xsl:choose>
      <xsl:when test="$XRef = 'true'">
        <a href="bible://{@osisRef}"><xsl:apply-templates/></a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="reference" mode="jesus">
    <xsl:choose>
      <xsl:when test="$XRef = 'true'">
        <a href="bible://{@osisRef}"><xsl:apply-templates mode="jesus"/></a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates mode="jesus"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="caption">
    <div class="caption"><xsl:apply-templates/></div>
  </xsl:template>
  
  <xsl:template match="caption" mode="jesus">
    <div class="caption"><xsl:apply-templates/></div>
  </xsl:template>
  
  <xsl:template match="catchWord">
    <span class="catchword"><xsl:apply-templates/></span>
  </xsl:template>
  
  <xsl:template match="catchWord" mode="jesus">
    <span class="catchword"><xsl:apply-templates mode="jesus"/></span>
  </xsl:template>
  
  <!--
      <cell> is handled shortly after <table> below and thus does not appear
      here.
  -->
  
  <xsl:template match="closer">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="closer" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>
  
  <xsl:template match="date">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="date" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>
  
  <xsl:template match="divineName">
    <span class="divineName"><xsl:apply-templates/></span>
  </xsl:template>
  
  <xsl:template match="divineName" mode="jesus">
    <span class="divineName"><xsl:apply-templates mode="jesus"/></span>
  </xsl:template>
  
  <xsl:template match="figure">
    <div class="figure">
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/')">
          <img src="{concat($baseURL, @src)}"/>   <!-- FIXME: Not necessarily an image... -->
        </xsl:when>
        <xsl:otherwise>
          <img src="{concat($baseURL, '/',  @src)}"/>   <!-- FIXME: Not necessarily an image... -->
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="figure" mode="jesus">
    <div class="figure">
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/')">
          <img src="{concat($baseURL, @src)}"/>   <!-- FIXME: Not necessarily an image... -->
        </xsl:when>
        <xsl:otherwise>
          <img src="{concat($baseURL, '/',  @src)}"/>   <!-- FIXME: Not necessarily an image... -->
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="jesus"/>
    </div>
  </xsl:template>
  
  <xsl:template match="foreign">
    <em class="foreign"><xsl:apply-templates/></em>
  </xsl:template>
  
  <xsl:template match="foreign" mode="jesus">
    <em class="foreign"><xsl:apply-templates mode="jesus"/></em>
  </xsl:template>
  
  <!-- This is a subheading. -->
  <xsl:template match="head//head">
    <h5 class="head"><xsl:apply-templates/></h5>
  </xsl:template>
  
  <!-- This is a top-level heading. -->
  <xsl:template match="head">
    <h4 class="head"><xsl:apply-templates/></h4>
  </xsl:template>
  
  <xsl:template match="index">
    <a id="index{@id}" class="index"/>
  </xsl:template>

  <xsl:template match="inscription">
    <span class="inscription"><xsl:apply-templates/></span>
  </xsl:template>

  <xsl:template match="inscription" mode="jesus">
    <span class="inscription"><xsl:apply-templates mode="jesus"/></span>
  </xsl:template>

  <xsl:template match="item">
    <li class="item"><xsl:apply-templates/></li>
  </xsl:template>

  <xsl:template match="item" mode="jesus">
    <li class="item"><xsl:apply-templates mode="jesus"/></li>
  </xsl:template>
  
  <!--
      <item> and <label> are covered by <list> below and so do not appear here.
  -->

  <xsl:template match="lg">
    <div class="lg"><xsl:apply-templates/></div>
  </xsl:template>
  
  <xsl:template match="lg" mode="jesus">
    <div class="lg"><xsl:apply-templates mode="jesus"/></div>
  </xsl:template>
  
  <xsl:template match="lg[@sID]"><br/></xsl:template>
  <xsl:template match="lg[@sID]" mode="jesus"><br/></xsl:template>
  <xsl:template match="lg[@eID]"/>
  <xsl:template match="lg[@eID]" mode="jesus"/>

  <xsl:template match="l[@sID]">
	<xsl:call-template name="indent"/>
  </xsl:template>

  <xsl:template match="l[@sID]" mode="jesus">
    <xsl:call-template name="indent"/>
  </xsl:template>

  <xsl:template match="l[@eID]"><br/></xsl:template>
  <xsl:template match="l[@eID]" mode="jesus"><br/></xsl:template>

  <xsl:template match="l">
	<xsl:call-template name="indent"/><xsl:apply-templates/><br/>
  </xsl:template>
  
  <xsl:template match="l" mode="jesus">
    <xsl:apply-templates mode="jesus"/><br/>
  </xsl:template>

  <!-- Generate poetry indent. The x-indent values are from an old ESV module.
       This mechanism is not ideal. The visual appearance does not fully account for verse numbers.
    -->
  <xsl:template name="indent">
    <!-- Account for the verse number by not indenting -->
    <xsl:if test="$NoVNum = 'false' and preceding-sibling::*[local-name() != 'verse']">
      <xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
      <xsl:choose>
        <xsl:when test="@type = 'selah'">
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
        </xsl:when>
        <xsl:when test="@level = '1'">
          <xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
        </xsl:when>
        <xsl:when test="@level = '2' or @type = 'x-indent'">
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
        </xsl:when>
        <xsl:when test="@level = '3' or @type = 'x-indent-2'">
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
        </xsl:when>
        <xsl:when test="@level = '4'">
          <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>&#160;&#160;&#160;&#160;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <!-- While a BR is a break, if it is immediately followed by punctuation,
       indenting this rule can introduce whitespace.
    -->
  <xsl:template match="lb"><br/></xsl:template>
  <xsl:template match="lb" mode="jesus"><br/></xsl:template>

  <xsl:template match="list">
    <xsl:choose>
      <xsl:when test="label">
        <!-- If there are <label>s in the list, it's a <dl>. -->
        <dl class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::label">
                <dt class="label"><xsl:apply-templates/></dt>
              </xsl:when>
              <xsl:when test="self::item">
                <dd class="item"><xsl:apply-templates/></dd>
              </xsl:when>
              <xsl:when test="self::list">
                <dd class="list-wrapper"><xsl:apply-templates select="."/></dd>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </dl>
      </xsl:when>

      <xsl:otherwise>
        <!-- If there are no <label>s in the list, it's a plain old <ul>. -->
        <ul class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::item">
                <li class="item"><xsl:apply-templates/></li>
              </xsl:when>
              <xsl:when test="self::list">
                <li class="list-wrapper"><xsl:apply-templates select="."/></li>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="list" mode="jesus">
    <xsl:choose>
      <xsl:when test="label">
        <!-- If there are <label>s in the list, it's a <dl>. -->
        <dl class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::label">
                <dt class="label"><xsl:apply-templates mode="jesus"/></dt>
              </xsl:when>
              <xsl:when test="self::item">
                <dd class="item"><xsl:apply-templates mode="jesus"/></dd>
              </xsl:when>
              <xsl:when test="self::list">
                <dd class="list-wrapper"><xsl:apply-templates select="." mode="jesus"/></dd>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates mode="jesus"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </dl>
      </xsl:when>

      <xsl:otherwise>
        <!-- If there are no <label>s in the list, it's a plain old <ul>. -->
        <ul class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::item">
                <li class="item"><xsl:apply-templates mode="jesus"/></li>
              </xsl:when>
              <xsl:when test="self::list">
                <li class="list-wrapper"><xsl:apply-templates select="." mode="jesus"/></li>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates mode="jesus"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="mentioned">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="mentioned" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>
  
  <!-- Milestones represent characteristics of the original manuscript.
    == that are being preserved. For this reason, most are ignored.
    ==
    == The defined types are:
    == column   Marks the end of a column where there is a multi-column display.
    == footer   Marks the footer region of a page.
    == halfLine Used to mark half-line units if not otherwise encoded.
    == header   Marks the header region of a page.
    == line     Marks line breaks, particularly important in recording appearance of an original text, such as a manuscript.
    == pb       Marks a page break in a text.
    == screen   Marks a preferred place for breaks in an on-screen rendering of the text.
    == cQuote   Marks the location of a continuation quote mark, with marker containing the publishers mark.
    -->
  <!--  This is used by the KJV for paragraph markers. -->
  <xsl:template match="milestone[@type = 'x-p']"><xsl:text> </xsl:text><xsl:value-of select="@marker"/><xsl:text> </xsl:text></xsl:template>
  <xsl:template match="milestone[@type = 'x-p']" mode="jesus"><xsl:text> </xsl:text><xsl:value-of select="@marker"/><xsl:text> </xsl:text></xsl:template>

  <xsl:template match="milestone[@type = 'cQuote']">
    <xsl:value-of select="@marker"/>
  </xsl:template>

  <xsl:template match="milestone[@type = 'cQuote']" mode="jesus">
    <xsl:value-of select="@marker"/>
  </xsl:template>

  <xsl:template match="milestone[@type = 'line']"><br/></xsl:template>

  <xsl:template match="milestone[@type = 'line']" mode="jesus"><br/></xsl:template>

  <!--
    == Milestone start and end are deprecated.
    == At this point we expect them to not be in the document.
    == These have been replace with milestoneable elements.
    -->
  <xsl:template match="milestoneStart"/>
  <xsl:template match="milestoneEnd"/>
  
  <xsl:template match="name">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="name" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>

  <!-- If there is a milestoned q then just output a quotation mark -->
  <xsl:template match="q[@sID or @eID]">
    <xsl:choose>
      <xsl:when test="@marker"><xsl:value-of select="@marker"/></xsl:when>
      <!-- The chosen mark should be based on the work's author's locale. -->
      <xsl:otherwise>"</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="q[@sID or @eID]" mode="jesus">
    <xsl:choose>
      <xsl:when test="@marker"><xsl:value-of select="@marker"/></xsl:when>
      <!-- The chosen mark should be based on the work's author's locale. -->
      <xsl:otherwise>"</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="q[@who = 'Jesus']">
    <span class="jesus"><xsl:value-of select="@marker"/><xsl:apply-templates mode="jesus"/><xsl:value-of select="@marker"/></span>
  </xsl:template>

  <xsl:template match="q[@type = 'blockquote']">
    <blockquote class="q"><xsl:value-of select="@marker"/><xsl:apply-templates/><xsl:value-of select="@marker"/></blockquote>
  </xsl:template>

  <xsl:template match="q[@type = 'blockquote']" mode="jesus">
    <blockquote class="q"><xsl:value-of select="@marker"/><xsl:apply-templates mode="jesus"/><xsl:value-of select="@marker"/></blockquote>
  </xsl:template>

  <xsl:template match="q[@type = 'citation']">
    <blockquote class="q"><xsl:value-of select="@marker"/><xsl:apply-templates/><xsl:value-of select="@marker"/></blockquote>
  </xsl:template>

  <xsl:template match="q[@type = 'citation']" mode="jesus">
    <blockquote class="q"><xsl:value-of select="@marker"/><xsl:apply-templates mode="jesus"/><xsl:value-of select="@marker"/></blockquote>
  </xsl:template>

  <xsl:template match="q[@type = 'embedded']">
    <xsl:choose>
      <xsl:when test="@marker">
        <xsl:value-of select="@marker"/><xsl:apply-templates/><xsl:value-of select="@marker"/>
      </xsl:when>
      <xsl:otherwise>
        <quote class="q"><xsl:apply-templates/></quote>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="q[@type = 'embedded']" mode="jesus">
    <xsl:choose>
      <xsl:when test="@marker">
      <xsl:value-of select="@marker"/><xsl:apply-templates mode="jesus"/><xsl:value-of select="@marker"/>
      </xsl:when>
      <xsl:otherwise>
        <quote class="q"><xsl:apply-templates/></quote>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- An alternate reading. -->
  <xsl:template match="rdg">
    <span class="rdg"><xsl:apply-templates/></span>
  </xsl:template>

  <xsl:template match="rdg" mode="jesus">
    <span class="rdg"><xsl:apply-templates mode="jesus"/></span>
  </xsl:template>

  <!--
      <row> is handled near <table> below and so does not appear here.
  -->
  
  <xsl:template match="salute">
    <xsl:apply-templates/>
  </xsl:template>
  
 <!-- Avoid adding whitespace -->
  <xsl:template match="salute" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>

  <xsl:template match="signed">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="signed" mode="jesus">
    <xsl:apply-templates mode="jesus"/>
  </xsl:template>

  <xsl:template match="speech">
    <div class="speech"><xsl:apply-templates/></div>
  </xsl:template>
  
  <xsl:template match="speech" mode="jesus">
    <div class="speech"><xsl:apply-templates mode="jesus"/></div>
  </xsl:template>

  <xsl:template match="table">
    <table class="table">
      <xsl:copy-of select="@rows|@cols|@border"/>
      <xsl:attribute name="cellspacing">0</xsl:attribute>
      <xsl:if test="head">
        <thead class="head"><xsl:apply-templates select="head"/></thead>
      </xsl:if>
      <tbody><xsl:apply-templates select="row"/></tbody>
    </table>
  </xsl:template>

  <xsl:template match="row">
    <tr class="row"><xsl:apply-templates/></tr>
  </xsl:template>
  
  <xsl:template match="cell">
    <xsl:variable name="element-name">
      <xsl:choose>
        <xsl:when test="@role = 'label'">
          <xsl:text>th</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>td</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="{$element-name}">
      <xsl:attribute name="class">cell</xsl:attribute>
      <xsl:attribute name="valign">top</xsl:attribute>
      <xsl:if test="@rows">
        <xsl:attribute name="rowspan">
          <xsl:value-of select="@rows"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@cols">
        <xsl:attribute name="colspan">
          <xsl:value-of select="@cols"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="transChange">
    <span class="w"><span class="text added"><xsl:apply-templates/></span></span>
  </xsl:template>
  <xsl:template match="transChange" mode="jesus">
    <span class="w"><span class="text added"><xsl:apply-templates/></span></span>
  </xsl:template>
  
  <!-- @type is OSIS, @rend is TEI -->
  <xsl:template match="hi">
    <xsl:variable name="style">
      <xsl:choose>
        <xsl:when test="@type">
          <xsl:value-of select="@type"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@rend"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$style = 'acrostic'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$style = 'bold'">
        <strong><xsl:apply-templates/></strong>
      </xsl:when>
      <xsl:when test="$style = 'emphasis'">
        <em><xsl:apply-templates/></em>
      </xsl:when>
      <xsl:when test="$style = 'illuminated'">
        <span class="illuminated"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'italic'">
        <em><xsl:apply-templates/></em>
      </xsl:when>
      <xsl:when test="$style = 'line-through'">
        <span class="strike"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'normal'">
        <span class="normal"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'small-caps'">
        <span class="small-caps"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'spaced-letters'">
        <span class="spaced-letters"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'sub'">
        <sub><xsl:apply-templates/></sub>
      </xsl:when>
      <xsl:when test="$style = 'super'">
        <sup><xsl:apply-templates/></sup>
      </xsl:when>
      <xsl:when test="$style = 'underline'">
        <u><xsl:apply-templates/></u>
      </xsl:when>
      <xsl:when test="$style = 'x-caps'">
        <span class="caps"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="hi" mode="jesus">
    <xsl:variable name="style">
      <xsl:choose>
        <xsl:when test="@type">
          <xsl:value-of select="@type"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@rend"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$style = 'acrostic'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$style = 'bold'">
        <strong><xsl:apply-templates/></strong>
      </xsl:when>
      <xsl:when test="$style = 'emphasis'">
        <em><xsl:apply-templates/></em>
      </xsl:when>
      <xsl:when test="$style = 'illuminated'">
        <strong><em><xsl:apply-templates/></em></strong>
      </xsl:when>
      <xsl:when test="$style = 'italic'">
        <em><xsl:apply-templates/></em>
      </xsl:when>
      <xsl:when test="$style = 'line-through'">
        <span class="strike"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'normal'">
        <span class="normal"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'small-caps'">
        <span class="small-caps"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'spaced-letters'">
        <span class="spaced-letters"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:when test="$style = 'sub'">
        <sub><xsl:apply-templates/></sub>
      </xsl:when>
      <xsl:when test="$style = 'super'">
        <sup><xsl:apply-templates/></sup>
      </xsl:when>
      <xsl:when test="$style = 'underline'">
        <u><xsl:apply-templates/></u>
      </xsl:when>
      <xsl:when test="$style = 'x-caps'">
        <span class="caps"><xsl:apply-templates/></span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
    The following elements are actually TEI and there is some expectation
    that these will make it into OSIS.
  -->
  <xsl:template match="superentry">
    <!-- output each entry element in turn -->
    <xsl:for-each select="entry|entryFree">
      <xsl:apply-templates/><br/><br/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="entry">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="entryFree">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="form">
    <xsl:apply-templates/><br/>
  </xsl:template>

  <xsl:template match="orth">
    <span class="orth"><xsl:apply-templates/></span>
  </xsl:template>

  <xsl:template match="pron">
    <span class="pron"><xsl:apply-templates/></span>
  </xsl:template>

  <xsl:template match="etym">
    <span class="etym"><xsl:apply-templates/></span>
  </xsl:template>

  <xsl:template match="def">
    <span class="def"><xsl:apply-templates/></span>
  </xsl:template>

  <xsl:template match="usg">
    <span class="usg"><xsl:apply-templates/></span>
  </xsl:template>

  <!--
    Generate a css or an inline style representation of a font spec.
    The fontspec that is passed in is in one of two forms:
    FamilyName-STYLE-size, where STYLE is either PLAIN, BOLD, ITALIC or BOLDITALIC
    or
    FamilyName,style,size, where STYLE is 0 for PLAIN, 1 for BOLD, 2 for ITALIC or 3 for BOLDITALIC.
  -->
  <xsl:template name="generateFontStyle">
    <xsl:param name="fontspec"/>
    <xsl:variable name="fontSeparator">
      <xsl:choose>
        <xsl:when test="contains($fontspec, ',')">
          <xsl:value-of select="','"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'-'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="aFont">
      <xsl:choose>
        <xsl:when test="substring-before($fontspec, $fontSeparator) = ''"><xsl:value-of select="$fontspec"/>,0,16</xsl:when>
        <xsl:otherwise><xsl:value-of select="$fontspec"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="fontfamily" select="substring-before($aFont, $fontSeparator)" />
    <xsl:variable name="fontsize" select="substring-after(substring-after($aFont, $fontSeparator), $fontSeparator)" />
    <xsl:variable name="styling" select="substring-before(substring-after($aFont, $fontSeparator), $fontSeparator)" />
    <xsl:variable name="fontweight">
      <xsl:choose>
        <xsl:when test="$styling = '1' or $styling = '3' or contains($styling, 'bold')">bold</xsl:when>
        <xsl:otherwise>normal</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="fontstyle">
      <xsl:choose>
        <xsl:when test="$styling = '2' or $styling = '3' or contains($styling, 'italic')">italic</xsl:when>
        <xsl:otherwise>normal</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:value-of select='concat("font-family: &apos;", $fontfamily, "&apos;, Serif; ",
                                 "font-size:   ",       $fontsize,   "pt; ",
                                 "font-weight: ",       $fontweight, "; ",
                                 "font-style:  ",       $fontstyle,  ";")'/>
  </xsl:template>
  
</xsl:stylesheet>
