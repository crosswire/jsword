
<jsp:include page="header.jsp">
  <jsp:param name="title" value="JSword - API Primer" />
</jsp:include>

<h1>API Primer</h1>

<h2>How do I obtain Bible text?</h2>
<p>The short version:</p>
<pre>
    Project.init();
    Bible b = Defaults.getBibleMetaData().getBible();
    Passage p = PassageFactory.createPassage("Eph 1:18");
    BibleData bd = b.getData(p);
    String s = OSISUtil.getPlainText(bd);
</pre>
<p>
  Now this is over simplified in 2 ways. Firstly it assumes you always only 
  want the default Bible, and secondly it only gets you plain text - since 
  we are using OSIS you would probably want something better. </p> 
</p>

<h2>How do I select a Bible other than the default?</h2>
<p>
  Books is the interface to all Bibles/Dictionaries/Commentaries, and 
  you need some way to tell it what you want. The sledgehammer approach 
  is.
</p>
<pre>
    List everything = Books.getBooks();
</pre>
<p>Or you can get just the Bibles:</p>
<pre>
    List bibles = Books.getBooks(BookFilters.getBibles());
</pre>
<p>
  BookFilters has a selection of commonly used filters, or you can write 
  your own filter (see BookFilter)
</p>
<p>
  Now all of the above get you a (list of) BibleMetaData objects from 
  which you get the real Bible. A BibleMetaData object will tell you everything 
  about the Bible but it will not read any data, to get the real data 
  you need to call getBible().
</p>

<h2>How do I get something better than plain text?</h2>
<p>The short version would be something like:</p>
<pre>
    SAXEventProvider provider = OSISUtil.getSAXEventProvider(bd);
    Style style = new Style("swing");
    String html = style.applyStyleToString(provider, "simple.xsl");
</pre>
<p>
  If you have a look in the resource directory you should see where the 
  stylesheets are, so you're not limited to HTML. 
</p>

<h2>How do I get the Commentary for a verse?</h2>
<p>
  There is an inheritance hierarchy; Book is at the top, and from this, Bible, Dictionary 
  and Commentary are derived. The Books class will get you any of the 
  above - you just need to give it a different BookFilter. From then on 
  just continue as you would for a Bible. Dictionaries work in a similar 
  way.
</p>

<h2>Something to be aware of</h2>
<p>
  Caveat - The current CVS has full support for Sword modules except that Commentary 
  and Dictionary support is being finished as we speak, so don't expect 
  wonders just yet. 2nd Caveat - I don't expect the API above to change 
  much, but we've not got to 1.0 yet so I'm not making any promises.
</p>

<p>&nbsp; </p>

<jsp:include page="footer.jsp" />
