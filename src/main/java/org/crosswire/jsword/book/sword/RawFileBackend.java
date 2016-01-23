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
 * © CrossWire Bible Society, 2009 - 2016
 *
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

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawBackendState;
import org.crosswire.jsword.book.sword.state.RawFileBackendState;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Raw File format that allows for each verse to have it's own storage. The
 * basic structure of the index is as follows:
 * <ul>
 * <li><strong>incfile</strong> -- Is initialized with 1 and is incremented once
 * for each non-linked verse that is actually stored in the Book.</li>
 * <li><strong>idx</strong> -- There is one index file for each testament having
 * verses, named nt and ot. These index files contain offsets into the
 * corresponding data file. The idx files are indexed by the ordinal value of
 * the verse within the Testament for the Book's versification.</li>
 * <li><strong>dat</strong> -- There is a data file for each testament having
 * verses, named nt.vss and ot.vss. These data files do not contain the verses
 * but rather the file names that contain the verse text.</li>
 * <li><strong>verse</strong> -- For each stored verse there is a file
 * containing the verse text. The filename is a zero padded number corresponding
 * to the current increment from incfile, when it was created. It is this 7
 * character name that is stored in a dat file.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author mbergmann
 * @author DM Smith
 */
public class RawFileBackend extends RawBackend<RawFileBackendState> {

    public RawFileBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd, datasize);
    }

    @Override
    public RawFileBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getRawFileBackendState(getBookMetaData());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.RawBackend#getEntry(java.lang.String, org.crosswire.jsword.versification.Testament, long)
     */
    @Override
    protected String getEntry(RawBackendState state, String name, Testament testament, long index) throws IOException {
        RandomAccessFile idxRaf;
        RandomAccessFile txtRaf;
        idxRaf = state.getIdxRaf(testament);
        txtRaf = state.getTextRaf(testament);

        DataIndex dataIndex = getIndex(idxRaf, index);
        int size = dataIndex.getSize();
        if (size == 0) {
            return "";
        }

        if (size < 0) {
            log.error("In {}: Verse {} has a bad index size of {}.", getBookMetaData().getInitials(), name, Integer.toString(size));
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
     * 
     */
    public void setRawText(RawFileBackendState state, Key key, String text) throws BookException, IOException {

        String v11nName = getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION);
        Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse verse = KeyUtil.getVerse(key);
        int index = verse.getOrdinal();
        Testament testament = v11n.getTestament(index);
        index = v11n.getTestamentOrdinal(index);

        RandomAccessFile idxRaf = state.getIdxRaf(testament);
        RandomAccessFile txtRaf = state.getTextRaf(testament);
        File txtFile = state.getTextFile(testament);

        DataIndex dataIndex = getIndex(idxRaf, index);
        File dataFile;
        if (dataIndex.getSize() == 0) {
            dataFile = createDataTextFile(state.getIncfileValue());
            updateIndexFile(idxRaf, index, txtRaf.length());
            updateDataFile(state.getIncfileValue(), txtFile);
            checkAndIncrementIncfile(state, state.getIncfileValue());
        } else {
            dataFile = getDataTextFile(txtRaf, dataIndex);
        }

        byte[] textData = text.getBytes("UTF-8");
        encipher(textData);
        writeTextDataFile(dataFile, textData);
    }

    public void setAliasKey(RawFileBackendState state, Key alias, Key source) throws IOException {
        String v11nName = getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION);
        Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse aliasVerse = KeyUtil.getVerse(alias);
        Verse sourceVerse = KeyUtil.getVerse(source);
        int aliasIndex = aliasVerse.getOrdinal();
        Testament testament = v11n.getTestament(aliasIndex);
        aliasIndex = v11n.getTestamentOrdinal(aliasIndex);
        RandomAccessFile idxRaf = state.getIdxRaf(testament);

        int sourceOIndex = sourceVerse.getOrdinal();
        sourceOIndex = v11n.getTestamentOrdinal(sourceOIndex);
        DataIndex dataIndex = getIndex(idxRaf, sourceOIndex);

        // Only the index is updated to point to the same place as what is
        // linked.
        updateIndexFile(idxRaf, aliasIndex, dataIndex.getOffset());
    }

    private File createDataTextFile(int index) throws BookException, IOException {
        String dataPath = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath();
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
     * @param txtRaf
     *            The random access file containing the file names for the verse
     *            storage.
     * @param dataIndex
     *            The index of where to get the data
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
     * @param txtRaf
     *            The random access file containing the file names for the verse
     *            storage.
     * @param dataIndex
     *            The index of where to get the data
     * @return the file having the verse text.
     * @throws IOException
     * @throws BookException
     */
    private File getDataTextFile(RandomAccessFile txtRaf, DataIndex dataIndex) throws IOException, BookException {
        String dataFilename = getTextFilename(txtRaf, dataIndex);
        String dataPath = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath() + File.separator + dataFilename;
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
            bos.write(fileName.getBytes(getBookMetaData().getBookCharset()));
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    private void checkAndIncrementIncfile(RawFileBackendState state, int index) throws IOException {
        if (index >= state.getIncfileValue()) {
            int incValue = index + 1;

            // FIXME(CJB) this portion of code is unsafe for concurrency
            // operations, but without knowing the
            // reason for the inc file, I would guess it unlikely to be a
            // problem
            // this essentially destroys consistency of the state
            state.setIncfileValue(incValue);
            writeIncfile(state, incValue);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.sword.RawBackend#create()
     */
    // FIXME(CJB) : DM - API change to pass in state, or is this a one-off for
    // which we want to release resources afterwards (opted for the second
    // option)
    @Override
    public void create() throws IOException, BookException {
        super.create();

        createDataFiles();
        createIndexFiles();

        RawFileBackendState state = null;
        try {
            state = initState();

            createIncfile(state);

            prepopulateIndexFiles(state);
            prepopulateIncfile(state);
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    private void createDataFiles() throws IOException, BookException {
        String path = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath();

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
        String path = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath();
        File otIndexFile = new File(path + File.separator + SwordConstants.FILE_OT + SwordConstants.EXTENSION_VSS);
        if (!otIndexFile.exists() && !otIndexFile.createNewFile()) {
            throw new IOException("Could not create ot index file.");
        }

        File ntIndexFile = new File(path + File.separator + SwordConstants.FILE_NT + SwordConstants.EXTENSION_VSS);
        if (!ntIndexFile.exists() && !ntIndexFile.createNewFile()) {
            throw new IOException("Could not create nt index file.");
        }
    }

    private void prepopulateIndexFiles(RawFileBackendState state) throws IOException {

        String v11nName = getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION);
        Versification v11n = Versifications.instance().getVersification(v11nName);
        int otCount = v11n.getCount(Testament.OLD);
        int ntCount = v11n.getCount(Testament.NEW) + 1;
        BufferedOutputStream otIdxBos = new BufferedOutputStream(new FileOutputStream(state.getIdxFile(Testament.OLD), false));
        try {
            for (int i = 0; i < otCount; i++) {
                writeInitialIndex(otIdxBos);
            }
        } finally {
            otIdxBos.close();
        }

        BufferedOutputStream ntIdxBos = new BufferedOutputStream(new FileOutputStream(state.getIdxFile(Testament.NEW), false));
        try {
            for (int i = 0; i < ntCount; i++) {
                writeInitialIndex(ntIdxBos);
            }
        } finally {
            ntIdxBos.close();
        }
    }

    private void createIncfile(RawFileBackendState state) throws IOException, BookException {
        File tempIncfile = new File(SwordUtil.getExpandedDataPath(getBookMetaData()).getPath() + File.separator + RawFileBackendState.INCFILE);
        if (!tempIncfile.exists() && !tempIncfile.createNewFile()) {
            throw new IOException("Could not create incfile file.");
        }
        state.setIncfile(tempIncfile);
    }

    private void prepopulateIncfile(RawFileBackendState state) throws IOException {
        writeIncfile(state, 1);
    }

    private void writeIncfile(RawFileBackendState state, int value) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(state.getIncfile(), false);
            fos.write(littleEndian32BitByteArrayFromInt(value));
        } catch (FileNotFoundException e) {
            log.error("Error on writing to incfile, file should exist already!", e);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
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
                log.error("Read data is not of appropriate size of {} bytes!", Integer.toString(len));
                throw new IOException("data is not " + len + " bytes long");
            }
            return textData;
        } catch (FileNotFoundException ex) {
            log.error("Could not read text data file, file not found: {}", dataFile.getName(), ex);
            throw ex;
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

    private static final Logger log = LoggerFactory.getLogger(RawFileBackend.class);
}
