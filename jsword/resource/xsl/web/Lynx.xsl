<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/TR/REC-html40">
<xsl:output method="html"/>

<xsl:template match='/web'>
  <html>
  <head>
  <title><xsl:value-of select='@title'/></title>
  <xsl:apply-templates select='script'/>
  </head>
  <body>
    <h1><xsl:value-of select='@title'/></h1>
    <xsl:apply-templates/>
    <hr/>
    <xsl:text> [ </xsl:text><a href='sweb?page=home' target='_top'>Home</a>
    <xsl:text> | </xsl:text><a href='spage' target='_top'>Bible</a>
    <xsl:text> | </xsl:text><a href='sconfig1' target='_top'>Config</a>
    <xsl:text> | </xsl:text><a href='sweb?page=download' target='_top'>Download</a>
    <xsl:text> | </xsl:text><a href='sweb?page=source' target='_top'>Source</a>
    <xsl:text> ] </xsl:text>
  </body>
  </html>
</xsl:template>

<xsl:template match='line'>
  <h2><xsl:value-of select='@title'/></h2>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='link'>
  <a href='?page={@src}'>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match='script'>
  <script>
  <xsl:text><![CDATA[<!--]]></xsl:text>
    <xsl:apply-templates/>
  <xsl:text><![CDATA[//-->]]></xsl:text>
  </script>
</xsl:template>

<xsl:template match='*|@*' priority='-1'>
  <xsl:copy>
    <xsl:apply-templates select='@*|*|text()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
