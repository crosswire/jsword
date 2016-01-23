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
 * The Book package provides an interface to a real store of data.
 * Different sources of data interact with the rest of the code via a 
 * <code>Book</code> interface. An implementation of <code>Book</code> is
 * expected to be able to retrieve Biblical text for a given
 * <code>Passage</code>, and to return a <code>Passage</code> of verses that
 * contain a word.
 * 
 * <p>A Bible is a specialization of a Book that uses Passages as basic data
 * types.</p>
 * 
 * <p>The following is out of date since the Version -&gt; Book/Bible split</p>
 * 
 * <p>There are several specializations of the <code>Book</code> interface.
 * A <code>MutableBook</code> is-a <code>Book</code> that can be changed
 * either to accommodate Verse based notes or for a normal <code>Book</code>
 * that can be created at run-time. The latter option allows this system to
 * be installed on a computer that already has OLB installed without
 * requiring a download of the complete Bible source, but the source is
 * created either as part of the install or on the fly, and cached here.</p>
 * 
 * <p>Secondly a <code>Translation</code> is a <code>Book</code> that
 * understands Strong's numbering and can give information about how words are
 * translated to and from the original.</p>
 * 
 * <p>Currently there are 2 basic implementations of the <code>Book</code>
 * interface - an <code>ODBCBook</code> and a <code>FileBook</code>. The
 * former acts on an Access database via JBDC and ODBC. The latter on an
 * experimental serialized data store. Probably neither of these
 * implementations are of any long term use, however they do help get me up
 * to speed quickly.</p>
 * 
 * <p>I envisage at least 2 more production stores - an
 * <code>OLBBook</code> that reads directly from OLB files exactly as
 * installed on Windows. (Maybe in reality there needs to be an
 * <code>OLB7Book</code> and an <code>OLB8Book</code> or something)
 * and a <code>RemoteBook</code> that uses RMI / CORBA / HTTP or whatever
 * to retrieve the data from a remote server)</p>
 * 
 * <p>It is important that multiple data sources are available, to allow a
 * compact download time. In an ideal world the base download, would be
 * small enough to place as an applet on a web page, with some kind of
 * &quot;Save as application functionality&quot; that still uses a remote
 * data server. A caching Book could then store verses locally, only
 * using the network for verses not retrieved yet.</p>
 * 
 * <p>The Biblical text is returned to the application as an XML document.
 * Allowing <code>Book</code>s to contain data like red-letter markup
 * that a display module may not want to use. See the display section for
 * more details.</p>
 * 
 * <p>More work is needed here to with Strong's numbers and non-Biblical text
 * sources. (Lexicons and such like)</p>
 * 
 * 
 * <h3>Startup</h3>
 * 
 * <p>The startup procedure goes like this. When <code>Books</code> is first called
 * it looks up the implementations of BookDriver (using Project.resource()) and
 * calls registerDriver() for each. This calls getBooks() on the new BookDriver
 * which should return an array of BookMetaData objects.</p>
 * 
 * <p>Ideally when the BookDriver creates a BookMetaData object it should also
 * create a Book to go with it, and do everything it can to ensure that future reads
 * from the Book will be Exception free, but without consuming significant system
 * resources. So the process of constructing a BookMetaData and associated Book
 * should check that the index file exists and is readable, but not actually load
 * it. BookDriver is allowed to complain that it can't do everything it wants, but
 * it should not hand BookMetaData objects back that are faulty.</p>
 * 
 * <p>This means that the user can browse through the BookMetaData objects without
 * causing any stress to the system, and yet be fairly assured that when they do
 * call getBook() on the BookMetaData that everything will work fine.</p>
 * 
 * <p>Finally when BookMetaData.getBook() is called, it should ask the Book to load
 * any system resources that are needed to fulfill the requests.</p>
 * 
 * 
 * <h3>Caching</h3>
 * 
 * <p>We used to have a CacheingBookDriver (now deleted) that was designed to allow
 * data to be cached as it is read. Maybe at some stage we should add it back in
 * again, but we would need to make Drivers writable before we do that.</p>
 * 
 * <p>The designed features of CacheingBookDriver are:
 * <ul>
 *   <li>Ability to cache multiple (possibly remote) sources</li>
 *   <li>Use of JDBC style URL to help cached data to re-connect with source</li>
 *   <li>Can be used with multiple caching schemes (sword, ser, ...)</li>
 * </ul> 
 * 
 * <p>So it is up to the actual caching scheme to distinguish cached data from
 * other normal data, and to be able to return a original source URL in case
 * not all of the data is present.</p>
 */
package org.crosswire.jsword.book;
