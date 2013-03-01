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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookMetaDataTest extends TestCase {
    public BookMetaDataTest(String s) {
        super(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() {
    }

    public void testVersion() {
        /*
         * log("VersionFactory.getVersion(String, String)"); Version niv1 =
         * VersionFactory.getVersion("New International Version","Original");
         * Version niv2 =
         * VersionFactory.getVersion("New International Version","Anglicized");
         * Version niv3 =VersionFactory.getVersion("New International Version",
         * "Inclusive Language"); Version av =
         * VersionFactory.getVersion("King James Version","");
         * test(VersionFactory
         * .getVersion("New International Version","Original"), niv1);
         * test(VersionFactory
         * .getVersion("New International Version","Original"), niv1);
         * test(VersionFactory
         * .getVersion("New International Version","Anglicized"), niv2);
         * test(VersionFactory.getVersion("King James Version",""), av);
         * 
         * log("VersionFactory.getVersion(String)");
         * test(VersionFactory.getVersion("New International Version,Original"),
         * niv1);
         * test(VersionFactory.getVersion("New International Version, Original"
         * ), niv1);
         * test(VersionFactory.getVersion("New International Version, Original"
         * ), niv1);
         * test(VersionFactory.getVersion("New International Version, Anglicized"
         * ), niv2);
         * test(VersionFactory.getVersion("New International Version,Anglicized"
         * ), niv2); test(VersionFactory.getVersion("King James Version"), av);
         * test(VersionFactory.getVersion("King James Version, "), av);
         * test(VersionFactory.getVersion("King James Version,  "), av);
         */

        /*
         * log("VersionFactory.decodeVersion(String, String)"); Version v1 =
         * VersionFactory.decodeVersion("a, b",
         * "initials,1 1 1990, PD, http://localhost/file.txt");
         * test(v1.getName(), "a"); test(v1.getEdition(), "b");
         * test(v1.getFirstPublished(), new Date(631152000000L)); // 1 1 1990
         * test(v1.getName(), "a,b"); test(v1.getInitials(), "initials");
         * test(v1.getLicense(), new URL("http://localhost/file.txt"));
         * test(v1.getOpenness(), Version.STATUS_PD); v1 =
         * VersionFactory.decodeVersion("a", "i, ,XXX, "); test(v1.getName(),
         * "a"); test(v1.getEdition(), ""); test(v1.getFirstPublished(), null);
         * test(v1.getName(), "a"); test(v1.getInitials(), "i");
         * test(v1.getLicense(), null); test(v1.getOpenness(),
         * Version.STATUS_UNKNOWN); v1 = VersionFactory.decodeVersion("abcabc",
         * " , , , "); test(v1.getName(), "abcabc"); test(v1.getEdition(), "");
         * test(v1.getFirstPublished(), null); test(v1.getName(), "abcabc");
         * test(v1.getInitials(), "a"); test(v1.getLicense(), null);
         * test(v1.getOpenness(), Version.STATUS_UNKNOWN);
         * 
         * log("VersionFactory.decodeStatus(String)");
         * test(VersionFactory.decodeStatus("PD"), Version.STATUS_PD);
         * test(VersionFactory.decodeStatus(" PD"), Version.STATUS_PD);
         * test(VersionFactory.decodeStatus("PD "), Version.STATUS_PD);
         * test(VersionFactory.decodeStatus(" PD "), Version.STATUS_PD);
         * test(VersionFactory.decodeStatus(" pd "), Version.STATUS_PD);
         * test(VersionFactory.decodeStatus(" commercial "),
         * Version.STATUS_COMMERCIAL);
         * test(VersionFactory.decodeStatus(" copyable "),
         * Version.STATUS_COPYABLE); test(VersionFactory.decodeStatus("free"),
         * Version.STATUS_FREE); test(VersionFactory.decodeStatus("unknown"),
         * Version.STATUS_UNKNOWN); test(VersionFactory.decodeStatus(""),
         * Version.STATUS_UNKNOWN); test(VersionFactory.decodeStatus("XXX"),
         * Version.STATUS_UNKNOWN);
         * 
         * log("VersionFactory.getName(String)");
         * test(VersionFactory.getName("a,b"), "a");
         * test(VersionFactory.getName(" a , b "), "a");
         * test(VersionFactory.getName(" ab , b "), "ab");
         * test(VersionFactory.getName(" ab "), "ab");
         * 
         * log("VersionFactory.getEdition(String)");
         * test(VersionFactory.getEdition("a,b"), "b");
         * test(VersionFactory.getEdition(" a , b "), "b");
         * test(VersionFactory.getEdition(" ab , b "), "b");
         * test(VersionFactory.getEdition(" ab "), "");
         * 
         * log("VersionFactory.getName(String, String)");
         * test(VersionFactory.getName("a", "b"), "a,b");
         * test(VersionFactory.getName("a", null), "a");
         * test(VersionFactory.getName("a", ""), "a");
         * test(VersionFactory.getName("a", " "), "a");
         */
    }
}
