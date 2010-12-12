This iso639_all.txt is generated from:
	http://www.sil.org/iso639-3/iso-639-3_20090210.tab
and
	http://www.sil.org/iso639-3/iso-639-3_Name_Index_20090210.tab

Sort the file if desired with:
	sort -t = -k 2
Convert it from UTF-8 to Java's ASCII representation with:
	native2ascii

The catalog iso639 changes content and format frequently.
Change the code below and rebuild the file as the need arises.

The iso639*.properties files are kept with a pruned set of names
for the languages found in the Sword catalog of Books.

This is primarily for performance, but it is also to facilitate translation.

Using:
#!/usr/bin/perl
# The file currently is:

use strict;
use Unicode::Normalize;
binmode(STDOUT, ":utf8");

my %names = ();
open(my $nameIndexFile, "<:utf8", "iso-639-3_Name_Index_20090210.tab");
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

open(my $langFile,         "<:utf8", "iso-639-3_20090210.tab");
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
	my $name = $names{$line[0],$line[6]};
	print "$line[3]=$name\n" if ($line[3]);
	print "$line[0]=$name\n";
}

