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
 * Copyright: 2013 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.crosswire.common.config.ConfigException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author chrisburrell
 */
public final class VersificationsMapper {
    /**
     * Prevent instantiation
     */
    private VersificationsMapper() {
        // we have no mapper for the KJV, since everything maps map to the KJV, so we'll simply add an entry
        // in there to avoid ever trying to load it
        MAPPERS.put(KJV, null);
    }

    /**
     * @return a singleton instance of the mapper -
     */
    public static VersificationsMapper instance() {
        if (instance == null) {
            synchronized (VersificationsMapper.class) {
                if (instance == null) {
                    instance = new VersificationsMapper();
                }
            }
        }
        return instance;
    }


    /**
     * Maps a whole passage, and does so verse by verse. We can't do any better, since, we may for
     * example have:
     * Ps.1.1-10 => Ps.1.2-11 so one would think we can simply map each of the start and end verses.
     * However, this would be inacurate since verse 9 might map to verse 12, 13, etc.
     *
     * @param key    the key if the source versification
     * @param target the target versification
     * @return the new key in the new versification
     */
    public Passage map(final Passage key, final Versification target) {
        if (key.getVersification().equals(target)) {
            return key;
        }

        Passage newPassage = KeyUtil.getPassage(PassageKeyFactory.instance().createEmptyKeyList(target));
        Iterator<Key> verses = key.iterator();
        while (verses.hasNext()) {
            Key verseKey = verses.next();
            if (!(verseKey instanceof Verse)) {
                throw new UnsupportedOperationException("Somehow, a passage is not resolving to verses");
            }

            Verse verse = (Verse) verseKey;
            newPassage.addAll(this.mapVerse(verse, target));
        }

        return newPassage;
    }

    /**
     * @param v                   the verse
     * @param targetVersification the final versification that we want
     */
    public Key mapVerse(Verse v, Versification targetVersification) {
        if (v.getVersification().equals(targetVersification)) {
            return v;
        }

        ensure(v.getVersification());
        ensure(targetVersification);

        // caution, mappers can be null if they are missing or failed to load.
        // get the source mapper, to get to the KJV
        VersificationToKJVMapper mapper = MAPPERS.get(v.getVersification());

        // mapped verses could be more than 1 verse in KJV
        List<QualifiedKey> kjvVerses;
        if (mapper == null) {
            // we can't map to the KJV, so we're going to take a wild guess and
            // return the equivalent verse
            // and assume that it maps directly on to the KJV, and thereby
            // continue with the process
            kjvVerses = new ArrayList<QualifiedKey>();
            kjvVerses.add(new QualifiedKey(new Verse(KJV, v.getBook(), v.getChapter(), v.getVerse())));
        } else {
            //we need qualified keys back, so as to preserve parts
            kjvVerses = mapper.map(new QualifiedKey(v));
        }

        if (KJV.equals(targetVersification)) {
            // we're done, so simply return the key we have so far.
            return getKeyFromQualifiedKeys(KJV, kjvVerses);
        }

        // we're continuing, so we need to unmap from the KJV qualified key onto
        // the new versification.
        VersificationToKJVMapper targetMapper = MAPPERS.get(targetVersification);
        if (targetMapper == null) {
            // failed to load, so we'll do our wild-guess again, and assume that
            // the KJV keys map to the
            // target
            return guessKeyFromKjvVerses(targetVersification, kjvVerses);
        }

        // we can use the unmap method for that. Since we have a list of
        // qualified keys, we do so for every qualified
        // key in the list - this means that parts would get transported as
        // well.
        Key finalKeys = PassageKeyFactory.instance().createEmptyKeyList(targetVersification);
        for (QualifiedKey qualifiedKey : kjvVerses) {
            finalKeys.addAll(targetMapper.unmap(qualifiedKey));
        }
        return finalKeys;
    }

    /**
     * This is a last attempt at trying to get something, on the basis that
     * something is better than nothing.
     *
     * @param targetVersification the target versification
     * @param kjvVerses           the verses in the KJV versification.
     * @return the possible verses in the target versification, no guarantees
     *         made
     */
    private Key guessKeyFromKjvVerses(final Versification targetVersification, final List<QualifiedKey> kjvVerses) {
        final Key finalKeys = PassageKeyFactory.instance().createEmptyKeyList(targetVersification);
        try {
            for (QualifiedKey qualifiedKey : kjvVerses) {
                if (qualifiedKey.getKey() != null) {
                    finalKeys.addAll(PassageKeyFactory.instance().getKey(targetVersification, qualifiedKey.getKey().getOsisRef()));
                }
            }
            return finalKeys;
        } catch (NoSuchKeyException ex) {
            // we swallow the exception, as we've already alerted that we failed
            // to load the missing resources.
            LOGGER.trace(ex.getMessage(), ex);
            return finalKeys;
        }
    }

    /**
     * @param kjvVerses the list of keys
     * @return the aggregate key
     */
    private Key getKeyFromQualifiedKeys(Versification versification, final List<QualifiedKey> kjvVerses) {
        final Key finalKey = PassageKeyFactory.instance().createEmptyKeyList(versification);
        for (QualifiedKey k : kjvVerses) {
            // we simply ignore everything else at this stage. The other bits
            // and pieces are used while we're converting
            // from one to the other.
            if (k.getKey() != null) {
                finalKey.addAll(k.getKey());
            }
        }
        return finalKey;
    }

    /** 
     * Call this to ensure mapping data is loaded (maybe for newly installed books).  
     * Should normally be called from a background thread, not the ui thread.

     * @param versification the versification we want to load mapping data for
     */
    public void ensureMappingDataLoaded(Versification versification) {
        ensure(versification);
    }

    /**
     * Reads the mapping from file if it does not exist
     *
     * @param versification the versification we want to load
     */
    private void ensure(final Versification versification) {
        if (MAPPERS.containsKey(versification)) {
            return;
        }

        try {
            MAPPERS.put(versification, new VersificationToKJVMapper(versification, new FileVersificationMapping(versification)));
        } catch (IOException e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed to load versification mappings for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        } catch (ConfigException e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed to load versification mappings for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        } catch (MissingResourceException e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed to load versification mappings for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        } catch (Exception e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed for an unknown reason for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        }
    }

    private static volatile VersificationsMapper instance;
    private static final Versification KJV = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);
    private static final Map<Versification, VersificationToKJVMapper> MAPPERS = new HashMap<Versification, VersificationToKJVMapper>();
    private static final Logger LOGGER = LoggerFactory.getLogger(VersificationsMapper.class);
}
