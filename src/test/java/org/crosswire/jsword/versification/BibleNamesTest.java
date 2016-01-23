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
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.versification;

import java.util.Locale;

import org.junit.Test;

/**
 * JUnit test.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BibleNamesTest {
    @Test
    public void testLoadEnglish() {
        BibleNames.instance().load(Locale.ENGLISH);
    }

    @Test
    public void testLoadAF() {
        BibleNames.instance().load(new Locale("af"));
    }

    @Test
    public void testLoadEgyptianArabic() {
        BibleNames.instance().load(new Locale("ar", "EG"));
    }

    @Test
    public void testLoadBG() {
        BibleNames.instance().load(new Locale("bg"));
    }

    @Test
    public void testLoadCS() {
        BibleNames.instance().load(new Locale("cs"));
    }

    @Test
    public void testLoadCY() {
        BibleNames.instance().load(new Locale("cy"));
    }

    @Test
    public void testLoadDanish() {
        BibleNames.instance().load(new Locale("da"));
    }

    @Test
    public void testLoadGerman() {
        BibleNames.instance().load(new Locale("de"));
    }

    @Test
    public void testLoadSpanish() {
        BibleNames.instance().load(new Locale("es"));
    }

    @Test
    public void testLoadET() {
        BibleNames.instance().load(new Locale("et"));
    }

    @Test
    public void testLoadFarsi() {
        BibleNames.instance().load(new Locale("fa"));
    }

    @Test
    public void testLoadFinnish() {
        BibleNames.instance().load(new Locale("fi"));
    }

    @Test
    public void testLoadFO() {
        BibleNames.instance().load(new Locale("fo"));
    }

    @Test
    public void testLoadFrench() {
        BibleNames.instance().load(new Locale("fr"));
    }

    @Test
    public void testLoadHebrew() {
        BibleNames.instance().load(new Locale("he"));
    }

    @Test
    public void testLoadHU() {
        BibleNames.instance().load(new Locale("hu"));
    }

    @Test
    public void testLoadID() {
        BibleNames.instance().load(new Locale("id"));
    }

    @Test
    public void testLoadIN() {
        BibleNames.instance().load(new Locale("in"));
    }

    @Test
    public void testLoadItalian() {
        BibleNames.instance().load(new Locale("it"));
    }

    @Test
    public void testLoadIW() {
        BibleNames.instance().load(new Locale("iw"));
    }

    @Test
    public void testLoadKO() {
        BibleNames.instance().load(new Locale("ko"));
    }

    @Test
    public void testLoadLA() {
        BibleNames.instance().load(new Locale("la"));
    }

    @Test
    public void testLoadLT() {
        BibleNames.instance().load(new Locale("lt"));
    }

    @Test
    public void testLoadNB() {
        BibleNames.instance().load(new Locale("nb"));
    }

    @Test
    public void testLoadDutch() {
        BibleNames.instance().load(new Locale("nl"));
    }

    @Test
    public void testLoadNN() {
        BibleNames.instance().load(new Locale("nn"));
    }

    @Test
    public void testLoadPL() {
        BibleNames.instance().load(new Locale("pl"));
    }

    @Test
    public void testLoadBrazillianPortuguese() {
        BibleNames.instance().load(new Locale("pt", "BR"));
    }

    @Test
    public void testLoadPortuguese() {
        BibleNames.instance().load(new Locale("pt"));
    }

    @Test
    public void testLoadRo() {
        BibleNames.instance().load(new Locale("ro"));
    }

    @Test
    public void testLoadRU() {
        BibleNames.instance().load(new Locale("ru"));
    }

    @Test
    public void testLoadSK() {
        BibleNames.instance().load(new Locale("sk"));
    }

    @Test
    public void testLoadSL() {
        BibleNames.instance().load(new Locale("sl"));
    }

    @Test
    public void testLoadSwedish() {
        BibleNames.instance().load(new Locale("sv"));
    }

    @Test
    public void testLoadThai() {
        BibleNames.instance().load(new Locale("th"));
    }

    @Test
    public void testLoadTR() {
        BibleNames.instance().load(new Locale("tr"));
    }

    @Test
    public void testLoadUkranian() {
        BibleNames.instance().load(new Locale("uk"));
    }

    @Test
    public void testLoadVietnamese() {
        BibleNames.instance().load(new Locale("vi"));
    }

    @Test
    public void testLoadChineseTraditional() {
        BibleNames.instance().load(new Locale("zh", "CN"));
    }

    @Test
    public void testLoadChineseSimplified() {
        BibleNames.instance().load(new Locale("zh"));
    }

    @Test
    public void testLoadSwahili() {
        BibleNames.instance().load(new Locale("sw"));
    }

}
