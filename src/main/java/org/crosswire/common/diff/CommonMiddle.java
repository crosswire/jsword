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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.diff;

/**
 * A CommonMiddle represents an overlap between a baseline/source text and a
 * changed/target text.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class CommonMiddle {
    /**
     * A CommonMiddle represents an overlap between a baseline/source text and a
     * changed/target text.
     * 
     * @param sourcePrefix
     *            The text before the commonality form the source
     * @param sourceSuffix
     *            The text after the commonality form the source
     * @param targetPrefix
     *            The text before the commonality form the target
     * @param targetSuffix
     *            The text after the commonality form the target
     * @param commonality
     *            The text in common
     */
    public CommonMiddle(String sourcePrefix, String sourceSuffix, String targetPrefix, String targetSuffix, String commonality) {
        this.sourcePrefix = sourcePrefix;
        this.sourceSuffix = sourceSuffix;
        this.targetPrefix = targetPrefix;
        this.targetSuffix = targetSuffix;
        this.commonality = commonality;
    }

    /**
     * @return the source start
     */
    public String getSourcePrefix() {
        return sourcePrefix;
    }

    /**
     * @return the target start
     */
    public String getTargetPrefix() {
        return targetPrefix;
    }

    /**
     * @return the commonality
     */
    public String getCommonality() {
        return commonality;
    }

    /**
     * @return the source end
     */
    public String getSourceSuffix() {
        return sourceSuffix;
    }

    /**
     * @return the target end
     */
    public String getTargetSuffix() {
        return targetSuffix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(sourcePrefix);
        buf.append(',');
        buf.append(sourceSuffix);
        buf.append(',');
        buf.append(targetPrefix);
        buf.append(',');
        buf.append(targetSuffix);
        buf.append(',');
        buf.append(commonality);
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 31 + ((sourcePrefix == null) ? 0 : sourcePrefix.hashCode());
        result = 31 * result + ((sourceSuffix == null) ? 0 : sourceSuffix.hashCode());
        result = 31 * result + ((targetPrefix == null) ? 0 : targetPrefix.hashCode());
        result = 31 * result + ((targetSuffix == null) ? 0 : targetSuffix.hashCode());
        return 31 * result + ((commonality == null) ? 0 : commonality.hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final CommonMiddle other = (CommonMiddle) obj;

        return sourcePrefix.equals(other.sourcePrefix) && sourceSuffix.equals(other.sourceSuffix) && targetPrefix.equals(other.targetPrefix)
                && targetSuffix.equals(other.targetSuffix) && commonality.equals(other.commonality);
    }

    private String sourcePrefix;
    private String sourceSuffix;
    private String targetPrefix;
    private String targetSuffix;
    private String commonality;
}
