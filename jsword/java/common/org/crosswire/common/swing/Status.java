
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.crosswire.common.config.Config;
import org.crosswire.common.config.swing.SwingConfig;
import org.crosswire.common.swing.config.LookAndFeelChoices;
import org.crosswire.common.util.event.CaptureEvent;
import org.crosswire.common.util.event.CaptureListener;

/**
 * We implement CaptureListener so that we can catch exceptions
 * and print them out to the Log.
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
 */
public class Status implements CaptureListener
{
    /**
     *
     */
    private Status(Config config, URL url)
    {
        this.config = config;
        this.url = url;

        JPanel pan_lg = new JPanel(new BorderLayout());

        status.setLayout(new BoxLayout(status, BoxLayout.Y_AXIS));
        status.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        status.add(except);

        logs.setModel(logpile);
        logs.setVisibleRowCount(7);
        logs.setCellRenderer(new CustomListCellRenderer());

        pan_lg.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        pan_lg.add("Center", new JScrollPane(logs));

        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { options(); }
        });
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { setWindowVisible(false, null, null); }
        });
        shutdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { System.exit(0); }
        });
        shutdown.setEnabled(false);

        bar.setLayout(new FlowLayout());
        bar.add(options);
        bar.add(close);
        bar.add(shutdown);

        LookAndFeelChoices.addWindow(frame);
        frame.getRootPane().setDefaultButton(close);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add("South", bar);
        frame.getContentPane().add("Center", pan_lg);
        frame.getContentPane().add("North", status);
        frame.pack();
    }

    /**
     * Log(package, priority, format, params)
     */
    public void captureMessage(CaptureEvent ev)
    {
        String stream = ev.getSourceName();
        String message = ev.getMessage();

        logpile.register(new LogLine(stream, message));
    }

    /**
     * Log(package, priority, format, params)
     */
    public void captureException(CaptureEvent ev)
    {
        String stream = ev.getSourceName();
        String message = ev.getException().getMessage();

        logpile.register(new LogLine(stream, message));
    }

    /**
     * Show the options dialog box
     */
    protected void options()
    {
        config.applicationToLocal();
        SwingConfig.showDialog(config, null, url);
    }

    /** The status area */
    private JPanel status = new JPanel();

    /** The reported text messages */
    private JList logs = new JList();

    /** The options button */
    private JButton options = new JButton("Options");

    /** The exit button */
    private JButton close = new JButton("Close");

    /** The exit button */
    private JButton shutdown = new JButton("Shutdown");

    /** The button bar */
    private JPanel bar = new JPanel();

    /** The window we are displayed in */
    private JFrame frame = new JFrame("EPAY Status");

    /** Where we can report problems */
    private ExceptionShelf except = new ExceptionShelf();

    /** The options */
    private Config config;

    /** How we save the status */
    private URL url;

    /**
     * Is the status window visible
     */
    public static boolean getWindowVisible()
    {
        if (just_starting)
        {
            just_starting = false;
            return true;
        }

        just_starting = false;
        return (window != null);
    }

    /**
     * Is the status window visible
     */
    public static void setWindowVisible(boolean open, Config config, URL url)
    {
        if (open && window == null)
        {
            window = new Status(config, url);
            window.frame.setVisible(true);
        }

        if (!open && window != null)
        {
            window.frame.setVisible(false);
            window.frame.dispose();
            window = null;
        }
    }

    /**
     * Log(package, priority, format, params)
     */
    public static void addCounter(String name, int max, int maj_tick, int min_tick)
    {
        JPanel panel = new JPanel(new BorderLayout());
        JSlider slide = new JSlider(0, max, 0);

        slide.setPaintTicks(true);
        slide.setMajorTickSpacing(maj_tick);
        slide.setMinorTickSpacing(min_tick);
        slide.setLabelTable(slide.createStandardLabels(maj_tick, 0));
        slide.setPaintLabels(true);
        slide.setSnapToTicks(true);

        panel.setBorder(BorderFactory.createTitledBorder(name));
        panel.add(slide, BorderLayout.CENTER);

        if (window != null)
        {
            window.status.add(panel);
            window.frame.pack();
        }

        sliders.put(name, slide);
    }

    /**
     * The number on a named slider
     */
    public static void setCounter(String name, int value)
    {
        JSlider slide = (JSlider) sliders.get(name);

        int max = slide.getMaximum();
        if (value > max)
            slide.setMaximum(value);

        slide.setValue(value);
    }

    /**
     * The style associated with a log stream
     */
    public static void setLogStyle(int index, String value)
    {
        if (value == null || value.equals(""))
        {
            advice[index] = null;
            return;
        }

        int c1 = value.indexOf(":", 0);
        int c2 = value.indexOf(":", c1+1);
        int c3 = value.indexOf(":", c2+1);
        int c4 = value.indexOf(":", c3+1);

        if (c4 == -1) throw new IllegalArgumentException(value);

        String match     = value.substring(0, c1);
        Font font        = GuiConvert.string2Font(value.substring(c1+1, c2));
        Color foreground = GuiConvert.string2Color(value.substring(c2+1, c3));
        Color background = GuiConvert.string2Color(value.substring(c3+1, c4));
        String icon      = value.substring(c4+1);

        advice[index] = new CellRendererAdvice(match, font, foreground, background, icon);
    }

    /**
     * The style associated with a log stream
     */
    public static String getLogStyle(int index)
    {
        if (advice[index] == null) return "";

        String match = advice[index].match;
        String font = GuiConvert.font2String(advice[index].font);
        String foreground = GuiConvert.color2String(advice[index].foreground);
        String background = GuiConvert.color2String(advice[index].background);
        String iconfile = advice[index].iconfile;

        if (match == null) match = "";
        if (font == null) font = "";
        if (foreground == null) foreground = "";
        if (background == null) background = "";
        if (iconfile == null) iconfile = "";

        return match+":"+font+":"+foreground+":"+background+":"+iconfile;
    }

    /**
     * The style associated with a log stream
     */
    public static void setLogStyle(int index, String match, Font font, Color foreground, Color background, Icon icon)
    {
        advice[index] = new CellRendererAdvice(match, font, foreground, background, icon);
    }

    /** The sliders */
    private static Hashtable sliders = new Hashtable();

    /** Where we store the messages */
    private static CustomListModel logpile = new CustomListModel();

    /** The Status window */
    private static Status window = null;

    /** Do we capture to the shelf */
    private static boolean capture_shelf = false;

    /** A tag to allow the default window status to be open */
    private static boolean just_starting = true;

    /**
     * The information we store about a single message
     */
    static class LogLine
    {
        /**
         * Create a new logline
         */
        public LogLine(String stream, String text)
        {
            this.stream = stream;
            this.text = text;
            this.time = System.currentTimeMillis();
        }

        /**
         * Accessor for the stream we are reporting against
         */
        public String getStreamName()
        {
            return stream;
        }

        /**
         * Accessor for the text of the message
         */
        public String getText()
        {
            return text;
        }

        /** The stream we are reporting against */
        private String stream;

        /** The text of the message */
        private String text;

        /** The time of the message */
        private long time;
    }

    /** The index of log streams */
    protected static Hashtable streams = new Hashtable();

    /** The index of log streams */
    protected static Vector visible = new Vector();

    /**
     * Where we store the messages
     */
    static class CustomListModel extends AbstractListModel
    {
        /**
         * Returns the length of the list.
         */
        public int getSize()
        {
            return visible.size();
        }

        /**
         * Returns the value at the specified index.
         */
        public Object getElementAt(int index)
        {
            return visible.elementAt(index);
        }

        /**
         * Register a new LogLine
         */
        public void register(LogLine line)
        {
            visible.addElement(line);
            Vector list = (Vector) streams.get(line.getStreamName());

            if (list == null)
            {
                list = new Vector();
                streams.put(line.getStreamName(), list);
            }

            list.addElement(line);
            fireIntervalAdded(this, visible.size()-1, visible.size());
        }
    }

    /**
     * A description of how to render a cell
     */
    static class CellRendererAdvice
    {
        /**
         * Create a CellRendererAdvice
         */
        public CellRendererAdvice(String match, Font font, Color foreground, Color background, Icon icon)
        {
            this.match = match;
            this.font = font;
            this.foreground = foreground;
            this.background = background;
            this.icon = icon;
        }

        /**
         * Create a CellRendererAdvice
         */
        public CellRendererAdvice(String match, Font font, Color foreground, Color background, String iconfile)
        {
            this.match = match;
            this.font = font;
            this.foreground = foreground;
            this.background = background;
            this.iconfile = iconfile;
            this.icon = new ImageIcon(iconfile);
        }

        public String match;
        public Font font;
        public Color foreground;
        public Color background;
        public Icon icon;
        public String iconfile;
    }

    /**
     * Renders an item in a list.
     */
    static class CustomListCellRenderer extends JLabel implements ListCellRenderer, Serializable
    {
        /**
         * Constructs a default renderer object for an item in a list.
         */
        public CustomListCellRenderer()
        {
            empty = new EmptyBorder(1, 1, 1, 1);
            setBorder(empty);
            setOpaque(true);
        }

        /**
         * Return a label customized for this list member
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
        {
            LogLine line = (LogLine) value;
            String stream = line.getStreamName();

            // Defaults
            setFont(list.getFont());
            setForeground(list.getForeground());
            setBackground(list.getBackground());
            setIcon(null);

            // Override with the matches
            for (int i=0; i<advice.length; i++)
            {
                if (advice[i] != null && stream.indexOf(advice[i].match) != -1)
                {
                    //log.fine("Found a match: "+stream+" matches "+advice[i].match);
                    if (advice[i].font != null)         setFont(advice[i].font);
                    if (advice[i].foreground != null)   setForeground(advice[i].foreground);
                    if (advice[i].background != null)   setBackground(advice[i].background);
                    if (advice[i].icon != null)         setIcon(advice[i].icon);
                }
            }

            //setText("<html><b>"+line.getStreamName()+"</b>: "+line.getText());
            setText(line.getStreamName()+": "+line.getText());
            setEnabled(list.isEnabled());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : empty);

            if (selected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }

            return this;
        }

        /**
         * A subclass of DefaultListCellRenderer that implements UIResource.
         * DefaultListCellRenderer doesn't implement UIResource
         * directly so that applications can safely override the
         * cellRenderer property with DefaultListCellRenderer subclasses.
         */
        public static class UIResource extends DefaultListCellRenderer implements javax.swing.plaf.UIResource
        {
        }

        /** The default border */
        protected static Border empty;
    }

    /** The instructions for the Renderer */
    protected static CellRendererAdvice[] advice = new CellRendererAdvice[10];

    static
    {
        Font norm_ss = new Font("SansSerif", Font.PLAIN, 12);
        Font small_ss = new Font("SansSerif", Font.PLAIN, 10);
        //Font norm_s = new Font("Serif", Font.PLAIN, 12);
        //Font small_s = new Font("Serif", Font.PLAIN, 10);

        Icon cup = UIManager.getIcon("InternalFrame.icon");

        setLogStyle(0, "", norm_ss, Color.black, Color.white, null);
        setLogStyle(1, "Debug", small_ss, null, null, null);
        setLogStyle(2, "Status", null, Color.red, null, null);
        setLogStyle(3, "Merchants", null, null, null, cup);
    }
}

