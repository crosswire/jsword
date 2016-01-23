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
  The Query package provides JSword with the ability to construct a search request against an Index.
  The specific syntax of a query is an implementation detail.
</p>
<h2>Query</h2>
<p>
  The search request consists of one of the following:
</p>
<ul>
  <li><code>Query</code>: search for a single atom, or</li>
  <li><code>BinaryQuery</code>: search consisting of a left query and a right query joined by an operator, or</li>
  <li><code>NullQuery</code>: search for nothing, returning nothing.</li>
</ul>
<p>
  Note: an atom is the smallest unit of search that can be handled directly by the index.
</p>
<p>
  The typical binary operators are:
</p>
<ul>
  <li><code>AndQuery</code>: All that is common between the left and the right.</li>
  <li><code>OrQuery</code>: All that is in both the left and the right.</li>
  <li><code>AndNotQuery</code>: All that is in the left minus what is in the right.</li>
</ul>
<p>
  The uncommon binary operators are:
</p>
<ul>
  <li><code>RangeQuery</code>: There are two types of ranges: within a and without.
    <ul>
      <li>Within: All the results that are <em>within</em> a Range of Keys.</li>
      <li>Without: All the results that are <em>outside</em> a Range of Keys.</li>
    </ul>
  </li>
  <li><code>BlurQuery</code>: Like an AndQuery except that the right query is first blurred by a requested amount.</li>
</ul>
<h2>Query Parsing</h2>
<p>
  The <code>QueryBuilder</code> takes a search request as a string and generates a Query from it.
  The primary characteristic of the Query builder is to determine the atoms of search and construct
  a Query appropriately.
</p>
<h2>Query Decorations</h2>
<p>
 Beyond the above queries, most modern query languages allow for specialized
 searching using notation that is peculiar to it. The <code>QueryDecorator</code>
 allows for phrases to be decorated in a way that is appropriate for the Index.
 The following are the decorations that are currently defined:
</p>
<ul>
  <li>Phrase decoration - find the phrase.</li>
  <li>Spelling decoration - find words that sound like or are similar to the one that is given</li>
  <li>Start With decoration - find words that start with the given words.</li>
  <li>All Words decoration - an AndQuery decorator</li>
  <li>Any Words decoration - an OrQuery decorator</li>
  <li>Not Words decoration - an AndNotQuery decorator</li>
  <li>Range decoration - decorates an AND range</li>
</ul>
*/
package org.crosswire.jsword.index.query;
