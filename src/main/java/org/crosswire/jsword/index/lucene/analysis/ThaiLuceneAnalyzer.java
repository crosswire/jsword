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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import org.apache.lucene.util.Version;

/**
 * Tokenization using ThaiWordFilter. It uses java.text.BreakIterator to break
 * words. Stemming: Not implemented
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author sijo cherian
 */
public class ThaiLuceneAnalyzer extends AbstractBookAnalyzer {

    public ThaiLuceneAnalyzer() {
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream ts = new StandardTokenizer(matchVersion, reader);
        ts = new ThaiWordFilter(ts);
        if (doStopWords && stopSet != null) {
            ts = new StopFilter(false, ts, stopSet);
        }
        return ts;
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#reusableTokenStream(java.lang.String, java.io.Reader)
     */
    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams(new StandardTokenizer(matchVersion, reader));
            streams.setResult(new ThaiWordFilter(streams.getResult()));

            if (doStopWords && stopSet != null) {
                streams.setResult(new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(matchVersion), streams.getResult(), stopSet));
            }

            setPreviousTokenStream(streams);
        } else {
            streams.getSource().reset(reader);
            streams.getResult().reset(); // reset the ThaiWordFilter's state
        }
        return streams.getResult();
    }

    private final Version matchVersion = Version.LUCENE_29;
}
