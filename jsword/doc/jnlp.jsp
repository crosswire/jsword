<%@ page %>

<html>
<head>
  <title>JSword - News</title>
  <meta name="keywords" content="bible, free, software, christian, study, gpl, bible software,">
  <meta http-equiv="keywords" content="bible, free, software, christian, study, gpl, bible software,">
  <meta name="distribution" content="global">
  <link rel="stylesheet" href="sword.css" type="text/css">
</head>

<body>

<%@ include file="header.jsp" %>

            <h1>Java WebStart</h1>
            <p>JSword is now available in a convenient <a href="http://java.sun.com/products/javawebstart/">WebStart</a> 
              package. You will need to have WebStart installed. On MacOSX that job 
              is done for you. It is an easy <a href="http://java.sun.com/products/javawebstart/">download</a> 
              on Windows since it comes with JDK 1.4.1. Once you have this installed 
              ... </p>
            <p align="center"><a href="jnlp/jsword.jnlp"><img src="images/webstart.jpg" width="247" height="60" border="0"></a></p>
            <p>The are some report of problems under Windows, and the download is 
              on the large side due to the Bible data that is currently included, 
              but we're working on those problems.</p>
            <%@ include file="footer.jsp" %>

</body>
</html>
