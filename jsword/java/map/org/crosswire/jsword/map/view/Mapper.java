
package org.crosswire.jsword.map.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.crosswire.common.config.Config;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.ExtensionFileFilter;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.map.model.AntiGravityRule;
import org.crosswire.jsword.map.model.BoundsRule;
import org.crosswire.jsword.map.model.BrownianRule;
import org.crosswire.jsword.map.model.DefraggingRule;
import org.crosswire.jsword.map.model.FrictionRule;
import org.crosswire.jsword.map.model.LinkArray;
import org.crosswire.jsword.map.model.LinkAttractionRule;
import org.crosswire.jsword.map.model.Map;
import org.crosswire.jsword.map.model.Rule;
import org.crosswire.jsword.map.model.VBAExport;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleChooser;
import org.jdom.Document;

/**
 * Mapper is GUI wrapper around Map to allow it to be run standalone.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Mapper extends JFrame
{
    /**
    * Central start point.
    * @param args The command line arguments
    */
    public static void main(String[] args)
    {
        Mapper map = new Mapper(args);

        map.pack();
        GuiUtil.centerWindow(map);
        map.setVisible(true);
    }

    /**
     * Basic constructor
     */
    public Mapper(String[] args)
    {
        Project.init();

        // The Verse colourizers
        vcols = new VerseColor[76];
        vcols[0] = new RainbowVerseColor();
        vcols[1] = new GroupVerseColor();
        for (int i = Books.Section.Pentateuch; i <= Books.Section.Letters; i++)
        {
            vcols[i + 1] = new SectionVerseColor(i);
        }
        for (int i = Books.Names.Genesis; i <= Books.Names.Revelation; i++)
        {
            vcols[i + 1 + Books.Section.Letters] = new BookVerseColor(i);
        }

        jbInit();

        if (args.length != 0)
        {
            try
            {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[0]));

                setMap((Map) in.readObject());
                saved = true;

                in.close();
                setFilename(args[0]);
            }
            catch (Exception ex)
            {
                ExceptionPane.showExceptionDialog(this, ex);
            }
        }
        else
        {
            setFilename(null);
        }

        try
        {
            LookAndFeelUtil.addComponentToUpdate(this);

            Document xmlconfig = Project.resource().getDocument("config");
            config.add(xmlconfig);

            Properties prop = Project.resource().getProperties("mapper");
            if (prop != null)
                config.setProperties(prop);
            config.localToApplication(true);
        }
        catch (Exception ex)
        {
            ExceptionPane.showExceptionDialog(this, ex);
        }

        map.setLayoutSimple();
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void jbInit()
    {
        menu_file.setText("File");
        menu_file.setMnemonic('F');
        menu_edit.setText("Edit");
        menu_edit.setMnemonic('E');
        menu_help.setText("Help");
        menu_help.setMnemonic('H');

        item_new.setText("New Map ...");
        item_new.setMnemonic('N');
        item_new.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                create();
            }
        });

        item_open.setText("Open Map ...");
        item_open.setMnemonic('O');
        item_open.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                open();
            }
        });

        item_save.setText("Save Map");
        item_save.setMnemonic('S');
        item_save.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                save();
            }
        });

        item_saveas.setText("Save Map As ...");
        item_saveas.setMnemonic('A');
        item_saveas.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                saveas();
            }
        });

        item_word.setText("Export Map to Word ...");
        item_word.setMnemonic('W');
        item_word.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                word();
            }
        });

        item_print.setText("Print ...");
        item_print.setMnemonic('P');
        item_print.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                print();
            }
        });
        item_print.setEnabled(false);

        item_exit.setText("Exit");
        item_exit.setMnemonic('X');
        item_exit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                exit();
            }
        });

        item_link.setText("Generate Links ...");
        item_link.setMnemonic('L');
        item_link.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                link();
            }
        });
        item_link.setEnabled(false);

        item_start.setText("Start Layout");
        item_start.setMnemonic('S');
        item_start.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                start();
            }
        });

        item_initial.setText("Set Initial Positions");
        item_initial.setMnemonic('I');
        item_initial.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                simple();
            }
        });

        item_random.setText("Set Random Positions");
        item_random.setMnemonic('R');
        item_random.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                random();
            }
        });

        item_contents.setText("Contents ...");
        item_contents.setMnemonic('C');
        item_contents.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                help();
            }
        });

        item_about.setText("About ...");
        item_about.setMnemonic('A');
        item_about.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                about();
            }
        });

        menubar.add(menu_file);
        menubar.add(menu_edit);
        menubar.add(menu_help);
        menu_file.add(item_new);
        menu_file.add(item_open);
        menu_file.addSeparator();
        menu_file.add(item_save);
        menu_file.add(item_saveas);
        menu_file.add(item_word);
        menu_file.addSeparator();
        menu_file.add(item_print);
        menu_file.addSeparator();
        menu_file.add(item_exit);
        menu_edit.add(item_link);
        menu_edit.addSeparator();
        menu_edit.add(item_start);
        menu_edit.add(item_initial);
        menu_edit.add(item_random);
        menu_help.add(item_contents);
        menu_help.addSeparator();
        menu_help.add(item_about);

        cmd_zoom_in.setText("+");
        cmd_zoom_in.setFont(new Font("Dialog", 1, 14));
        cmd_zoom_in.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                zoom(+10);
            }
        });
        cmd_zoom_out.setText("-");
        cmd_zoom_out.setFont(new Font("Dialog", 1, 14));
        cmd_zoom_out.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                zoom(-10);
            }
        });
        lay_zoom.setAlignment(FlowLayout.RIGHT);
        pnl_zoom.setLayout(lay_zoom);
        pnl_zoom.add(cmd_zoom_out, null);
        pnl_zoom.add(cmd_zoom_in, null);
        pnl_canvas.setMap(map);
        pnl_canvas.setSize(map_size, map_size);
        pnl_canvas.setVerseColor(vcols[0]);
        scr_map.getViewport().add(pnl_canvas, null);
        pnl_mapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnl_mapper.setLayout(new BorderLayout());
        pnl_mapper.add(scr_map, BorderLayout.CENTER);
        pnl_mapper.add(pnl_zoom, BorderLayout.SOUTH);

        mdl_map.setMap(map);
        tbl_table.setModel(mdl_map);
        pnl_table.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnl_table.setLayout(new BorderLayout());
        pnl_table.add(scr_table, BorderLayout.CENTER);
        scr_table.getViewport().add(tbl_table, null);

        tab_main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tab_main.add(pnl_mapper, "Display");
        tab_main.add(pnl_table, "Table");

        cbo_color = new JComboBox(vcols);
        cbo_color.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                verseColor();
            }
        });
        pnl_color.setBorder(BorderFactory.createTitledBorder("Verse Colour Scheme"));
        pnl_color.setLayout(new BorderLayout());
        pnl_color.add(cbo_color, BorderLayout.CENTER);

        pnl_tools.setLayout(new BoxLayout(pnl_tools, BoxLayout.Y_AXIS));
        pnl_tools.add(new JPanel());
        pnl_tools.add(pnl_rules);
        pnl_tools.add(pnl_color);

        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                exit();
            }
        });
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(tab_main, BorderLayout.CENTER);
        this.getContentPane().add(pnl_tools, BorderLayout.EAST);
        this.setJMenuBar(menubar);
        this.setEnabled(true);
        // this.setIconImage(GuiUtil.loadImageIcon("org.crosswire.common.resource.task_small.gif"));
    }

    /**
     * The name of the file that we are saving under
     * @param filename The new filename to save to
     */
    public void setFilename(String filename)
    {
        this.filename = filename;

        if (filename == null)
        {
            this.setTitle("Mapper - [Untitled]");
        }
        else
        {
            this.setTitle("Mapper - [" + filename + "]");
        }
    }

    /**
     * Set everything to use the given map
     * @param map The new map
     */
    public void setMap(Map map)
    {
        this.map = map;

        mdl_map.setMap(map);
        pnl_canvas.setMap(map);
    }

    /**
     * Get the currently used Map
     * @return The current map
     */
    public Map getMap()
    {
        return map;
    }

    /**
     * Have we made changes or can we abandon them
     */
    protected boolean okToClose()
    {
        if (!saved)
        {
            int reply = JOptionPane.showConfirmDialog(null, "Changes have been made to the current map.\nLose changes?", "Lose Changes?", JOptionPane.YES_NO_OPTION);

            return reply == JOptionPane.YES_OPTION;
        }
        else
        {
            return true;
        }
    }

    /**
     * Kill the current file
     */
    protected void close()
    {
        setMap(new Map(2));
        saved = true;
    }

    /**
     * Select a Bible and create the link info
     */
    protected void create()
    {
        if (!okToClose())
            return;

        setMap(new Map(2));
        saved = true;
    }

    /**
     * Open a saved map file
     */
    protected void open()
    {
        if (!okToClose())
            return;

        try
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(bmap_filter);
            if (filename != null)
                chooser.setSelectedFile(new File(filename));

            int reply = chooser.showOpenDialog(this);
            if (reply == JFileChooser.APPROVE_OPTION)
            {
                File file = chooser.getSelectedFile();
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

                setMap((Map) in.readObject());
                saved = true;

                in.close();
                setFilename(file.getPath());
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Save a map file to disk
     */
    protected void save()
    {
        if (filename == null)
        {
            saveas();
            return;
        }

        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));

            out.writeObject(map);
            saved = true;

            out.close();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Save a map file to disk
     */
    protected void saveas()
    {
        try
        {
            if (filename == null)
                filename = System.getProperty("user.dir") + File.separator + "Untitled.bmap";

            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(bmap_filter);
            chooser.setSelectedFile(new File(filename));

            int reply = chooser.showSaveDialog(this);
            if (reply == JFileChooser.APPROVE_OPTION)
            {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".bmap"))
                {
                    file = new File(file.getPath() + ".bmap");
                }

                if (file.exists())
                {
                    if (JOptionPane.showConfirmDialog(this, "A file called " + file.getPath() + " already exists.\n" + "Do you want to replace this file?") != JOptionPane.OK_OPTION)
                    {
                        return;
                    }
                }

                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

                out.writeObject(map);
                saved = true;

                out.close();
                setFilename(file.getPath());
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Create a Word Macro file
     */
    protected void word()
    {
        try
        {
            String basname = filename.substring(0, filename.length() - 5) + ".bas";

            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(bas_filter);
            chooser.setSelectedFile(new File(basname));

            int reply = chooser.showSaveDialog(this);
            if (reply == JFileChooser.APPROVE_OPTION)
            {
                File file = chooser.getSelectedFile();

                if (file.exists())
                {
                    if (JOptionPane.showConfirmDialog(this, "A file called " + file.getPath() + " already exists.\n" + "Do you want to replace this file?") != JOptionPane.OK_OPTION)
                    {
                        return;
                    }
                }

                PrintWriter out = new PrintWriter(new FileWriter(file));

                vba.export(map, out);

                out.close();
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Print the map
     */
    protected void print()
    {
    }

    /**
     * Action from clicking on the exit button. Exits the VM.
     */
    protected void exit()
    {
        if (okToClose())
        {
            System.exit(0);
        }
    }

    /**
     * Create the set of links
     */
    protected void link()
    {
        try
        {
            BibleChooser chooser = new BibleChooser();
            int reply = chooser.showDialog(this);

            if (reply == BibleChooser.APPROVE_OPTION)
            {
                //Bible bible = chooser.getSelectedBible();
                //map.createLinks(bible);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Start an layout thread
     */
    protected void start()
    {
        if (autolayout == null)
        {
            item_start.setText("Stop AutoLayout");

            alr = new AutoLayoutRunnable();
            autolayout = new Thread(alr);
            autolayout.setPriority(Thread.MIN_PRIORITY);
            autolayout.start();
        }
        else
        {
            item_start.setText("Start AutoLayout");
            alr.die();

            autolayout = null;
        }
    }

    /**
     * Set the simple layout
     */
    protected void simple()
    {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to reset the layout?") == JOptionPane.OK_OPTION)
        {
            saved = false;
            map.setLayoutSimple();
            pnl_canvas.repaint();
        }
    }

    /**
     * Set the random layout
     */
    protected void random()
    {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to reset the layout?") == JOptionPane.OK_OPTION)
        {
            saved = false;
            map.setLayoutRandom();
            pnl_canvas.repaint();
        }
    }

    /**
     * Set the verse colourizer
     */
    protected void verseColor()
    {
        VerseColor vcol = (VerseColor) cbo_color.getSelectedItem();
        pnl_canvas.setVerseColor(vcol);
    }

    /**
     * Not implemented
     */
    protected void help()
    {
    }

    /**
     * Not implemented
     */
    protected void about()
    {
    }

    /**
     * Zoom in or out of the map
     */
    protected void zoom(int change)
    {
        map_size += change;
        pnl_canvas.setSize(new Dimension(map_size, map_size));
    }

    /**
     * Load the default link array from disk
     */
    private LinkArray getLinkArray()
    {
        try
        {
            FileInputStream fin = new FileInputStream("S:\\Joe\\Profile\\linkarray.bla");
            ObjectInputStream oin = new ObjectInputStream(fin);

            return (LinkArray) oin.readObject();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return null;
        }
    }

    /** The size (int pixels of the map canvas */
    private int map_size = 500;

    /** The current file name */
    private String filename;

    /** The thing that does the real work */
    protected Map map = new Map(2);

    /** The table view of the map */
    private MapTableModel mdl_map = new MapTableModel();

    /** The thread that does background updates */
    private Thread autolayout;

    /** The Layout Runnable */
    private AutoLayoutRunnable alr;

    /** Have the map been changed? */
    protected boolean saved = true;

    /** File filter for map files */
    private ExtensionFileFilter bmap_filter = new ExtensionFileFilter(new String[] { "bmap" }, "JSword Map Files (*.bmap)");

    /** File filter for VB files */
    private ExtensionFileFilter bas_filter = new ExtensionFileFilter(new String[] { "bas" }, "Word VBA Macro (*.bas)");

    /** Where is this config info stored */
    private URL config_url;

    /** The Configuration options */
    private Config config = new Config("Mapper Options");

    /** The VBA export routine */
    private VBAExport vba = new VBAExport();

    /** The Rules */
    protected Rule[] rules = new Rule[] { new AntiGravityRule(), new DefraggingRule(), new LinkAttractionRule(getLinkArray()), new BrownianRule(0.001F), new BoundsRule(), new FrictionRule(), };

    /** The Verse colorizers */
    private VerseColor[] vcols;

    /* Various GUI components */
    private JPanel pnl_tools = new JPanel();
    private RulesPane pnl_rules = new RulesPane(rules);
    private JTabbedPane tab_main = new JTabbedPane();
    private JTable tbl_table = new JTable();
    private JScrollPane scr_table = new JScrollPane();
    private JPanel pnl_table = new JPanel();
    private MapperPane pnl_canvas = new MapperPane();
    private JScrollPane scr_map = new JScrollPane();
    private JPanel pnl_mapper = new JPanel();
    private JButton cmd_zoom_in = new JButton();
    private JButton cmd_zoom_out = new JButton();
    private FlowLayout lay_zoom = new FlowLayout();
    private JPanel pnl_zoom = new JPanel();
    private JPanel pnl_color = new JPanel();
    private JComboBox cbo_color;

    /* Menubar */
    private JMenuBar menubar = new JMenuBar();
    private JMenu menu_file = new JMenu();
    private JMenu menu_edit = new JMenu();
    private JMenu menu_help = new JMenu();
    private JMenuItem item_new = new JMenuItem();
    private JMenuItem item_open = new JMenuItem();
    private JMenuItem item_save = new JMenuItem();
    private JMenuItem item_saveas = new JMenuItem();
    private JMenuItem item_close = new JMenuItem();
    private JMenuItem item_word = new JMenuItem();
    private JMenuItem item_print = new JMenuItem();
    private JMenuItem item_exit = new JMenuItem();
    private JMenuItem item_link = new JMenuItem();
    private JMenuItem item_start = new JMenuItem();
    private JMenuItem item_initial = new JMenuItem();
    private JMenuItem item_random = new JMenuItem();
    private JMenuItem item_contents = new JMenuItem();
    private JMenuItem item_about = new JMenuItem();

    /**
     * A Layout Thread Runnable
     */
    class AutoLayoutRunnable implements Runnable
    {
        /**
         * Repeatedly do the layout thing
         */
        public void run()
        {
            while (alive)
            {
                saved = false;
                map.applyRules(rules);
                Thread.currentThread().yield();
            }
        }

        /**
         * Stop laying out
         */
        public void die()
        {
            alive = false;
        }

        /** Should we carry on */
        private boolean alive = true;
    }
}
