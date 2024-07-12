package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.ZVerseBackendState;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Integer.parseInt;

public class ZVerseBackendDStrong {

    public static String augmentDStrong(final String resultFromJSword, final int ordinalInTestament, final Testament testament,
                                        final Versification v11n, final SwordBookMetaData bmd, final Verse verse,
                                        ZVerseBackendState rafBook) {
        if ((!bmd.hasFeature(FeatureType.STRONGS_NUMBERS)) || // No Strong in the selected Bible or
            (verse.getBook().ordinal() >= 69))                // It is Deutro canon
            return resultFromJSword;                          // Do not need to augment DStrong
        String versificationName = v11n.getName();
        int index = ordinalInTestament;
        if ((!versificationName.equals("NRSV")) && (!versificationName.equals("MT"))) {
            if (versificationName.equals("Leningrad")) {
                try {
                    index = VerseFactory.fromString(Versifications.instance().getVersification("MT"), verse.getOsisID()).getOrdinal();
                } catch (NoSuchVerseException e) {
                    log.error("Unable to look up strongs " + e);
                    return resultFromJSword;
                }
            } else {
                final Versification v11nNRSV = Versifications.instance().getVersification("NRSV");
                try {
                    Verse nrsvKey = VerseFactory.fromString(v11nNRSV, verse.getOsisID());
                    index = nrsvKey.getOrdinal();
                    index = v11nNRSV.getTestamentOrdinal(index);
                } catch (NoSuchVerseException e) {
                    log.error("Unable to look up strongs " + e);
                    return resultFromJSword;
                }
            }
        }

        int[] ordinals;
        String translation = bmd.getInitials();
        boolean isGreek = true;
        if (testament == Testament.OLD) {
            if (translation.equals("abpen_sb")  || translation.equals("LXX_th")  || translation.equals("abpgk_sb") ) {
                ordinals = OpenFileStateManager.osArray.ordinalOTGreek;
            }
            else {
                if ((!versificationName.equals("MT")) && (!versificationName.equals("Leningrad")))
                    ordinals = OpenFileStateManager.osArray.ordinalOTHebrewRSV;
                else
                    ordinals = OpenFileStateManager.osArray.ordinalOTHebrewOHB;
                isGreek = false;
            }
        }
        else
            ordinals = OpenFileStateManager.osArray.ordinalNT;
        byte[] augmentStrongs = getAugStrongsForVerse(ordinals, index, isGreek);
        String augmentedText = augmentDStrongInVerse(resultFromJSword, augmentStrongs, testament, isGreek);
        if (bmd.getIndexStatus() == IndexStatus.CREATING)
            createStepCacheForAugStrong(v11n, testament, ordinalInTestament, rafBook, bmd, augmentedText);
        return augmentedText;
    }

    private static void createStepCacheForAugStrong(final Versification v11n, final Testament testament,
                                                   final int ordinalInTestament, final ZVerseBackendState rafBook,
                                                   final SwordBookMetaData bmd, final String augmentedText) {
        try {
            int ntMaxOrdinal = v11n.maximumOrdinal() - v11n.maximumOTOrdinal(); // Max NT - max OT ordinal
            int otMaxOrdinal = v11n.maximumOTOrdinal();
            int maxOrdinalInTestament = (testament == Testament.NEW) ? ntMaxOrdinal : otMaxOrdinal;
            if (bmd.getInitials().equals("SBLG_th"))
                maxOrdinalInTestament--;
            if (ordinalInTestament == 1) {
                rafBook.createAugStrongCache(maxOrdinalInTestament, bmd, testament);
            }
            rafBook.addToAugStrongCache(ordinalInTestament, augmentedText, testament);
            Testament testamentThatNeedToBeFinalized = testament;
            // This is needed to detect KJVA switching from OT to NT.  KJVA has many verses in Deutro cannon which have
            // ordinals between the OT and NT ordinals
            boolean justGotFromOT2NT = false;
            if ((ordinalInTestament == 1) && (testament == Testament.NEW) &&
                    rafBook.isBuildingOTAugStrongCache()) {
                justGotFromOT2NT = true;
                testamentThatNeedToBeFinalized = Testament.OLD;
            }
            if ((ordinalInTestament == maxOrdinalInTestament) || (justGotFromOT2NT)) {
                try {
                    rafBook.finalizeAugStrongCache(bmd, testamentThatNeedToBeFinalized);
                    rafBook.openAndCacheAugmentedFiles(bmd.getLocation().getPath(), testamentThatNeedToBeFinalized);
                } catch (IOException e) {
                    log.error("createStepCacheForAugStrong", e);
                }
            }
        }
        catch (Exception e) {
            log.error("createStepCacheForAugStrong", e);
        }
    }
    private static byte[] getAugStrongsForVerse(int[] ordinals, int index, final boolean isGreek) {
        int currentPos = ordinals[index];
        if (currentPos > 0) {
            byte[] augStrongsForVerse = (isGreek) ? OpenFileStateManager.osArray.greekAugStrong :
                    OpenFileStateManager.osArray.hebrewAugStrong;
            int len = augStrongsForVerse[currentPos];
            currentPos ++;
            byte[] result = new byte[len];
            System.arraycopy(augStrongsForVerse, currentPos, result, 0, len);
            return result;
        }
        return new byte[0];
    }

    private static String normalizeStrongNumber(final String input, final Testament testament) {
        String copyOfInput = input.replace("strong:", "").trim();
        Pattern pattern = Pattern.compile("^([GH])(\\d+)[!A-Z.]?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(copyOfInput);
        String prefix;
        String number;
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

    private static String lookupMorphology(final String currentStrong) {
        if ((currentStrong.charAt(0) != 'G') || (currentStrong.charAt(1) != '5')) return "";
        char char3 = currentStrong.charAt(2);
        char char4 = currentStrong.charAt(3);
        char char5 = currentStrong.charAt(4);
        if (char3 == '6') {
            if (char4 == '5') {
                if (char5 == '6') return "AAI";
                if (char5 == '7') return "AAM";
                if (char5 == '8') return "AAN";
                if (char5 == '9') return "AAO";
            } else if (char4 == '6') {
                if (char5 == '0') return "AAP";
                if (char5 == '1') return "AAS";
                if (char5 == '2') return "AAI";
                if (char5 == '3') return "AAM";
                if (char5 == '4') return "AAN";
                if (char5 == '5') return "AAO";
                if (char5 == '6') return "AAP";
                if (char5 == '7') return "AAS";
                if (char5 == '8') return "AMI";
                if (char5 == '9') return "AMM";
            } else if (char4 == '7') {
                if (char5 == '0') return "AMN";
                if (char5 == '1') return "AMP";
                if (char5 == '2') return "AMS";
                if (char5 == '4') return "AAP";
                if (char5 == '5') return "AAI";
                if (char5 == '9') return "AAP";
            } else if (char4 == '8') {
                if (char5 == '0') return "AAS";
                if (char5 == '1') return "API";
                if (char5 == '2') return "APM";
                if (char5 == '3') return "APN";
                if (char5 == '4') return "APO";
                if (char5 == '5') return "APP";
                if (char5 == '6') return "APS";
            } else if (char4 == '9') {
                if (char5 == '2') return "FAI";
                if (char5 == '4') return "FAP";
                if (char5 == '5') return "FAI";
                if (char5 == '6') return "FAN";
                if (char5 == '7') return "FAP";
                if (char5 == '8') return "FMI";
                if (char5 == '9') return "FAI";
            }
        }
        else if (char3 == '7') {
            if (char4 == '0') {
                if (char5 == '1') return "FPI";
                if (char5 == '2') return "FPP";
                if (char5 == '7') return "IAI";
                if (char5 == '8') return "IAI";
            }
            else if (char4 == '1') {
                if (char5 == '0') return "IMI";
                if (char5 == '1') return "IAI";
                if (char5 == '2') return "IPI";
                if (char5 == '5') return "LAI";
                if (char5 == '7') return "LMI";
                if (char5 == '8') return "LPI";
                if (char5 == '9') return "PAI";
            }
            else if (char4 == '2') {
                if (char5 == '0') return "PAM";
                if (char5 == '1') return "PAN";
                if (char5 == '2') return "PAO";
                if (char5 == '3') return "PAP";
                if (char5 == '5') return "PAS";
                if (char5 == '6') return "PAP";
                if (char5 == '7') return "PPI";
            }
            else if (char4 == '3') {
                if (char5 == '1') return "PMI";
                if (char5 == '2') return "PMM";
                if (char5 == '3') return "PMN";
                if (char5 == '4') return "PMP";
                if (char5 == '5') return "PMS";
                if (char5 == '6') return "PAI";
                if (char5 == '7') return "PAM";
                if (char5 == '8') return "PAI";
                if (char5 == '9') return "PAO";
            }
            else if (char4 == '4') {
                if (char5 == '0') return "PAP";
                if (char5 == '1') return "PAS";
                if (char5 == '2') return "PAP";
                if (char5 == '3') return "PPI";
                if (char5 == '4') return "PPM";
                if (char5 == '5') return "PPI";
                if (char5 == '6') return "PPP";
                if (char5 == '7') return "PPS";
            }
            else if (char4 == '5') {
                if (char5 == '8') return "RAI";
                if (char5 == '9') return "RAM";
            }
            else if (char4 == '6') {
                if (char5 == '0') return "RAN";
                if (char5 == '1') return "RAP";
                if (char5 == '2') return "RAS";
                if (char5 == '4') return "RMI";
                if (char5 == '5') return "RMP";
                if (char5 == '6') return "RAI";
                if (char5 == '8') return "RAP";
                if (char5 == '9') return "RPI";
            }
            else if (char4 == '7') {
                if (char5 == '0') return "RPM";
                if (char5 == '1') return "RPI";
                if (char5 == '2') return "RPP";
            }
        }
        return "";
    }

    private static String augmentDStrongInWord(final String strongsListedWithWord, final Testament testament,
                                               final byte[] augStrongs, int[] augStrongPos, final boolean isGreek) { // final String translation, final String ref) {
        String[] strongsWithWord = strongsListedWithWord.split(" ");
        String result = "";
        String morphology = "";
        for (int i = 0; i < strongsWithWord.length; i++) {
            String currentStrong = normalizeStrongNumber(strongsWithWord[i], testament);
//            if (morphology.length() > 0) {
//                if (lookupMorphology(currentStrong).length() > 0)
//                    System.out.println("More than one morphology");
//            }
//            morphology = lookupMorphology(currentStrong);
            boolean assigned = false;
            int j = 0;
            int pos = 0;
            while (pos  < augStrongs.length) {
                ByteBuffer buffer = ByteBuffer.wrap(augStrongs, pos, 2);
                pos += 2;
                short numberPartOfStrong = buffer.getShort();
                boolean multiOccurrences = false;
                byte augment = augStrongs[pos];
                pos ++;
                byte multiOccurrencesBitmap = 0; // There are 8 bits.  Each bit is used for the occurrence of that word in a verse.  For example, 2nd occurrence of a word in a verse.
                boolean matchForNinthOccurrence = false;
                if (numberPartOfStrong < -1) { // If it is negative number, it is multi occurrences with uses 4 bytes
                    numberPartOfStrong = (short) (numberPartOfStrong * -1);
                    multiOccurrences = true;
                    if (augment < 1) {
                        matchForNinthOccurrence = true;
                        augment = (byte) (augment * -1);
                    }
                    multiOccurrencesBitmap = augStrongs[pos];
                    pos ++;
                }
                String nonAugStrong = (isGreek ? "G" : "H") + String.format("%04d", numberPartOfStrong);
                int compareValue = currentStrong.compareTo(nonAugStrong);
                if (compareValue == 0) {
                    augStrongPos[j] ++;
                    if (!multiOccurrences) {
                        if (!result.equals("")) result += " strong:";
                        result += nonAugStrong + (char) augment;
                        assigned = true;
                    }
                    else {
                        boolean match;
                        if (augStrongPos[j] == 9)
                            match = matchForNinthOccurrence;
                        else
                            match = ((0x01 << (augStrongPos[j] - 1)) & multiOccurrencesBitmap) > 0;
                        if (match) {
                            if (!result.equals("")) result += " strong:";
                            result += nonAugStrong + (char) augment;
                            assigned = true;
                        }
                    }
                }
                else if (compareValue == -1) // No more match because the aug strongs are sorted
                    break;
                j++;
            }
            if (!assigned) {
                short[] strongsWithAugments;
                byte[] defaultAugment;
                boolean greekInOT = false;
                if (testament == Testament.OLD) {
                    if (currentStrong.charAt(0) == 'G') {
                        strongsWithAugments = OpenFileStateManager.osArray.strongsWithAugmentsOTGreek;
                        defaultAugment = OpenFileStateManager.osArray.defaultAugmentOTGreek;
                        greekInOT = true;
                    }
                    else {
                        strongsWithAugments = OpenFileStateManager.osArray.strongsWithAugmentsOTHebrew;
                        defaultAugment = OpenFileStateManager.osArray.defaultAugmentOTHebrew;
                    }
                }
                else {
                    strongsWithAugments = OpenFileStateManager.osArray.strongsWithAugmentsNTGreek;
                    defaultAugment = OpenFileStateManager.osArray.defaultAugmentNTGreek;
                }
                int index = binarySearchOfStrong(currentStrong, strongsWithAugments);
                if ((index == -1) && (greekInOT)) {
                    index = binarySearchOfStrong(currentStrong, OpenFileStateManager.osArray.strongsWithAugmentsNTGreek);
                    defaultAugment = OpenFileStateManager.osArray.defaultAugmentNTGreek;
                }
                String strongToReturn = currentStrong;
                if (index > -1) {
                    strongToReturn += new String(new byte[]{defaultAugment[index]});
                }
                result += (result.equals("")) ? strongToReturn : (" strong:" + strongToReturn);
            }
        }
        if (morphology.length() == 0)
            return result;
        return result + "\" morph=\"robinson:V-" + morphology;
    }

    private static String augmentDStrongInVerse(final String fromJSword, final byte[] augStrongs,
                                                final Testament testament, final boolean isGreek) { // final String translation, final String ref) {
        final String lcFromJSword = fromJSword.toLowerCase();
        int pos = 0;
        int nummOfAugStrongsForThisVerse = 0;
        while (pos < augStrongs.length) {
            nummOfAugStrongsForThisVerse ++;
            ByteBuffer buffer = ByteBuffer.wrap(augStrongs, pos, 2);
            pos += 2;
            short numberPartOfStrong = buffer.getShort();
            if (numberPartOfStrong < -1)
                pos += 2;
            else
                pos ++;
        }
        int[] augStrongPos = new int[nummOfAugStrongsForThisVerse];
        int posOfStrongTag = lcFromJSword.indexOf("lemma=\"strong:");
        int resultCopyPos = 0;
        String result = "";
        while ((posOfStrongTag > -1) && (posOfStrongTag < lcFromJSword.length())) {
            posOfStrongTag += 14;
            result += fromJSword.substring(resultCopyPos, posOfStrongTag);
            int posEndOfStrongTag = lcFromJSword.indexOf("\"", posOfStrongTag);
            if (posEndOfStrongTag == -1) {
//                log.info("Cannot find end of strong tag: \" at " + translation + " " + ref);
                return fromJSword;
            }
            String strongsListedForThisWord = fromJSword.substring(posOfStrongTag, posEndOfStrongTag).trim();
            if (strongsListedForThisWord.length() > 1) {
                char char1 = strongsListedForThisWord.charAt(0);
                char char2 = strongsListedForThisWord.charAt(1);
                if ( (strongsListedForThisWord.length() > 4) ||
                     (((char1 == 'H') || (char1 == 'G')) && ((char2 >= '0') && (char2 <= '9'))) ) {
                    strongsListedForThisWord = augmentDStrongInWord(strongsListedForThisWord, testament, augStrongs,
                            augStrongPos, isGreek); // translation, ref);
                }
            }
            result += strongsListedForThisWord + '"';
            posOfStrongTag = posEndOfStrongTag + 1;
            resultCopyPos = posOfStrongTag;
            posOfStrongTag = lcFromJSword.indexOf("lemma=\"strong:", posOfStrongTag);
        }
        result += fromJSword.substring(resultCopyPos);
        return result;
    }

    private static int binarySearchOfStrong(final String augStrong, final short[] strongsWithAugments) {
        if (augStrong.length() < 2)
            return -1;
        int first = 0;
        int last = strongsWithAugments.length - 1;
        int key = convertStrong2Short(augStrong);
        if (key == -1)
            return -1;
        int mid = (first + last) / 2;
        while( first <= last ) {
            if ( strongsWithAugments[mid] < key ) first = mid + 1;
            else if ( strongsWithAugments[mid] == key ) return mid;
            else last = mid - 1;
            mid = (first + last) / 2;
        }
        return -1;
    }

    private static int convertStrong2Short(final String strong) {
        int startPos = 1;
        int endPos = strong.length() - 1;
        char suffix = strong.charAt(endPos);
        if (Character.isDigit(suffix)) endPos++;
        int num;
        try {
            num = parseInt(strong.substring(startPos, endPos)); // If the augmented Strong file has issue, it will run into an exception.
        } catch (NumberFormatException e) {
//            log.error("Strong number is not numeric at the expected positions: " + strong + " Something wrong with the tagging of Strong.");
            return -1;
        }

        if (num > 32767) {
//            log.error("Strong number has too many digits: " + strong + " Something wrong with the augmented Strong file.");
            return -1;
        }
        return num;
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ZVerseBackendState.class);

}