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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.icu.DateFormatter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawLDBackendState;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * An implementation AbstractKeyBackend to read RAW format files.
 * 
 * @param <T> The type of the RawLDBackendState that this class extends.
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class RawLDBackend<T extends RawLDBackendState> extends AbstractKeyBackend<RawLDBackendState> {
    /**
     * Simple ctor
     * 
     * @param sbmd 
     * @param datasize
     *            We need to know how many bytes in the size portion of the
     *            index
     */
    public RawLDBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd);
        this.datasize = datasize;
        this.entrysize = OFFSETSIZE + datasize;
    }

    public String readRawContent(RawLDBackendState state, Key key) throws IOException {
        return doReadRawContent(state, key.getName());
    }

    public RawLDBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getRawLDBackendState(getBookMetaData());
    }

    private String doReadRawContent(RawLDBackendState state, String key) throws IOException {
        if (key == null || key.length() == 0) {
            return "";
        }
        int pos = search(state, key);
        if (pos >= 0) {
            DataIndex index = getIndex(state, pos);
            DataEntry entry = getEntry(state, key, index);
            entry = getEntry(state, entry);
            if (entry.isLinkEntry()) {
                return doReadRawContent(state, entry.getLinkTarget());
            }
            return getRawText(entry);
        }

        // TRANSLATOR: Error condition: Indicates that something could not
        // be found in the book. {0} is a placeholder for the unknown key.
        throw new IOException(JSMsg.gettext("Key not found {0}", key));
    }

    protected String getRawText(DataEntry entry) {
        String cipherKeyString = getBookMetaData().getProperty(SwordBookMetaData.KEY_CIPHER_KEY);
        byte[] cipherKeyBytes = null;
        if (cipherKeyString != null) {
            try {
                cipherKeyBytes = cipherKeyString.getBytes(getBookMetaData().getBookCharset());
            } catch (UnsupportedEncodingException e) {
                cipherKeyBytes = cipherKeyString.getBytes();
            }
        }
        return entry.getRawText(cipherKeyBytes);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        RawLDBackendState state = null;
        try {
            state = initState();

            if (state.getSize() == -1) {
                state.setSize((int) (state.getIdxRaf().length() / entrysize));
            }
            return state.getSize();
        } catch (BookException e) {
            return 0;
        } catch (IOException e) {
            return 0;
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        RawLDBackendState state = null;
        try {
            state = initState();

            if (index < getCardinality()) {
                DataIndex dataIndex = getIndex(state, index);
                DataEntry entry = getEntry(state, getBookMetaData().getInitials(), dataIndex);
                String keytitle = internal2external(entry.getKey());
                return new DefaultLeafKeyList(keytitle);
            }
        } catch (BookException e) {
            // This is allowed
            // Fall through to throw an AIOOBE.
        } catch (IOException e) {
            // This is allowed
            // Fall through to throw an AIOOBE.
        } finally {
            OpenFileStateManager.instance().release(state);
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that) {
        RawLDBackendState state = null;
        try {
            state = initState();
            return search(state, that.getName());
        } catch (IOException e) {
            return -getCardinality() - 1;
        } catch (BookException e) {
            return -getCardinality() - 1;
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#size(org.crosswire.jsword.passage.Key)
     */
    @Override
    public int getRawTextLength(Key key) {
        RawLDBackendState state = null;
        try {
            state = initState();
            int entry = search(state, key.getName());
            // Read the offset and size for this key from the index
            byte[] buffer = SwordUtil.readRAF(state.getIdxRaf(), entry * entrysize, entrysize);
            int entrySize = 0;
            switch (datasize) {
            case 2:
                entrySize = SwordUtil.decodeLittleEndian16(buffer, 4);
                break;
            case 4:
                entrySize = SwordUtil.decodeLittleEndian32(buffer, 4);
                break;
            default:
                assert false : datasize;
            }

            return entrySize;
        } catch (IOException e) {
            return 0;
        } catch (BookException e) {
            return 0;
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    /**
     * Get the Index (that is offset and size) for an entry.
     * 
     * @param entry
     * @return the index of the entry
     * @throws IOException
     */
    protected DataIndex getIndex(RawLDBackendState state, long entry) throws IOException {
        // Read the offset and size for this key from the index
        byte[] buffer = SwordUtil.readRAF(state.getIdxRaf(), entry * entrysize, entrysize);
        int entryOffset = SwordUtil.decodeLittleEndian32(buffer, 0);
        int entrySize = -1;
        switch (datasize) {
        case 2:
            entrySize = SwordUtil.decodeLittleEndian16(buffer, 4);
            break;
        case 4:
            entrySize = SwordUtil.decodeLittleEndian32(buffer, 4);
            break;
        default:
            assert false : datasize;
        }
        return new DataIndex(entryOffset, entrySize);
    }

    /**
     * Get the text for an indexed entry in the book.
     * 
     * @param state
     *            the state object for the storage
     * @param reply
     *            the context for this dataIndex, used for debugging
     * @param dataIndex
     *            the entry to get
     * @return the text for the entry.
     * @throws IOException
     */
    private DataEntry getEntry(RawLDBackendState state, String reply, DataIndex dataIndex) throws IOException {
//        DataIndex dataIndex = getIndex(state, index);
        // Now read the data file for this key using the offset and size
        byte[] data = SwordUtil.readRAF(state.getDatRaf(), dataIndex.getOffset(), dataIndex.getSize());
        return new DataEntry(reply, data, getBookMetaData().getBookCharset());
    }

    /**
     * Get the entry indicated by this entry. If this entry doesn't indicate any other entry
     * then it returns the entry. Note, this is used by compressed dictionaries to get the deeper stuff.
     * 
     * @param state the state where the entry can be found
     * @param entry the entry that might indicate a deeper entry
     * @return the final entry
     */
    protected DataEntry getEntry(RawLDBackendState state, DataEntry entry) {
        return entry;
    }

    /**
     * Find a matching entry, returning it's index. Otherwise return &lt; 0, such
     * that (-pos - 1) gives the insertion index.
     * 
     * @param key
     * @return the match
     * @throws IOException
     */
    private int search(RawLDBackendState state, String key) throws IOException {
        // Note: In some dictionaries, the first element is out of order and
        // represents the title of the work.
        // So, do the bin search from 1 to end and if not found, check the
        // first element as a special case.
        // If that does not match return the position found otherwise.

        // Initialize to one beyond both ends.
        int total = getCardinality();
        int low = 0;
        int high = total;
        int match = -1;
        DataIndex dataIndex = null;

        String suppliedKey = null;
        while (high - low > 1) {
            // use >>> to keep mid always in range
            int mid = (low + high) >>> 1;

            // Get the key for the item at "mid"
            dataIndex = getIndex(state, mid);
            // Occasionally there's a bogus index entry (size == 0)
            // in the middle of the index. It needs to be skipped.
            while (dataIndex.getSize() == 0) {
                // reset mid toward the longer end
                mid += high - mid > mid - low ? 1 : -1;
                // ensure that we are in bounds.
                if (mid < low || mid > high) {
                    break;
                }
                dataIndex = getIndex(state, mid);
            }
            String entryKey = normalizeForSearch(getEntry(state, key, dataIndex).getKey());
            // Normalize the key based upon the first entry looked at.
            if (suppliedKey == null) {
                suppliedKey = normalizeForSearch(external2internal(key, entryKey));
            }
            int cmp = entryKey.compareTo(suppliedKey);
            if (cmp < 0) {
                low = mid;
            } else if (cmp > 0) {
                high = mid;
            } else {
                match = mid;
                break;
            }
        }

        // Do we have an exact match?
        if (match >= 0) {
            return match;
        }

        // Many dictionaries have an introductory entry, so check it for a match.
        dataIndex = getIndex(state, 0);
        String entryKey = normalizeForSearch(getEntry(state, key, dataIndex).getKey());
        if (suppliedKey == null) {
            suppliedKey = normalizeForSearch(external2internal(key, entryKey));
        }
        if (entryKey.compareTo(suppliedKey) == 0) {
            return 0;
        }

        // It wasn't found so see if it is present in a linear search if case sensitive keys are used.
        if ("true".equalsIgnoreCase(getBookMetaData().getProperty(SwordBookMetaData.KEY_CASE_SENSITIVE_KEYS))) {
           for (int i = 0; i < total; i++) {
               dataIndex = getIndex(state, i);
               if (getEntry(state, key, dataIndex).getKey().compareTo(key) == 0) {
                   return i;
               }
           }
        }

        return -(high + 1);
    }

    /**
     * Convert the supplied key to something that can be understood by the module.
     * Use firstKey to determine the pattern for Strong's numbers.
     * 
     * @param externalKey The external key to normalize
     * @param pattern The first non-introduction key in the module.
     * @return the internal representation of the key.
     */
    private String external2internal(String externalKey, String pattern) {
        if (externalKey.length() == 0) {
            return externalKey;
        }
        BookMetaData bmd = getBookMetaData();
        String keytitle = externalKey;
        if (BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory())) {
            // Is it already in internal format? If so, just return it.
            Matcher m = DEVOTION_PATTERN.matcher(keytitle);
            if (m.matches()) {
                return keytitle;
            }
            Calendar greg = new GregorianCalendar();
            DateFormatter nameDF = DateFormatter.getDateInstance();
            nameDF.setLenient(true);
            Date date = nameDF.parse(keytitle);
            greg.setTime(date);
            Object[] objs = {
                    Integer.valueOf(1 + greg.get(Calendar.MONTH)), Integer.valueOf(greg.get(Calendar.DATE))
            };
            return DATE_KEY_FORMAT.format(objs);
        }

        if (bmd.hasFeature(FeatureType.GREEK_DEFINITIONS) || bmd.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
            // Is the string valid?
            Matcher m = STRONGS_PATTERN.matcher(keytitle);
            if (!m.matches()) {
                return keytitle;
            }
            if ("true".equalsIgnoreCase(bmd.getProperty(SwordBookMetaData.KEY_STRONGS_PADDING))) {
                // pad to 4 digits
                // NASB has trailing letters!
                int pos = keytitle.length() - 1;
                char lastLetter = keytitle.charAt(pos);
                boolean hasTrailingLetter = Character.isLetter(lastLetter);
                if (hasTrailingLetter) {
                    keytitle = keytitle.substring(0, pos);
                    // And it might be preceded by a !
                    pos--;
                    if (pos > 0 && keytitle.charAt(pos) == '!') {
                        keytitle = keytitle.substring(0, pos);
                    }
                }

                // Get the G or the H.
                char type = keytitle.charAt(0);

                // Get the number after the G or H
                int strongsNumber = Integer.parseInt(keytitle.substring(1));
                // If it has both Greek and Hebrew, then the G and H are needed.
                StringBuilder buf = new StringBuilder();
                if (bmd.hasFeature(FeatureType.GREEK_DEFINITIONS) && bmd.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
                    // The convention is that a Strong's dictionary with both Greek
                    // and Hebrew have G or H prefix
                    buf.append(type);
                    buf.append(getZero4Pad().format(strongsNumber));

                    // The NAS lexicon has some entries that end in A-Z, but it is
                    // not preceded by a !
                    if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                        buf.append(lastLetter);
                    }
                    return buf.toString();
                }

                m = STRONGS_PATTERN.matcher(pattern);
                if (m.matches()) {
                    buf.append(type);
                    int numLength = m.group(2).length();
                    if (numLength == 4) {
                        buf.append(getZero4Pad().format(strongsNumber));
                    } else {
                        buf.append(getZero5Pad().format(strongsNumber));
                    }
                    // The NAS lexicon has some entries that end in A-Z, but it is
                    // not preceded by a !
                    if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                        buf.append(lastLetter);
                    }
                    return buf.toString();
                }

                // It is just the number
                return getZero5Pad().format(strongsNumber);
            }
            // else unpad, E.g. G0001 to G1
            // This test is merely an optimization to prevent unnecessary work.
            if (keytitle.charAt(1) == '0') {
                char type = keytitle.charAt(0);
                // NASB has trailing letters!
                int pos = keytitle.length() - 1;
                char lastLetter = keytitle.charAt(pos);
                boolean hasTrailingLetter = Character.isLetter(lastLetter);
                if (hasTrailingLetter) {
                    keytitle = keytitle.substring(0, pos);
                    // And it might be preceded by a !
                    pos--;
                    if (pos > 0 && keytitle.charAt(pos) == '!') {
                        keytitle = keytitle.substring(0, pos);
                    }
                }
                // Get the number after the G or H
                int strongsNumber = Integer.parseInt(keytitle.substring(1));
                // The convention is that a Strong's dictionary with both Greek
                // and Hebrew have G or H prefix
                StringBuilder buf = new StringBuilder();
                buf.append(type);
                buf.append(strongsNumber);

                // The NAS lexicon has some entries that end in A-Z, but it is
                // not preceded by a !
                if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                    buf.append(lastLetter);
                }
            }
        }
        return keytitle;
    }

    private String internal2external(String internalKey) {
        BookMetaData bmd = getBookMetaData();
        String keytitle = internalKey;
        if (BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory()) && keytitle.length() >= 3) {
            Calendar greg = new GregorianCalendar();
            DateFormatter nameDF = DateFormatter.getDateInstance();
            String[] spec = StringUtil.splitAll(keytitle, '.');
            greg.set(Calendar.MONTH, Integer.parseInt(spec[0]) - 1);
            greg.set(Calendar.DATE, Integer.parseInt(spec[1]));
            keytitle = nameDF.format(greg.getTime());
        }
        return keytitle;
    }

    private String normalizeForSearch(String internalKey) {
        BookMetaData bmd = getBookMetaData();
        String keytitle = internalKey;
        String caseSensitive = bmd.getProperty(SwordBookMetaData.KEY_CASE_SENSITIVE_KEYS);
        if (!"true".equalsIgnoreCase(caseSensitive) && !BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory())) {
            return keytitle.toUpperCase(Locale.US);
        }

        return keytitle;
    }

    /**
     * A means to normalize Strong's Numbers.
     */
    private DecimalFormat getZero5Pad() {
        return new DecimalFormat("00000");
    }

    /**
     * A means to normalize Strong's Numbers.
     */
    private DecimalFormat getZero4Pad() {
        return new DecimalFormat("0000");
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
    }

    /** 
     * Experimental code.
     */
    public void dumpIdxRaf() {
        RawLDBackendState state = null;
        long end = -1;
        try {
            state = initState();
            end = getCardinality();
            StringBuilder buf = new StringBuilder();
            System.out.println("index\toffset\tsize\tkey\tvalue");
            for (long i = 0; i < end; ++i) {
                DataIndex index = getIndex(state, i);
                int offset = index.getOffset();
                int size   = index.getSize();
                buf.setLength(0);
                buf.append(i);
                buf.append('\t');
                buf.append(offset);
                buf.append('\t');
                buf.append(size);
                if (size > 0) {
                    // Now read the data file for this key using the offset and size
                    byte[] data = SwordUtil.readRAF(state.getDatRaf(), offset, size);
                    DataEntry entry = new DataEntry(Long.toString(i), data, getBookMetaData().getBookCharset());
                    String key = entry.getKey();
                    String raw = getRawText(entry);
                    buf.append('\t');
                    buf.append(key);
                    buf.append('\t');
                    if (raw.length() > 43) {
                        buf.append(raw.substring(0, 40).replace('\n', ' '));
                        buf.append("...");
                    } else {
                        buf.append(raw.replace('\n', ' '));
                    }
                } else {
                    buf.append("\t\t");
                }
                System.out.println(buf.toString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BookException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    /** 
     * Experimental code.
     */
    public void toIMP() {
        RawLDBackendState state = null;
        long end = -1;
        try {
            state = initState();
            end = getCardinality();
            StringBuilder buf = new StringBuilder();
            for (long i = 0; i < end; ++i) {
                DataIndex index = getIndex(state, i);
                int offset = index.getOffset();
                int size   = index.getSize();
                buf.setLength(0);
                buf.append("$$$");
                if (size > 0) {
                    // Now read the data file for this key using the offset and size
                    byte[] data = SwordUtil.readRAF(state.getDatRaf(), offset, size);
                    DataEntry entry = new DataEntry(Long.toString(i), data, getBookMetaData().getBookCharset());
                    String key = entry.getKey();
                    String raw = getRawText(entry);
                    buf.append(key);
                    buf.append("\n");
                    buf.append(raw);
                }
                System.out.println(buf.toString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BookException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }
    /**
     * Date formatter
     */
    private static final MessageFormat DATE_KEY_FORMAT = new MessageFormat("{0,number,00}.{1,number,00}");

    /**
     * This is the pattern of a Strong's Number. It begins with a G or H. Is
     * followed by a number. It can be followed by a ! and a letter or just a
     * letter.
     */
    private static final Pattern STRONGS_PATTERN = Pattern.compile("^([GH])(\\d+)((!)?([a-z])?)$");
    /**
     * This is the pattern of a date for a DailyDevotion, DD.MM
     */
    private static final Pattern DEVOTION_PATTERN = Pattern.compile("^\\d\\d\\.\\d\\d$");

    /**
     * The number of bytes in the size count in the index
     */
    private final int datasize;

    /**
     * The number of bytes for each entry in the index: either 6 or 8
     */
    private final int entrysize;

    /**
     * How many bytes in the offset pointers in the index
     */
    private static final int OFFSETSIZE = 4;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 818089833394450383L;
}
