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

#include <filemgr.h>
#include <utilstr.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#ifndef __GNUC__
#include <io.h>
#else
#include <unistd.h>
#endif


FileMgr FileMgr::systemFileMgr;


FileDesc::FileDesc(FileMgr *parent, char *path, int mode, int perms) {
	this->parent = parent;
	this->path = 0;
	stdstr(&this->path, path);
	this->mode = mode;
	this->perms = perms;
	offset = 0;
	fd = -77;
}


FileDesc::~FileDesc() {
	if (fd > 0)
		close(fd);
		
	if (path)
		delete [] path;
}


int FileDesc::getFd() {
	if (fd == -77)
		fd = parent->sysOpen(this);
	return fd;
}


FileMgr::FileMgr(int maxFiles) {
	this->maxFiles = maxFiles;		// must be at least 2
	files = 0;
}


FileMgr::~FileMgr() {
	FileDesc *tmp;
	
	while(files) {
		tmp = files->next;
		delete files;
		files = tmp;
	}
}


FileDesc *FileMgr::open(char *path, int mode, int perms) {
	FileDesc **tmp, *tmp2;
	
	for (tmp = &files; *tmp; tmp = &((*tmp)->next)) {
		if ((*tmp)->fd < 0)		// insert as first non-system_open file
			break;
	}

	tmp2 = new FileDesc(this, path, mode, perms);
	tmp2->next = *tmp;
	*tmp = tmp2;
	
	return tmp2;
}


void FileMgr::close(FileDesc *file) {
	FileDesc **loop;
	
	for (loop = &files; *loop; loop = &((*loop)->next)) {
		if (*loop == file) {
			*loop = (*loop)->next;
			delete file;
			break;
		}
	}
}


int FileMgr::sysOpen(FileDesc *file) {
	FileDesc **loop;
	int openCount = 1;		// because we are presently opening 1 file, and we need to be sure to close files to accomodate, if necessary
	
	for (loop = &files; *loop; loop = &((*loop)->next)) {

		if ((*loop)->fd > 0) {
			if (++openCount > maxFiles) {
				(*loop)->offset = lseek((*loop)->fd, 0, SEEK_CUR);
				::close((*loop)->fd);
				(*loop)->fd = -77;
			}
		}

		if (*loop == file) {
			if (*loop != files) {
				*loop = (*loop)->next;
				file->next = files;
				files = file;
			}
			file->fd = ::open(file->path, file->mode, file->perms);
			if (file->fd > 0)
				lseek(file->fd, file->offset, SEEK_SET);
			return file->fd;
		}
	}
	return -1;
}
