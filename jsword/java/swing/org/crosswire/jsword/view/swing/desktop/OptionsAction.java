package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.config.swing.ConfigEditorFactory;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.readings.ReadingsBookDriver;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.util.ConfigurableSwingConverter;
import org.crosswire.jsword.view.swing.util.SimpleSwingConverter;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Action from clicking on the options button. Opens a config dialog.
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
public class OptionsAction extends DesktopAbstractAction
{
    /**
     * Setup configuration
     */
    public OptionsAction(Desktop tools)
    {
        super(tools,
              "Options ...",
              "toolbarButtonGraphics/general/Properties16.gif",
              "toolbarButtonGraphics/general/Properties24.gif",
              "Options", "Alter system settings.",
              'O', null);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // I'm not 100% sure that this will update the dialog with the
            // current list of Bibles, but it should.
            fillChoiceFactory();
            Books.addBooksListener(new CustomBooksListener());

            URL config_url = Project.instance().getWritablePropertiesURL("desktop");
            ConfigEditorFactory.showDialog(config, getDesktop().getJFrame(), config_url);
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Load the config.xml file
     */
    public void createConfig() throws IOException, JDOMException
    {
        fillChoiceFactory();

        config = new Config("Desktop Options");
        Document xmlconfig = Project.instance().getDocument("config");
        config.add(xmlconfig);
    }

    /**
     * Load the desktop.properties file
     */
    public void loadConfig() throws IOException
    {
        config.setProperties(Project.instance().getProperties("desktop"));
        config.localToApplication(true);
    }

    /**
     * Setup the choices so that the options dialog knows what there is to
     * select from.
     */
    private static void fillChoiceFactory()
    {
        refreshBooks();

        // Create the array of readings sets
        ChoiceFactory.getDataMap().put("readings", ReadingsBookDriver.getInstalledReadingsSets());

        // And the array of allowed osis>html converters
        Map converters = ConverterFactory.getKnownConverters();
        Set keys = converters.keySet();
        String[] names = (String[]) keys.toArray(new String[keys.size()]);
        ChoiceFactory.getDataMap().put("converters", names);

        // The choice of simple XSL stylesheets
        SimpleSwingConverter sstyle = new SimpleSwingConverter();
        String[] sstyles = sstyle.getStyles();
        ChoiceFactory.getDataMap().put("swing-styles", sstyles);

        // The choice of configurable XSL stylesheets
        ConfigurableSwingConverter cstyle = new ConfigurableSwingConverter();
        String[] cstyles = cstyle.getStyles();
        ChoiceFactory.getDataMap().put("cswing-styles", cstyles);
    }

    /**
     * Setup the book choices
     */
    protected static void refreshBooks()
    {
        // Create the array of Bibles
        String[] bnames = getFullNameArray(BookFilters.getBibles());
        ChoiceFactory.getDataMap().put("biblenames", bnames);

        // Create the array of Commentaries
        String[] cnames = getFullNameArray(BookFilters.getCommentaries());
        ChoiceFactory.getDataMap().put("commentarynames", cnames);

        // Create the array of Dictionaries
        String[] dnames = getFullNameArray(BookFilters.getDictionaries());
        ChoiceFactory.getDataMap().put("dictionarynames", dnames);
    }

    /**
     * Convert a filter into an array of names of Books that pass the filter.
     */
    private static String[] getFullNameArray(BookFilter filter)
    {
        List bmds = Books.getBookMetaDatas(filter);
        List names = new ArrayList();

        for (Iterator it = bmds.iterator(); it.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) it.next();
            names.add(bmd.getFullName());
        }

        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * The configuration engine
     */
    private Config config = null;

    /**
     * Allow us to keep up with changes to the known books
     */
    private static class CustomBooksListener implements BooksListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev)
        {
            refreshBooks();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev)
        {
            refreshBooks();
        }
    }
}
