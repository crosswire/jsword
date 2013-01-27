/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import java.util.Locale;

import junit.framework.TestCase;

/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BibleNamesTest extends TestCase {

    /**
     * @param name
     */
    public BibleNamesTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadEnglish() {
        new BibleNames(Locale.ENGLISH);
    }

    public void testLoadAF() {
        new BibleNames(new Locale("af"));
    }

    public void testLoadEgyptianArabic() {
        new BibleNames(new Locale("ar", "EG"));
    }

    public void testLoadBG() {
        new BibleNames(new Locale("bg"));
    }

    public void testLoadCS() {
        new BibleNames(new Locale("cs"));
    }

    public void testLoadCY() {
        new BibleNames(new Locale("cy"));
    }

    public void testLoadDanish() {
        new BibleNames(new Locale("da"));
    }

    public void testLoadGerman() {
        new BibleNames(new Locale("de"));
    }

    public void testLoadSpanish() {
        new BibleNames(new Locale("es"));
    }

    public void testLoadET() {
        new BibleNames(new Locale("et"));
    }

    public void testLoadFarsi() {
        new BibleNames(new Locale("fa"));
    }

    public void testLoadFinnish() {
        new BibleNames(new Locale("fi"));
    }

    public void testLoadFO() {
        new BibleNames(new Locale("fo"));
    }

    public void testLoadFrench() {
        new BibleNames(new Locale("fr"));
    }

    public void testLoadHebrew() {
        new BibleNames(new Locale("he"));
    }

    public void testLoadHU() {
        new BibleNames(new Locale("hu"));
    }

    public void testLoadID() {
        new BibleNames(new Locale("id"));
    }

    public void testLoadIN() {
        new BibleNames(new Locale("in"));
    }

    public void testLoadItalian() {
        new BibleNames(new Locale("it"));
    }

    public void testLoadIW() {
        new BibleNames(new Locale("iw"));
    }

    public void testLoadKO() {
        new BibleNames(new Locale("ko"));
    }

    public void testLoadLA() {
        new BibleNames(new Locale("la"));
    }

    public void testLoadLT() {
        new BibleNames(new Locale("lt"));
    }

    public void testLoadNB() {
        new BibleNames(new Locale("nb"));
    }

    public void testLoadDutch() {
        new BibleNames(new Locale("nl"));
    }

    public void testLoadNN() {
        new BibleNames(new Locale("nn"));
    }

    public void testLoadPL() {
        new BibleNames(new Locale("pl"));
    }

    public void testLoadBrazillianPortuguese() {
        new BibleNames(new Locale("pt", "BR"));
    }

    public void testLoadPortuguese() {
        new BibleNames(new Locale("pt"));
    }

    public void testLoadRo() {
        new BibleNames(new Locale("ro"));
    }

    public void testLoadRU() {
        new BibleNames(new Locale("ru"));
    }

    public void testLoadSK() {
        new BibleNames(new Locale("sk"));
    }

    public void testLoadSL() {
        new BibleNames(new Locale("sl"));
    }

    public void testLoadSwedish() {
        new BibleNames(new Locale("sv"));
    }

    public void testLoadThai() {
        new BibleNames(new Locale("th"));
    }

    public void testLoadTR() {
        new BibleNames(new Locale("tr"));
    }

    public void testLoadUkranian() {
        new BibleNames(new Locale("uk"));
    }

    public void testLoadVietnamese() {
        new BibleNames(new Locale("vi"));
    }

    public void testLoadChineseTraditional() {
        new BibleNames(new Locale("zh", "CN"));
    }

    public void testLoadChineseSimplified() {
        new BibleNames(new Locale("zh"));
    }
    

    public void testLoadSwahili() {
        new BibleNames(new Locale("sw"));
    }
}
