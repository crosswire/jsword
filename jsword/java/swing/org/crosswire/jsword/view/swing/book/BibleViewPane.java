package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.view.swing.display.splitlist.OuterDisplayPane;

/**
 * A quick Swing Bible display pane.
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
public class BibleViewPane extends JPanel
{
    /**
     * Simple ctor
     */
    public BibleViewPane()
    {
        initialize();
    }

    /**
     * Setup the GUI
     */
    private void initialize()
    {
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new CustomFileFilter());
        chooser.setMultiSelectionEnabled(false);

        pnlSelect.addCommandListener(pnlPassg.getDisplaySelectListener());
        pnlSelect.addCommandListener(new DisplaySelectListener()
        {
            /* (non-Javadoc)
             * @see org.crosswire.jsword.view.swing.book.DisplaySelectListener#passageSelected(org.crosswire.jsword.view.swing.book.DisplaySelectEvent)
             */
            public void passageSelected(DisplaySelectEvent ev)
            {
                if (saved == null)
                {
                    fireTitleChanged(new TitleChangedEvent(BibleViewPane.this, getTitle()));
                }
            }

            /* (non-Javadoc)
             * @see org.crosswire.jsword.view.swing.book.DisplaySelectListener#bookChosen(org.crosswire.jsword.view.swing.book.DisplaySelectEvent)
             */
            public void bookChosen(DisplaySelectEvent ev)
            {
            }
        });
        pnlPassg.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout());
        this.add(pnlSelect, BorderLayout.NORTH);
        this.add(pnlPassg, BorderLayout.CENTER);
    }

    /**
     * Allow the current
     */
    public void adjustFocus()
    {
        pnlSelect.adjustFocus();
    }

    /**
     * How has this view been saved
     */
    public String getTitle()
    {
        if (saved == null)
        {
            String deft = pnlSelect.getDefaultName();
            if (deft.length() > shortlen)
            {
                deft = StringUtil.shorten(deft, shortlen);
            }

            return deft;
        }

        return saved.getName();
    }

    /**
     * Save the view to disk.
     * @throws IOException
     */
    public void save() throws IOException
    {
        Passage passage = getPassage();
        if (passage == null)
        {
            return;
        }

        if (saved == null)
        {
            querySaveFile();
        }

        Writer out = new FileWriter(saved);
        passage.writeDescription(out);
    }
    
    /**
     * Returns true if there is something to save.
     * @return
     */
    public boolean maySave()
    {
        return getPassage() != null;
    }

    /**
     * Save the view to disk, but ask the user where to save it first.
     * @throws IOException
     */
    public void saveAs() throws IOException
    {
        Passage passage = getPassage();
        if (passage == null)
        {
            return;
        }

        querySaveFile();

        Writer out = new FileWriter(saved);
        getPassage().writeDescription(out);
    }

    /**
     * Open a saved verse list form disk
     */
    public void open() throws NoSuchVerseException, IOException
    {
        int reply = chooser.showOpenDialog(getRootPane());
        if (reply == JFileChooser.APPROVE_OPTION)
        {
            saved = chooser.getSelectedFile();
            if (saved.length() == 0)
            {
                Reporter.informUser(getRootPane(), "File '" + saved.getName() + "' is empty"); //$NON-NLS-1$
                return;
            }

            Reader in = new FileReader(saved);
            Passage ref = PassageFactory.createPassage();
            ref.readDescription(in);
            setPassage(ref);
        }
    }

    /**
     * Ask the user where to store the data
     */
    private boolean querySaveFile()
    {
        if (saved == null)
        {
            File guess = new File(getTitle() + EXTENSION);
            chooser.setSelectedFile(guess);
        }
        else
        {
            chooser.setSelectedFile(saved);
        }

        int reply = chooser.showSaveDialog(getRootPane());
        if (reply == JFileChooser.APPROVE_OPTION)
        {
            saved = chooser.getSelectedFile();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return pnlPassg.getPassage();
    }

    /**
     * Accessor for the current passage
     */
    public void setPassage(Passage ref)
    {
        pnlSelect.setPassage(ref);
        pnlPassg.setPassage(ref);
    }

    /**
     * Accessor for the OuterDisplayPane
     */
    public OuterDisplayPane getPassagePane()
    {
        return pnlPassg;
    }

    /**
     * Accessor for the DisplaySelectPane
     */
    public DisplaySelectPane getSelectPane()
    {
        return pnlSelect;
    }

    /**
     * Add a listener when someone clicks on a browser 'link'
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        pnlPassg.addHyperlinkListener(li);
    }

    /**
     * Remove a listener when someone clicks on a browser 'link'
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        pnlPassg.removeHyperlinkListener(li);
    }

    /**
     * Add a listener to the list
     */
    public synchronized void addTitleChangedListener(TitleChangedListener li)
    {
        List temp = new ArrayList();
        if (listeners == null)
        {
            temp.add(li);
            listeners = temp;
        }
        else
        {
            temp.addAll(listeners);

            if (!temp.contains(li))
            {
                temp.add(li);
                listeners = temp;
            }
        }
    }

    /**
     * Remove a listener from the list
     */
    public synchronized void removeTitleChangedListener(TitleChangedListener li)
    {
        if (listeners != null && listeners.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(listeners);
            temp.remove(li);
            listeners = temp;
        }
    }

    /**
     * Inform the listeners that a title has changed
     */
    protected void fireTitleChanged(TitleChangedEvent ev)
    {
        if (listeners != null)
        {
            List temp = listeners;
            int count = temp.size();
            for (int i = 0; i < count; i++)
            {
                ((TitleChangedListener) temp.get(i)).titleChanged(ev);
            }
        }
    }

    protected File saved = null;
    private transient List listeners;
    private DisplaySelectPane pnlSelect = new DisplaySelectPane();
    private OuterDisplayPane pnlPassg = new OuterDisplayPane();
    private static int shortlen = 30;
    private JFileChooser chooser = new JFileChooser();
    private static final String EXTENSION = ".lst"; //$NON-NLS-1$

    /**
     * Returns the shortlen.
     * @return int
     */
    public static int getShortlen()
    {
        return shortlen;
    }

    /**
     * Sets the shortlen.
     * @param shortlen The shortlen to set
     */
    public static void setShortlen(int shortlen)
    {
        BibleViewPane.shortlen = shortlen;
    }

    /**
     * Filter out verse lists
     */
    private static final class CustomFileFilter extends FileFilter
    {
        /* (non-Javadoc)
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file)
        {
            return file.getName().endsWith(EXTENSION);
        }

        /* (non-Javadoc)
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        public String getDescription()
        {
            return "Verse Lists ("+EXTENSION+")";
        }
    }
}
