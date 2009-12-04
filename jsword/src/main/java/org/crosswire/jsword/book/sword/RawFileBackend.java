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
 * Copyright: 2009
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 */
public class RawFileBackend extends RawBackend {

    public RawFileBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd, datasize);

        initIncFile();
        try {
            incfileValue = readIncfile();
        } catch (IOException e) {
            log.error("Error on reading incfile!"); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.sword.RawBackend#getRawText(org.crosswire.jsword
     * .passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        return super.getRawText(key);
    }

    /**
     * Get the text for an indexed entry in the book.
     * 
     * @param name
     *            name of the entry
     * @param testament
     *            testament number 1 or 2
     * @param index
     *            the entry to get
     * @return the text for the entry.
     * @throws java.io.IOException
     *             on file error
     */
    protected String getEntry(String name, int testament, long index) throws IOException {

        DataIndex dataIndex = getIndex(idxRaf[testament], index);
        int size = dataIndex.getSize();
        if (size == 0) {
            return ""; //$NON-NLS-1$
        }

        if (size < 0) {
            log.error("In " + getBookMetaData().getInitials() + ": Verse " + name + " has a bad index size of " + size); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return ""; //$NON-NLS-1$
        }

        try {
            File dataFile = getDataTextFile(testament, dataIndex);
            byte[] textBytes = readTextDataFile(dataFile);
            decipher(textBytes);
            return SwordUtil.decode(name, textBytes, getBookMetaData().getBookCharset());
        } catch (BookException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void setRawText(Key key, String text) throws BookException, IOException {
        checkActive();

        Verse verse = KeyUtil.getVerse(key);
        int testament = SwordConstants.getTestament(verse);
        long index = SwordConstants.getIndex(verse);
        int oIndex = verse.getOrdinal() - 1;

        DataIndex dataIndex = getIndex(idxRaf[testament], index);
        File dataFile;
        if (dataIndex.getSize() == 0) {
            dataFile = createDataTextFile(oIndex);
            updateIndexFile(testament, index);
            updateDataFile(testament, oIndex);
        } else {
            dataFile = getDataTextFile(testament, dataIndex);
        }

        byte[] textData = text.getBytes("UTF-8"); //$NON-NLS-1$
        encipher(textData);
        writeTextDataFile(dataFile, textData);

        checkAndIncrementIncfile(oIndex);
    }

    public void setAliasKey(Key alias, Key source) throws IOException {
        Verse aliasVerse = KeyUtil.getVerse(alias);
        Verse sourceVerse = KeyUtil.getVerse(source);
        int testament = SwordConstants.getTestament(aliasVerse);
        long aliasIndex = SwordConstants.getIndex(aliasVerse);
        //long sourceIndex = SwordConstants.getIndex(sourceVerse);
        int aliasOIndex = aliasVerse.getOrdinal() - 1;
        int sourceOIndex = sourceVerse.getOrdinal() - 1;

        updateIndexFile(testament, aliasIndex);
        updateDataFile(testament, sourceOIndex);

        checkAndIncrementIncfile(aliasOIndex);
    }

    private void initIncFile() {
        try {
            File tempIncfile = new File(getExpandedDataPath().getPath() + File.separator + INCFILE);
            if (tempIncfile.exists()) {
                this.incfile = tempIncfile;
            }
        } catch (BookException e) {
            log.error("Error on checking incfile: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    private File createDataTextFile(int index) throws BookException, IOException {
        String dataPath = getExpandedDataPath().getPath();
        // JDK15: Use String.format instead
        // dataPath += File.separator + String.format("%07d", index);
        dataPath += File.separator + new DecimalFormat("0000000").format(index); //$NON-NLS-1$
        File dataFile = new File(dataPath);
        if (!dataFile.exists() && !dataFile.createNewFile()) {
            throw new IOException("Could not create data file."); //$NON-NLS-1$
        }
        return dataFile;
    }

    private File getDataTextFile(int testament, DataIndex dataIndex) throws IOException, BookException {
        File dataFile;

        // data size to be read from the data file (ot or nt) should be 9 bytes
        // this will be the filename of the actual text file "\r\n"
        byte[] data = SwordUtil.readRAF(txtRaf[testament], dataIndex.getOffset(), dataIndex.getSize());
        decipher(data);
        if (data.length == 7) {
            String dataFilename = new String(data, 0, 7);
            String dataPath = getExpandedDataPath().getPath() + File.separator + dataFilename;
            dataFile = new File(dataPath);
        } else {
            log.error("Read data is not of appropriate size of 9 bytes!"); //$NON-NLS-1$
            throw new IOException("Datalength is not 9 bytes!"); //$NON-NLS-1$
        }
        return dataFile;
    }

    protected void updateIndexFile(int testament, long index) throws IOException {
        long indexFileWriteOffset = index * entrysize;
        long dataFileStartPosition = txtRaf[testament].length();
        int dataFileLengthValue = 7; // filename is 7 bytes + 2 bytes for "\r\n"
        byte[] startPositionData = littleEndian32BitByteArrayFromInt((int) dataFileStartPosition);
        byte[] lengthValueData = littleEndian16BitByteArrayFromShort((short) dataFileLengthValue);
        byte[] indexFileWriteData = new byte[6];

        indexFileWriteData[0] = startPositionData[0];
        indexFileWriteData[1] = startPositionData[1];
        indexFileWriteData[2] = startPositionData[2];
        indexFileWriteData[3] = startPositionData[3];
        indexFileWriteData[4] = lengthValueData[0];
        indexFileWriteData[5] = lengthValueData[1];

        SwordUtil.writeRAF(idxRaf[testament], indexFileWriteOffset, indexFileWriteData);
    }

    protected void updateDataFile(int testament, long ordinal) throws IOException {
        // JDK15: Use String.format instead
        // String fileName = String.format("%07d\r\n", ordinal);
        StringBuffer buf = new StringBuffer();
        buf.append(new DecimalFormat("0000000").format(ordinal)); //$NON-NLS-1$
        buf.append("\r\n"); //$NON-NLS-1$
        String fileName = buf.toString();
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(txtFile[testament], true));
            bos.write(fileName.getBytes());
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    private void checkAndIncrementIncfile(int index) throws IOException {
        if (index >= this.incfileValue) {
            this.incfileValue = index + 1;
            writeIncfile(this.incfileValue);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.sword.RawBackend#create()
     */
    public void create() throws IOException, BookException {
        super.create();
        createDataFiles();
        createIndexFiles();
        createIncfile();

        checkActive();

        prepopulateIndexFiles();
        prepopulateIncfile();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.sword.RawBackend#isWritable()
     */
    public boolean isWritable() {
        File otTextFile = txtFile[1];
        File ntTextFile = txtFile[2];
        File otIndexFile = idxFile[1];
        File ntIndexFile = idxFile[2];
        File incFile = this.incfile;

        if (otTextFile.exists() && otTextFile.canRead() && otTextFile.canWrite() && ntTextFile.exists() && ntTextFile.canRead() && ntTextFile.canWrite()
                && otIndexFile.exists() && otIndexFile.canRead() && otIndexFile.canWrite() && ntIndexFile.exists() && ntIndexFile.canRead()
                && ntIndexFile.canWrite() && incFile.exists() && incFile.canRead() && incFile.canWrite())
        {
            return true;
        }
        return false;
    }

    private void createDataFiles() throws IOException, BookException {
        String path = getExpandedDataPath().getPath();

        File otTextFile = new File(path + File.separator + SwordConstants.FILE_OT);
        if (!otTextFile.exists() && !otTextFile.createNewFile()) {
            throw new IOException("Could not create ot text file."); //$NON-NLS-1$
        }

        File ntTextFile = new File(path + File.separator + SwordConstants.FILE_NT);
        if (!ntTextFile.exists() && !ntTextFile.createNewFile()) {
            throw new IOException("Could not create nt text file."); //$NON-NLS-1$
        }
    }

    private void createIndexFiles() throws IOException, BookException {
        String path = getExpandedDataPath().getPath();
        File otIndexFile = new File(path + File.separator + SwordConstants.FILE_OT + SwordConstants.EXTENSION_VSS);
        if (!otIndexFile.exists() && !otIndexFile.createNewFile()) {
            throw new IOException("Could not create ot index file."); //$NON-NLS-1$
        }

        File ntIndexFile = new File(path + File.separator + SwordConstants.FILE_NT + SwordConstants.EXTENSION_VSS);
        if (!ntIndexFile.exists() && !ntIndexFile.createNewFile()) {
            throw new IOException("Could not create nt index file."); //$NON-NLS-1$
        }
    }

    private void prepopulateIndexFiles() throws IOException {
        File otIndexFile = idxFile[SwordConstants.TESTAMENT_OLD];
        BufferedOutputStream otIdxBos = new BufferedOutputStream(new FileOutputStream(otIndexFile, false));

        try {
            for (int i = 0; i < SwordConstants.ORDINAL_MAT11; i++) {
                writeInitialIndex(otIdxBos);
            }
        } finally {
            otIdxBos.close();
        }

        File ntIndexFile = idxFile[SwordConstants.TESTAMENT_NEW];
        BufferedOutputStream ntIdxBos = new BufferedOutputStream(new FileOutputStream(ntIndexFile, false));
        try {
            int totVerses = BibleInfo.versesInBible();
            for (int i = SwordConstants.ORDINAL_MAT11; i < totVerses; i++) {
                writeInitialIndex(ntIdxBos);
            }
        } finally {
            ntIdxBos.close();
        }
    }

    private void createIncfile() throws IOException, BookException {
        File tempIncfile = new File(getExpandedDataPath().getPath() + File.separator + INCFILE);
        if (!tempIncfile.exists() && !tempIncfile.createNewFile()) {
            throw new IOException("Could not create incfile file."); //$NON-NLS-1$
        }
        this.incfile = tempIncfile;
    }

    private void prepopulateIncfile() throws IOException {
        writeIncfile(1);
    }

    private void writeIncfile(int value) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.incfile, false);
            fos.write(littleEndian32BitByteArrayFromInt(value));
        } catch (FileNotFoundException e) {
            log.error("Error on writing to incfile, file should exist already!"); //$NON-NLS-1$
            log.error(e.getMessage());
        } finally {
            fos.close();
        }
    }

    private int readIncfile() throws IOException {
        int ret = -1;
        if (this.incfile != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(this.incfile);
                byte[] buffer = new byte[4];
                if (fis.read(buffer) != 4) {
                    log.error("Read data is not of appropriate size of 4 bytes!"); //$NON-NLS-1$
                    throw new IOException("Incfile is not 4 bytes long"); //$NON-NLS-1$
                }
                ret = SwordUtil.decodeLittleEndian32(buffer, 0);
            } catch (FileNotFoundException e) {
                log.error("Error on writing to incfile, file should exist already!"); //$NON-NLS-1$
                log.error(e.getMessage());
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }

        return ret;
    }

    private void writeInitialIndex(BufferedOutputStream outStream) throws IOException {
        outStream.write(littleEndian32BitByteArrayFromInt(0)); // offset
        outStream.write(littleEndian16BitByteArrayFromShort((short) 0)); // length
    }

    private byte[] readTextDataFile(File dataFile) throws IOException {
        BufferedInputStream inStream = null;
        try {
            int len = (int) dataFile.length();
            byte[] textData = new byte[len];
            inStream = new BufferedInputStream(new FileInputStream(dataFile));
            if (inStream.read(textData) != len) {
                log.error("Read data is not of appropriate size of " + len + " bytes!"); //$NON-NLS-1$ //$NON-NLS-2$
                throw new IOException("data is not " + len + " bytes long"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return textData;
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage());
            throw new IOException("Could not read text data file, file not found: " + dataFile.getName()); //$NON-NLS-1$
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    private void writeTextDataFile(File dataFile, byte[] textData) throws IOException {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(dataFile, false));
            bos.write(textData);
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    private byte[] littleEndian32BitByteArrayFromInt(int val) {
        byte[] buffer = new byte[4];
        SwordUtil.encodeLittleEndian32(val, buffer, 0);
        return buffer;
    }

    private byte[] littleEndian16BitByteArrayFromShort(short val) {
        byte[] buffer = new byte[2];
        SwordUtil.encodeLittleEndian16(val, buffer, 0);
        return buffer;
    }

    private static final Logger log = Logger.getLogger(RawFileBackend.class);
    private static final String INCFILE = "incfile"; //$NON-NLS-1$

    private File incfile;
    private int incfileValue;
}
