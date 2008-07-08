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
 * ID: $Id:  $
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.util.Properties;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.Book;

/**
 * A factory creating the appropriate Analyzer for natural language analysis of text for Lucene 
 * Indexing and Query Parsing.
 * Note: [Lang] refers to CommonName for ISO639 Language
 * Dependency: Analyzer from lucene contrib: lucene-analyzers-[version].jar, lucene-snowball-[version].jar
 * 
 * Properties used:
 * <Key> : <Value> 
 * Default.Analyzer : The default analyzer class
 * [Lang].Analyzer : Appropriate Analyzer class to be used for the language of the book
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class AnalyzerFactory
{
    public AbstractBookAnalyzer createAnalyzer(Book book)
    {
        AbstractBookAnalyzer newObject = null;
        String lang = book == null ? null : book.getLanguage().getName();
        if (lang != null)
        {
            String adjustLang = lang;
            // Deal with non-standard language names
            if (adjustLang.startsWith("Greek, Modern")) //$NON-NLS-1$
            {
                adjustLang = "Greek"; //$NON-NLS-1$
            }

            String aClass = getAnalyzerValue(adjustLang);

            log.debug("Creating analyzer:" + aClass + " BookLang:" + adjustLang); //$NON-NLS-1$ //$NON-NLS-2$

            if (aClass != null)
            {
                try
                {
                    Class impl = ClassUtil.forName(aClass);

                    newObject = (AbstractBookAnalyzer) impl.newInstance();
                }
                catch (ClassNotFoundException e)
                {
                    log.error("Configuration error in AnalyzerFactory properties", e); //$NON-NLS-1$
                }
                catch (IllegalAccessException e)
                {
                    log.error("Configuration error in AnalyzerFactory properties", e); //$NON-NLS-1$
                }
                catch (InstantiationException e)
                {
                    log.error("Configuration error in AnalyzerFactory properties", e); //$NON-NLS-1$
                }
            }
        }

        if (newObject == null)
        {
            newObject = new SimpleLuceneAnalyzer();
        }

        // Configure the analyzer
        newObject.setBook(book);
        newObject.setDoStemming(getDefaultStemmingProperty());
        newObject.setDoStopWords(getDefaultStopWordProperty());
        newObject.setNaturalLanguage(lang);
        return newObject;
    }

    public static AnalyzerFactory getInstance()
    {
        return myInstance;
    }

    private AnalyzerFactory()
    {
        loadProperties();
    }

    public String getAnalyzerValue(String lang)
    {
        String key = lang + ".Analyzer"; //$NON-NLS-1$
        return myProperties.getProperty(key);
    }

    public boolean getDefaultStemmingProperty()
    {
        String key = DEFAULT_ID + ".Stemming"; //$NON-NLS-1$
        return Boolean.valueOf(myProperties.getProperty(key)).booleanValue();
    }

    public boolean getDefaultStopWordProperty()
    {
        String key = DEFAULT_ID + ".StopWord"; //$NON-NLS-1$
        return Boolean.valueOf(myProperties.getProperty(key)).booleanValue();
    }

    private void loadProperties()
    {
        try
        {
            myProperties = ResourceUtil.getProperties(getClass());
        }
        catch (IOException e)
        {
            log.error("AnalyzerFactory property load from file failed", e); //$NON-NLS-1$
        }
    }

    public static final String     DEFAULT_ID = "Default";                              //$NON-NLS-1$
    private static final Logger    log        = Logger.getLogger(AnalyzerFactory.class);
    private static AnalyzerFactory myInstance = new AnalyzerFactory();

    private Properties             myProperties;

}
