

package docs.template;

/**
 * The point of base is that it contains some example implementations of
 * code that is commonly written, and often done incorrectly.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version D8.I8.T0
 */
class Example extends Object implements Cloneable
{
    /**
     * Get a copy of ourselves. Points to note:
     *   Call clone() not new() on member Objects, and on us.
     *   Do not use Copy Constructors! - they do not inherit well.
     *   Think about this needing to be synchronized
     *   If this is not cloneable then writing cloneable children is harder
     * @return A complete (shallow of deep) copy of ourselves
     * @exception java.lang.CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        Example copy = (Example) super.clone();

        // For all of the member objects
        if (data != null)
            copy.data = (Example) data.clone();

        return copy;
    }

    /**
     * Is this Object equal to us. Points to note:
     *   If you override equals(), you must override hashCode() too.
     *   If you are doing this it is a good idea to be immutable.
     * @param obj The thing to test against
     * @return True/False is we are or are not equal to obj
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
            return false;

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
            return false;

        // If super does equals ...
        if (super.equals(obj) == false)
            return false;

        // The real bit ...
        Example that = (Example) obj;

        // Remember that many things will be better using that.equals(this)
        return that.data == this.data;
    }

    /**
     * Get a moderately unique id for this Object. Points to note:
     *   JDK1.x - hashCode should not change for an Object.
     *   JDK2.0 - hashCode may change if it's data changes
     *   If you override hashCode(), you must override equals() too.
     *   If you are doing this it is a good idea to be immutable.
     * @return The hashing number
     */
    public int hashCode()
    {
        // For JDK1.c this is nigh on impossible so a default
        // could be something like
        // return 0;

        // On JDK2 hashCode is allowed to change so:
        return data.hashCode();
    }

    /**
     * Get a sting representation of this object. Points to note:
     *   Think about inheritance when using string names.
     * @return The string representation
     */
    public String toString()
    {
        return getClass().getName() + "[" + data + "]";
    }

    /**
     * Clean up after ourselves. Points to note:
     *   There is no garantee that this will be called
     *   Call the superclasses filalize()
     * @exception java.lang.Throwable Almost anything could go wrong
     */
    protected void finalize() throws Throwable
    {
        try
        {
            // Whatever
        }
        catch(Throwable ex)
        {
            // Should we think about ThreadDeath here?
        }

        super.finalize();
    }

    /**
     * Example member, used in the clone example
     */
    private Example data;
}
