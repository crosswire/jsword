package org.crosswire.modedit;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import org.crosswire.sword.frontend.im.HebrewDurusauIM;
import org.crosswire.sword.frontend.im.HebrewMCIM;
import org.crosswire.sword.frontend.im.NullIM;
import org.crosswire.sword.frontend.im.SWInputMethod;

public class MainFrame extends JFrame {
    JPanel contentPane;
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JPanel jPanel3 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JTextField jTextField1 = new JTextField();
    JTextField jTextField2 = new JTextField();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    JPanel jPanel4 = new JPanel();
    JTextArea jTextArea1 = new JTextArea();
    JLabel statusBar = new JLabel();
    BorderLayout borderLayout4 = new BorderLayout();
    ButtonGroup buttonGroup1 = new ButtonGroup();
    JPanel jPanel5 = new JPanel();
    JSlider jSlider1 = new JSlider();
    BorderLayout borderLayout5 = new BorderLayout();
    JLabel jLabel3 = new JLabel();
    JComboBox imComboBox = new JComboBox();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenuItem jMenuItem1 = new JMenuItem();
    JMenuItem jMenuItem2 = new JMenuItem();
    JMenuItem jMenuItem3 = new JMenuItem();
    JMenu jMenu2 = new JMenu();
    JMenuItem jMenuItem4 = new JMenuItem();
    JMenuItem jMenuItem5 = new JMenuItem();

    /**Construct the frame*/
    public MainFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**Component initialization*/
    private void jbInit() throws Exception  {
        //setIconImage(Toolkit.getDefaultToolkit().createImage(Frame1.class.getResource("[Your Icon]")));
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setJMenuBar(jMenuBar1);
        this.setSize(new Dimension(549, 300));
        this.setTitle("ModEdit");
        jPanel1.setLayout(borderLayout5);
        jPanel2.setLayout(borderLayout2);
        jPanel3.setLayout(borderLayout3);
        jLabel1.setMinimumSize(new Dimension(90, 13));
        jLabel1.setPreferredSize(new Dimension(90, 13));
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText("FontURL");
        jLabel2.setMinimumSize(new Dimension(90, 13));
        jLabel2.setPreferredSize(new Dimension(90, 13));
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText("contentURL");
        jTextField1.setToolTipText("");
        jTextField1.setText("http://www.crosswire.org/~scribe/ElEdit/1kg1.uni");
        jTextField2.setText("http://www.crosswire.org/~scribe/ElEdit/yoyo.ttf");
        jButton1.setText("Load");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jButton2.setText("Load");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        jTextArea1.setText("");
        jTextArea1.setLineWrap(true);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                jTextArea1_keyTyped(e);
            }
        });
        jTextArea1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        statusBar.setText("");
        jPanel4.setLayout(borderLayout4);
        jSlider1.setMinimum(1);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                jSlider1_stateChanged(e);
            }
        });
        jLabel3.setText("Keyboard");
        imComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                imComboBox_itemStateChanged(e);
            }
        });
        jMenu1.setText("File");
        jMenuItem1.setText("Lookup Url");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem1_actionPerformed(e);
            }
        });
        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem2_actionPerformed(e);
            }
        });
        jMenuItem3.setText("Save");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem3_actionPerformed(e);
            }
        });
        jMenu2.setText("Edit");
        jMenuItem4.setText("Copy");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem4_actionPerformed(e);
            }
        });
        jMenuItem5.setText("Paste");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem5_actionPerformed(e);
            }
        });
        jPanel2.add(jLabel1,  BorderLayout.WEST);
        jPanel2.add(jTextField2,  BorderLayout.CENTER);
        jPanel2.add(jButton2,  BorderLayout.EAST);
        jPanel2.add(jSlider1,  BorderLayout.SOUTH);
        contentPane.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(jTextArea1, null);
        jPanel1.add(jPanel2, BorderLayout.NORTH);
        jPanel1.add(jPanel3, BorderLayout.CENTER);
        jPanel3.add(jLabel2,  BorderLayout.WEST);
        jPanel3.add(jButton1,  BorderLayout.EAST);
        jPanel3.add(jTextField1, BorderLayout.CENTER);
        contentPane.add(jPanel4, BorderLayout.SOUTH);
        jPanel4.add(statusBar, BorderLayout.NORTH);
        jPanel4.add(jPanel5,  BorderLayout.EAST);
        jPanel5.add(jLabel3, null);
        jPanel5.add(imComboBox, null);
        contentPane.add(jPanel1, BorderLayout.NORTH);
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenu1.add(jMenuItem1);
        jMenu1.add(jMenuItem3);
        jMenu1.add(jMenuItem2);
        jMenu2.add(jMenuItem4);
        jMenu2.add(jMenuItem5);
        jSlider1.setValue(jTextArea1.getFont().getSize());
        imComboBox.addItem(new HebrewDurusauIM("Durusau"));
        imComboBox.addItem(new NullIM("Latin"));
        imComboBox.addItem(new HebrewMCIM("Michigan-Claremont"));
    }
    /**Overridden so we can exit when window is closed*/
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    void jButton2_actionPerformed(ActionEvent e) {
        try {
            statusBar.setText("Loading font...");
            statusBar.paintImmediately(statusBar.getVisibleRect());
            URLConnection connection = new URL(jTextField2.getText()).openConnection();
            InputStream is = connection.getInputStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            Font newFont = font.deriveFont((float)18.0);
            jSlider1.setValue(18);
            this.jTextArea1.setFont(newFont);
            is.close();
            statusBar.setText("New Font Loaded.");
        }
        catch (Exception ex) { ex.printStackTrace(); }

    }

    void jButton1_actionPerformed(ActionEvent e) {
        try {
            statusBar.setText("Loading content...");
            statusBar.paintImmediately(statusBar.getVisibleRect());
            URLConnection connection = new URL(jTextField1.getText()).openConnection();
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            String newText = connection.toString();

            int len;
            byte inBuf[] = new byte[8192];
            do {
                len = bis.read(inBuf, 0, 8192);
                if (len != -1)
                    bos.write(inBuf, 0, len);
            } while (len != -1);
            newText = new String(bos.toByteArray(), "UTF-8");
            jTextArea1.setText(newText);
            statusBar.setText(Integer.toString(newText.length())+" characters of content loaded.");
        }
        catch (Exception ex) { ex.printStackTrace(); }

    }

    void jTextArea1_keyTyped(KeyEvent e) {
        char typedChar = e.getKeyChar();
        String pushChar = null;
        statusBar.setText("");

        SWInputMethod inputMethod = (SWInputMethod) imComboBox.getSelectedItem();

        pushChar = inputMethod.translate(typedChar);
        if (inputMethod.getState() > 1) {
            statusBar.setText("Compound '"+typedChar+"'");
            e.consume();
        }
        else {
            if (pushChar.length() > 1) {
                e.consume();
                jTextArea1.insert(pushChar, jTextArea1.getCaretPosition());
            }
            else e.setKeyChar(pushChar.charAt(0));
        }
    }

    void jSlider1_stateChanged(ChangeEvent e) {
            Font font = jTextArea1.getFont();
            Font newFont = font.deriveFont((float)jSlider1.getValue());
            jTextArea1.setFont(newFont);
    }

    void imComboBox_itemStateChanged(ItemEvent e) {
        // let's set focus back to text box after IM is changed
    }

    void jMenuItem1_actionPerformed(ActionEvent e) {
        String currentEntry = jTextField1.getText();
        javax.swing.JFileChooser fileChooser = new JFileChooser((currentEntry.startsWith("file://") ? currentEntry.substring(7):"."));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jTextField1.setText("file://"+ fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    void jMenuItem3_actionPerformed(ActionEvent e) {
        String currentEntry = jTextField1.getText();
        javax.swing.JFileChooser fileChooser = new JFileChooser((currentEntry.startsWith("file://") ? currentEntry.substring(7):"."));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outFile = fileChooser.getSelectedFile();
            try {
                jTextArea1.write(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            }
            catch (Exception e1) { e1.printStackTrace(); }
        }

    }

    void jMenuItem4_actionPerformed(ActionEvent e) {
        jTextArea1.copy();
    }

    void jMenuItem5_actionPerformed(ActionEvent e) {
        jTextArea1.paste();
    }

    void jMenuItem2_actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
