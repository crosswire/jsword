<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>JSword - Web Demo</title>
  <link rel="stylesheet" type="text/css" href="sword.css"/>
</head>
<body>
<jsp:scriptlet><![CDATA[
  String search = (String) request.getAttribute("search");
  if (search == null) search = "";
  String match = (String) request.getAttribute("match");
  if (match == null) match = "";
  String view = (String) request.getAttribute("view");
  if (view == null) view = "";
]]></jsp:scriptlet>
<h1>Live</h1>
<p>
This is a quick demonstration of how easy it is to write new front-ends 
to JSword.
</p>
<p>Please enter a reference to view or a search:</p>
<table width="100%">
  <tr>
    <td>Search:</td>
    <td nowrap="nowrap">
      <form name="search" method="post" action="demo">
        <jsp:text>&lt;input type="text" name="search" value="</jsp:text>
        <jsp:expression>search</jsp:expression>
        <jsp:text>" /&gt;</jsp:text>
        <input name="GO" type="submit" id="GO" value="GO" />
	  </form>
	</td>
	<td><small>E.g. &quot;joseph + mary&quot;. Enter a search 
	  string using ampersand or + to specify AND, / to specify OR, - to specify 
	  NOT.</small></td>
  </tr>
  <tr> 
	<td>Best Match: </td>
	<td nowrap="nowrap">
	  <form name="match" method="post" action="demo">
        <jsp:text>&lt;input type="text" name="match" value="</jsp:text>
        <jsp:expression>match</jsp:expression>
        <jsp:text>" /&gt;</jsp:text>
		<input name="GO" type="submit" id="GO" value="GO" />
	  </form>
	</td>
	<td><small>E.g. &quot;for god so loved the world&quot;. Enter 
	  a phrase to find verses that contain similar language.</small></td>
  </tr>
  <tr>
	<td>View:</td>
	<td nowrap="nowrap">
	  <form name="view" method="post" action="demo">
        <jsp:text>&lt;input type="text" name="view" value="</jsp:text>
        <jsp:expression>view</jsp:expression>
        <jsp:text>" /&gt;</jsp:text>
		<input name="GO" type="submit" id="GO" value="GO" />
	  </form>
	</td>
	<td><small>E.g. &quot;Gen 1&quot; or &quot;Luke 4:2-6&quot;. 
	  A set of verses to display.</small></td>
  </tr>
</table>
<br/>
<jsp:scriptlet><![CDATA[
  String reply = (String) request.getAttribute("reply");
  if (reply != null) {
]]></jsp:scriptlet>
<jsp:expression>reply</jsp:expression>
<jsp:scriptlet><![CDATA[ } ]]></jsp:scriptlet>
<jsp:scriptlet><![CDATA[
String link = (String) request.getAttribute("next-link");
if (link != null) {
]]></jsp:scriptlet>
<hr />
<p>The following <jsp:expression>request.getAttribute("next-overview")</jsp:expression>
were trimmed to avoid overloading server.</p>
<p><font size="-1">
<jsp:expression>request.getAttribute("next-name")</jsp:expression>
</font></p>
<p>
  To see them 
  <jsp:text>&lt;a href="demo?view=</jsp:text>
  <jsp:expression>link</jsp:expression>
  <jsp:text>"&gt;click here&lt;/a&gt;</jsp:text>
  .
</p>
<jsp:scriptlet><![CDATA[ } ]]></jsp:scriptlet>
</body>
</html>
</jsp:root>
