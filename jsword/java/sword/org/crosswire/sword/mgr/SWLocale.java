/******************************************************************************
 *  swlocale.h   - definition of Class SWLocale used for retrieval
 *				of locale lookups
 *
 * $Id$
 *
 * Copyright 2000 CrossWire Bible Society (http://www.crosswire.org)
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

import java.util.HashMap;
import org.crosswire.sword.keys.*;

public class SWLocale {
    HashMap lookupTable;
    SWConfig localeSource;
    String name;
    String description;
    Canon.Abbrev bookAbbrevs;
    char []BMAX;
    Canon.Book [][]books;

    public SWLocale(String ifilename) throws java.io.IOException {

        name         = null;
        description  = null;
        bookAbbrevs  = null;
        BMAX         = null;
        books        = null;
        localeSource = new SWConfig(ifilename);

        try {
            name = localeSource.getProperty("Meta", "Name");
        }
        catch (Exception e) { /* no name, bad locale file */ }

        try {
            description = localeSource.getProperty("Meta", "Description");
        }
        catch (Exception e) { /* no description, maybe bad locale file */ }
    }


    public String translate(String text) {

        String entry = (String)lookupTable.get(text);

        if (entry == null) {
            String confEntry = null;
            try {
                confEntry = localeSource.getProperty("Text", text);
            }
            catch (Exception e) { /* no entry in locale file */}
            if (confEntry == null)
                lookupTable.put(text, text);
            else	lookupTable.put(text, confEntry);
            entry = (String)lookupTable.get(text);
        }
        return entry;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public void augment(SWLocale addFrom) {
        localeSource.augment(addFrom.localeSource);
    }


/*
    public Abbrev getBookAbbrevs() {
        String nullstr = "";
        if (bookAbbrevs == null) {
            int i;
            Map bookAbrevs = (Map)localeSource.sections.get("Book Abbrevs");
        bookAbbrevs = new struct abbrev[size + 1];
        for (i = 0, it = localeSource->Sections["Book Abbrevs"].begin(); it != localeSource->Sections["Book Abbrevs"].end(); it++, i++) {
            bookAbbrevs[i].ab = (*it).first.c_str();
            bookAbbrevs[i].book = atoi((*it).second.c_str());
        }
        bookAbbrevs[i].ab = nullstr;
        bookAbbrevs[i].book = -1;
    }

    return bookAbbrevs;
}


    public void getBooks(char **iBMAX, struct sbook ***ibooks);
void SWLocale::getBooks(char **iBMAX, struct sbook ***ibooks) {
    if (!BMAX) {
        BMAX = new char [2];
        BMAX[0] = VerseKey::builtin_BMAX[0];
        BMAX[1] = VerseKey::builtin_BMAX[1];

        books = new struct sbook *[2];
        books[0] = new struct sbook[BMAX[0]];
        books[1] = new struct sbook[BMAX[1]];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < BMAX[i]; j++) {
                books[i][j] = VerseKey::builtin_books[i][j];
                books[i][j].name = translate(VerseKey::builtin_books[i][j].name);
            }
        }
    }

    *iBMAX  = BMAX;
    *ibooks = books;
}
*/
}
