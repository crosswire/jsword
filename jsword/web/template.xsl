<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="html"
    >

<xsl:template match="/jsp:root">
  <jsp:root version="1.2">
    <jsp:scriptlet><![CDATA[
      response.setContentType("text/html");
    ]]></jsp:scriptlet>
    <xsl:apply-templates/>
  </jsp:root>
</xsl:template>

<xsl:template match="html:html">

<html>
<xsl:apply-templates select="html:head"/>

<body>
<xsl:apply-templates select="html:body/@*"/>

<table width="100%">
  <tr align="center">
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/">Crosswire</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/index.jsp">Sword Project</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/purpose">Purpose Statement</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/contact.jsp">Contact Us</a></td>
  </tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="logo" align="center" bgcolor="#cccc99">
      <a href="index.jsp"><img src="images/jsword.gif" width="512" height="75" border="0" alt="JSword Logo"/></a>
	</td>
  </tr>
</table>

<table width="100%">
  <tr align="center">
    <td class="navbutton" align="center"><a href="news.html">Latest News</a></td>
    <td class="navbutton" align="center"><a href="devt.html">Getting Involved</a></td>
    <td class="navbutton" align="center"><a href="screenshot.html">Screenshots</a></td>      
    <td class="navbutton" align="center"><a href="download.jsp">Download</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/modules/index.jsp">Module Add-ins</a></td>
  </tr>
</table>

<table width="100%" border="0">
  
  <tr> 
    <td class="sidenav" valign="top"> 
      <p>About JSword</p>
      <ul>
        <li><a href="index.html">Home</a></li>
        <li><a href="news.html">News</a></li>
      </ul>
      <p>Getting-JSword</p>
      <ul>
        <li><a href="demo.jsp">Web-Demo</a></li>
        <li><a href="jnlp.html">Web-Start</a></li>
        <li><a href="screenshot.html">Screenshots</a></li>
        <li><a href="download.jsp">Download</a></li>
        <li><a href="http://www.crosswire.org/sword/modules/index.jsp">Modules</a></li>
      </ul>
      <p><a href="devt.html">Getting-Involved</a></p>
      <ul>
        <li><a href="writingcode.html">Intro</a></li>
        <li><a href="primer.html">API Primer</a></li>
        <li><a href="design.html">Design</a></li>
        <li><a href="osisCore.1.1.html">OSIS</a></li>
        <li><a href="config.html">Config</a></li>
        <li><a href="change.html">Changes</a></li>
        <li><a href="api/index.html">JavaDoc</a></li>
        <li><a href="java2html/index.html">Java-Source</a></li>
        <li><a href="test/index.html">Test-Results</a></li>
      </ul>
      <p>Other-Projects</p>
      <ul>
        <li><a href="http://www.crosswire.org/">Crosswire</a></li>
        <li><a href="http://www.crosswire.org/sword/index.jsp">Sword</a></li>
        <li><a href="http://www.sourceforge.net/projects/projectb/">Project-B</a></li>
      </ul>
      <p><a href="http://www.crosswire.org/sword/contact.jsp">Contact</a></p>
    </td>

    <td valign="top" class="maincell">
      <table cellpadding="5" border="0" width="100%">
        <tr>
          <td>
            <xsl:apply-templates select="html:body"/>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<table width="100%" cellpadding="3">
  <tr>
    <td class="navbutton" align="CENTER">
      The SWORD Project; P. O. Box 2528; Tempe, AZ 85280-2528 USA
    </td>
  </tr>
</table>

</body>
</html>

</xsl:template>

<xsl:template match="html:head">
  <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="html:body">
  <xsl:copy-of select="node()"/>
</xsl:template>

<xsl:template match="html:body/@*">
  <xsl:copy/>
</xsl:template>

</xsl:stylesheet>
