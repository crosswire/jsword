package org.crosswire.sword.mgr;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.IOException;


public class SWConfig {

    private String filename;
    private HashMap propMap = null;

    public SWConfig(String filename)
        throws IOException
    {
        this.filename = filename;
        propMap = new HashMap();
        load(filename);
    }

    /**
     * Loads the properties from the file into the object
     * making them ready to retrieve.
     */
    public void load(String file)
        throws IOException
    {
        try {
            Properties props = new Properties();
            FileInputStream cfile = new FileInputStream(file);
            props.load( cfile );
            propMap.put( props.getSectionName(), props );
        }
        catch (Exception e) {
            throw new IOException( e.getMessage() );
        }
    }

    /**
     * Retrieves the value of the named property.
     */
    public String getProperty(String section, String key) {
        Properties props = (Properties)propMap.get( section );
        return props != null ? props.getProperty( key ) : null;
    }

    /**
     * Retrieves an Iterator of values for the named property.
     * If the property only has a single value a one item
     * iterator will be returned rather than forcing the
     * caller to check for null and recall the getProperty()
     * method.
     */
    public Iterator getProperties(String section, String key) {
        Properties props = (Properties)propMap.get( section );
        return props != null ? props.getProperties( key ) : null;
    }

    /**
     * Retrieve a list of property names for the given section.
     */
    public Iterator propertyNames(String section) {
        Properties props = (Properties)propMap.get( section );
        return props != null ? enumToIterator( props.propertyNames() ) : null;
    }

    private Iterator enumToIterator(Enumeration enum) {
        List list = new LinkedList();
        while( enum.hasMoreElements() )
            list.add( enum.nextElement() );
        return list.iterator();
    }


    public boolean contains(String section) {
        Properties props = (Properties)propMap.get( section );
        return (props != null);
    }


    public boolean contains(String section, String key) {
        return (getProperty(section, key) != null);
    }


    public Iterator sectionNames() {
        return propMap.keySet().iterator();
    }

    /**
     * Copies the properties of the <code>addFrom</code> SWConfig
     * object into the current property set.
     */
    public SWConfig augment(SWConfig addFrom)
    {
        Iterator sit = addFrom.sectionNames();
        while( sit.hasNext() ) {
            String sname = (String)sit.next();
            Object props = addFrom.propMap.get( sname );
            this.propMap.put( sname, props );
        }
        return this;
    }

    /**
     * Usage: java org.crosswire.sword.mgr.SWConfig <file> [<file>].
     *
     * Prints the contents of each configuration file to STDOUT.
     */
    public static void main(String[] args) {
        if( args.length < 1 ) {
            System.out.println("Usage: java SWConfig <file> [<file> <file> ...]");
            System.exit( 0 );
        }

        try {
            System.out.println("Showing Properties in file: " + args[0] );
            SWConfig config = new SWConfig( args[0] );
            // If there were multiple files given, load them
            // all.
            if( args.length > 1 ) {
                for( int i = 1; i < args.length; i++ ) {
                    config.load( args[i] );
                }
            }
            Iterator sit = config.sectionNames();
            while( sit.hasNext() ) {
                String sectionName = (String)sit.next();
                System.out.println("Section: " + sectionName);
                Iterator spit = config.propertyNames( sectionName );
                while( spit.hasNext() ) {
                    String key = (String)spit.next();
                    Iterator vit = config.getProperties( sectionName, key );
                    while( vit.hasNext() ) {
                        System.out.println("\t" + key + ": " + vit.next() );
                    }
                }
            }
        }catch(Exception ex) {
            System.err.println("Error: " + ex.getMessage() );
            ex.printStackTrace();
        }

    }

    private class Properties extends java.util.Properties {

        private String name = null;
        public String getSectionName() { return name; }

        /**
         * Overrides the super classes (HashMap) put(Object,Object) method
         * taking into account that some properties may have multiple
         * values.
         */
        public Object put(Object key, Object value) {
            // if the key isn't a String it's not one of the properties
            // we are looking for, so return without setting it.
            if( !(key instanceof String) ) { return null; }

            // check to see if this is the SectionName
            if( ((String)key).startsWith("[") ) {
                String skey = (String)key;
                this.name = skey.substring(1, (skey.length() - 1));
                // Shouldn't be any values associated with this
                // key so just return so it isn't set in the
                // HashMap
                return null;
            }
            // get the existing value.
            Object svalue = get( key );
            // see if it exists
            if( svalue != null ) {
                if( svalue instanceof List ) {
                    // if it's a List, then add to it
                    // and put it back.
                    ((List)svalue).add( value );
                    return super.put( key, svalue );
                }else{
                    // if it's not a list, assume it's
                    // a String. Create a List collection
                    // add the current value and the pased
                    // in value to the list and then put
                    // the List in the Map associated with
                    // the key.
                    List list = new LinkedList();
                    list.add( svalue );
                    list.add( value );
                    return super.put( key, list );
                }
            }
            // key doesn't already exist so just add
            // it as normal.
            return super.put( key, value );
        }

        /**
         * Overrides the super classes getProperty(String,String) method
         * taking into account that some properties may have multiple
         * values.
         */
        public String getProperty(String key, String defaultValue) {
            String value = getProperty( key );
            if( value != null )
                // if the value existed, return it.
                return value;
            // otherwise return the default value.
            return defaultValue;
        }

        /**
         * Overrides the super classes getProperty(String) method
         * taking into account that some properties may have multiple
         * values.
         */
        public String getProperty(String key) {
            Object value = get(key);
            // if the value associated with the key is a List
            // then return the first element in it as the value
            if( value != null && value instanceof List ) {
                return (String)((List)value).get(0);
            }
            // otherwise just return the value as a Stirng
            return (String)value;
        }

        /**
         * Provides a method to retrieve multiple values of a property
         * if they exist. This method can safely be called with keys
         * that only have a single value (an Iterator with one element
         * in it will be returned).
         */
        protected Iterator getProperties(String key) {
            Object value = get( key );
            if( value != null && value instanceof List )
                // If the value is already a List then just
                // return and Iterator for it.
                return ((List)value).iterator();

            // Otherwise create a List, set a single value
            // and return an Iterator for it.
            List list = new LinkedList();
            list.add( value != null ? value : "" );
            return list.iterator();
        }
    }
}
