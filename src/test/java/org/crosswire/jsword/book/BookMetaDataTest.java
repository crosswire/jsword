/**
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
package org.crosswire.jsword.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class BookMetaDataTest {
    @Test
    public void testDifferentInitialsNotEqual() {
        String kjvMetaData = "[KJV]\nDataPath=./modules/texts/ztext/kjv/\nModDrv=zText\nEncoding=UTF-8\nBlockType=BOOK\nCompressType=ZIP\nSourceType=OSIS\nLang=en\nVersion=2.3\nDescription=King James Version (1769) with Strongs Numbers and Morphology\nLCSH=Bible. English.\n";
        // the only difference is the initials
        String kjvaMetaData = "[KJVA]\nDataPath=./modules/texts/ztext/kjva/\nModDrv=zText\nEncoding=UTF-8\nBlockType=BOOK\nCompressType=ZIP\nSourceType=OSIS\nLang=en\nVersion=2.3\nDescription=King James Version (1769) with Strongs Numbers and Morphology\nLCSH=Bible. English.\n";
        try {
            BookMetaData bmKJV = new SwordBookMetaData(kjvMetaData.getBytes(), "KJV");
            BookMetaData bmKJV2 = new SwordBookMetaData(kjvMetaData.getBytes(), "KJV");
            BookMetaData bmKJVA = new SwordBookMetaData(kjvaMetaData.getBytes(), "KJVA");
            Assert.assertTrue("Same metadata should equal", bmKJV.equals(bmKJV2));
            Assert.assertFalse("Different initials should not equal", bmKJV.equals(bmKJVA));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BookException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSortOrderIsByInitials() {
        String kjvMetaData = "[KJV]\nDataPath=./modules/texts/ztext/kjv/\nModDrv=zText\nEncoding=UTF-8\nBlockType=BOOK\nCompressType=ZIP\nSourceType=OSIS\nLang=en\nVersion=2.3\nDescription=King James Version (1769) with Strongs Numbers and Morphology\nLCSH=Bible. English.\n";
        // Desc starts with 'The...' but initials are 'Common' so comes first if sorted by initials
        String commonMetaData = "[Common]\nDataPath=./modules/texts/ztext/common/\nModDrv=zText\nEncoding=UTF-8\nBlockType=BOOK\nCompressType=ZIP\nSourceType=OSIS\nLang=en\nVersion=2.3\nDescription=The Common Edition: New Testament\nLCSH=Bible. English.\n";
        // Ensure sort is case insensitive
        String aaaMetaData = "[AAA]\nDataPath=./modules/texts/ztext/common/\nModDrv=zText\nEncoding=UTF-8\nBlockType=BOOK\nCompressType=ZIP\nSourceType=OSIS\nLang=en\nVersion=2.3\nDescription=aaa aaa aaa\nLCSH=Bible. English.\n";
        try {
            // create some book meta data
            BookMetaData bmKJV = new SwordBookMetaData(kjvMetaData.getBytes(), "KJV");
            BookMetaData bmCommon = new SwordBookMetaData(commonMetaData.getBytes(), "Common");
            BookMetaData bmaaa = new SwordBookMetaData(aaaMetaData.getBytes(), "AAA");

            // sort them
            List<BookMetaData> mdList = new ArrayList<BookMetaData>();
            mdList.add(bmaaa);
            mdList.add(bmKJV);
            mdList.add(bmCommon);
            Collections.sort(mdList);

            // ensure the book order is as expected
            Assert.assertEquals("AAA should be first in sorted book list", bmaaa, mdList.get(0));
            Assert.assertEquals("Common should be second in sorted book list", bmCommon, mdList.get(1));
            Assert.assertEquals("KJV should be last in sorted book list", bmKJV, mdList.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BookException e) {
            e.printStackTrace();
        }
    }

}
