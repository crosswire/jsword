<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:template match='bible'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='title'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='section'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='ref'>
  <xsl:text> </xsl:text>
  <xsl:if test="self::node()[@para='true']"><br/></xsl:if>
  <xsl:value-of select='@v'/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select='*'/>
</xsl:template>

<xsl:template match='it'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='*|@*' priority='-1'>
  <xsl:copy>
    <xsl:apply-templates select='@*|*|text()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
