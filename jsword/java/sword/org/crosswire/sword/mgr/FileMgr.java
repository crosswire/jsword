/******************************************************************************
 *  filemgr.cpp	- implementation of class FileMgr used for pooling file handles
 *
 * $Id$
 *
 * Copyright 1998 CrossWire Bible Society (http://www.crosswire.org)
 *	CrossWire Bible Society
 *	P. O. Box 2528
 *	Tempe, AZ  85280-2528
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation version 2.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 */
package org.crosswire.sword.mgr;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.io.FileInputStream;

public class FileMgr {

	public static FileMgr systemFileMgr = new FileMgr(20);
	int maxFiles;
	List files = new LinkedList();


	FileMgr(int maxFiles) {
		this.maxFiles = maxFiles;		// must be at least 2
		files = null;
	}


	InputStream open(String path) {
		InputStream tmp;
	
	tmp = new InputStream(this, path);
	files.add(0, tmp);

	return tmp;
}


void close(InputStream file) throws java.io.IOException {
	if (file.istream != null)
		file.istream.close();
	files.remove(file);
}


	FileInputStream sysOpen(InputStream file) throws java.io.IOException {
		int openCount = 1;		// because we are presently opening 1 file, and we need to be sure to close files to accomodate, if necessary
		ListIterator loop;
		InputStream is;
		
		loop = files.listIterator();
		while (loop.hasNext()) {
			is = (InputStream) loop.next();
			if (is.istream != null) {
				if (++openCount > maxFiles) {
					is.offset = is.istream.skip(0);
					is.istream.close();
					is.istream = null;
				}
			}

			if (loop == file) {
				files.remove(file);
				files.add(0, file);
				file.istream = new FileInputStream(file.path);
				file.istream.skip(file.offset);

				return file.istream;
			}
		}
		return null;
	}


	public class InputStream extends java.io.InputStream {
		FileMgr fileMgr = null;
		String path = null;
		long offset;
		FileInputStream istream;

		public InputStream(FileMgr fileMgr, String path) {
			this.fileMgr = fileMgr;
			this.path = path;
			offset = 0;
			istream = null;
		}


		public int available() {
			int retVal = 0;
			try {
				if (istream == null)
					istream = fileMgr.sysOpen(this);

				retVal = istream.available();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return retVal;
		}


		public void close() throws java.io.IOException {
			fileMgr.close(this);
		}


		public synchronized void mark(int i) {
		}


		public boolean markSupported() {
			return false;
		}


	    public int read() throws java.io.IOException {
			if (istream == null)
				istream = fileMgr.sysOpen(this);

			return istream.read();
		}


		public int read(byte[] bytes) throws java.io.IOException {
			if (istream == null)
				istream = fileMgr.sysOpen(this);

			return istream.read(bytes);
		}


		public int read(byte[] bytes, int off, int len) throws java.io.IOException {
			if (istream == null)
				istream = fileMgr.sysOpen(this);

			return istream.read(bytes, off, len);
		}


		public synchronized void reset() throws java.io.IOException {
			if (istream == null)
				istream = fileMgr.sysOpen(this);

			istream.reset();
		}
		
	    public long skip(long jump) throws java.io.IOException {
			if (istream == null)
				istream = fileMgr.sysOpen(this);

			return istream.skip(jump);
		}
	}
}
