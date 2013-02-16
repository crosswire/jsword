#!/bin/bash

DIRNAME=`dirname $0`
if [ $DIRNAME = "." ]
then
    BUILD_HOME=`pwd`
else
    FIRST=`echo $DIRNAME | cut -c 1`
    if [ $FIRST = "/" ]
    then
        BUILD_HOME=$DIRNAME
    else
        cd `pwd`/$DIRNAME
        BUILD_HOME=`pwd`
    fi
fi

cd $BUILD_HOME/../../..
JSWORD_HOME=`pwd`/jsword

. $JSWORD_HOME/etc/build/settings.`dnsdomainname`.sh
. $JSWORD_HOME/etc/build/settings.global.sh

rm -f $LOGFILE

{
    echo JSWORD_HOME=$JSWORD_HOME

    echo ""
    echo "=============================================================================="
    echo "Building jsword-web at `date`"
    cd jsword-web
    $ANT_HOME/bin/ant "$@" $PROPERTIES
} > $LOGFILE 2>&1

{
  echo "## Removing old nightly builds"
  find $FTP_BASE/nightly -type f -mtime +7 -exec rm -v {} \;
  echo ""
  echo "## Build log"
  cat $LOGFILE
} | /bin/mail -s "jsword buildlog (from `dnsdomainname`)" $EMAIL
