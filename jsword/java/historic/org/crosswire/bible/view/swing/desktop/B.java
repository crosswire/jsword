
package com.eireneh.bible.view.swing.desktop;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import com.eireneh.swing.*;
import com.eireneh.swing.config.*;
import com.eireneh.config.*;
import com.eireneh.config.swing.*;
import com.eireneh.util.*;

import com.eireneh.bible.passage.*;
import com.eireneh.bible.book.*;
import com.eireneh.bible.util.*;
import com.eireneh.bible.view.swing.beans.*;

import com.eireneh.config.swing.config.*;
import com.eireneh.util.config.*;
// import com.eireneh.crypto.config.*;

import com.eireneh.bible.passage.config.*;
import com.eireneh.bible.util.config.*;
import com.eireneh.bible.book.config.*;
import com.eireneh.bible.control.search.config.*;

/**
 * Something we can look at...
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
 * @see docs.Licence
 * @author Joe Walker
 */
public class B extends JApplet
{
    /**
     * The application, applet initialization.
     */
    public void init()
    {
        try
        {
            try
            {
                resources = ResourceBundle.getBundle("com.eireneh.bible.view.swing.desktop.B");
            }
            catch (MissingResourceException ex)
            {
                Reporter.informUser(this, ex);
            }

            pan_desktop.setOpaque(true);
            pan_desktop.setDoubleBuffered(true);
            pan_desktop.setMinimumSize(new Dimension(500, 500));
            // pan_desktop.setBackground(Color.white);
            // pan_desktop.setBackground(Color.lightGray);

            Action[] actions =
            {
                new NewAction(),
                new OpenAction(),
                new TestAction(),
                new BshAction(),
                new ConfigAction(),
                new ExitAction(),
            };
            //Action[] actions = TextAction.augmentList(editor.getActions(), actions);
            MenuUtil.addActions(actions);

            MenuUtil.setResourceBundle(resources);
            menubar = MenuUtil.createMenubar();
            statusbar = new StatusBar();
            startbar = new StartBar();

            setJMenuBar(menubar);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add("Center", pan_desktop);
            getContentPane().add("South", statusbar);
            getContentPane().add("North", startbar);

            /*
            JScrollPane disp = new JScrollPane(new JTree(new BibleTreeNode()));
            getContentPane().add("West", disp);

            getContentPane().add("East", new SearchTree());
            */

            //getContentPane().add("North", MenuUtil.createToolbar());

            /*
            new VerseView(pan_desktop);
            new VerseView(pan_desktop);
            new FileView(pan_desktop, undo_handler);
            */
        }
        catch (Exception ex)
        {
            ExceptionPane.showExceptionDialog(this, ex);
            dead = true;
        }
    }

    /**
     * Initial startup
     */
    public static void main(String args[])
    {
        Project.init();

        try
        {
            B top = new B();
            top.frm_desktop = new AppletFrame(title, top, 750, 550);

            // If there are config classes - register ourselves as a window
            LookAndFeelChoices.addWindow(top.frm_desktop);

            // Load the default choices
            URL url = NetUtil.lengthenURL(Project.getConfigRoot(), "BApplet.properties");
            top.config = getBConfig();
            top.config.permanentToLocal(url);

            GuiUtil.maximizeWindow(top.frm_desktop);
        }
        catch (Throwable ex)
        {
            if (ex instanceof ThreadDeath) throw (ThreadDeath) ex;
            ExceptionPane.showExceptionDialog(null, ex);
            dead = true;
        }

        //if (dead) System.exit(0);
    }

    /**
     * Really lame implementation of an exit command
     */
    class ConfigAction extends AbstractAction
    {
        ConfigAction()
        {
            super(configAction);
        }

        public void actionPerformed(ActionEvent ev)
        {
            try
            {
                URL url = NetUtil.lengthenURL(Project.getConfigRoot(), "BApplet.properties");
                SwingConfig.showDialog(config, B.this, url);
            }
            catch (Exception ex)
            {
                ExceptionPane.showExceptionDialog(null, ex);
            }
        }
    }

    /**
     * Really lame implementation of an exit command
     */
    class TestAction extends AbstractAction
    {
        TestAction()
        {
            super(testAction);
        }

        public void actionPerformed(ActionEvent ev)
        {
            // launcher.openTest();
        }
    }

    /**
     * Really lame implementation of an exit command
     */
    class BshAction extends AbstractAction
    {
        BshAction()
        {
            super(bshAction);
        }

        public void actionPerformed(ActionEvent ev)
        {
            //bsh.Console.main(new String[] { });
        }
    }

    /**
     * Really lame implementation of an exit command
     */
    class ExitAction extends AbstractAction
    {
        ExitAction()
        {
            super(exitAction);
        }

        public void actionPerformed(ActionEvent ev)
        {
            System.exit(0);
        }
    }

    class OpenAction extends NewAction
    {
        OpenAction()
        {
            super(openAction);
        }

        public void actionPerformed(ActionEvent e)
        {
            try
            {
                launcher.openPassage("Gen 1:1-5");
            }
            catch (NoSuchVerseException ex)
            {
                ExceptionPane.showExceptionDialog(null, ex);
            }

            /*
            Frame frame = getFrame();
            if (fileDialog == null) fileDialog = new FileDialog(frame);

            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.show();

            String filename = fileDialog.getFile();
            if (filename == null) return;
            String directory = fileDialog.getDirectory();

            File file = new File(directory, filename);
            if (file.exists())
            {
            Document oldDoc = editor.getDocument();
            if (oldDoc != null) oldDoc.removeUndoableEditListener(undo_handler);

            editor.setDocument(new PlainDocument());
            frame.setTitle(filename);

            Thread loader = new FileLoader(file, editor.getDocument());
            loader.start();
            }
            */
        }
    }

    public static JDesktopPane getDesktop()
    {
        return pan_desktop;
    }

    public static Launcher getLauncher()
    {
        return launcher;
    }

    private static Launcher launcher = null;

    /**
     * Actually set up the hashtable
     */
    public static Config getBConfig() throws ClassNotFoundException
    {
        Config config = new Config("EPay Configuration");

        ConfigClassChoices conf_class = new ConfigClassChoices();
        LookAndFeelChoices plaf_class = new LookAndFeelChoices();

        // AuthenticateClassChoices auth_class = new AuthenticateClassChoices();
        // ConfigServerChoices.setConfig(config);
        // ConfigServerChoices.setAuthField(auth_class);

        config.add("Passage.Display Case", new PassageChoices.DisplayCaseChoice());
        config.add("Passage.Blur Restriction", new PassageChoices.BlurRestrictionChoice());
        config.add("Passage.Persistent Naming", new PassageChoices.PersistentNamingChoice());

        config.add("Bible.Default Bible", new DefaultBibleChoice());

        config.add("Search.Commands", new SearchChoices.SearchCommandsChoice());

        config.add("User Information.User Level", new UserLevelChoice());

        config.add("User Information.Username", new RemoteChoices.UsernameChoice());
        config.add("User Information.Organization", new RemoteChoices.OrganizationChoice());
        config.add("User Information.EMail Address", new RemoteChoices.EMailAddressChoice());

        config.add("Display.Current Look", plaf_class.getCurrentChoice());
        // config.add("Display.Java Color", new MetalColorChoice());
        // config.add("Display.Default Font", new DefaultFontChoice());
        config.add("Display.Available Looks", plaf_class.getOptionsChoice());

        config.add("Configuration.Display Style", conf_class.getCurrentChoice());
        config.add("Configuration.Available Styles", conf_class.getOptionsChoice());

        // config.add("Configuration.Server.Start Server", new ConfigServerChoices.StartServerChoice());
        // config.add("Configuration.Server.Listen Port", new ConfigServerChoices.ConfigPortChoice());
        // config.add("Configuration.Server.Authenticator", auth_class.getCurrentChoice());
        // config.add("Configuration.Server.Available Auths", auth_class.getOptionsChoice());

        config.add("Development.Source Path", new SourcePathChoice());

        return config;
    }

    static
    {
        try
        {
            launcher = new Launcher();
        }
        catch (BookException ex)
        {
            Reporter.informUser(B.class, ex);
        }

        JTextField f = new JTextField();
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        Keymap map = f.getKeymap();
        map.removeKeyStrokeBinding(enter);
    }

    /*
    * Data
    */
    private JFrame frm_desktop;

    /**
     * This is a limitation - to only having one of these. This is fine
     * right now, but should be sorted out. This whole module is a state
     * so I'm not going to do it now
     */
    private static JDesktopPane pan_desktop = new JDesktopPane();
    private JMenuBar menubar;
    private StatusBar statusbar;
    private StartBar startbar;
    private FileDialog fileDialog;
    private Config config;

    private static final String title = "OnLine Bible (Java Edition)";
    private static boolean dead = false;
    private static ResourceBundle resources;

    public static final String openAction = "open";
    public static final String newAction  = "new";
    public static final String saveAction = "save";
    public static final String exitAction = "exit";
    public static final String testAction = "test";
    public static final String configAction = "config";
    public static final String bshAction = "bsh";
    public static final String showElementTreeAction = "showElementTree";

    private UndoAction undo_action = new UndoAction();
    private RedoAction redo_action = new RedoAction();

    /** UndoManager that we add edits to. */
    private UndoManager undo = new UndoManager();

    /** Listener for the edits on the current document. */
    private UndoableEditListener undo_handler = new UndoHandler();

    /**
     *
     */
    class UndoHandler implements UndoableEditListener
    {
        /**
         * Messaged when the Document has created an edit, the edit is
         * added to <code>undo</code>, an instance of UndoManager.
         */
        public void undoableEditHappened(UndoableEditEvent ev)
        {
            undo.addEdit(ev.getEdit());
            undo_action.update();
            redo_action.update();
        }
    }

    class UndoAction extends AbstractAction
    {
        public UndoAction()
        {
            super("Undo");
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            try
            {
                undo.undo();
            }
            catch (CannotUndoException ex)
            {
                Reporter.informUser(B.this, ex);
            }
            update();
            redo_action.update();
        }

        protected void update()
        {
            if (undo.canUndo())
            {
                this.setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            }
            else
            {
                this.setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction
    {
        public RedoAction()
        {
            super("Redo");
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e)
        {
            try
            {
                undo.redo();
            }
            catch (CannotRedoException ex)
            {
                Reporter.informUser(B.this, ex);
            }
            update();
            undo_action.update();
        }

        protected void update()
        {
            if (undo.canRedo())
            {
                this.setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            }
            else
            {
                this.setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    class NewAction extends AbstractAction
    {
        NewAction()
        {
            super(newAction);
        }

        NewAction(String nm)
        {
            super(nm);
        }

        public void actionPerformed(ActionEvent e)
        {
            /*
            Document oldDoc = editor.getDocument();
            if(oldDoc != null)
            oldDoc.removeUndoableEditListener(undo_handler);
            editor.setDocument(new PlainDocument());
            editor.getDocument().addUndoableEditListener(undo_handler);

            // revalidate();
            */
        }
    }

    /**
     * Thread to load a file into the text storage model
     */
    class FileLoader extends Thread
    {

        FileLoader(File f, Document doc)
        {
            setPriority(4);
            this.f = f;
            this.doc = doc;
        }

        public void run()
        {
            try
            {
                // initialize the statusbar
                statusbar.removeAll();
                JProgressBar progress = new JProgressBar();
                progress.setMinimum(0);
                progress.setMaximum((int) f.length());
                statusbar.add(progress);
                statusbar.revalidate();

                // try to start reading
                Reader in = new FileReader(f);
                char[] buff = new char[4096];
                int nch;
                while ((nch = in.read(buff, 0, buff.length)) != -1)
                {
                    doc.insertString(doc.getLength(), new String(buff, 0, nch), null);
                    progress.setValue(progress.getValue() + nch);
                }

                // we are done... get rid of progressbar
                doc.addUndoableEditListener(undo_handler);
                statusbar.removeAll();
                statusbar.revalidate();
            }
            catch (Exception ex)
            {
                Reporter.informUser(B.this, ex);
            }
        }

        Document doc;
        File f;
    }
}

