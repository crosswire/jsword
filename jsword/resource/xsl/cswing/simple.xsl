<?xml version="1.0"?>

<xsl:stylesheet xmlns="http://www.w3.org/TR/REC-html40" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" omit-xml-declaration = "yes" indent="yes"/>

  <xsl:param name="strongs.hebrew.url" select="'dict:'"/>
  <xsl:param name="strongs.greek.url" select="'dict:'"/>

  <xsl:param name="font"/>

  <!--
  For now, we assume that all the works inside a corpus are of the
  same type.
  -->
  <xsl:variable name="osis-id-type" select="substring-before((//osisText)[1]/@osisIDWork, '.')"/>

  <xsl:variable name="page-div-type">
    <xsl:choose>
      <!--
      KJV is a special case. It should be Bible.KJV, but some OSIS
      transcriptions just use KJV instead.
      -->
      <xsl:when test="$osis-id-type = 'Bible' or $osis-id-type = 'KJV'">
        <xsl:text>chapter</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Dictionary'">
        <xsl:text>x-lexeme</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Lexicon'">
        <xsl:text>x-lemma</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Morph'">
        <xsl:text>x-tag</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>FIXME</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!--=======================================================================-->
  <xsl:template match="/osis">
    <html>
      <body>
        <xsl:apply-templates/>
        <xsl:apply-templates select="//note" mode="print-notes"/>
      </body>
    </html>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="osisCorpus">
    <xsl:for-each select="osisText">
      <!-- If this text has a header, apply templates to the header. -->
      <xsl:if test="preceding-sibling::*[1][self::header]">
        <div class="corpus-text-header">
          <xsl:apply-templates select="preceding-sibling::*[1][self::header]"/>
        </div>
      </xsl:if>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="osisText">
    <xsl:apply-templates/>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="div">
    <xsl:if test="@divTitle">
      <h2><xsl:value-of select="@divTitle"/></h2>
    </xsl:if>
    <xsl:if test="@type = 'testament'">
      <h2>
        <xsl:choose>
          <xsl:when test="preceding::div[@type = 'testament']">
           <xsl:text>New Testament</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Old Testament</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </h2>
    </xsl:if>
    <xsl:if test="@osisID">
      <a href="{@osisID}">
      <xsl:apply-templates/>
      </a>
    </xsl:if>
    <xsl:if test="not(@osisID)">
      <xsl:apply-templates/>
    </xsl:if>
    <xsl:if test="@divTitle">
      <p>&#0160;</p>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="verse">
    <font size="-1" color="gray" face="{$font}">
      <a name="{@osisID}"><xsl:value-of select="substring-after(substring-after(@osisID, '.'), '.')"/></a>
    </font>
    <font size="+1" face="{$font}">
      <xsl:apply-templates />
    </font>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="a">
    <a class="a" href="{@href}">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="note">
    <!-- if the note length is less than 4 chars this must be an insertion point and not the text -->
    <xsl:text> </xsl:text>
    <xsl:if test="string-length(.) &lt; 4">
      <a href="#{generate-id(.)}">
        <font size="-1" face="{$font}">
          <xsl:number level="any" from="/" count="note[string-length(.) &lt; 4]" format="a"/>
        </font>
      </a>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="note" mode="print-notes">
    <xsl:if test="string-length(.) &gt; 4">
      <xsl:choose>
        <xsl:when test="@type">
          <xsl:value-of select="concat('note-', @type)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>note</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <a name="{generate-id(.)}">
        <a href="#{ancestor::verse[1]/@osisID}">
          <xsl:number level="any" from="/" count="note[string-length(.) &lt; 4]" format="a"/>
        </a>
      </a>
      <xsl:text> </xsl:text>
      <xsl:apply-templates/>
      <p><xsl:comment>fix jtextpane</xsl:comment></p>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="p">
    <p><xsl:comment>fix jtextpane</xsl:comment></p>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="p" mode="print-notes">
    <!-- don't put para's in notes -->
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="w">
    <!-- FIXME: Handle all the other attributes besides lemma. -->
      <xsl:choose>
        <xsl:when test="starts-with(@lemma, 'x-Strongs:')">
          <xsl:variable name="orig-strongs-number" select="substring-after(@lemma, ':')"/>
          <xsl:variable name="strongs-type" select="substring($orig-strongs-number, 1, 1)"/>
          <xsl:variable name="numeric-portion" select="substring($orig-strongs-number, 2)"/>
          <xsl:variable name="strongs-number">
            <xsl:value-of select="$strongs-type"/>
            <xsl:call-template name="trim-zeros-from-number">
              <xsl:with-param name="number" select="$numeric-portion"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:choose>
            <xsl:when test="$strongs-type = 'H'">
              <a href="{$strongs.hebrew.url}{$numeric-portion}"><xsl:apply-templates/></a>
            </xsl:when>
            <xsl:otherwise>
              <a href="${strongs.greek.url}{$numeric-portion}"><xsl:apply-templates/></a>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="seg">
    <xsl:choose>
      <xsl:when test="@type='font-style: italic;'">
        <i>
          <xsl:apply-templates/>
        </i>
      </xsl:when>
      <xsl:when test="@type='font-weight: bold;'">
        <b>
          <xsl:apply-templates/>
        </b>
      </xsl:when>
      <xsl:when test="@type='text-decoration: underline;'">
        <u>
          <xsl:apply-templates/>
        </u>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'color:')">
        <font color="substring-before(substring-after(@type, 'color: '), ';')" face="{$font}">
          <xsl:apply-templates/>
        </font>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'font-size:')">
        <font size="substring-before(substring-after(@type, 'font-size: '), ';')" face="{$font}">
          <xsl:apply-templates/>
        </font>
      </xsl:when>
      <xsl:otherwise>
        <p>
          <xsl:apply-templates/>
        </p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="abbr">
    <abbr class="abbr">
      <xsl:if test="@expansion">
        <xsl:attribute name="title">
          <xsl:value-of select="@expansion"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </abbr>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="speaker">
    <xsl:choose>
      <xsl:when test="@who='Jesus'">
        <font color="red" face="{$font}">
          <xsl:apply-templates/>
        </font>
      </xsl:when>
      <xsl:otherwise>
        <font color="blue" face="{$font}">
          <xsl:apply-templates/>
        </font>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="title">
    <b>
      <xsl:apply-templates/>
    </b><br/>
  </xsl:template>

  <!--=======================================================================-->

  <xsl:template match="caption">
    <div class="caption">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="catchWord">
    <span class="catchWord">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <!--
      <cell> is handled shortly after <table> below and thus does not appear
      here.
  -->
  
  <xsl:template match="closer">
    <div class="closer">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="date">
    <span class="date">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="divineName">
    <span class="divineName">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="figure">
    <div class="figure">
      <img src="@src"/>  <!-- FIXME: Not necessarily an image... -->
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="foreign">
    <em class="foreign">
      <xsl:apply-templates/>
    </em>
  </xsl:template>
  
  <!-- This is a subheading. -->
  <xsl:template match="head//head">
    <h5 class="head">
      <xsl:apply-templates/>
    </h5>
  </xsl:template>
  
  <!-- This is a top-level heading. -->
  <xsl:template match="head">
    <h4 class="head">
      <xsl:apply-templates/>
    </h4>
  </xsl:template>
  
  <xsl:template match="hi">
    <span class="hi">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="index">
    <a name="index{@id}" class="index"/>
  </xsl:template>
  
  <xsl:template match="inscription">
    <span class="inscription">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="item">
    <li class="item">
      <xsl:apply-templates/>
    </li>
  </xsl:template>
  
  <!--
      <item> and <label> are covered by <list> below and so do not appear here.
  -->
  
  <xsl:template match="l">
    <div class="l">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="lg">
    <div class="lg">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="list">
    <xsl:choose>
      <xsl:when test="label">
        <!-- If there are <label>s in the list, it's a <dl>. -->
        <dl class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::label">
                <dt class="label">
                  <xsl:apply-templates/>
                </dt>
              </xsl:when>
              <xsl:when test="self::item">
                <dd class="item">
                  <xsl:apply-templates/>
                </dd>
              </xsl:when>
              <xsl:when test="self::list">
                <dd class="list-wrapper">
                  <xsl:apply-templates select="."/>
                </dd>
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
                <li class="item">
                  <xsl:apply-templates/>
                </li>
              </xsl:when>
              <xsl:when test="self::list">
                <li class="list-wrapper">
                  <xsl:apply-templates select="."/>
                </li>
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
  
  <xsl:template match="mentioned">
    <span class="mentioned">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <!--
      Note: I have not covered <milestone>, <milestoneStart>, or
            <milestoneEnd> here, since I have no idea what they are supposed
            to do, based on the spec.
  -->
  
  <xsl:template match="name">
    <span class="name">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="q">
    <!--
        FIXME: Should I use <span> here?  The spec says that this can be used
               as an embedded quote or a block quote, but there seems to be no
               way to figure out which it is based on context.  Currently I've
               got it as a <blockquote> because it has block-level elements in
               it.
        
        FIXME: Should I include the speaker in the text, e.g.:
               
                   {@who}: {text()}
               
               ?  I'm not sure.  Currently I've just got it as a "title"
               attribute on the <span>.
    -->
    <blockquote class="q">
      <xsl:if test="@who">
        <xsl:attribute name="title"><xsl:value-of select="@who"/></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>
  
  <xsl:template match="rdg">
    <div class="rdg">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="reference">
    <!--
        osisRefType conforms to the following regular expression, where:
        
            X = (\p{L}|\p{N}|_)+
            Y = (\p{L}|\p{N}|_)*
            Z = (\p{L}|\p{N}|\s)*
            W = (X)((\.X)*)?
        
        Here goes:
        
            (W:)?W(@(cp:\[(\p{Nd})*\]|s:\[Z\]))?(\-(W)(@(cp:\[(\p{Nd})*\]|s:\[Z\]))?)?
                                                    ^ almost [actually (Y)((\.X)*)]
        
        Since I have no clue what this is supposed to mean (no examples or
        explanation in the spec), I don't treat the osisRef attribute here.  I
        assume it's some sort of reference to an osisID in another work, but I
        don't really know.  (Maybe cp: is a page number?  I don't know what s:
        is.)
    -->
    <span class="reference">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <!--
      <row> is handled near <table> below and so does not appear here.
  -->
  
  <xsl:template match="salute">
    <div class="salute">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="signed">
    <span class="signed">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="speech">
    <div class="speech">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="table">
    <table class="table">
      <xsl:copy-of select="@rows|@cols"/>
      <xsl:if test="head">
        <thead class="head">
          <xsl:apply-templates select="head"/>
        </thead>
      </xsl:if>
      <tbody>
        <xsl:apply-templates select="row"/>
      </tbody>
    </table>
  </xsl:template>
  
  <xsl:template match="row">
    <tr class="row">
      <xsl:apply-templates/>
    </tr>
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
    <span class="transChange">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  
  <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      Named templates
   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
  
  <xsl:template name="trim-zeros-from-number">
    <xsl:param name="number" select="'0'"/>
    <xsl:value-of select="string(number($number))"/>
  </xsl:template>
  
  <xsl:template name="print-prev-next-links">
    <xsl:param name="div" select="."/>
    
    <xsl:variable name="previous-section" select="$div/preceding::div[@type = $page-div-type][1]"/>
    <xsl:variable name="next-section" select="$div/following::div[@type = $page-div-type][1]"/>
    <xsl:if test="$previous-section or $next-section">
      <table width="100%" class="navigation">
        <tr>
          <xsl:if test="$previous-section">
            <td align="left">
              <a href="{$previous-section/@osisID}.html" class="previous-link">[&lt; Previous]</a>
            </td>
          </xsl:if>
          <xsl:if test="$next-section">
            <td align="right">
              <a href="{$next-section/@osisID}.html" class="next-link">[Next &gt;]</a>
            </td>
          </xsl:if>
        </tr>
      </table>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
