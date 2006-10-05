package org.crosswire.jsword.passage;

import junit.framework.TestCase;

public class AccuracyTypeTest extends TestCase
{

    public AccuracyTypeTest(String s)
    {
        super(s);
    }

    public void testFromText_onePartInvalidBook() throws Exception
    {
        try
        {
            AccuracyType.fromText("10", new String[] {"10"}, null, null); //$NON-NLS-1$//$NON-NLS-2$
        }
        catch (NoSuchVerseException nsve)
        {
            // expected
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException"); //$NON-NLS-1$
        }

    }

    public void testFromText_TooManyParts()
    {
        boolean caught = false;
        try
        {
            AccuracyType.fromText("1:2:3:4", new String[] {"1", "2", "3", "4"}, null, null); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }
        catch (NoSuchVerseException nsve)
        {
            NoSuchVerseException correctException = new NoSuchVerseException(Msg.VERSE_PARTS, new Object[] {"1:2:3:4, 1, 2, 3, 4"});  //$NON-NLS-1$
            assertEquals("Unexpected exception message", correctException.getMessage(), nsve.getMessage()); //$NON-NLS-1$
            caught = true;
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException"); //$NON-NLS-1$
        }
        
        if (!caught){
            fail("Expected fromText to throw an exception when passed too many parts"); //$NON-NLS-1$
        }
    }

    public void testFromText_ThreePartsInvalidBook()
    {
        boolean caught = false;
        try
        {
            AccuracyType.fromText("-1:2:3", new String[] {"-1", "2", "3"}, null, null); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        catch (NoSuchVerseException nsve)
        {
            NoSuchVerseException correctException = new NoSuchVerseException(Msg.VERSE_PARTS, new Object[] {"-1:2:3, -1, 2, 3"});  //$NON-NLS-1$
            assertEquals("Unexpected exception message", correctException.getMessage(), nsve.getMessage()); //$NON-NLS-1$
            caught = true;
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {
            fail("ArrayIndexOutOfBoundsException caught, expecting NoSuchVerseException"); //$NON-NLS-1$
        }

        if (!caught){
            fail("Expected fromText to throw an exception when passed three parts with an invalid book"); //$NON-NLS-1$
        }
}
    
}
