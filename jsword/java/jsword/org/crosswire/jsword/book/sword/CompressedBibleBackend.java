package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.net.URL;

/**
 * A stub for the Compressed sword Bible backend.
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
 * @see docs.Licence
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author The Sword project (don't know who - no credits in original files (canon.h))
 * @version $Id: $
 */
public class CompressedBibleBackend implements SwordBibleBackend 
{
	public CompressedBibleBackend(URL swordBase, SwordConfig config)
	{
		if(config.getCompressType()==SwordConstants.COMPRESSION_LZSS)
		{
			// plug in LZSS de / compressor
		}
		if(config.getCompressType()==SwordConstants.COMPRESSION_ZIP)
		{
			// plug in ZIP de / compressor
		}
		
		// implementation of different compression types looks to be identical other than the above.
	}
	
	/**
	 * @see org.crosswire.jsword.book.sword.SwordBibleBackend#getText(int, int, int, int)
	 */
	public String getText(int testament, int book, int chapter, int verse)
		throws IOException {
			return "A stub for compressed bible backend. ";
	}

}
