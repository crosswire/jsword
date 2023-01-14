package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.LetterTokenizer;

import java.io.Reader;

public class HebrewCharTokenizer  extends LetterTokenizer {

    public HebrewCharTokenizer(Reader in) {
        super(in);
    }

    @Override
    protected boolean isTokenChar(char c) {
        return (c >= 0x590 && c <= 0x5ff );
    }

    @Override
    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }
}

