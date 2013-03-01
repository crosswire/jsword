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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import java.util.Locale;

import org.crosswire.jsword.versification.system.Versifications;

import junit.framework.TestCase;

/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public class BibleNamesTest extends TestCase {

    private Versification v11n;


    /**
     * @param name
     */
    public BibleNamesTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getDefaultVersification();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadEnglish() {
        new BibleNames(v11n, Locale.ENGLISH);
    }

    public void testLoadAF() {
        new BibleNames(v11n, new Locale("af"));
    }

    public void testLoadEgyptianArabic() {
        new BibleNames(v11n, new Locale("ar", "EG"));
    }

    public void testLoadBG() {
        new BibleNames(v11n, new Locale("bg"));
    }

    public void testLoadCS() {
        new BibleNames(v11n, new Locale("cs"));
    }

    public void testLoadCY() {
        new BibleNames(v11n, new Locale("cy"));
    }

    public void testLoadDanish() {
        new BibleNames(v11n, new Locale("da"));
    }

    public void testLoadGerman() {
        new BibleNames(v11n, new Locale("de"));
    }

    public void testLoadSpanish() {
        new BibleNames(v11n, new Locale("es"));
    }

    public void testLoadET() {
        new BibleNames(v11n, new Locale("et"));
    }

    public void testLoadFarsi() {
        new BibleNames(v11n, new Locale("fa"));
    }

    public void testLoadFinnish() {
        new BibleNames(v11n, new Locale("fi"));
    }

    public void testLoadFO() {
        new BibleNames(v11n, new Locale("fo"));
    }

    public void testLoadFrench() {
        new BibleNames(v11n, new Locale("fr"));
    }

    public void testLoadHebrew() {
        new BibleNames(v11n, new Locale("he"));
    }

    public void testLoadHU() {
        new BibleNames(v11n, new Locale("hu"));
    }

    public void testLoadID() {
        new BibleNames(v11n, new Locale("id"));
    }

    public void testLoadIN() {
        new BibleNames(v11n, new Locale("in"));
    }

    public void testLoadItalian() {
        new BibleNames(v11n, new Locale("it"));
    }

    public void testLoadIW() {
        new BibleNames(v11n, new Locale("iw"));
    }

    public void testLoadKO() {
        new BibleNames(v11n, new Locale("ko"));
    }

    public void testLoadLA() {
        new BibleNames(v11n, new Locale("la"));
    }

    public void testLoadLT() {
        new BibleNames(v11n, new Locale("lt"));
    }

    public void testLoadNB() {
        new BibleNames(v11n, new Locale("nb"));
    }

    public void testLoadDutch() {
        new BibleNames(v11n, new Locale("nl"));
    }

    public void testLoadNN() {
        new BibleNames(v11n, new Locale("nn"));
    }

    public void testLoadPL() {
        new BibleNames(v11n, new Locale("pl"));
    }

    public void testLoadBrazillianPortuguese() {
        new BibleNames(v11n, new Locale("pt", "BR"));
    }

    public void testLoadPortuguese() {
        new BibleNames(v11n, new Locale("pt"));
    }

    public void testLoadRo() {
        new BibleNames(v11n, new Locale("ro"));
    }

    public void testLoadRU() {
        new BibleNames(v11n, new Locale("ru"));
    }

    public void testLoadSK() {
        new BibleNames(v11n, new Locale("sk"));
    }

    public void testLoadSL() {
        new BibleNames(v11n, new Locale("sl"));
    }

    public void testLoadSwedish() {
        new BibleNames(v11n, new Locale("sv"));
    }

    public void testLoadThai() {
        new BibleNames(v11n, new Locale("th"));
    }

    public void testLoadTR() {
        new BibleNames(v11n, new Locale("tr"));
    }

    public void testLoadUkranian() {
        new BibleNames(v11n, new Locale("uk"));
    }

    public void testLoadVietnamese() {
        new BibleNames(v11n, new Locale("vi"));
    }

    public void testLoadChineseTraditional() {
        new BibleNames(v11n, new Locale("zh", "CN"));
    }

    public void testLoadChineseSimplified() {
        new BibleNames(v11n, new Locale("zh"));
    }
    

    public void testLoadSwahili() {
        new BibleNames(v11n, new Locale("sw"));
    }
}
