/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.book.install.sword;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Prototype code
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class FTPExample {
    private FTPExample() { }

    static void copyDirectory(FTPClient ftpClient, String sourceDir, String module, String destDir, String currentDir) throws IOException {
        String dirToList = module;
        if (!"".equals(currentDir)) {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(sourceDir + '/' + dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                // skip parent directory and directory itself
                if (".".equals(currentFileName) || "..".equals(currentFileName)) {
                    continue;
                }
                if (aFile.isFile()) {
                    copyFile(ftpClient, sourceDir, dirToList + '/'  + currentFileName, destDir);
                } else if (aFile.isDirectory() && !"lucene".equalsIgnoreCase(currentFileName)) {
                    System.out.println("mkdir " + destDir + dirToList + '/' + currentFileName + "/");
                    copyDirectory(ftpClient, sourceDir, dirToList, destDir, currentFileName);
                }
            }
        }
    }

    static void copyFile(FTPClient ftpClient, String sourceDir, String file, String destDir) throws IOException {
        System.out.println("cp " + sourceDir + '/' + file + ' ' + destDir + '/' + file);
    }

    static long getSize(FTPClient ftpClient, String sourceDir, String module) throws IOException {
        long total = 0;
        String dirToList = module;
        FTPFile[] subFiles = ftpClient.listFiles(sourceDir + '/' + dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (".".equals(currentFileName) || "..".equals(currentFileName)) {
                    // skip parent directory and directory itself
                    continue;
                }
                if (aFile.isFile()) {
                    total += aFile.getSize();
                } else if (aFile.isDirectory() && !"lucene".equalsIgnoreCase(currentFileName)) {
                    total += getSize(ftpClient, sourceDir, dirToList);
                }
            }
        }
        return total;
    }
/*
20081216195754=FTPSource=CrossWire|ftp.crosswire.org|/pub/sword/raw
20090224125400=FTPSource=CrossWire Beta|ftp.crosswire.org|/pub/sword/betaraw
20090514005700=FTPSource=Bible.org|ftp.bible.org|/sword
20090514005900=FTPSource=Xiphos|ftp.xiphos.org|.
20120224005000=FTPSource=CrossWire av11n|ftp.crosswire.org|/pub/sword/avraw
20120224005100=FTPSource=CrossWire Attic|ftp.crosswire.org|/pub/sword/atticraw
20120224005200=FTPSource=IBT|ftp.ibt.org.ru|/pub/modsword/raw
20120711005000=FTPSource=CrossWire Wycliffe|ftp.crosswire.org|/pub/sword/wyclifferaw
20120711005100=FTPSource=CrossWire av11n Attic|ftp.crosswire.org|/pub/sword/avatticraw

OutputStream output = new FileOutputStream(local);
ftp.retrieveFile(remote, output);
output.close();
 */
    public static void main(String[] args) {
        String server = "ftp.crosswire.org";
        int port = 21;
        String user = "anonymous";
        String pass = "jsword@crosswire.org";
        String dirToList = "/pub/sword/avraw";
        String confPath = "mods.d/azeri.conf";
        String dataPath = "modules/texts/ztext/azeri";

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Connect failed");
                return;
            }
            boolean success = ftpClient.login(user, pass);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            }
            ftpClient.setUseEPSVwithIPv4(true);
            copyFile(ftpClient, dirToList, confPath, "/Users/DM/Library/Application Support/Sword");
            System.out.println("Size is " + getSize(ftpClient, dirToList, confPath));
            copyDirectory(ftpClient, dirToList, dataPath, "/Users/DM/Library/Application Support/Sword", "");
            System.out.println("Size is " + getSize(ftpClient, dirToList, dataPath));
        } catch (IOException e) {
            System.out.println("Oops! Something wrong happened");
            e.printStackTrace();
        } finally {
            // logs out and disconnects from server
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                System.out.println("Oops! Something wrong happened");
                ex.printStackTrace();
            }
        }
    }
}
