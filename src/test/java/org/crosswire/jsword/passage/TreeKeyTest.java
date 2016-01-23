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
package org.crosswire.jsword.passage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class TreeKeyTest {

    private TreeKey pilgrimPart1;
    private TreeKey pilgrimPart1FirstStage;
    private TreeKey pilgrimPart1FirstStageClone;
    private TreeKey pilgrimPart2FirstStage;

    @Before
    public void setUp() throws Exception {
        pilgrimPart1 = new TreeKey("Part 1");
        pilgrimPart1FirstStage = new TreeKey("First Stage", pilgrimPart1);
        pilgrimPart1FirstStageClone = new TreeKey("First Stage", new TreeKey("Part 1"));
        pilgrimPart2FirstStage = new TreeKey("First Stage", new TreeKey("Part 2"));
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertTrue(pilgrimPart1FirstStage.equals(pilgrimPart1FirstStageClone));
        Assert.assertFalse(pilgrimPart1FirstStage.equals(pilgrimPart2FirstStage));
        Assert.assertFalse(pilgrimPart1FirstStage.equals(null));
        Assert.assertFalse(pilgrimPart1FirstStage.equals(pilgrimPart1));
        Assert.assertTrue(pilgrimPart1FirstStage.getParent().equals(pilgrimPart1));
    }

}
