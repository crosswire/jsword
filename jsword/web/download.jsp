<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>JSword - Download</title>
  <link rel="stylesheet" type="text/css" href="sword.css"/>
</head>
<body>

<jsp:directive.page import="org.crosswire.jsword.view.web.DownloadSet"/>

<h1>Download</h1>

<jsp:scriptlet><![CDATA[
  String localprefix = application.getInitParameter("localprefix");
  if (localprefix == null)
  	throw new NullPointerException("localprefix");

  String webprefix = application.getInitParameter("webprefix");
  if (nightlydir == null)
  	throw new NullPointerException("webprefix");
]]></jsp:scriptlet>

<h3>Official Releases</h3>
<p>
Version 0.9.5 is the first beta for JSword 1.0.
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
  DownloadSet[] dls = DownloadSet.getDownloadSets(localprefix+"/release", webdlprefix+"/release", false);
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

<h3>Nightly Releases</h3>
<p>
Regular releases are made and stored for a short time:
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
