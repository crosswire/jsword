/*
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
/**
<p>
  The core responsibility of the passage package is to store and collate
  information about the structure of the Bible - The most important classes are
  <code>Passage</code>, <code>PassageTally</code> and <code>Strongs</code>. A
  <code>Passage</code> stores lists of verses, for example
  &quot;<em>Gen 1:1-5, 10</em>&quot;. <code>PassageTally</code> is similar but
  stores verses ordered by a tally against each verse. A <code>Strongs</code> 
  represents a Greek or Hebrew word as categorized by James Strong.
</p>


<h1>Passage</h1>
<p>
  A <code>Passage</code> is modeled after the JDK 2.0 <code>Collections</code>
  interface - so all the usual <code>add()</code>, <code>remove()</code> type
  actions are available. (A <code>PassageCollection</code> proxy class is
  available in order to treat a <code>Passage</code> exactly like a
  <code>Collection</code>)
</p>

<p>In addition to this a <code>Passage</code> will do:
<ul>
  <li>List blurring (for find X within 5 verses of Y type requests)</li>
  <li>
    List change notification, so you can register to receive notification of
    changes to a <code>Passage</code>. This will be of most use with a
    multi-threaded search engine.
  </li>
  <li>
    An understanding of VerseRanges (see below) for range counting and iteration
    (in addition to Verse counting etc)
  </li>
</ul>

<p>
  The <code>Passage</code> interface uses <code>Verse</code> and
  <code>VerseRange</code> in many of its methods. A <code>Verse</code> is
  obvious - a single Bible verse e.g. &quot;<em>Exo 2:4</em>&quot;, or
  &quot;<em>Jude 4</em>&quot;. A <code>VerseRange</code> has a start Verse and
  an end Verse e.g. &quot;<em>Exo 3:5-7</em>&quot;, or
  &quot;<em>Mat 25:1-Mar 2:4</em>&quot;.
</p>

<p>
  <code>Verse</code> and <code>VerseRange</code> have a superclass interface of
  <code>VerseBase</code>, and this interface is collected and sorted by
  <code>Passage</code> .So an example <code>Passage</code> is
  &quot;<em>Exo 2:4, 3:5-7, Mat 25:1-Mar 2:4, Jude 4</em>&quot;.
</p>

<p>
  The <code>Passage</code> interface is implemented by 3 concrete classes -
  <code>DistinctPassage</code> is a simple sorted collection of
  <code>Verse</code>s, <code>RangedPassage</code> is a sorted collection of
  <code>VerseRange</code>s, and <code>BitwisePassage</code> uses an array -
  essentially <code>boolean[31104]</code> to specify whether a verse is a member
  of the <code>Passage</code>. Obviously each of these implementations has
  different strengths, which the user should not need to be bothered with. So
  the <code>PassageFactory</code> class is responsible for creating
  <code>Passages</code> of a suitable type.
</p>

<h2>PassageTally</h2>

<p>
  The <code>PassageTally</code> class is-a to <code>Passage</code> however it's
  job is to store a rank to a <code>Verse</code>. This is for a best-match type
  application - &quot;find the verse that best matches these words&quot;. It is
  the intent to marry this with a Thesarus interface, because a gripe with the
  OLB is that I search for &quot;God &amp; loves &amp; world&quot; and expect to
  find John 3:16, but the search fails because John 3:16 uses the word
  &quot;loved&quot; and not &quot;loves&quot;.
</p>

<p>
  The final aim is a fuzzy matching scheme to I can search for
  &quot;God loves us and gave Jesus to save us&quot; and correctly be told
  John 3:16.
</p>

<h2>Strongs</h2>

<p>
  The <code>Strongs</code> class represents a Hebrew or Greek word, or a
  parsing number indicating the way the verse is aimed.
</p>

<h2>Other Stuff</h2>

<p>
  This package has a <code>SelfTest</code> class that is designed to stress
  every line of code in the rest of the package to make it bug-free. Otherwise
  this package is largely complete. I still need to inspect
  [Bitwise|Distinct|Ranged]Passage and PassageTally.
</p>
*/
package org.crosswire.jsword.passage;
