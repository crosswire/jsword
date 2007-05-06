package org.crosswire.jsword.index.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

public class LuceneAnalyzer extends Analyzer
{

    public LuceneAnalyzer()
    {
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        // do not tokenize keys
        if (LuceneIndex.FIELD_KEY.equals(fieldName))
        {
            return KEYWORD.tokenStream(fieldName, reader);
        }
        // Split Strong's Numbers on whitespace
        else if (LuceneIndex.FIELD_STRONG.equals(fieldName))
        {
            return WHITESPACE.tokenStream(fieldName, reader);
        }
        // Split xrefs's on whitespace
        else if (LuceneIndex.FIELD_XREF.equals(fieldName))
        {
            return WHITESPACE.tokenStream(fieldName, reader);
        }
        // just use the standard tokenizer
        else
        {
            return SIMPLE.tokenStream(fieldName, reader);
        }
    }

    private static final Analyzer KEYWORD = new KeywordAnalyzer();
    private static final Analyzer WHITESPACE = new WhitespaceAnalyzer();
    private static final Analyzer SIMPLE = new SimpleAnalyzer();
}
