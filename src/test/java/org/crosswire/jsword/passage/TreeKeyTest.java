package org.crosswire.jsword.passage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TreeKeyTest {

    private TreeKey pilgrimPart1;
    private TreeKey pilgrimPart1FirstStage;
    private TreeKey pilgrimPart1FirstStageClone = new TreeKey("First Stage", new TreeKey("Part 1"));
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
        
        assertTrue(pilgrimPart1FirstStage.equals(pilgrimPart1FirstStageClone));
        assertFalse(pilgrimPart1FirstStage.equals(pilgrimPart2FirstStage));
        assertFalse(pilgrimPart1FirstStage.equals(null));
        assertFalse(pilgrimPart1FirstStage.equals(pilgrimPart1));
        assertTrue(pilgrimPart1FirstStage.getParent().equals(pilgrimPart1));
    }

}
