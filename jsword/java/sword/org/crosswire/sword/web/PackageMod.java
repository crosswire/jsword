
/**
 * Title:        The SWORD Project<p>
 * Description:  <p>
 * Copyright:    Copyright (c) CrossWire Java Development Team<p>
 * Company:      CrossWire Bible Society<p>
 * @author CrossWire Java Development Team
 * @version 1.0
 */
package org.crosswire.sword.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.crosswire.sword.mgr.SWConfig;
import org.crosswire.sword.mgr.SWMgr;

public class PackageMod {

     public PackageMod() {
     }

     public static void copyFileToZip(ZipOutputStream zos, String sourceDir, String fileName, String prefix) {
        try {
            if ((prefix.length() > 0) && (!prefix.endsWith("/")))
                prefix += "/";
            zos.putNextEntry(new ZipEntry(prefix+fileName));
                fileName = sourceDir + ((sourceDir.endsWith("/")) ? "" : "/") + fileName;
            FileInputStream fin = new FileInputStream(fileName);
            byte [] buffer = new byte[ 20000 ];
            int len;
            while ((len = fin.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            fin.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static long getLastModFile(SWMgr mgr, String sourceDir, String modName) {
        String entry = null;
        //Enumeration fileList = null;
        //Vector files = new Vector();
        long lastModFile = 0;

        if (!sourceDir.endsWith("/"))
            sourceDir += "/";

        try {
            if (mgr.config.contains(modName)) {
/*
                fileList = section.keys("File");
                if (fileList.hasMoreElements()) {	// copy each file
                    while (fileList.hasMoreElements()) {
                        files.add((String)fileList.nextElement());
                    }

                }
                else {	//copy all files in DataPath directory
*/
                    String modDir;
                    String modFile;
                    String sourceOrig = sourceDir;

                    modDir = mgr.config.getProperty(modName, "DataPath");
                    if (modDir != null) {
                        entry = mgr.config.getProperty(modName, "ModDrv");
                        if (entry != null) {
                            if (entry.equals("RawLD") || entry.equals("RawLD4"))
                                modDir = modDir.substring(0, modDir.lastIndexOf('/'));
                        }
                        if (modDir.startsWith("./"))
                            modDir = modDir.substring(2);

                        sourceDir += modDir;
                        File dataDir = new File(sourceDir);
                        String names[] = dataDir.list();

                        for (int i = 0; i < names.length; i++) {
                            if ((!names[i].equals(".")) && (!names[i].equals(".."))) {
                                File testFile = new File(dataDir, names[i]);
                                if (testFile.canRead()) {
                                    if (testFile.lastModified() > lastModFile) {
                                        lastModFile = testFile.lastModified();
                                    }
                                }
                            }
                        }
                    }
                    sourceDir = sourceOrig;
                    sourceDir += "mods.d/";

                        File dataDir = new File(sourceDir);
                    String names[] = dataDir.list(
                        new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".conf");
                            }
                        });

                    for (int i = 0; i < names.length; i++) {
                        if ((!names[i].equals(".")) && (!names[i].equals(".."))) {
                            modFile = sourceDir;
                            modFile += names[i];
                            SWConfig config = new SWConfig(modFile);
                            if (config.contains(modName)) {
                                File testFile = new File(dataDir, names[i]);
                                if (testFile.canRead()) {
                                    if (testFile.lastModified() > lastModFile) {
                                        lastModFile = testFile.lastModified();
                                    }
                                }
                            }
                        }
                    }
//				}
                }
        }
        catch (Exception e1) { e1.printStackTrace(); }
        return lastModFile;
    }

    public static void sendToZipStream(OutputStream os, SWMgr mgr, String sourceDir, String modName, boolean triggerInstall) {
        ZipOutputStream zstream = new ZipOutputStream(os);
        String entry = null;
        Iterator fileList = null;

        if (!sourceDir.endsWith("/"))
            sourceDir += "/";

        try {

            if (mgr.config.contains(modName)) {

                fileList = mgr.config.getProperties(modName, "File");
                if (fileList.hasNext()) {	// copy each file
                    while (fileList.hasNext()) {
                        copyFileToZip(zstream, sourceDir, (String)fileList.next(), "");
                    }
                }
                else {	//copy all files in DataPath directory
                    String modDir;
                    String modFile;
                    String sourceOrig = sourceDir;

                    modDir = mgr.config.getProperty(modName, "DataPath");
                    if (modDir != null) {
                        entry = mgr.config.getProperty(modName, "ModDrv");
                        if (entry != null) {
                            if (entry.equals("RawLD") || entry.equals("RawLD4"))
                                modDir = modDir.substring(0, modDir.lastIndexOf('/'));
                        }
                        if (modDir.startsWith("./"))
                            modDir = modDir.substring(2);

                        sourceDir += modDir;
                        File dataDir = new File(sourceDir);
                        String names[] = dataDir.list();

                        for (int i = 0; i < names.length; i++) {
                            if ((!names[i].equals(".")) && (!names[i].equals(".."))) {
                                modFile = modDir;
                                if (!modFile.endsWith("/"))
                                    modFile += "/";
                                modFile += names[i];
                                copyFileToZip(zstream, sourceOrig, modFile, "");
                            }
                        }
                    }
                    sourceDir = sourceOrig;
                    sourceDir += "mods.d/";

                        File dataDir = new File(sourceDir);
                    String names[] = dataDir.list(
                        new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".conf");
                            }
                        });

                    for (int i = 0; i < names.length; i++) {
                        if ((!names[i].equals(".")) && (!names[i].equals(".."))) {
                            modFile = sourceDir;
                            modFile += names[i];
                            SWConfig config = new SWConfig(modFile);
                            if (config.contains(modName)) {
                                if (triggerInstall)
                                    copyFileToZip(zstream, sourceOrig + "mods.d/", names[i], "newmods/");
                                else	copyFileToZip(zstream, sourceOrig, "mods.d/" + names[i], "");
                            }
                        }
                    }
                }
                zstream.flush();
                zstream.close();
                }
        }
        catch (Exception e1) { e1.printStackTrace(); }
    }
}
