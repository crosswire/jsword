<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Language" content="en-gb"/>
  <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
  <title><xsl:select match="/html/head/title"/></title>
  <link rel="stylesheet" type="text/css" href="generic.css"/>
  <link rel="stylesheet" type="text/css" href="sword.css"/>
</head>

<body>

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
    <td class="navbutton" align="center"><a href="news.jsp">Latest News</a></td>
    <td class="navbutton" align="center"><a href="devt.jsp">Getting Involved</a></td>
    <td class="navbutton" align="center"><a href="screenshot.jsp">Screenshots</a></td>      
    <td class="navbutton" align="center"><a href="download.jsp">Download</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/modules/index.jsp">Module Add-ins</a></td>
  </tr>
</table>

<table width="100%" border="0">
  
  <tr> 
    <td class="sidenav" valign="top"> 
      <p>About JSword</p>
      <ul>
        <li><a href="index.jsp">Home</a></li>
        <li><a href="faq.jsp">FAQ</a></li>
        <li><a href="news.jsp">News</a></li>
      </ul>
      <p>Getting&nbsp;JSword</p>
      <ul>
        <li><a href="demo.jsp">Web&nbsp;Demo</a></li>
        <li><a href="jnlp.jsp">Web&nbsp;Start</a></li>
        <li><a href="screenshot.jsp">Screenshots</a></li>
        <li><a href="download.jsp">Download</a></li>
        <li><a href="http://www.crosswire.org/sword/modules/index.jsp">Modules</a></li>
      </ul>
      <p><a href="devt.jsp">Getting&nbsp;Involved</a></p>
      <ul>
        <li><a href="intro.jsp">Intro</a></li>
        <li><a href="primaer.jsp">API Primer</a></li>
        <li><a href="design.jsp">Design</a></li>
        <li><a href="xml.jsp">XML</a></li>
        <li><a href="osisCore.1.1.html">OSIS</a></li>
        <li><a href="config.jsp">Config</a></li>
        <li><a href="change.jsp">Changes</a></li>
        <li><a href="api/index.html">JavaDoc</a></li>
        <li><a href="java2html/index.html">Java&nbsp;Source</a></li>
        <li><a href="test/index.html">Test&nbsp;Results</a></li>
      </ul>
      <p>Other&nbsp;Projects</p>
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
            <xsl:apply-templates select="/html/body"/>
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
</xsl:stylesheet>
