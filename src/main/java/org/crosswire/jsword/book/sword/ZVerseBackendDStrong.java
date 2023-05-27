package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.ZVerseBackendState;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import static java.lang.Integer.parseInt;

public class ZVerseBackendDStrong {

    public static String augmentDStrong(final String resultFromJSword, final int ordinalInTestament, final Testament testament,
                                        final Versification v11n, final SwordBookMetaData bmd, final Verse verse,
                                        ZVerseBackendState rafBook) {

        if ((!bmd.hasFeature(FeatureType.STRONGS_NUMBERS)) || (verse.getBook().ordinal() >= 69))
            return resultFromJSword; // If it is Deutro canon or no Strong, don't augment it.
        String versificationName = v11n.getName();
        int index = ordinalInTestament;
        if (!versificationName.equals("Leningrad") && !versificationName.equals("NRSV")) {
            if (versificationName.equals("MT")) {
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
        String translation = bmd.getInitials();
        String augmentedText = augmentDStrongInVerse(resultFromJSword, augmentStrongs, testament, translation, verse.toString());
        if (bmd.getIndexStatus() == IndexStatus.CREATING)
            createStepCacheForAugStrong(v11n, testament, ordinalInTestament, rafBook, bmd, augmentedText);
        return augmentedText;
    }

    private static void createStepCacheForAugStrong(final Versification v11n, final Testament testament,
                                                   final int ordinalInTestament, final ZVerseBackendState rafBook,
                                                   final SwordBookMetaData bmd, final String augmentedText) {
        int ntMaxOrdinal = v11n.maximumOrdinal() - v11n.maximumOTOrdinal(); // Max NT - max OT ordinal
        int otMaxOrdinal = v11n.maximumOTOrdinal();
        int maxOrdinalInTestament = (testament == Testament.NEW) ? ntMaxOrdinal : otMaxOrdinal;
        if (bmd.getInitials().equals("SBLG_th"))
            maxOrdinalInTestament --;
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
                throw new RuntimeException(e);
            }
        }
    }
    private static String[] getAugStrongsForVerse(final boolean combineAugStrongOfTwoVerses, int[] ordinals, int index,
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

    private static String normalizeStrongNumber(final String input, final Testament testament) {
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
    private static String augmentDStrongInWord(final String strongsListedWithWord, final Testament testament, final String[][] augStrongParts,
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
                }
                result += (result.equals("")) ? strongToReturn : (" strong:" + strongToReturn);
            }
        }
        return result;
    }

    private static String augmentDStrongInVerse(final String fromJSword, final String[] augStrongs, final Testament testament,
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
                System.out.println("Cannot find end of strong tag: \" at " + translation + " " + ref);
                return fromJSword;
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

    private static int binarySearchOfStrong(final String augStrong) {
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

    private static int convertStrong2Short(final String strong) {
        int startPos = 1;
        int endPos = strong.length() - 1;
        char suffix = strong.charAt(endPos);
        if (Character.isDigit(suffix)) endPos++;
        int num;
        try {
            num = parseInt(strong.substring(startPos, endPos)); // If the augmented Strong file has issue, it will run into an exception.
        } catch (NumberFormatException e) {
//            System.out.println("Strong number is not numeric at the expected positions: " + strong + " Something wrong with the tagging of Strong.");
            return -1;
        }

        if (num > 32767) {
            System.out.println("Strong number has too many digits: " + strong + " Something wrong with the augmented Strong file.");
            return -1;
        }
        return num;
    }
}

