
package docs;

/**
Some hints, ideas and lesssons on coding for this project.

<h3>JDK Versions</h3>

<p>This is all currently being written in JDK 1.1 and not JDK 1.2.
1.1 has the advantage that it is available in more browsers and the MVM
that ships with IE4, Win95 OSR2.5, Win98 and WinNT is mostly JDK 1.1
compliant.
The things we are missing out on:<ul>
  <li>Collections, Swing (which can be downloaded for 1.1 separately)</li>
  <li>References, Classloader tweaks, Security (where we feel the pain)</li>
  <li>Other improvments. (can easily live without them)</li>
</ul>
Currently I feel that platform availability is more important than the
problems mentioned above.
</p>

<h3>Momentum</h3>

<p>CatB talked about getting people involved -
&quot;Release early and often&quot;, &quot;Mailing Lists&quot; and so on.
I plan to have a menu item &quot;Get involved ...&quot; This will present
a wizard thing to guide people to help. The ways of getting people
involved so far include:
<ul>
  <li>Writing help files</li>
  <li>Writing tips</li>
  <li>Bug reports</li>
  <li>Development requests/ideas</li>
  <li>Packaging offers</li>
  <li>Support/Mentoring other users</li>
  <li>Creating a live lexicon</li>
  <li>Reporting usage data</li>
</ul>

<h3>Coding Standards</h3>

<p>The coding standards used on this project so far are
very much based on the SUN coding standards. This is some notes on style we are
using, some of this deviates from the SUN standard, and some is just extensions
to it </p>

<h5>Packaging</h5>

<p>The is a split between classes that are specific to this project and those
that could be usefully employed elsewhere. Generally we should:
<ul>
  <li>Make a much code general as possible</li>
  <li>Only generalize where there is a point - specifically and code that does
      something Bible specific should not be general</li>
  <li>Never import specific code in generalized code</li>
</ul>
Generalized code exists in <code>org.crosswire</code>. Code specific to this
project lives in <code>org.crosswire.jsword</code>.</p>

<p>Also, since there is an MVC relationship between various parts of the code
we avoid importing <code>org.crosswire.jsword.view.*</code> in any other code,
and avoid importing <code>org.crosswire.jsword.control.*</code> in anything but
<code>org.crosswire.jsword.view.*</code></p>

<p>Also any code specific to an interface X should be in a package containing
x. For example all swing specific code is in a *swing* package. This helps us in
packaging code for a distribution, in excluding other interfaces.</p>

<h5>Bracket Indentation</h5>
<p>The "Sun Way" conserves screen space at the expense of readibility -
which given the cost of decent size monitors these days seems silly.
Most code I have seen, seems to ignore Sun and do it this way:<br>
<code>if (something)
<br>{
<br>&nbsp;&nbsp;&nbsp;&nbsp;func();
<br>}
</code></p>

<h5>Variable Naming</h5>
<p>Sun say use the same convention as for method names. I think this is
daft since it confuses method names and variable names. So I use
all lower case with underscores between words. I use word_count and not
wordCount. The Sun method makes some sense if you intend to use public
member variables, however that is generally not recommended behavior</p>

<h5>Class Ordering</h5>
<p>Variables are not important member of a class so why put them at the
top? I put them at the bottom.</p>

<h5>Tabbing</h5>
<p>A 4 space indents is a good thing, however using a tab character
instead of 4 spaces just causes problems when you want to print, open in
a different editor, or want to paste into an html doc using &lt;pre&gt;
tags. Everything will look exactly the same if you use spaces instead of
tabs.</p>

<h5>JavaDoc</h5>
<p>Is used extensively. I'm using the @version tag in a slightly
unconventional way to note the state of the class. A perfect class would
have an @version tag something like this <code>@version D9.I9.T9</code>.
In the real world, code like this will be rare.</p>
<ul>
  <li>Documentation status is defined like: &quot;Dn&quot; where n means
      - 0: no JavaDoc, 1: empty tags, 2-9 various degrees of
      completeness</li>
  <li>Test module status is defined like: &quot;Tn&quot; where n is a
      number - 0: no test module, 1: module exists but doesn't work, 2-9:
      varing degrees of completeness.</li>
  <li>Visual inspection status: &quot;In&quot; where n means: 0: no
      inspection to 9: thoroughly inspected.</li>
</ul>
<p>Another variation from the standard in the indentation which goes
something like:
<pre>
/**
* Stuff
*\
</pre>
I'm becoming unsure that this was originally a good idea - the original
justification was that it made indentation easier, however modern editors
make that untrue.</p>
<p>More importantly all the HTML markup in JavaDoc should be in XHTML
format so that it can be used in an XML engine.</p>

<h5>Code inclusion</h5>
<p>I am not excluding from the codebase code which I do not expect to be
in the final project - I think that we will need to consider how we do a
rollout, to get rid of the code that we never call. (the testing modules
could make automatic sortout more complex). My feeling now is - early
days, lets not get too harsh on amount of code in the codebase so long as
it is good quality.</p>

<h3>Lessons</h3>
<p><strong>XML</strong>: Where possible use DOM or SAX to access XML/XSL
implementations, where it is not possible separate the code into an
isolated class, and only do <code>import com.proprietoryvendor.xml.*;
</code> from within there.</p>

*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class WritingCode { }
