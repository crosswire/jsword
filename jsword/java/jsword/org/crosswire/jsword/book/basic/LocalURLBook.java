package org.crosswire.jsword.book.basic;

/**
 * LocalURLBible is a helper for drivers that want to store files locally.
 * 
 * It takes care of providing you with a directory to work from and managing the
 * files stored in that directory.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class LocalURLBook
{
    /*
     * Flush the data written to disk
     *
    public void flush() throws BookException
    {
        try
        {
            Properties wprop = new Properties();
            wprop.put("Version", getBookMetaData().getFullName());

            URL prop_url = NetUtil.lengthenURL(getURL(), "bible.properties");
            OutputStream prop_out = NetUtil.getOutputStream(prop_url);
            wprop.store(prop_out, "Bible Config");
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.FLUSH_FAIL, ex);
        }
    }
    */

    /*
     * Read from the given source version to generate ourselves
     * @param source The Bible to read data from
     * @throws BookException If generation fails
     *
    public void generateText(Book source) throws BookException
    {
        // URGENT(joe): we need somewhere to do ...
        // DefaultBookMetaData bbmd = new DefaultBookMetaData(this, url, sbmd);

        Passage temp = PassageFactory.createPassage(PassageFactory.SPEED);

        Job job = JobManager.createJob("Copying Bible data to new driver", Thread.currentThread(), false);
        int percent = -1;

        // For every verse in the Bible
        Iterator it = WHOLE.verseIterator();
        while (it.hasNext())
        {
            // Create a Passage containing that verse alone
            Verse verse = (Verse) it.next();
            temp.clear();
            temp.add(verse);

            // Fire a progress event?
            int newpercent = 100 * verse.getOrdinal() / BibleInfo.versesInBible(); 
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, "Writing Verses");
            }

            // Read the document from the original version
            BookData doc = source.getData(temp);

            // Write the document to the mutable version
            setDocument(verse, doc);

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }
    }
    */

    /*
     * Write the XML to disk. Children will almost certainly want to
     * override this.
     * @param verse The verse to write
     * @param text The data to write
     *
    public void setDocument(Verse verse, BookData text) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY, new Object[] { verse.toString(), text.getPlainText() });
    }
    */

    /**
     * The Whole Bible
     */
    //private static final Passage WHOLE = PassageFactory.getWholeBiblePassage();
}
