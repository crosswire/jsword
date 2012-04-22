<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
    <font face="serif" color="#223355" size="+1">
    <xsl:apply-templates/>
    </font>
</xsl:template>

<xsl:template match="title">
  <h3>
  <img src="/examples/images/execute.gif"/>
  <xsl:apply-templates/>
  </h3>
</xsl:template>

<xsl:template match="section">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="ref">
    <xsl:if test=".[@para='true']"><p/></xsl:if>
    <xsl:text> </xsl:text>
    <a href="/examples/servlet/com.eireneh.bible.servlet.PageServer?type=0&amp;input=Gen+5&amp;go=GO&amp;style=S%3A%5CJoe%5CDevt%5CDoE%5Csrc%5Cnotes%5Ctest2.xsl">
    <font size="1" color="#ff00ff"><i><xsl:value-of select="@v"/></i></font>
    </a>
    <xsl:text> </xsl:text>
    <xsl:apply-templates select="it"/>
</xsl:template>

<xsl:template match="ut">
  <font size="-2" color="#ff0000">
  <xsl:text>[</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]</xsl:text>
  </font>
</xsl:template>

<xsl:template match="p">
  <p/>
  <xsl:apply-templates/>
</xsl:template>

<!-- not done
  ue_psnote
  ue_small
  ue_christ
  ue_poetry
  ue_clarify
  ue_quote
  ut_head
-->
  
<!-- Breaks HTMLDocument
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
  <meta http-equiv="Expires" content="0"/>
-->

</xsl:stylesheet>
