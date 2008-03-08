This iso639_all.txt is generated from http://www.sil.org/iso639-3/iso-dis-639-3_20060421.tab
 Using
#!/usr/bin/perl
while (<>)
  {
          chomp();
          next if (/^$/o);
          my @line = split(/\t/o, $_);
          print "$line[2]=$line[5]\n" if ($line[2]);
          print "$line[0]=$line[5]\n";
  }
then sorting it with:
  sort -t = -k 2
and then finally running it through
  native2ascii
The catalog iso639 changes frequently. Rebuild this file as the need arises.

The iso639*.properties files are kept with a pruned set of names
for the languages found in the Sword catalog of Books.

This is primarily for performance, but it is also to facilitate translation.
