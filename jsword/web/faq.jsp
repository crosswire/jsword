
<jsp:include page="header.jsp">
  <jsp:param name="title" value="JSword - FAQ" />
</jsp:include>

<h1>Frequently Asked/Answered Questions</h1>

<h3>What is the status of JSword?</h3>
<p>Currently the core Bible view functions work fine, and we are working 
on completing Sword module compatibility. There are no official releases 
just yet, but we hope there will be one before too long, however it 
is available in CVS right now.</p>

<h3>How similar is the Java Sword API to the C++ Sword API?</h3>
<p>It depends on your perspective! The core Java API is similar in that 
there are concepts like Key, Verse etc in both. However there are differences; 
In Java Verse is stateless but the C++ SWVerse is stateful. For those 
wanting to write Java code using the C++ API there is an API compatibility 
tree (under development) that calls the core tree. This compatibility 
tree tries to mimic the C++ wherever possible.</p>

<h3>Why not make the C++ Sword API the primary API?</h3>
<p>There are a number of problems with language independant APIs. They 
all risk falling into the same traps as CORBA, DOM and SAX - all good 
examples of how *not* to write an API, and hence the need for RMI/EJB, 
JDOM/XOM/DOM4J/JAXB/etc.<br>
An example for our case would be statefulness. Should the low level 
APIs for Verse etc be stateful? In C++/Sword they are, and there is 
sense for this in C++ because operator overloading makes incrementing 
a verse easy. Java however has a powerful concept of immutability that 
does not seem to be as important in C++, so a Java string is guaranteed 
not to change, where C++ lets you cast away any protection. Immutability 
lets us return values without cloning them, and be sure that they will 
not change, and it lets them have an ordering in a list that otherwise 
would be meaningless.<br>
</p>

<jsp:include page="footer.jsp" />
