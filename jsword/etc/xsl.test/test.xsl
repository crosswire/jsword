<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
  <html>
  <head><title>
    <xsl:text>Bible Viewer</xsl:text>
  </title></head>
  <body>
  <table>
    <xsl:apply-templates/>
  </table>
  </body>
  </html>
</xsl:template>

<xsl:template match="title">
  <tr>
  <th>
  <xsl:apply-templates/>
  </th>
  </tr>
</xsl:template>

<xsl:template match="section">
  <tr>
  <td>
    <xsl:apply-templates/>
  </td>
  </tr>
</xsl:template>

<xsl:template match="ref">
    <p>
    <xsl:if test=".[@para='true']"><hr/></xsl:if>
    <xsl:text> </xsl:text>
    <font size="-2" color="#0000ff"><i><xsl:value-of select="@v"/></i></font>
    <xsl:text> </xsl:text>
    <xsl:apply-templates select="it"/>
    </p>

    <!-- xsl:apply-templates select="ut"/ -->
</xsl:template>

<xsl:template match="ut">
  <font size="-2" color="#444444">
  <xsl:text>[</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]</xsl:text>
  </font>
</xsl:template>

<xsl:template match="p">
  <br/> <br/>
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
