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

<h1>Download</h1>

<h2>Official Installer Based Releases</h2>
<p>From Version 0.9.6 we are using ZeroG based installers.</p>
<table cellpadding="2" cellspacing="0" bordercolor="#000000" border="1" align="center" width="90%">
  <tr>
    <td>-</td>
    <td align="center" colspan="2">Windows</td>
    <td align="center" colspan="2">Linux</td>
    <td align="center">MacOS</td>
    <td align="center">Other</td>
  </tr>
  <tr>
    <td>Includes JVM?</td>
    <td>No VM</td>
    <td>With VM</td>
    <td>No VM</td>
    <td>With VM</td>
    <td>No VM</td>
    <td>No VM</td>
  </tr>
  <tr>
    <td>Version 0.9.6</td>
    <td><a href="http://www.crosswire.org/ftpmirror/pub/jsword/release/jsword-0.9.6-winnovm.exe">7.88 Mb</a></td
    ><td><a href="http://www.crosswire.org/ftpmirror/pub/jsword/release/jsword-0.9.6-winvm.exe">7.81 Mb</a></td>
    <td><a href="http://www.crosswire.org/ftpmirror/pub/jsword/release/jsword-0.9.6-linuxnovm.bin">14.56 Mb</a></td>
    <td><a href="http://www.crosswire.org/ftpmirror/pub/jsword/release/jsword-0.9.6-linuxvm.bin">13.98 Mb</a></td>
    <td><a href="http://www.crosswire.org/ftpmirror/pub/jsword/release/jsword-0.9.6-macosx.zip">27.53 Mb</a></td>
    <td><a href="http://www.crosswire.org/ftpmirror/pub/jsword/release/jsword-0.9.6-other.jar">23.22 Mb</a>
  </tr>
</table>

<h2>Nightly Releases</h2>
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
