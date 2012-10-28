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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.icu.DateFormatter;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawLDBackendState;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * An implementation AbstractKeyBackend to read RAW format files.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class RawLDBackend<T extends RawLDBackendState> extends AbstractKeyBackend<RawLDBackendState> {
    /**
     * Simple ctor
     * 
     * @param datasize
     *            We need to know how many bytes in the size portion of the
     *            index
     */
    public RawLDBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd);
        this.datasize = datasize;
        this.entrysize = OFFSETSIZE + datasize;
    }

    public String readRawContent(RawLDBackendState state, Key key, String keyName) throws IOException {
        String result = readRawContent(state, key.getName());
        return result;
    }

    public RawLDBackendState initState() throws BookException {
        return OpenFileStateManager.getRawLDBackendState(getBookMetaData());
    }


    public String readRawContent(RawLDBackendState state, String key) throws IOException {

        try {
            int pos = search(state, key);
            if (pos >= 0) {
                DataEntry entry = getEntry(state, key, pos);
                if (entry.isLinkEntry()) {
                    return readRawContent(state, entry.getLinkTarget());
                }
                return getRawText(state, entry);
            }
            // TRANSLATOR: Error condition: Indicates that something could not
            // be found in the book. {0} is a placeholder for the unknown key.
            throw new IOException(JSMsg.gettext("Key not found {0}", key));
        } catch (IOException ex) {
            // TRANSLATOR: Common error condition: The file could not be read.
            // There can be many reasons.
            // {0} is a placeholder for the file.
            throw new IOException(JSMsg.gettext("Error reading {0}", key), ex);
        }
    }

    protected String getRawText(RawLDBackendState state, DataEntry entry) {
        String cipherKeyString = (String) getBookMetaData().getProperty(ConfigEntryType.CIPHER_KEY);
        try {
            return entry.getRawText((cipherKeyString != null) ? cipherKeyString.getBytes(getBookMetaData().getBookCharset()) : null);
        } catch (UnsupportedEncodingException e) {
            return entry.getRawText(cipherKeyString.getBytes());
        }
    }

    /*
     * (non-Javadoc)
     * 
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
            IOUtil.close(state);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        RawLDBackendState state = null;
        try {
            state = initState();

            if (index < getCardinality()) {
                DataEntry entry = getEntry(state, getBookMetaData().getInitials(), index);
                String keytitle = internal2external(entry.getKey());
                return new DefaultLeafKeyList(keytitle);
            }
        } catch (BookException e) {
            // fall through FIXM(CJB) Log?
        } catch (IOException e) {
            // fall through FIXM(CJB) Log?
        } finally {
            IOUtil.close(state);
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage
     * .Key)
     */
    public int indexOf(Key that) {
        RawLDBackendState state;
        try {
            state = initState();
            return search(state, that.getName());
        } catch (IOException e) {
            return -getCardinality() - 1;
        } catch (BookException e) {
            return -getCardinality() - 1;
        }
    }

    /**
     * Get the Index (that is offset and size) for an entry.
     * 
     * @param entry
     * @return the index of the entry
     * @throws IOException
     */
    private DataIndex getIndex(RawLDBackendState state, long entry) throws IOException {
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
     * @param index
     *            the entry to get
     * @return the text for the entry.
     * @throws IOException
     */
    private DataEntry getEntry(RawLDBackendState state, String reply, int index) throws IOException {
        DataIndex dataIndex = getIndex(state, index);
        // Now read the data file for this key using the offset and size
        byte[] data = SwordUtil.readRAF(state.getDatRaf(), dataIndex.getOffset(), dataIndex.getSize());

        return new DataEntry(reply, data, getBookMetaData().getBookCharset());
    }

    /**
     * Find a matching entry, returning it's index. Otherwise return < 0, such
     * that (-pos - 1) gives the insertion index.
     * 
     * @param key
     * @return the match
     * @throws IOException
     */
    private int search(RawLDBackendState state, String key) throws IOException {
            String target = external2internal(key);

            // Initialize to one beyond both ends.
            int total = getCardinality();
            // Note: In some dictionaries, the first element is out of order and
            // represents the title of the work.
            // So, do the bin search from 1 to end and if not found, check the
            // first
            // element as a special case.
            // If that does not match return the position found otherwise.
            int low = 0;
            int high = total;
            int match = -1;

            while (high - low > 1) {
                // use >>> to keep mid always in range
                int mid = (low + high) >>> 1;

                // Get the key for the item at "mid"
                int cmp = normalizeForSearch(getEntry(state, key, mid).getKey()).compareTo(target);
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

            // Strong's Greek And Hebrew dictionaries have an introductory
            // entry, so
            // check it for a match.
            if (normalizeForSearch(getEntry(state, key, 0).getKey()).compareTo(target) == 0) {
                return 0;
            }

            return -(high + 1);
    }

    /**
     * Convert the supplied key to something that can be understood by the
     * module.
     * 
     * @param externalKey
     * @return the internal representation of the key.
     */
    private String external2internal(String externalKey) {
        SwordBookMetaData bmd = getBookMetaData();
        String keytitle = externalKey;
        if (BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory())) {
            Calendar greg = new GregorianCalendar();
            DateFormatter nameDF = DateFormatter.getDateInstance();
            nameDF.setLenient(true);
            try {
                Date date = nameDF.parse(keytitle);
                greg.setTime(date);
                Object[] objs = {
                        Integer.valueOf(1 + greg.get(Calendar.MONTH)), Integer.valueOf(greg.get(Calendar.DATE))
                };
                return DATE_KEY_FORMAT.format(objs);
            } catch (ParseException e) {
                assert false : e;
            }
        } else if (bmd.hasFeature(FeatureType.GREEK_DEFINITIONS) || bmd.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
            // Is the string valid?
            Matcher m = STRONGS_PATTERN.matcher(keytitle);
            if (!m.matches()) {
                return keytitle.toUpperCase(Locale.US);
            }

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
            if (bmd.hasFeature(FeatureType.GREEK_DEFINITIONS) && bmd.hasFeature(FeatureType.HEBREW_DEFINITIONS)) {
                // The convention is that a Strong's dictionary with both Greek
                // and Hebrew have G or H prefix
                StringBuilder buf = new StringBuilder();
                buf.append(Character.toUpperCase(type));
                buf.append(getZero4Pad().format(strongsNumber));

                // The NAS lexicon has some entries that end in A-Z, but it is
                // not preceded by a !
                if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) {
                    buf.append(Character.toUpperCase(lastLetter));
                }
                return buf.toString();
            }

            return getZero5Pad().format(strongsNumber);
        } else {
            return keytitle.toUpperCase(Locale.US);
        }

        return keytitle;
    }

    private String internal2external(String internalKey) {
        SwordBookMetaData bmd = getBookMetaData();
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
        SwordBookMetaData bmd = getBookMetaData();
        String keytitle = internalKey;
        if (!BookCategory.DAILY_DEVOTIONS.equals(bmd.getBookCategory())) {
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
     * Serialization ID
     */
    private static final long serialVersionUID = 818089833394450383L;

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
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawLDBackend.class);
}
