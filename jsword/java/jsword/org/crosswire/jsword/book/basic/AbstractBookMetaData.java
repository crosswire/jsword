package org.crosswire.jsword.book.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.event.EventListenerList;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.IndexStatus;

/**
 * An implementaion of the Propery Change methods from BookMetaData.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class AbstractBookMetaData implements BookMetaData
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook()
    {
        return book;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver()
    {
        return driver;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        if (getDriver() == null)
        {
            return null;
        }

        return getDriver().getDriverName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getOsisID()
    {
        return getType().toString() + '.' + getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        if (fullName == null)
        {
            fullName = computeFullName();
        }
        return fullName;
    }

    /**
     * 
     */
    private String computeFullName()
    {
        StringBuffer buf = new StringBuffer(getName());

        if (getDriver() != null)
        {
            buf.append(" (").append(getDriverName()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLanguage()
     */
    public String getLanguage()
    {
        return (String) getProperty(KEY_LANGUAGE);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperties()
     */
    public Map getProperties()
    {
        return prop;
    }
    
    /**
     * @param newProperties
     */
    public void setProperties(Map newProperties)
    {
        prop = newProperties;
    }
    
    /**
     * @param key
     * @return the object found by the key
     */
    protected Object getProperty(String key)
    {
        return prop.get(key);
    }

    /**
     * @param key
     * @param value
     */
    protected void putProperty(String key, Object value)
    {
        prop.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getIndexStatus()
     */
    public IndexStatus getIndexStatus()
    {
        return indexStatus;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setIndexStatus(java.lang.String)
     */
    public void setIndexStatus(IndexStatus newValue)
    {
        IndexStatus oldValue = this.indexStatus;
        this.indexStatus = newValue;
        prop.put(KEY_INDEXSTATUS, newValue);
        firePropertyChange(KEY_INDEXSTATUS, oldValue, newValue);
    }

    /**
     * @param book The book to set.
     */
    public void setBook(Book book)
    {
        this.book = book;
    }

    /**
     * @param driver The driver to set.
     */
    public void setDriver(BookDriver driver)
    {
        this.driver = driver;
    }


    /**
     * Get the language name from the language code. Note, this code does not support dialects.
     * @param iso639Code
     * @return the name of the language
     */
    protected String getLanguage(String ident, String iso639Code)
    {
        String lookup = iso639Code;
        if (lookup == null || lookup.length() == 0)
        {
            log.warn("Book " + ident + " named " + getName() + " has no language specified. Assuming English."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return getLanguage(ident, DEFAULT_LANG_CODE);
        }

        if (lookup.indexOf('_') != -1)
        {
            String[] locale = StringUtil.split(lookup, '_');
            return getLanguage(ident, locale[0]);
        }

        char firstLangChar = lookup.charAt(0);
        // If the language begins w/ an x then it is "Undetermined"
        // Also if it is not a 2 or 3 character code then it is not a valid
        // iso639 code.
        if (firstLangChar == 'x' || firstLangChar == 'X' || lookup.length() > 3)
        {
            return getLanguage(ident, UNKNOWN_LANG_CODE);
        }

        try
        {
            return languages.getString(lookup);
        }
        catch (MissingResourceException e)
        {
            log.error("Not a valid language code:" + iso639Code + " in book " + ident); //$NON-NLS-1$ //$NON-NLS-2$
            return getLanguage(ident, UNKNOWN_LANG_CODE);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // We might consider checking for equality against all BookMetaDatas?
        // However currently we dont.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // The real bit ...
        BookMetaData that = (BookMetaData) obj;

        return getName().equals(that.getName());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        BookMetaData that = (BookMetaData) obj;
        return this.getInitials().compareTo(that.getInitials());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (displayName == null)
        {
            StringBuffer buf = new StringBuffer("["); //$NON-NLS-1$
            buf.append(getInitials());
            buf.append("] - "); //$NON-NLS-1$
            buf.append(getFullName());
            displayName = buf.toString();
        }
        return displayName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (listeners == null)
        {
            listeners = new EventListenerList();
        }
        listeners.add(PropertyChangeListener.class, listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (listeners == null)
        {
            return;
        }

        listeners.remove(PropertyChangeListener.class, listener);
    }

    /**
     * Reports bound property changes.
     * If <code>oldValue</code> and <code>newValue</code> are not equal and the
     * <code>PropertyChangeEvent</code> listener list isn't empty,
     * then fire a <code>PropertyChange</code> event to each listener.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue the old value of the property (as an Object)
     * @param newValue the new value of the property (as an Object)
     * @see java.beans.PropertyChangeSupport
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (listeners != null)
        {
            if (oldValue != null && newValue != null && oldValue.equals(newValue))
            {
                return;
            }

            if (listeners != null)
            {
                Object[] listenerList = listeners.getListenerList();
                for (int i = 0; i <= listenerList.length - 2; i += 2)
                {
                    if (listenerList[i] == PropertyChangeListener.class)
                    {
                        PropertyChangeEvent ev = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
                        PropertyChangeListener li = (PropertyChangeListener) listenerList[i + 1];
                        li.propertyChange(ev);
                    }
                }
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractBookMetaData.class);

    private static final String DEFAULT_LANG_CODE = "en"; //$NON-NLS-1$
    private static final String UNKNOWN_LANG_CODE = "und"; //$NON-NLS-1$

    /**
     * The list of property change listeners
     */
    private transient EventListenerList listeners;

    private static/*final*/ResourceBundle languages;
    static
    {
        try
        {
            languages = ResourceBundle.getBundle("iso639", Locale.getDefault(), new CWClassLoader()); //$NON-NLS-1$;
        }
        catch (MissingResourceException e)
        {
            assert false;
        }
    }

    /**
     * The single key version of the properties
     */
    private Map prop = new LinkedHashMap();

    private Book book;
    private BookDriver driver;
    private String fullName;
    private String displayName;
    private IndexStatus indexStatus = IndexStatus.UNDONE;
}
