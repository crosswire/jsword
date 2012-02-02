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
 * Copyright: 2009-2012
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
import java.io.RandomAccessFile;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * The basic structure of the index is as follows:
 * <ul>
 * <li><strong>incfile</strong> --
 *      Is initialized with 1 and is incremented once for each non-linked verse
 *      that is actually stored in the Book.</li>
 * <li><strong>idx</strong> --
 *      There is one index file for each testament having verses, named nt and ot.
 *      These index files contain offsets into the corresponding data file.
 *      The idx files are indexed by the ordinal value of the verse within the Testament
 *      for the Book's versification.</li>
 * <li><strong>dat</strong> --
 *      There is a data file for each testament having verses, named nt.vss and ot.vss.
 *      These data files do not contain the verses but rather the file names that
 *      contain the verse text.</li>
 * <li><strong>verse</strong> --
 *      For each stored verse there is a file containing the verse text.
 *      The filename is a zero padded number corresponding to the current increment
 *      from incfile, when it was created. It is this 7 character name that is stored
 *      in a dat file.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class RawFileBackend extends RawBackend {

    public RawFileBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd, datasize);

        initIncFile();
        try {
            incfileValue = readIncfile();
        } catch (IOException e) {
            log.error("Error on reading incfile!");
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    @Override
    public String getRawText(Key key) throws BookException {
        return super.getRawText(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawBackend#getEntry(java.lang.String, org.crosswire.jsword.versification.Testament, long)
     */
    @Override
    protected String getEntry(String name, Testament testament, long index) throws IOException {
        RandomAccessFile idxRaf = otIdxRaf;
        RandomAccessFile txtRaf = otTxtRaf;
        if (testament == Testament.NEW) {
            idxRaf = ntIdxRaf;
            txtRaf = ntTxtRaf;
        }

        DataIndex dataIndex = getIndex(idxRaf, index);
        int size = dataIndex.getSize();
        if (size == 0) {
            return "";
        }

        if (size < 0) {
            log.error("In " + getBookMetaData().getInitials() + ": Verse " + name + " has a bad index size of " + size);
            return "";
        }

        try {
            File dataFile = getDataTextFile(txtRaf, dataIndex);
            byte[] textBytes = readTextDataFile(dataFile);
            decipher(textBytes);
            return SwordUtil.decode(name, textBytes, getBookMetaData().getBookCharset());
        } catch (BookException e) {
            throw new IOException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawBackend#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    @Override
    public void setRawText(Key key, String text) throws BookException, IOException {
        checkActive();

        Verse verse = KeyUtil.getVerse(key);
        String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);
        int index = v11n.getOrdinal(verse);
        Testament testament = v11n.getTestament(index);
        index = v11n.getTestamentOrdinal(index);
        RandomAccessFile idxRaf = otIdxRaf;
        RandomAccessFile txtRaf = otTxtRaf;
        File txtFile = otTxtFile;
        if (testament == Testament.NEW) {
            idxRaf = ntIdxRaf;
            txtRaf = ntTxtRaf;
            txtFile = ntTxtFile;
        }

        DataIndex dataIndex = getIndex(idxRaf, index);
        File dataFile;
        if (dataIndex.getSize() == 0) {
            dataFile = createDataTextFile(incfileValue);
            updateIndexFile(idxRaf, index, txtRaf.length());
            updateDataFile(incfileValue, txtFile);
            checkAndIncrementIncfile(incfileValue);
        } else {
            dataFile = getDataTextFile(txtRaf, dataIndex);
        }

        byte[] textData = text.getBytes("UTF-8");
        encipher(textData);
        writeTextDataFile(dataFile, textData);
    }

    @Override
    public void setAliasKey(Key alias, Key source) throws IOException {
        Verse aliasVerse = KeyUtil.getVerse(alias);
        Verse sourceVerse = KeyUtil.getVerse(source);
        String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);
        int aliasIndex = v11n.getOrdinal(aliasVerse);
        Testament testament = v11n.getTestament(aliasIndex);
        aliasIndex = v11n.getTestamentOrdinal(aliasIndex);
        RandomAccessFile idxRaf = otIdxRaf;
        if (testament == Testament.NEW) {
            idxRaf = ntIdxRaf;
        }

        int sourceOIndex = v11n.getOrdinal(sourceVerse);
        sourceOIndex = v11n.getTestamentOrdinal(sourceOIndex);
        DataIndex dataIndex = getIndex(idxRaf, sourceOIndex);

        // Only the index is updated to point to the same place as what is linked.
        updateIndexFile(idxRaf, aliasIndex, dataIndex.getOffset());
    }

    private void initIncFile() {
        try {
            File tempIncfile = new File(getExpandedDataPath().getPath() + File.separator + INCFILE);
            if (tempIncfile.exists()) {
                this.incfile = tempIncfile;
            }
        } catch (BookException e) {
            log.error("Error on checking incfile: " + e.getMessage());
        }
    }

    private File createDataTextFile(int index) throws BookException, IOException {
        String dataPath = getExpandedDataPath().getPath();
        dataPath += File.separator + String.format("%07d", Integer.valueOf(index));
        File dataFile = new File(dataPath);
        if (!dataFile.exists() && !dataFile.createNewFile()) {
            throw new IOException("Could not create data file.");
        }
        return dataFile;
    }

    /**
     * Gets the Filename for the File having the verse text.
     * 
     * @param txtRaf The random access file containing the file names for the verse storage.
     * @param dataIndex The index of where to get the data
     * @return the file having the verse text.
     * @throws IOException
     */
    private String getTextFilename(RandomAccessFile txtRaf, DataIndex dataIndex) throws IOException {
        // data size to be read from the data file (ot or nt) should be 9 bytes
        // this will be the filename of the actual text file "\r\n"
        byte[] data = SwordUtil.readRAF(txtRaf, dataIndex.getOffset(), dataIndex.getSize());
        decipher(data);
        if (data.length == 7) {
            return new String(data, 0, 7);
        }
        log.error("Read data is not of appropriate size of 9 bytes!");
        throw new IOException("Datalength is not 9 bytes!");
    }

    /**
     * Gets the File having the verse text.
     * 
     * @param txtRaf The random access file containing the file names for the verse storage.
     * @param dataIndex The index of where to get the data
     * @return the file having the verse text.
     * @throws IOException
     * @throws BookException
     */
    private File getDataTextFile(RandomAccessFile txtRaf, DataIndex dataIndex) throws IOException, BookException {
        String dataFilename = getTextFilename(txtRaf, dataIndex);
        String dataPath = getExpandedDataPath().getPath() + File.separator + dataFilename;
        return new File(dataPath);
    }


    protected void updateIndexFile(RandomAccessFile idxRaf, long index, long dataFileStartPosition) throws IOException {
        long indexFileWriteOffset = index * entrysize;
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

        SwordUtil.writeRAF(idxRaf, indexFileWriteOffset, indexFileWriteData);
    }

    protected void updateDataFile(long ordinal, File txtFile) throws IOException {
        String fileName = String.format("%07d\r\n", Long.valueOf(ordinal));
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(txtFile, true));
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
    @Override
    public void create() throws IOException, BookException {
        super.create();
        createDataFiles();
        createIndexFiles();
        createIncfile();

        checkActive();

        prepopulateIndexFiles();
        prepopulateIncfile();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawBackend#isWritable()
     */
    @Override
    public boolean isWritable() {
        File incFile = this.incfile;

        if (otTxtFile.exists() && otTxtFile.canRead() && otTxtFile.canWrite() && ntTxtFile.exists() && ntTxtFile.canRead() && ntTxtFile.canWrite()
                && otIdxFile.exists() && otIdxFile.canRead() && otIdxFile.canWrite() && ntIdxFile.exists() && ntIdxFile.canRead()
                && ntIdxFile.canWrite() && incFile.exists() && incFile.canRead() && incFile.canWrite())
        {
            return true;
        }
        return false;
    }

    private void createDataFiles() throws IOException, BookException {
        String path = getExpandedDataPath().getPath();

        File otTextFile = new File(path + File.separator + SwordConstants.FILE_OT);
        if (!otTextFile.exists() && !otTextFile.createNewFile()) {
            throw new IOException("Could not create ot text file.");
        }

        File ntTextFile = new File(path + File.separator + SwordConstants.FILE_NT);
        if (!ntTextFile.exists() && !ntTextFile.createNewFile()) {
            throw new IOException("Could not create nt text file.");
        }
    }

    private void createIndexFiles() throws IOException, BookException {
        String path = getExpandedDataPath().getPath();
        File otIndexFile = new File(path + File.separator + SwordConstants.FILE_OT + SwordConstants.EXTENSION_VSS);
        if (!otIndexFile.exists() && !otIndexFile.createNewFile()) {
            throw new IOException("Could not create ot index file.");
        }

        File ntIndexFile = new File(path + File.separator + SwordConstants.FILE_NT + SwordConstants.EXTENSION_VSS);
        if (!ntIndexFile.exists() && !ntIndexFile.createNewFile()) {
            throw new IOException("Could not create nt index file.");
        }
    }

    private void prepopulateIndexFiles() throws IOException {
        String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);
        int otCount = v11n.getCount(Testament.OLD);
        int ntCount = v11n.getCount(Testament.NEW) + 1;
        BufferedOutputStream otIdxBos = new BufferedOutputStream(new FileOutputStream(otIdxFile, false));
        try {
            for (int i = 0; i < otCount; i++) {
                writeInitialIndex(otIdxBos);
            }
        } finally {
            otIdxBos.close();
        }

        BufferedOutputStream ntIdxBos = new BufferedOutputStream(new FileOutputStream(ntIdxFile, false));
        try {
            for (int i = 0; i < ntCount; i++) {
                writeInitialIndex(ntIdxBos);
            }
        } finally {
            ntIdxBos.close();
        }
    }

    private void createIncfile() throws IOException, BookException {
        File tempIncfile = new File(getExpandedDataPath().getPath() + File.separator + INCFILE);
        if (!tempIncfile.exists() && !tempIncfile.createNewFile()) {
            throw new IOException("Could not create incfile file.");
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
            log.error("Error on writing to incfile, file should exist already!");
            log.error(e.getMessage());
        } finally {
            if (fos != null) {
                fos.close();
            }
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
                    log.error("Read data is not of appropriate size of 4 bytes!");
                    throw new IOException("Incfile is not 4 bytes long");
                }
                ret = SwordUtil.decodeLittleEndian32(buffer, 0);
            } catch (FileNotFoundException e) {
                log.error("Error on writing to incfile, file should exist already!");
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
                log.error("Read data is not of appropriate size of " + len + " bytes!");
                throw new IOException("data is not " + len + " bytes long");
            }
            return textData;
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage());
            throw new IOException("Could not read text data file, file not found: " + dataFile.getName());
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

    private static final String INCFILE = "incfile";

    private File incfile;
    private int incfileValue;

    private static final Logger log = Logger.getLogger(RawFileBackend.class);
}
