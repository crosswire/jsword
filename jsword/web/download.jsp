<?xml version="1.0" encoding="iso-8859-1"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <title>JSword - Download</title>
  <link rel="stylesheet" type="text/css" href="sword.css"/>
</head>
<body>

<jsp:directive.page import="org.crosswire.jsword.view.web.DownloadSet" contentType="text/html"/>

<jsp:scriptlet><![CDATA[
  String localprefix = application.getInitParameter("localprefix");
  if (localprefix == null)
  	throw new NullPointerException("localprefix");

  String webprefix = application.getInitParameter("webprefix");
  if (webprefix == null)
  	throw new NullPointerException("webprefix");
]]></jsp:scriptlet>

<h1>Stable Release</h1>

<h2>Webstart</h2>
<p>
  The latest release of J-Sword is 0.97.
  It is available via <a href="http://java.sun.com/products/javawebstart/">WebStart</a> which
  you will need installed. On MacOSX that job is done for you.
  On Windows it is an easy download either from
  the <a href="http://java.sun.com/products/javawebstart/">webstart download page</a> or
  it <a href="http://java.sun.com/j2se/1.4.2/download.html">comes with J2SE 1.4</a>.
  Once you have Java installed ...
</p>
<div align="center">
  <a href="stable/jsword.jnlp"><img src="images/webstart.jpg" width="247" height="60" border="0"/></a>
</div>

<h2>Zip/Tar Based Downloads</h2>
<p>We keep official releases hanging around for a while.</p>
<table width="90%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
	<td>-</td>
	<td colspan="2" align="center">Binary</td>
	<td colspan="2" align="center">Source</td>
	<td colspan="2" align="center">Docs</td>
  </tr>
  <tr>
    <td>Compression</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
  </tr>
  <jsp:scriptlet><![CDATA[
  DownloadSet[] dls = DownloadSet.getDownloadSets(localprefix+"/release", webprefix+"/release", false);
  for (int i=0; i<dls.length; i++)
  {
  ]]></jsp:scriptlet>
  <tr>
	<td><jsp:expression>dls[i].getVersionString()</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_ZIP)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_TGZ)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_ZIP)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_TGZ)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_ZIP)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_TGZ)</jsp:expression></td>
  </tr>
  <jsp:scriptlet><![CDATA[
  }
  ]]></jsp:scriptlet>
</table>


<h1>Nightly Releases</h1>

<h2>Webstart</h2>
<p>
  A nightly webstart build of J-Sword is available. You'll need an installation
  of Java as for the stable release of J-Sword.
</p>
<div align="center">
  <a href="jnlp/jsword.jnlp"><img src="images/webstart.jpg" width="247" height="60" border="0"/></a>
</div>

<h2>Zip/Tar Based Downloads</h2>
<p>
  Regular releases are made and stored for a short time. You will need to use GNU
  tar to extract the doc.tar.gz files, although any tar should do for the others.
</p>
<table width="90%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
	<td>-</td>
	<td colspan="2" align="center">Binary</td>
	<td colspan="2" align="center">Source</td>
	<td colspan="2" align="center">Docs</td>
  </tr>
  <tr>
    <td>Compression</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
  </tr>
  <jsp:scriptlet><![CDATA[
  dls = DownloadSet.getDownloadSets(localprefix+"/nightly", webprefix+"/nightly", true);
  for (int i=0; i<dls.length; i++)
  {
  ]]></jsp:scriptlet>
  <tr>
	<td><jsp:expression>dls[i].getDateString()</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_ZIP)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_TGZ)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_ZIP)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_TGZ)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_ZIP)</jsp:expression></td>
	<td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_TGZ)</jsp:expression></td>
  </tr>
  <jsp:scriptlet><![CDATA[
  }
  ]]></jsp:scriptlet>
</table>

<h3>CVS Access</h3>
<p>
The most up to date access is via CVS. There are CVS access instruction 
on the <a href="devt.html">Getting Involved</a> page.
</p>

<h3>Modules</h3>
<p>
Sword modules are available <a href="http://www.crossire.org/sword/modules/index.jsp">here</a>. 
Most of these modules are working with JSword so please report any that fail.
</p>

</body>
</html>
</jsp:root>
