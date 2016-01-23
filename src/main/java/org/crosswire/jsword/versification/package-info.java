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
  The core responsibility of the versification package is to understand verse references.
  It standardizes on OSIS book names and on a KJV versification. Each book in the KJV is
  numbered sequentially and each verse of the KJV is numbered sequentially.
</p>

<p>
  The primary abilities of this package are to flexibly understand Bible book names
  as might be found in a reference work or supplied by a user, to convert between
  these and OSIS book names and KJV book number.
</p>

<p>
  One can also request information concerning books of the Bible: The number of chapters
  or verses in a book, the number of verses in a particular chapter, the ordinal position
  of a verse in the KJV and so forth.
</p>
*/
package org.crosswire.jsword.versification;
