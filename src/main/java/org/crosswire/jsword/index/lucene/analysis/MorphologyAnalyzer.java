package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

public class MorphologyAnalyzer extends AbstractBookAnalyzer {

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream ts = new WhitespaceAnalyzer().tokenStream(fieldName, reader);
        return new LowerCaseFilter(ts);
    }
}
