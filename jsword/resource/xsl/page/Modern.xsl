<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="bible">
  <br/>
  <table border='0' cellPadding='10' cellSpacing='0' align='center' width='95%' bgColor='#ffffee'>
  <tr><td>
    <font color='#886655'>
    <xsl:apply-templates/>
    </font>
  </td></tr>
  </table>
  <br/>
</xsl:template>

<xsl:template match="title">
  <h3>
  <img border='0' src="/examples/images/execute.gif"/>
  <xsl:text> Passage: </xsl:text>
  <xsl:apply-templates/>
  </h3>
</xsl:template>

<xsl:template match="section">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="ref">
  <xsl:if test="self::node()[@para='true']"><p/></xsl:if>
  <xsl:text> </xsl:text>
  <a href='?type=0&amp;input={@b}+{@c}&amp;go=GO&amp;style=Modern'>
  <!-- xsl:value-of select="@c"/ -->
  <font size="1" color="#331100"><i><xsl:value-of select="@v"/></i></font>
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

<xsl:template match='*|@*' priority='-1'>
  <xsl:copy>
    <xsl:apply-templates select='@*|*|text()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
