<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/TR/REC-html40">
<xsl:output method="html"/>

<xsl:template match="/web">
  <html>
  <head>
  <title><xsl:value-of select='@title'/></title>
  <xsl:apply-templates select='script'/>
  </head>

  <body bgcolor='#eeeedd' aLink='#dd5555' link='#997777' text='#553333' vLink='#775555' width='100%' height='100%' bottomMargin='0' leftMargin='0' rightMargin='0' topMargin='0'>

  <table border='0' cellPadding='0' cellSpacing='0' width='100%' height='100%'>

  <tr>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sweb?page=home' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> H O M E </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='spage' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> B I B L E </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sconfig1' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> C O N F I G </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sweb?page=download' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> D O W N L O A D </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sweb?page=source' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> S O U R C E </b></font></a>
    </td>
  </tr>

  <tr>
    <td valign='top' colSpan='5'>

    <br/>

    <font color='#886666' face='verdana'>
    <h1 align='center'><xsl:value-of select='@title'/></h1>
    </font>

    <xsl:apply-templates/>
    <hr color='#886666' size='5' width='95%'/>

    </td>
  </tr>

  <tr>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sweb?page=home' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> H O M E </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='spage' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> B I B L E </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sconfig1' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> C O N F I G </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sweb?page=download' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> D O W N L O A D </b></font></a>
    </td>
    <td bgColor='#886666' nowrap='true' height='25' align='center'>
      <a href='sweb?page=source' target='_top'><font color='#ffffee' face='lucida console' size='-1'><b> S O U R C E </b></font></a>
    </td>
  </tr>

  </table>
  </body>
  </html>
</xsl:template>

<xsl:template match='line'>
  <hr color='#886666' size='5' width='95%'/>
  <table border='0' cellPadding='4' cellSpacing='0' width='95%' borderColor='#886666' align='center'>
  <tr>
    <td bgColor='#ddddcc'>
      <font color='#886666' face='verdana'>
      <b><xsl:value-of select="@title"/></b>
      </font>
    </td>
  </tr>

  <tr>
    <td bgColor='#ffffee' colSpan='2'>
      <font color='#55333' face='verdana'>
      <xsl:apply-templates/>
      </font>
    </td>
  </tr>
  </table>
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
