package org.crosswire.sword.keys;

/**
 * swkey.cpp - code for base class 'SWKey'.
 * SWKey is the basis for all types of keys for indexing into modules
 * (e.g. verse, word, place, etc.)
 */
public class SWKey implements Cloneable, Comparable
{
    public static final char KEYERR_OUTOFBOUNDS = 1;
    public static final char TOP = 1;
    public static final char BOTTOM = 2;

    String keytext;
    boolean persist;
    int error;

    /**
     * SWKey Constructor - initializes instance of SWKey.
     * ENT:	ikey - text key
     */
    public SWKey()
    {
        this(""); //$NON-NLS-1$
    }

    public SWKey(String ikey)
    {
        persist = false;
        keytext = ikey;
        error = 0;
    }

    public SWKey(SWKey k)
    {
        persist = k.persist;
        keytext = k.keytext;
        error = k.error;
    }

    public Object clone()
    {
        return new SWKey(this);
    }

    /**
     * SWKey::Persist - Gets whether this object itself persists within a module
     * that it was used to SetKey or just a copy: (true - persists in module;
     * false - a copy is attempted RET:	value of persist
     */
    public boolean isPersist()
    {
        return persist;
    }

    public boolean traversable()
    {
        return false;
    }

    /**
     * Sets whether this object itself persists within a module that it was used
     * to SetKey or just a copy: (1 - persists in module; 0 - a copy is
     * attempted ENT: ipersist - value which to set persist RET: value of
     * persist
     */
    public void setPersist(boolean ipersist)
    {
        persist = ipersist;
    }

    /**
     * SWKey::Error - Gets and clears error status.
     * RET:	error status
     */
    public int getError()
    {
        int retval = error;

        error = 0;
        return retval;
    }

    /**
     * SWKey::operator = Equates this SWKey to a character string.
     * 
     * ENT:	ikey - other swkey object
     */
    public void setText(String ikey)
    {
        keytext = ikey;
    }

    /**
     * SWKey::operator = Equates this SWKey to another SWKey object.
     * 
     * ENT:	ikey - other swkey object
     */
    public void set(SWKey ikey)
    {
        // not desirable	Persist(ikey.Persist());
        keytext = ikey.getText();
    }

    /**
     * SWKey::operator char * - returns text key if (char *) cast is requested
     */
    public String toString()
    {
        return getText();
    }

    public String getText()
    {
        return keytext;
    }

    /**
     * SWKey::compare - Compares another VerseKey object.
     * 
     * ENT:	ikey - key to compare with this one
     * RET:	> 0 if this key is greater than compare key &lt; 0
     */
    public int compareTo(Object ikey)
    {
        if (ikey instanceof SWKey)
            return toString().compareTo(ikey.toString());
        else
            return -1;
    }

    /**
     * SWKey::operator =(POSITION) - Positions this key if applicable
     */
    public void position(int p)
    {
        switch (p)
        {
            case TOP :
                //		*this = "";
                break;
            case BOTTOM :
                //		*this = "zzzzzzzzz";
                break;
        }
    }

    /**
     * SWKey::operator += - Increments key a number of entries.
     * 
     * ENT:	increment	- Number of entries to jump forward
     * RET: *this
    public void next(int i)
    {
        error = KEYERR_OUTOFBOUNDS;
    }
     */

    public void next()
    {
//        next(1);
    }

    /**
     * SWKey::operator -= - Decrements key a number of entries.
     * 
     * ENT: decrement - Number of entries to jump backward
     * RET: *this
    public void previous(int i)
    {
        error = KEYERR_OUTOFBOUNDS;
    }
     */

    public void previous()
    {
//        previous(1);
    }
}
