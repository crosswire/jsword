#!/bin/bash -x

export FTP_HOME=/home/ftp/pub/jsword
export NIGHTLY_HOME=$FTP_HOME/nightly
export RELEASE_HOME=$FTP_HOME/release
export BUILD_HOME=~jsword/build
export WEB_HOME=~jsword/bibledesktop/html
export PATH=/home/jsword/bin:$PATH:/usr/bin:

umask -S u=rwx,g=rwx,o=rx > /dev/null

if (( $# != 2 ))
then
  echo "Usage: $0 d.d.d yyyymmdd"
  exit
fi

typeset release_level
release_level=$1

typeset release_date
release_date=$2

for f in $NIGHTLY_HOME/jsword-$release_level-$release_date-*
do
  filename=$( basename $f )
  cp $f $RELEASE_HOME/${filename//$release_date-/}
done

for f in $RELEASE_HOME/jsword-$release_level-bin*
do
  filename=$( basename $f )
  cp $f $RELEASE_HOME/${filename//jsword/bibledesktop}
done

for f in $RELEASE_HOME/bibledesktop-$release_level*
do
  cp $f $WEB_HOME
done

#cp $BUILD_HOME/bibledesktop/etc/installer/BibleDesktopSetup.exe $WEB_HOME/stable
