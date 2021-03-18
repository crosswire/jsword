package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

import java.io.IOException;

/**
 * Simply removes pointing from the given term
 */
public class HebrewPointingFilter extends AbstractBookTokenFilter {
    private final TermAttribute termAtt;

    /**
     * @param input the token stream
     */
    public HebrewPointingFilter(final TokenStream input) {
        super(input);
        this.termAtt = addAttribute(TermAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final String unaccentedForm = unPoint(this.termAtt.term(), false);
            this.termAtt.setTermBuffer(unaccentedForm);
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param word text with pointing
     * @param unpointVowels true to indicate we also want to exclude vowels
     * @return text without pointing
     */
    public static String unPoint(final String word, boolean unpointVowels) {
        char endChar = unpointVowels ? ALEPH : SHEVA;

        final StringBuilder sb = new StringBuilder(word);
        int i = 0;
        while (i < sb.length()) {
            final char currentChar = sb.charAt(i);
            //ignore characters outside of the Hebrew character set
            if(currentChar < ETNAHTA || currentChar > ALEPH_LAMED) {
                i++;
            } else if (currentChar < endChar) {
                sb.deleteCharAt(i);
            } else if (currentChar >= HEBREW_COMBINED_RANGE_START && currentChar < ALEPH_LAMED) {
                sb.setCharAt(i, (char) (currentChar - DAGESH_GAP));
                i++;
            } else {
                i++;
            }
        }
        return sb.toString();
    }

    private static final char SHEVA = 0x05B0;
    private static final int ETNAHTA = 0x0591;
    private static final int DAGESH_GAP = 0xFB44 - 0x05e3;
    private static final int ALEPH = 0x05D0;
    private static final char ALEPH_LAMED = 0xFB4F;
    private static final char HEBREW_COMBINED_RANGE_START = 0xFB1D;
}
