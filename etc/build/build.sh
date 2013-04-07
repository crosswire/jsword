#!/bin/bash

DIRNAME=$(dirname $0)
if [ $DIRNAME = "." ]
then
    BUILD_HOME=$PWD
else
    FIRST=$(echo $DIRNAME | cut -c 1)
    if [ $FIRST = "/" ]
    then
        BUILD_HOME=$DIRNAME
    else
        cd $PWD/$DIRNAME
        BUILD_HOME=$PWD
    fi
fi

cd $BUILD_HOME/../../..
JSWORD_HOME=$PWD/jsword

. $JSWORD_HOME/etc/build/settings.$(dnsdomainname).sh
. $JSWORD_HOME/etc/build/settings.global.sh

rm -f $LOGFILE

{
    echo JSWORD_HOME=$JSWORD_HOME

    echo ""
    echo "=============================================================================="
    echo "Building jsword-web at $(date)"
    cd jsword-web
    $ANT_HOME/bin/ant "$@" $PROPERTIES
} > $LOGFILE 2>&1

{
  echo "## Removing old nightly builds, keeping 5 most recent"
  for app in jsword bibledesktop
  do
    for type in doc bin src
    do
      for compress in zip tar.gz
      do
        ls -1r $FTP_BASE/nightly/$app*-$type.$compress 2> /dev/null | awk 'NR>5'
        rm -f $(ls -1r $FTP_BASE/nightly/$app*-$type.$compress 2> /dev/null | awk 'NR>5')
      done
    done
  done
  echo ""
  echo "## Build log"
  cat $LOGFILE
} | /bin/mail -s "jsword buildlog (from $(dnsdomainname))" $EMAIL
