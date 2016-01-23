# Distribution License:
# JSword is free software; you can redistribute it and/or modify it under
# the terms of the GNU Lesser General Public License, version 2.1 or later
# as published by the Free Software Foundation. This program is distributed
# in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
# the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Lesser General Public License for more details.
#
# The License is available on the internet at:
#      http://www.gnu.org/copyleft/lgpl.html
# or by writing to:
#      Free Software Foundation, Inc.
#      59 Temple Place - Suite 330
#      Boston, MA 02111-1307, USA
#
# Copyright CrossWire Bible Society, 2005 - 2016
#
This iso639_all.txt is generated from:
	http://www.sil.org/iso639-3/iso-639-3_YYYYMMDD.tab
and
	http://www.sil.org/iso639-3/iso-639-3_Name_Index_YYYYMMDD.tab

Convert it from UTF-8 to Java's ASCII representation with:
	native2ascii

The catalog iso639 changes content and format frequently.
Change the code below and rebuild the file as the need arises.

The iso639*.properties files are kept with a pruned set of names
for the languages found in the SWORD catalog of Books
and those names found at www.crosswire.org/wiki.

iso639.properties uses localized names, where known, and is sync'd SWORD's locales.d/locales.conf
iso639_en.properties has the English name, date ranges in brackets [...] and localized name suffixed in ()

This is primarily for performance, but it is also to facilitate translation.

Using:
#!/usr/bin/perl

use strict;
use Unicode::Normalize;
binmode(STDOUT, ":utf8");

my $date = "20130123";
my %names = ();
my %codes = ();
open(my $nameIndexFile, "<:utf8", "iso-639-3_Name_Index_$date.tab");
# skip the first line
my $firstLine = <$nameIndexFile>;
while (<$nameIndexFile>)
{
	# chomp ms-dos line endings
	s/\r//o;
	chomp();
	# Skip blank lines
	next if (/^$/o);
	# ensure it is normalized to NFC
	$_ = NFC($_);
	my @line = split(/\t/o, $_);
	$names{$line[0],$line[1]} = $line[2];
}

open(my $langFile,         "<:utf8", "iso-639-3_$date.tab");
# skip the first line
$firstLine = <$langFile>;
while (<$langFile>)
{
	# chomp ms-dos line endings
	s/\r//o;
	chomp();
	# Skip blank lines
	next if (/^$/o);
	# ensure it is normalized to NFC
	$_ = NFC($_);
	my @line = split(/\t/o, $_);
	# exclude extinct languages
	next if ($line[5] eq 'E');
	my $name = $names{$line[0],$line[6]} || $line[6];
	$codes{$line[3]} = $name if ($line[3]);
	$codes{$line[0]} = $name;
}

print("# Distribution License:\
# JSword is free software; you can redistribute it and/or modify it under\
# the terms of the GNU Lesser General Public License, version 2.1 or later\
# as published by the Free Software Foundation. This program is distributed\
# in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even\
# the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\
# See the GNU Lesser General Public License for more details.\
#\
# The License is available on the internet at:\
#      http://www.gnu.org/copyleft/lgpl.html\
# or by writing to:\
#      Free Software Foundation, Inc.\
#      59 Temple Place - Suite 330\
#      Boston, MA 02111-1307, USA\
#\
# Copyright CrossWire Bible Society, 2005 - 2016\
#     The copyright to this program is held by its authors.\
#\
# This file is compiled from the data at www.sil.org/iso639-3\
# and used according to the conditions stated there.\

");

for my $code (sort keys %codes) {
    print "$code=$codes{$code}\n";
}

