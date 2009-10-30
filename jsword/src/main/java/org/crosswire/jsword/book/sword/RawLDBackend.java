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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.icu.DateFormatter;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
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
public class RawLDBackend extends AbstractKeyBackend {
    /**
     * Simple ctor
     * 
     * @param datasize
     *            We need to know how many bytes in the size portion of the
     *            index
     */
    public RawLDBackend(SwordBookMetaData sbmd, int datasize) {
        super(sbmd);
        this.size = -1;
        this.datasize = datasize;
        this.entrysize = OFFSETSIZE + datasize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire
     * .jsword.passage.Key, java.lang.String)
     */
    /* @Override */
    public String getRawText(Key key) throws BookException {
        return getRawText(key.getName());
    }

    public String getRawText(String key) throws BookException {
        if (!checkActive()) {
            return ""; //$NON-NLS-1$
        }

        try {
            int pos = search(key);
            if (pos >= 0) {
                DataEntry entry = getEntry(key, pos);
                if (entry.isLinkEntry()) {
                    return getRawText(entry.getLinkTarget());
                }
                return getRawText(entry);
            }
            throw new BookException(UserMsg.READ_FAIL, new Object[] {
                key
            });
        } catch (IOException ex) {
            throw new BookException(UserMsg.READ_FAIL, ex, new Object[] {
                key
            });
        }
    }

    protected String getRawText(DataEntry entry) {
        String cipherKeyString = (String) getBookMetaData().getProperty(ConfigEntryType.CIPHER_KEY);
        return entry.getRawText((cipherKeyString != null) ? cipherKeyString.getBytes() : null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        if (!checkActive()) {
            return 0;
        }

        if (size == -1) {
            try {
                size = (int) (idxRaf.length() / entrysize);
            } catch (IOException e) {
                size = 0;
            }
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        if (checkActive()) {
            try {
                if (index < getCardinality()) {
                    DataEntry entry = getEntry(getBookMetaData().getInitials(), index);
                    String keytitle = internal2external(entry.getKey());
                    return new DefaultLeafKeyList(keytitle);
                }
            } catch (IOException e) {
                // fall through
            }
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
        try {
            return search(that.getName());
        } catch (IOException e) {
            return -getCardinality() - 1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#activate(org.crosswire.common
     * .activate.Lock)
     */
    public void activate(Lock lock) {
        active = false;
        size = -1;
        idxFile = null;
        datFile = null;
        idxRaf = null;
        datRaf = null;

        URI path = null;
        try {
            path = getExpandedDataPath();
        } catch (BookException e) {
            Reporter.informUser(this, e);
            return;
        }

        try {
            idxFile = new File(path.getPath() + SwordConstants.EXTENSION_INDEX);
            datFile = new File(path.getPath() + SwordConstants.EXTENSION_DATA);

            if (!idxFile.canRead()) {
                Reporter.informUser(this, new BookException(UserMsg.READ_FAIL, new Object[] {
                    idxFile.getAbsolutePath()
                }));
                return;
            }

            if (!datFile.canRead()) {
                Reporter.informUser(this, new BookException(UserMsg.READ_FAIL, new Object[] {
                    datFile.getAbsolutePath()
                }));
                return;
            }

            // Open the files
            idxRaf = new RandomAccessFile(idxFile, FileUtil.MODE_READ);
            datRaf = new RandomAccessFile(datFile, FileUtil.MODE_READ);
        } catch (IOException ex) {
            log.error("failed to open files", ex); //$NON-NLS-1$
            idxRaf = null;
            datRaf = null;
            return;
        }

        active = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common
     * .activate.Lock)
     */
    public void deactivate(Lock lock) {
        size = -1;
        try {
            if (idxRaf != null) {
                idxRaf.close();
            }
            if (datRaf != null) {
                datRaf.close();
            }
        } catch (IOException ex) {
            log.error("failed to close files", ex); //$NON-NLS-1$
        } finally {
            idxRaf = null;
            datRaf = null;
        }

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected boolean checkActive() {
        if (!isActive()) {
            Activator.activate(this);
        }
        return isActive();
    }

    /**
     * Determine whether we are active.
     */
    protected boolean isActive() {
        return active;
    }

    /**
     * Get the Index (that is offset and size) for an entry.
     * 
     * @param entry
     * @return the index of the entry
     * @throws IOException
     */
    private DataIndex getIndex(long entry) throws IOException {
        // Read the offset and size for this key from the index
        byte[] buffer = SwordUtil.readRAF(idxRaf, entry * entrysize, entrysize);
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
    private DataEntry getEntry(String reply, int index) throws IOException {
        DataIndex dataIndex = getIndex(index);
        // Now read the data file for this key using the offset and size
        byte[] data = SwordUtil.readRAF(datRaf, dataIndex.getOffset(), dataIndex.getSize());

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
    private int search(String key) throws IOException {
        if (!checkActive()) {
            return -1;
        }

        String target = external2internal(key);

        // Initialize to one beyond both ends.
        int total = getCardinality();
        // Note: In some dictionaries, the first element is out of order and
        // represents the title of the work.
        // So, do the bin search from 1 to end and if not found, check the first
        // element as a special case.
        // If that does not match return the position found otherwise.
        int low = 0;
        int high = total;
        int match = -1;

        while (high - low > 1) {
            // use >>> to keep mid always in range
            int mid = (low + high) >>> 1;

            // Get the key for the item at "mid"
            int cmp = normalizeForSearch(getEntry(key, mid).getKey()).compareTo(target);
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

        // Strong's Greek And Hebrew dictionaries have an introductory entry, so
        // check it for a match.
        if (normalizeForSearch(getEntry(key, 0).getKey()).compareTo(target) == 0) {
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
                        new Integer(1 + greg.get(Calendar.MONTH)), new Integer(greg.get(Calendar.DATE))
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
                StringBuffer buf = new StringBuffer();
                buf.append(Character.toUpperCase(type));
                buf.append(ZERO_4PAD.format(strongsNumber));

                // The NAS lexicon has some entries that end in A-Z, but it is
                // not preceded by a !
                if (hasTrailingLetter && "naslex".equalsIgnoreCase(bmd.getInitials())) //$NON-NLS-1$
                {
                    buf.append(Character.toUpperCase(lastLetter));
                }
                return buf.toString();
            }

            return ZERO_5PAD.format(strongsNumber);
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
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        active = false;
        size = -1;
        idxFile = null;
        datFile = null;
        idxRaf = null;
        datRaf = null;
        is.defaultReadObject();
    }

    /**
     * How many bytes in the offset pointers in the index
     */
    private static final int OFFSETSIZE = 4;

    /**
     * Flags whether there are open files or not
     */
    private transient boolean active;

    /**
     * The number of bytes in the size count in the index
     */
    private int datasize;

    /**
     * The number of bytes for each entry in the index: either 6 or 8
     */
    private int entrysize;

    /**
     * The number of entries in the book.
     */
    private transient int size;

    /**
     * The index file
     */
    private transient File idxFile;

    /**
     * The index random access file
     */
    private transient RandomAccessFile idxRaf;

    /**
     * The data file
     */
    private transient File datFile;

    /**
     * The data random access file
     */
    private transient RandomAccessFile datRaf;

    /**
     * Date formatter
     */
    private static final MessageFormat DATE_KEY_FORMAT = new MessageFormat("{0,number,00}.{1,number,00}"); //$NON-NLS-1$

    /**
     * This is the pattern of a Strong's Number. It begins with a G or H. Is
     * followed by a number. It can be followed by a ! and a letter or just a
     * letter.
     */
    private static final Pattern STRONGS_PATTERN = Pattern.compile("^([GH])(\\d+)((!)?([a-z])?)$"); //$NON-NLS-1$

    /**
     * A means to normalize Strong's Numbers.
     */
    private static final DecimalFormat ZERO_5PAD = new DecimalFormat("00000"); //$NON-NLS-1$

    private static final DecimalFormat ZERO_4PAD = new DecimalFormat("0000"); //$NON-NLS-1$

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 818089833394450383L;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawLDBackend.class);
}
