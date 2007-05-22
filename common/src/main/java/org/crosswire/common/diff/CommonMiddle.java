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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: CallContext.java 1150 2006-10-10 19:28:31 -0400 (Tue, 10 Oct 2006) dmsmith $
 */
package org.crosswire.common.diff;

/**
 * A CommonMiddle represents an overlap between a baseline/source text and a changed/target text.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CommonMiddle
{
    /**
     * A CommonMiddle represents an overlap between a baseline/source text and a changed/target text.
     * 
     * @param sourceStart The text before the commonality form the source
     * @param targetStart The text before the commonality form the target
     * @param commonality The text in common
     * @param sourceEnd The text after the commonality form the source
     * @param targetEnd The text after the commonality form the target
     */
    public CommonMiddle(String sourceStart, String targetStart, String commonality, String sourceEnd, String targetEnd)
    {
        super();
        this.sourceStart = sourceStart;
        this.targetStart = targetStart;
        this.commonality = commonality;
        this.sourceEnd = sourceEnd;
        this.targetEnd = targetEnd;
    }

    /**
     * @return the source start
     */
    public String getSourceStart()
    {
        return sourceStart;
    }

    /**
     * @return the target start
     */
    public String getTargetStart()
    {
        return targetStart;
    }

    /**
     * @return the commonality
     */
    public String getCommonality()
    {
        return commonality;
    }

    /**
     * @return the source end
     */
    public String getSourceEnd()
    {
        return sourceEnd;
    }

    /**
     * @return the target end
     */
    public String getTargetEnd()
    {
        return targetEnd;
    }

    private String sourceStart;
    private String targetStart;
    private String commonality;
    private String sourceEnd;
    private String targetEnd;
}