#!/bin/sh

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

cd $BUILD_HOME/../..
JSWORD_HOME=`pwd`

echo JSWORD_HOME=$JSWORD_HOME

. $JSWORD_HOME/etc/build/settings.`dnsdomainname`.sh
. $JSWORD_HOME/etc/build/settings.global.sh

# keep the cvsup separate to allow build.xml to be updated

# This appears to be done by the jsword cvsup as a result of the ..?
# cd $SUPPORT_HOME
# $ANT_HOME/bin/ant cvsup $PROPERTIES

$ANT_HOME/bin/ant cvsup $PROPERTIES
$ANT_HOME/bin/ant nightly $PROPERTIES

