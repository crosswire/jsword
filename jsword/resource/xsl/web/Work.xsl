<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/TR/REC-html40">
<xsl:output method="html"/>

<xsl:template match="/web">
  <html>
  <head>
  <title><xsl:value-of select='@title'/></title>
  <xsl:apply-templates select='script'/>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="#000066">

  <table border="0" width="100%" height="100%" cellspacing="0" cellpadding="5">
  <tr>
    <td bgcolor="#000066" height="45" valign="middle" align="center"><font color="#000066">.</font></td>
    <td bgcolor="#000066" height="45" valign="middle" align="center" width="100%" style="border-bottom: 10 solid #6666FF">
      <b><tt><a href="http://www.eireneh.com"><font color="#FFFFFF" size="6">Eireneh.com</font></a><font color="#FFFFFF" size="6"> - <xsl:value-of select='@title'/></font></tt></b>
    </td>
    <td bgcolor="#000066" height="45" valign="middle" align="center"><font color="#000066">.</font></td>
  </tr>
  <tr>
    <td bgcolor="#000066"><font color="#000066">.</font></td>
    <td width="80%" bgcolor="#FFFFFF"><center>
      <table border="0" width="80%" cellpadding="0" cellspacing="0">
        <tr>
          <td width="100%"><tt>

          <xsl:apply-templates/>

          <br/>
          <tt>
          <xsl:text> [ </xsl:text><a href='sweb?page=home' target='_top'>Home</a>
          <xsl:text> | </xsl:text><a href='spage' target='_top'>Bible</a>
          <xsl:text> | </xsl:text><a href='sconfig1' target='_top'>Config</a>
          <xsl:text> | </xsl:text><a href='sweb?page=download' target='_top'>Download</a>
          <xsl:text> | </xsl:text><a href='sweb?page=source' target='_top'>Source</a>
          <xsl:text> ] </xsl:text>
          </tt>

          </tt></td>
        </tr>
      </table>
      </center></td>
    <td bgcolor="#000066"><font color="#000066">.</font></td>
  </tr>
  <tr>
    <td height="37" bgcolor="#000066" colspan="3"><font color="#000066">.</font></td>
  </tr>
  </table>

  </body>
  </html>
</xsl:template>

<xsl:template match='line'>
  <p>
  <tt><b><xsl:value-of select="@title"/></b></tt><br />
  <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match='link'>
  <a href='sweb?page={@src}'>
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

