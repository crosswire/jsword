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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.compress.CompressorType;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.ZVerseBackendState;
import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Integer.parseInt;

/**
 * A backend to read compressed data verse based files. While the text file
 * contains data compressed with ZIP or LZSS, it cannot be uncompressed using a
 * stand alone zip utility, such as WinZip or gzip. The reason for this is that
 * the data file is a concatenation of blocks of compressed data.
 * 
 * <p>
 * The blocks can either be "b", book (aka testament); "c", chapter or "v",
 * verse. The choice is a matter of trade offs. The program needs to uncompress
 * a block into memory. Having it at the book level is very memory expensive.
 * Having it at the verse level is very disk expensive, but takes the least
 * amount of memory. The most common is chapter.
 * </p>
 * 
 * <p>
 * In order to find the data in the text file, we need to find the block. The
 * first index (idx) is used for this. Each verse is indexed to a tuple (block
 * number, verse start, verse size). This data allows us to find the correct
 * block, and to extract the verse from the uncompressed block, but it does not
 * help us uncompress the block.
 * </p>
 * 
 * <p>
 * Once the block is known, then the next index (comp) gives the location of the
 * compressed block, its compressed size and its uncompressed size.
 * </p>
 * 
 * <p>
 * There are 3 files for each testament, 2 (idx and comp) are indexes into the
 * third (text) which contains the data. The key into each index is the verse
 * index within that testament, which is determined by book, chapter and verse
 * of that key.
 * </p>
 * 
 * <p>
 * All unsigned numbers are stored 2-complement, little endian.
 * </p>
 * <p>
 * Then proceed as follows, at all times working on the set of files for the
 * testament in question:
 * </p>
 * 
 * The three files are laid out in the following fashion:
 * <ul>
 * <li>The idx file has one entry per verse in the versification. The number
 * of verses varies by versification and testament. Each entry describes the
 * compressed block in which it is found, the start of the verse in the
 * uncompressed block and the length of the verse.
 * <ul>
 * <li>Block number - 32-bit/4-bytes - the number of the entry in the comp file.</li>
 * <li>Verse start - 32 bit/4-bytes - the start of the verse in the uncompressed block in the dat file.</li>
 * <li>Verse length - 16 bit/4-bytes - the length of the verse in the uncompressed block from the dat file.</li>
 * </ul>
 * Algorithm:
 * <ul>
 * <li>Given the ordinal value of the verse, seek to the ordinal * 10 and read 10 bytes.
 * <li>Decode the 10 bytes as Block Number, Verse start and length</li>
 * </ul>
 * </li>
 * <li>The comp file has one entry per block.
 * Each entry describes the location of a compressed block,
 * giving its start and size in the next file.
 * <ul>
 * <li>Block Start - 32-bit/4-byte - the start of the block in the dat file</li>
 * <li>Compressed Block Size - 32-bit/4-byte - the size of the compressed block in the dat file</li>
 * <li>Uncompressed Block Size - 32-bit/4-byte - the size of the block after uncompressing</li>
 * </ul>
 * Algorithm:
 * <ul>
 * <li>Given a block number, seek to block-index * 12 and read 12 bytes</li>
 * <li>Decode the 12 bytes as Block Start, Compressed Block Size and Uncompressed Block Size</li>
 * </ul>
 * </li>
 * <li> The dat file is compressed blocks of verses.
 * <br>
 * Algorithm:
 * <ul>
 * <li>Given the entry from the comp file, seek to the start and read the indicated compressed block size</li>
 * <li>If the book is enciphered it, decipher it.</li>
 * <li>Uncompress the block, using the uncompressed size as an optimization.</li>
 * <li>Using the verse start, seek to that location in the uncompressed block and read the indicated verse size.</li>
 * <li>Convert the bytes to a String using the books indicated charset.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 * @author DM Smith
 */
public class ZVerseBackend extends AbstractBackend<ZVerseBackendState> {
    /**
     * Simple ctor
     */
    public ZVerseBackend(SwordBookMetaData sbmd, BlockType blockType) {
        super(sbmd);
        this.blockType = blockType;
    }

    /* This method assumes single keys. It is the responsibility of the caller to provide the iteration. 
     * 
     * FIXME: this could be refactored to push the iterations down, but no performance benefit would be gained since we have a manager that keeps the file accesses open
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#contains(org.crosswire.jsword.passage.Key)
     */
    @Override
    public boolean contains(Key key) {
        return getRawTextLength(key) > 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#size(org.crosswire.jsword.passage.Key)
     */
    @Override
    public int getRawTextLength(Key key) {
        ZVerseBackendState rafBook = null;
        try {
            rafBook = initState();

            String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
            Versification v11n = Versifications.instance().getVersification(v11nName);
            Verse verse = KeyUtil.getVerse(key);

            int index = verse.getOrdinal();
            Testament testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);

            RandomAccessFile idxRaf = rafBook.getIdxRaf(testament);

            // If Bible does not contain the desired testament, then false
            if (idxRaf == null) {
                return 0;
            }

            // 10 because the index is 10 bytes long for each verse
            byte[] temp = SwordUtil.readRAF(idxRaf, 1L * index * IDX_ENTRY_SIZE, IDX_ENTRY_SIZE);

            // If the Bible does not contain the desired verse, return nothing.
            // Some Bibles have different versification, so the requested verse
            // may not exist.
            if (temp == null || temp.length == 0) {
                return 0;
            }

            // The data is little endian - extract the verseSize
            return SwordUtil.decodeLittleEndian16(temp, 8);

        } catch (IOException e) {
            return 0;
        } catch (BookException e) {
            // FIXME(CJB): fail silently as before, but i don't think this is
            // correct behaviour - would cause API changes
            log.error("Unable to ascertain key validity", e);
            return 0;
        } finally {
            OpenFileStateManager.instance().release(rafBook);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getGlobalKeyList()
     */
    @Override
    public Key getGlobalKeyList() throws BookException {
        ZVerseBackendState rafBook = null;
        try {
            rafBook = initState();

            String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
            Versification v11n = Versifications.instance().getVersification(v11nName);

            Testament[] testaments = new Testament[] {
                    Testament.OLD, Testament.NEW
            };

            BitwisePassage passage = new RocketPassage(v11n);
            passage.raiseEventSuppresion();
            passage.raiseNormalizeProtection();

            for (Testament currentTestament : testaments) {
                RandomAccessFile idxRaf = rafBook.getIdxRaf(currentTestament);

                // If Bible does not contain the desired testament, then false
                if (idxRaf == null) {
                    // no keys in this testament
                    continue;
                }

                int maxIndex = v11n.getCount(currentTestament);
                // For unknown reason the following three books need to add 1 to the maxIndex.  If not the last verse (Rev 22:20 or Rev 22:21) will not be included.
                if (currentTestament.equals(Testament.NEW)) //&&
//                     ((this.getBookMetaData().getInitials().equals("KJVA")) ||
//                      (this.getBookMetaData().getInitials().equals("KJV")) ||
//                      (this.getBookMetaData().getInitials().equals("ESV_th")) ||
//                      (this.getBookMetaData().getInitials().equals("NASB2020")) ||
//                      (this.getBookMetaData().getInitials().equals("THGNT")) ||
//                      (this.getBookMetaData().getInitials().equals("SBLG_th"))) )
                    maxIndex ++;

                // Read in the whole index, a few hundred Kb at most.
                byte[] temp = SwordUtil.readRAF(idxRaf, 0, IDX_ENTRY_SIZE * maxIndex);

                // For each entry of 10 bytes, the length of the verse in bytes
                // is in the last 2 bytes. If both bytes are 0, then there is no content.
                for (int ii = 0; ii < temp.length; ii += IDX_ENTRY_SIZE) {
                    // This can be simplified to temp[ii + 8] == 0 && temp[ii + 9] == 0.
                    // int verseSize = SwordUtil.decodeLittleEndian16(temp, ii + 8);
                    // if (verseSize > 0) {
                    if (temp[ii + 8] != 0 || temp[ii + 9] != 0) {
                        int ordinal = ii / IDX_ENTRY_SIZE;
                        passage.addVersifiedOrdinal(v11n.getOrdinal(currentTestament, ordinal));
                    }
                }
            }

            passage.lowerNormalizeProtection();
            passage.lowerEventSuppressionAndTest();

            return passage;
        } catch (IOException e) {
            throw new BookException(JSMsg.gettext("Unable to read key list from book."));
        } finally {
            OpenFileStateManager.instance().release(rafBook);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.StatefulFileBackedBackend#initState()
     */
    public ZVerseBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getZVerseBackendState(getBookMetaData(), blockType);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.StatefulFileBackedBackend#readRawContent(org.crosswire.jsword.book.sword.state.OpenFileState, org.crosswire.jsword.passage.Key)
     */
    public String readRawContent(ZVerseBackendState rafBook, Key key) throws IOException {

        SwordBookMetaData bookMetaData = getBookMetaData();
        final String charset = bookMetaData.getBookCharset();
        final String compressType = (String) bookMetaData.getProperty(ConfigEntryType.COMPRESS_TYPE);

        final String v11nName = getBookMetaData().getProperty(ConfigEntryType.VERSIFICATION).toString();
        final Versification v11n = Versifications.instance().getVersification(v11nName);
        Verse verse = KeyUtil.getVerse(key);

        int index = verse.getOrdinal();
        final Testament testament = v11n.getTestament(index);
        index = v11n.getTestamentOrdinal(index);

        final RandomAccessFile idxRaf;
        final RandomAccessFile compRaf;
        final RandomAccessFile textRaf;

        idxRaf = rafBook.getIdxRaf(testament);
        compRaf = rafBook.getCompRaf(testament);
        textRaf = rafBook.getTextRaf(testament);

        // If Bible does not contain the desired testament, return nothing.
        if (idxRaf == null) {
            return "";
        }

        //dumpIdxRaf(v11n, 0, compRaf);
        //dumpCompRaf(idxRaf);
        // 10 because the index is 10 bytes long for each verse
        byte[] temp = SwordUtil.readRAF(idxRaf, 1L * index * IDX_ENTRY_SIZE, IDX_ENTRY_SIZE);

        // If the Bible does not contain the desired verse, return nothing.
        // Some Bibles have different versification, so the requested verse
        // may not exist.
        if (temp == null || temp.length == 0) {
            return "";
        }

        // The data is little endian - extract the blockNum, verseStart
        // and
        // verseSize
        final long blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
        final int verseStart = SwordUtil.decodeLittleEndian32(temp, 4);
        final int verseSize = SwordUtil.decodeLittleEndian16(temp, 8);

        // Can we get the data from the cache
        byte[] uncompressed = null;
        if (blockNum == rafBook.getLastBlockNum() && testament == rafBook.getLastTestament()) {
            uncompressed = rafBook.getLastUncompressed();
        } else {
            // Then seek using this index into the idx file
            temp = SwordUtil.readRAF(compRaf, blockNum * COMP_ENTRY_SIZE, COMP_ENTRY_SIZE);
            if (temp == null || temp.length == 0) {
                return "";
            }

            final int blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
            final int blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
            final int uncompressedSize = SwordUtil.decodeLittleEndian32(temp, 8);

            // Read from the data file.
            final byte[] data = SwordUtil.readRAF(textRaf, blockStart, blockSize);

            decipher(data);

            uncompressed = CompressorType.fromString(compressType).getCompressor(data).uncompress(uncompressedSize).toByteArray();

            // cache the uncompressed data for next time
            rafBook.setLastBlockNum(blockNum);
            rafBook.setLastTestament(testament);
            rafBook.setLastUncompressed(uncompressed);
        }

        // and cut out the required section.
        final byte[] chopped = new byte[verseSize];
        System.arraycopy(uncompressed, verseStart, chopped, 0, verseSize);
        String resultFromJSword = SwordUtil.decode(key.getName(), chopped, charset);

        if ((verse.getBook().ordinal() < 69) &&
            (bookMetaData.hasFeature(FeatureType.STRONGS_NUMBERS))) // Not Deutro canon and Strong feature in Bible translation
            resultFromJSword = augmentDStrong(resultFromJSword, index, testament, v11nName, bookMetaData.getInitials(),
                    verse, v11nName);
        return resultFromJSword;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(ZVerseBackendState rafBook, Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(ZVerseBackendState rafBook, Key key, String text) throws BookException, IOException {
        throw new UnsupportedOperationException();
    }

    /** 
     * Experimental code.
     * 
     * @param v11n
     * @param ordinalStart
     * @param raf
     */
    public void dumpIdxRaf(Versification v11n, int ordinalStart, RandomAccessFile raf) {
        long end = -1;
        try {
            end = raf.length();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int i = ordinalStart;
        StringBuilder buf = new StringBuilder();
        System.out.println("osisID\tblock\tstart\tsize");
        for (long offset = 0; offset < end; offset += IDX_ENTRY_SIZE) {
            // 10 because the index is 10 bytes long for each verse
            byte[] temp = null;
            try {
                temp = SwordUtil.readRAF(raf, offset, IDX_ENTRY_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If the Bible does not contain the desired verse, return nothing.
            // Some Bibles have different versification, so the requested verse
            // may not exist.
            long blockNum = -1;
            int verseStart = -1;
            int verseSize = -1;
            if (temp != null && temp.length > 0) {
                // The data is little endian - extract the blockNum, verseStart and verseSize
                blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
                verseStart = SwordUtil.decodeLittleEndian32(temp, 4);
                verseSize = SwordUtil.decodeLittleEndian16(temp, 8);
            }
            buf.setLength(0);
            buf.append(v11n.decodeOrdinal(i++).getOsisID());
            buf.append('\t');
            buf.append(blockNum);
            buf.append('\t');
            buf.append(verseStart);
            buf.append('\t');
            buf.append(verseSize);
            System.out.println(buf.toString());
        }
    }

    /**
     * Experimental code.
     * 
     * @param raf
     */
    public void dumpCompRaf(RandomAccessFile raf) {
        long end = -1;
        try {
            end = raf.length();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int blockNum = 0;
        StringBuilder buf = new StringBuilder();
        System.out.println("block\tstart\tsize\tuncompressed");
        for (long offset = 0; offset < end; offset += COMP_ENTRY_SIZE) {
            // 12 because the index is 12 bytes long for each verse
            byte[] temp = null;
            try {
                temp = SwordUtil.readRAF(raf, offset, COMP_ENTRY_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If the Bible does not contain the desired verse, return nothing.
            // Some Bibles have different versification, so the requested verse
            // may not exist.
            int blockStart = -1;
            int blockSize = -1;
            int uncompressedSize = -1;
            if (temp != null && temp.length > 0) {
                // The data is little endian - extract the blockNum, verseStar and verseSize
                 blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                 blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
                 uncompressedSize = SwordUtil.decodeLittleEndian32(temp, 8);
            }
            buf.setLength(0);
            buf.append(blockNum);
            buf.append('\t');
            buf.append(blockStart);
            buf.append('\t');
            buf.append(blockSize);
            buf.append('\t');
            buf.append(uncompressedSize);
            System.out.println(buf.toString());
        }
    }

    /**
     * Whether the book is blocked by Book, Chapter or Verse.
     */
    private final BlockType blockType;

    /**
     * How many bytes in the idx index?
     */
    private static final int IDX_ENTRY_SIZE = 10;

    /**
     * How many bytes in the comp index?
     */
    private static final int COMP_ENTRY_SIZE = 12;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ZVerseBackend.class);

    private String augmentDStrong(final String resultFromJSword, int index, final Testament testament,
                                  final String versificationName, final String translation, final Verse verse,
                                  final String v11nName) {

        if (!v11nName.equals("Leningrad") && !v11nName.equals("NRSV")) {
            if (v11nName.equals("MT")) {
                final Versification v11nLeningrad = Versifications.instance().getVersification("Leningrad");
                try {
                    Verse leningradKey = VerseFactory.fromString(v11nLeningrad, verse.getOsisID());
                    index = leningradKey.getOrdinal();
                } catch (NoSuchVerseException e) {
                    System.out.println("Unable to look up strongs " + e);
                    return resultFromJSword;
                }
            } else {
                final Versification v11nNRSV = Versifications.instance().getVersification("NRSV");
                try {
                    Verse nrsvKey = VerseFactory.fromString(v11nNRSV, verse.getOsisID());
                    index = nrsvKey.getOrdinal();
                    index = v11nNRSV.getTestamentOrdinal(index);
                } catch (NoSuchVerseException e) {
                    System.out.println("Unable to look up strongs " + e);
                    return resultFromJSword;
                }
            }
        }

        int[] ordinals;
        boolean combineAugStrongOfTwoVerses = false;
        if (testament == Testament.OLD) {
            ordinals = OpenFileStateManager.osArray.OHBOrdinal;
            if ((!versificationName.equals("MT")) && (!versificationName.equals("Leningrad"))) {
                if (index >= OpenFileStateManager.osArray.OTRSVOrdinal.length)
                    return resultFromJSword;
                short indexToOTRSVOrdinal = (short) OpenFileStateManager.osArray.OTRSVOrdinal[index];
                if (indexToOTRSVOrdinal < 0) {
                    indexToOTRSVOrdinal = (short) (indexToOTRSVOrdinal & 0x7fff);
                    combineAugStrongOfTwoVerses = true;
                }
                index = indexToOTRSVOrdinal;
            }
        }
        else {
            ordinals = OpenFileStateManager.osArray.NTRSVOrdinal;
        }
        String[] augmentStrongs = getAugStrongsForVerse(combineAugStrongOfTwoVerses, ordinals, index, testament);
        //System.out.print(translation + " " + ref + ":");
        String returnVerse = augmentDStrongInVerse(resultFromJSword, augmentStrongs, testament, translation, verse.toString());
        //System.out.println("");
        return returnVerse;


    }

    private String[] getAugStrongsForVerse(final boolean combineAugStrongOfTwoVerses, int[] ordinals, int index,
                                           final Testament testament) {
        int currentPos = ordinals[index];
        if (currentPos > 0) {
            int lastPos = OpenFileStateManager.osArray.augStrong.length;
            int endPos = 0;
            if (combineAugStrongOfTwoVerses)
                index ++;
            for (int i = index + 1; ((i < ordinals.length) && (endPos == 0)); i++) {
                endPos = ordinals[i];
            }
            if (endPos == 0) {
                if (testament == Testament.OLD) { // reached the end of the OHBOrdinal
                    ordinals = OpenFileStateManager.osArray.NTRSVOrdinal; // look the first NTRSVOrdinal with a pointer
                    for (int i = 0; ((i < ordinals.length) && (endPos == 0)); i++) {
                        endPos = ordinals[i];
                    }
                } else endPos = lastPos;
            }
            int len = endPos - currentPos;
            int destPos = 0;
            if (combineAugStrongOfTwoVerses) len ++; // add one space between the 1st and 2nd string of augstrongs.
            byte[] b = new byte[len];
            if (combineAugStrongOfTwoVerses) {
                len = ordinals[index] - currentPos;
                System.arraycopy(OpenFileStateManager.osArray.augStrong, currentPos, b, destPos, len);
                b[len] = ' ';
                destPos = len + 1;
                currentPos = ordinals[index];
                len = endPos - currentPos;
            }
            System.arraycopy(OpenFileStateManager.osArray.augStrong, currentPos, b, destPos, len);
            String[] augStrongs = new String(b).trim().split(" ");
            if (combineAugStrongOfTwoVerses)
                Arrays.sort(augStrongs);
            return augStrongs;
        }
        return new String[0];
    }

    private String normalizeStrongNumber (final String input, final Testament testament) {
        String copyOfInput = input.replace("strong:", "").trim();
        Pattern pattern = Pattern.compile("^([GH])(\\d+)[!A-Z.]?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(copyOfInput);
        String prefix = "";
        String number = "";
        if (matcher.find()) {
            prefix = matcher.group(1).toUpperCase();
            number = matcher.group(2);
        }
        else {
            Pattern pattern2 = Pattern.compile("^(\\d+)[!A-Z.]?", Pattern.CASE_INSENSITIVE);
            Matcher matcher2 = pattern2.matcher(copyOfInput);
            if (matcher2.find()) {
                prefix = (testament == Testament.OLD) ? "H" : "G";
                number = matcher2.group(1);
            }
            else {
                return copyOfInput;
            }
        }
        int numLength = number.length(); // Make the Strong number Hnnnn or Gnnnn with 4 digits.  ESV uses 5 digits with a "0" in front, OHB does not have leading zero.
        if (numLength != 4) {
            // If it is 5 digits and does not start with 0, don't do anything.
            if ((numLength == 5) && (number.charAt(0) == '0')) number = number.substring(1, 5);
            else if (numLength == 3) number = "0" + number;
            else if (numLength == 2) number = "00" + number;
            else if (numLength == 1) number = "000" + number;
        }
        return prefix + number;
    }
    private String augmentDStrongInWord (final String strongsListedWithWord, final Testament testament, final String[][] augStrongParts,
                                         int[] augStrongPos, final String translation, final String ref) {
        String[] strongsWithWord = strongsListedWithWord.split(" ");
        String result = "";
        for (int i = 0; i < strongsWithWord.length; i++) {
            String currentStrong = normalizeStrongNumber(strongsWithWord[i], testament);
            boolean assigned = false;
            for (int j = 0; j < augStrongParts.length; j++) {
                String nonAugStrong = "";
                if (augStrongParts[j][0].length() >= 5)
                    nonAugStrong = augStrongParts[j][0].substring(0, 5);
                int compareValue = currentStrong.compareTo(nonAugStrong);
                if (compareValue == 0) {
                    augStrongPos[j] ++;
                    if (augStrongParts[j].length == 1) {
                        if (!result.equals("")) result += " strong:";
                        result += augStrongParts[j][0];
//                        System.out.print(" " + augStrongParts[j][0]);
                        assigned = true;
                    }
                    else if ((augStrongParts[j].length == 3) &&
                            (augStrongParts[j][2].indexOf( Integer.toString(augStrongPos[j])) > -1)) {
                        if (!result.equals("")) result += " strong:";
                        result += currentStrong + augStrongParts[j][1];
//                        System.out.print(" " + currentStrong + augStrongParts[j][1]);
                        assigned = true;
                    }
                }
                else if (compareValue == -1) // No more match because the aug strongs are sorted
                    break;
            }
            if (!assigned) {
                int index = binarySearchOfStrong(currentStrong);
                String strongToReturn = currentStrong;
                if (index > -1) {
                    strongToReturn += new String(new byte[]{OpenFileStateManager.osArray.defaultAugment[index]});
//                    System.out.print(" " + strongToReturn);
                }
                result += (result.equals("")) ? strongToReturn : (" strong:" + strongToReturn);
            }
        }
        return result;
    }

    private String augmentDStrongInVerse(final String fromJSword, final String[] augStrongs, final Testament testament,
                                         final String translation, final String ref) {
        final String lcFromJSword = fromJSword.toLowerCase();
        String[][] augStrongParts = new String[augStrongs.length][];
        String result = "";
        int resultCopyPos = 0;
        int[] augStrongPos = new int[augStrongs.length];
        for (int i = 0; i < augStrongs.length; i ++) {
            augStrongParts[i] = augStrongs[i].split(";");
        }
        int posOfStrongTag = lcFromJSword.indexOf("lemma=\"strong:", 0);
        while ((posOfStrongTag > -1) && (posOfStrongTag < lcFromJSword.length())) {
            posOfStrongTag += 14;
            result += fromJSword.substring(resultCopyPos, posOfStrongTag);
            int posEndOfStrongTag = lcFromJSword.indexOf("\"", posOfStrongTag);
            if (posEndOfStrongTag == -1) {
                System.out.println("Cannot find end of strong tag: \"");
                System.exit(404);
            }
            String strongsListedForThisWord = fromJSword.substring(posOfStrongTag, posEndOfStrongTag).trim();
            String updatedStrongs = augmentDStrongInWord(strongsListedForThisWord, testament, augStrongParts, augStrongPos, translation, ref);
            result += updatedStrongs + '"';
            posOfStrongTag = posEndOfStrongTag + 1;
            resultCopyPos = posOfStrongTag;
            posOfStrongTag = lcFromJSword.indexOf("lemma=\"strong:", posOfStrongTag);
        }
        result += fromJSword.substring(resultCopyPos);
        return result;
    }

    private int binarySearchOfStrong(final String augStrong) {
        if (augStrong.length() < 2)
            return -1;
        int first = 0;
        int last = OpenFileStateManager.osArray.strongsWithAugments.length - 1;
        if (augStrong.charAt(0) == 'G') {
            last = OpenFileStateManager.osArray.numOfGreekStrongWithAugments - 1;
        }
        else if (augStrong.charAt(0) == 'H') {
            first = OpenFileStateManager.osArray.numOfGreekStrongWithAugments;
        }
        else
            return -1;
        int key = convertStrong2Short(augStrong);
        if (key == -1)
            return -1;
        int mid = (first + last) / 2;
        while( first <= last ) {
            if ( OpenFileStateManager.osArray.strongsWithAugments[mid] < key ) first = mid + 1;
            else if ( OpenFileStateManager.osArray.strongsWithAugments[mid] == key ) return mid;
            else last = mid - 1;
            mid = (first + last) / 2;
        }
        return -1;
    }

    private int convertStrong2Short(final String strong) {
        int startPos = 1;
        int endPos = strong.length() - 1;
        char suffix = strong.charAt(endPos);
        if (Character.isDigit(suffix)) endPos++;
        int num;
        try {
            num = parseInt(strong.substring(startPos, endPos)); // If the augmented Strong file has issue, it will run into an exception.
        } catch (NumberFormatException e) {
//           System.out.println("Strong number is not numeric at the expected positions: " + strong + " Something wrong with the augmented Strong file.");
            return -1;
        }

        if (num > 32767) {
//            System.out.println("Strong number has too many digits: " + strong + " Something wrong with the augmented Strong file.");
            return -1;
        }
        return num;
    }

}

