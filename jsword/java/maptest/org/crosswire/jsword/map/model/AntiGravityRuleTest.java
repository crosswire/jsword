
package org.crosswire.jsword.map.model;

import org.crosswire.jsword.map.model.AntiGravityRule;

import junit.framework.TestCase;

/**
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class AntiGravityRuleTest extends TestCase
{

    /**
     * Constructor for AntiGravityRuleTest.
     * @param arg0
     */
    public AntiGravityRuleTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AntiGravityRuleTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testAddDistanceToTotals() throws Exception
    {
        float[] totals = new float[] { 0.0F, 0.0F };
        float[] that = null;
        float[] us = null;

        that = new float[] { 0.1F, 0.1F };
        us = new float[] { 0.2F, 0.2F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.067666754F, 0.000001F);
        assertEquals(totals[1], -0.067666754F, 0.000001F);

        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.135333508F, 0.00001F);
        assertEquals(totals[1], -0.135333508F, 0.00001F);

        that = new float[] { 0.3F, 0.3F };
        us = new float[] { 0.2F, 0.2F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], 0.067666754F, 0.000001F);
        assertEquals(totals[1], 0.067666754F, 0.000001F);

        that = new float[] { 0.1F, 0.3F };
        us = new float[] { 0.2F, 0.2F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.067666754F, 0.0001F);
        assertEquals(totals[1], 0.067666754F, 0.0001F);

        that = new float[] { 0.1F, 0.1F };
        us = new float[] { 0.3F, 0.3F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.007497886F, 0.005F);
        assertEquals(totals[1], -0.007497886F, 0.005F);
    }

    public void testGetNewDist() throws Exception
    {
        //*
        for (float i=-1f; i<1f; i=i+0.01f)
        {
            System.out.println("f("+i+")="+AntiGravityRule.getNewDistance(i));
        }
        // */

        assertTrue(AntiGravityRule.getNewDistance(-1F) < 0.0001F);
        assertTrue(AntiGravityRule.getNewDistance(-1F) > 0F);

        assertTrue(AntiGravityRule.getNewDistance(0F) > 0.49999F);
        assertTrue(AntiGravityRule.getNewDistance(0F) < 0.50001F);

        assertTrue(AntiGravityRule.getNewDistance(1F) < 0.0001F);
        assertTrue(AntiGravityRule.getNewDistance(1F) < 0F);
    }
}
