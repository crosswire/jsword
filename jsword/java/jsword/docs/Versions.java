
package docs;

/**
This details the versions and planned features.

<h3>Stuff I'm working on</h3>

<p>This is a quick list to help me think of the changes that I plan so
that my coding now will make implementing them easier. For this reason
some of them are a little cryptic. Sorry:</p>

<pre>

Bugs:
    Serious:
        Next task is left blank
        Press go for next n verses does not show
        TaskPane gives can't have more than 1 root on a DOM

    Minor:
        Verifier doesn't work
        Oxford.xsl gives The element type "xsl:if" must be terminated by the matching end-tag ""
        Source servlet fails on com.sun.java because directory is called java!
        Web servlet Applet page fails with "&nbsp; undefined" or something.
        Switch all testing to use JUnit

    Cosmetic:
        Make all documentation XHTML
        bible.book.jdbc: maher-shalal-hash-baz search fails
        bible.book.raw: gets some punctuation incorrectly

Enhancements:
    Web:
        bible.view.servlet.Page: History in combo box
        bible.view.servlet.Page: Version selection

    Tools:
        bible.view.swing.beans.VersionGenerator: multiple-serial-generation

    Strongs:
        bible.book.raw.RawUtil.isNewPara(): re-write using SectionEle and RefEle
        bible.book.raw: implement StudyBible
        bible.book.ser: implement StudyBible
        bible.book.jdbc: implement StudyBible

    Remote:
        bible.book.remote.RemoteBook
        bible.view.orb (rmi)
        bible.view.orb (corba)
        bible.view.awt: Base stuff for applets etc.

    Mapper:
        Better AntiGrav rule
        Line straightener rule
        Speed-up
        Printing
        Debug VBAExport with non-default box sizes
        Frame Icons
        Consider chapter simplification

    Office:
        bible.view.office: Insert styled text

    Control:
        bible.control.Dictionary.getConnectedWords: Create interface
        Thesaurus

    Books:
        bible.book.gbf.GBFBible
        bible.book.sword.SwordBible
        bible.book.raw: Incremental d/l
        bible.book.olb.OLBBible
        bible.book.thml.THMLBook
        Lexicons etc

    DoE:
        Distributed notes
        Contrib system
        Dictionary/Thesarus sources:
            http://www.dict.org
            http://www.cogsci.princeton.edu/~wn/
            http://web.cs.city.ac.uk/text/roget/thesaurus.html
            http://humanities.uchicago.edu/biblio/roget_headwords.html
            http://www.plumbdesign.com/thesaurus/
            http://humanities.uchicago.edu/homes/MICRA/

    JavaDoc:
        Integrate with JDK 1.2.2 / JDK 1.3
        Remove files and work using inheritence

    Various:
        swing: make windows pop to the front on load
        swing: why does ALT+Space not work?
        swing: LookAndFeelChoices does not need window registering?
        I18N
        bible.passage.Passage.blur(): recognise RESTRICT_BOOK
</pre>

<h3>Change History</h3>

<p>The following is the broad outline project plan.
<ul>
  <li><strong>Version 0.80</strong> - SWORD1 - Troy just did a call
    for Java Bible programs from which to make jsword. There have
    been many updates - Logging API updates, a new GUI framework and
    work on the WordNet thesaurus.

  <li><strong>Version 0.75</strong> - THE MAP - New Mapper
    application, lots of tuning, tweaks and fixes.

  <li><strong>Version 0.73</strong> - THE MUTE - Lots of tweaks to speed
    things up, history bug fixed, new PassageTally functionality,
    SerBible is now the preferred format.

  <li><strong>Version 0.72</strong> - THE SHOUT 2 -
    New lib project, many fixes to servlets and Project.java, Source
    syntax colouring, Office interface revamp.

  <li><strong>Version 0.71</strong> - THE UNANNOUNCED -
    2nd major upload, minor bug fixes on version 0.7

  <li><strong>Version 0.7</strong> - THE ANNOUNCED -
    Lots of testing, tidying up, and organizing. The Book package has
    been majorly re-worked, and many of the packages have been sorted
    out.

  <li><strong>Version 0.6</strong> - THE SERVANT -
    New servlet interface. Lots of work on the config i/f lots of tidying
    up. New web pages, mail list and so on.

  <li><strong>Version 0.5</strong> - THE CABBAGE -
    Lots of changes - RawBible, Search.bestMatch(), SelfTestBase, Some
    I18N, Config. Some GUI work.

  <li><strong>Version 0.46</strong> - THE FOREIGN APPLET -
    Quick release as v0.45 with I18N for the Germans.

  <li><strong>Version 0.45</strong> - THE APPLET -
    Quick release to get together an applet for demo to Sword people.

  <li><strong>Version 0.4</strong> - THE MIX -
    Merged passage and dictionary. The dictionary package was shrinking,
    and being simplified, and the classes Passage and Strongs considered
    core. TallyBoard renamed PassageTally and made to implement Passage.
    Outstanding work - Events and Editable book names/I18N.

  <li><strong>Version 0.3</strong> - THE SHOUT -
    The search system wanted TallyBoard, and I revised Passage being a
    Collection at the same time. The only remaining functionality
    requests are Sorting out Events, and I18N. Some work on Version.
    Search - I'm beginning to like this design. I just added ( ) in a few
    hours without any major changes to the engine at all. I need to
    tighten up the docs and sort out GrammarParamWord.

  <li><strong>Version 0.2</strong> - THE PLATFORM -
    Passage - I need to spend some time on the other bits of the system.
    The TODO list for this is getting more and more streched. What is
    here is good though. Search - Been through about 100 different search
    Engine designs, and I'm still not happy. However a working engine is
    better than none at all.

  <li><strong>Version 0.1</strong> - THE TODDLER -
    Proof of concept. Quickly hacked-up GUI. Passage - About 80% of the
    stuff I plan for this library is complete. There are bugs. The most
    notable of which is that Jude 2 does not exist - you must use
    Jude&nbsp;1:2, and so on. Included is full JavaDoc and a fairly
    exhaustive SelfTest module. Search - very hacked up and needs
    re-writing.

</ul>

<h3>Old Notes</h3>
Historically we had 2 packages passage and source that had central
classes called different things. It is better if we use up less
words and help people to find the central class in a package. The
central class in the passage package was called Reference. Reference is
poor becuase is clashes with java.lang.Reference in JDK1.2, and because
a reference is generally a pointer to only one thing, whereas our
Reference is a pointer to many things, so I renamed Reference to
Passage.
I think that Version is a better name that source, though I do not have
so much of a good justification for this. Eventually I renamed it to Book
although right now the central interface it Bible, the plan it to have
Bible inherit from Book.<br>

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
public class Versions { }

