<?xml version="1.0"?> 
<xsl:stylesheet xmlns="http://www.w3.org/TR/REC-html40" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" omit-xml-declaration = "yes"/>

<xsl:template match='bible'>
  <html>
  <head></head>
  <body>
  <xsl:apply-templates/>
  </body>
  </html>
</xsl:template>

<xsl:template match='title'>
  <h2 align='center'>
    <xsl:apply-templates/>
  </h2>
</xsl:template>

<xsl:template match='section'>
  <table border="0" cellpadding="4">
    <tr>
      <td bgcolor="#cccccc">
        <font size="+1" face="Verdana,sans-serif">
          <xsl:value-of select='@title'/>
        </font>
      </td>
    </tr>
    <tr>
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </table>
</xsl:template>

<xsl:template match='ref'>
  <xsl:text> </xsl:text>
  <xsl:if test="self::node()[@para='true']"><br/></xsl:if>
  <xsl:text> </xsl:text>
  <font face='sans-serif' size='-2' color='#666666'><xsl:value-of select='@v'/></font>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select='*'/>
</xsl:template>

<xsl:template match='it'>
  <font size="+1" color="#333333" face="Verdana,sans-serif">
    <xsl:apply-templates/>
  </font>
</xsl:template>

<xsl:template match='*|@*' priority='-1'>
  <xsl:copy>
    <xsl:apply-templates select='@*|*|text()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

