<%@ page %>

<html>
<head>
  <title>JSword - Getting Involved</title>
  <meta name="keywords" content="bible, free, software, christian, study, gpl, bible software,">
  <meta http-equiv="keywords" content="bible, free, software, christian, study, gpl, bible software,">
  <meta name="distribution" content="global">
  <link rel="stylesheet" href="sword.css" type="text/css">
</head>

<body>

<%@ include file="header.jsp" %>

<h1>Getting Involved</h1>

                  
            <h2>CVS Access</h2>
                  
            <p>There is a mailing list at <a href="mailto:jsword-devel@crosswire.org">jsword-devel@crosswire.org</a>. 
              To join, please fill out your email address in on <a href="http://www.crosswire.org/mailman/listinfo/jsword-devel ">this</a> 
              form.</p>
            <p> CVS is also hosted at crosswire. The CVS root is:<br />
              &nbsp;&nbsp;&nbsp;&nbsp;<code>:pserver:anonymous@cvs.crosswire.org:/cvs/jsword</code> 
              <br />
              If you need to get at the code but are not sure about CVS then join 
              the mailing list and we'll sort something out.
            <h2>Documentation</h2>
            <table width="100%" cellpadding="5" cellspacing="0">
              <tr> 
                <td><a href="intro.jsp">Introduction</a></td>
                <td>This is document describes the aims of the project, and has a 
                  list of the contributors, and some comments on contributing code. 
                </td>
              </tr>
              <tr> 
                <td><a href="design.jsp">Design</a></td>
                <td> This is overview documentation that explains how the Book and 
                  Bible interfaces are arranged and justifies the design decisions. 
                  Note that most of what is documented here is current for JSword 
                  - there are a few sections that reflect where we will be given a 
                  bit more re-factoring. </td>
              </tr>
              <tr> 
                <td><a href="change.jsp">Changes</a></td>
                <td> A list of the changes that have taken place in each of the released 
                  versions. </td>
              </tr>
              <tr> 
                <td><a href="xml.jsp">XML Layout</a></td>
                <td> This is some example XML that could have been produced by the 
                  Book package. </td>
              </tr>
              <tr> 
                <td><a href="api/docs/Licence.html">Licence</a></td>
                <td> JSword is distributed under the General Public Licence. A copy 
                  of this licence is stored within the JavaDoc here. </td>
              </tr>
            </table>
            <h2>Generated Documentation</h2>
            <table width="100%" cellpadding="5" cellspacing="0">
              <tr> 
                <td><a href="api/index.html">JavaDoc</a></td>
                <td>Built from the source-code - all the low-level documentation.</td>
              </tr>
              <tr> 
                <td><a href="java2html/index.html">Java&nbsp;Source</a></td>
                <td>A copy of the source-code colourized and made very navigable using 
                  hyperlinks in a JavaDoc like frameset.</td>
              </tr>
              <tr> 
                <td><a href="test/index.html">Test&nbsp;Results</a></td>
                <td>Summary of all the test results, including % fail rate.</td>
              </tr>
            </table>
<br>

<!--
<table width="100%" cellpadding="5" cellspacing="0">
  <tr> 
    <th colspan="2">Recent Build Logs <small>[At Crosswire]</small></th>
  </tr>
  <tr> 
    <td><a href="logs/lastlog.txt">Build&nbsp;Log</a></td>
    <td>The output from the last ant build done at crosswire.</td>
  </tr>
</table>
<br>
-->

<!--
<table width="100%" cellpadding="5" cellspacing="0">
  <tr> 
    <th colspan="2">Live Preview <small>[TODO]</small></th>
  </tr>
  <tr> 
    <td><a href="/jsword">Homepage</a></td>
    <td>Users general start point. (Currently not working)</td>
  </tr>
</table>
<br>
-->

<%@ include file="footer.jsp" %>

</body>
</html>
