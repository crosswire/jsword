<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="bible">
  <br/>
  <font face='Times New Roman'>
  <table border='1' borderColorDark='#000000' borderColorLight='#000000' borderColor='#000000' bgColor='#ffffff' cellPadding='10' cellSpacing='0' align='center' width='90%'>
  <tr>
  <td borderColorDark='#ffffff' borderColorLight='#ffffff'>
    <xsl:apply-templates/>
  </td>
  </tr>
  </table>
  </font>
  <br/>
</xsl:template>

<xsl:template match="title">
  <h2 align='center'>
  <xsl:apply-templates/>
  </h2>
</xsl:template>

<xsl:template match="section">
  <xsl:apply-templates/>
  <br/>
  <hr/>
</xsl:template>

<xsl:template match="ref">
  <font size="-2"><i><xsl:value-of select="@v"/></i></font>
  <xsl:text> </xsl:text>
  <xsl:if test="self::node()[@para='true']"><xsl:text>#</xsl:text></xsl:if>
  <xsl:text> </xsl:text>
  <font size='+1'>
  <xsl:apply-templates select="it"/>
  </font>
  <br/>
</xsl:template>

<xsl:template match="p">
  <br/>
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
  
<xsl:template match='*|@*' priority='-1'>
  <xsl:copy>
    <xsl:apply-templates select='@*|*|text()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
