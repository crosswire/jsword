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

. $JSWORD_HOME/etc/build/commands.`dnsdomainname`.sh


# keep the cvsup separate to allow build.xml to be updated

echo "Building common"
cd $JSWORD_HOME/../common
$ANT_HOME/bin/ant cvsup $PROPERTIES
$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building jsword-support"
cd $JSWORD_HOME/../jsword-support
cvs -q up -d -P
#$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building jsword"
cd $JSWORD_HOME/../jsword
$ANT_HOME/bin/ant cvsup $PROPERTIES
$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building jsword-sword"
cd $JSWORD_HOME/../jsword-sword
cvs -q up -d -P
#$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building jsword-web"
cd $JSWORD_HOME/../jsword-web
$ANT_HOME/bin/ant cvsup $PROPERTIES
$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building bibledesktop"
cd $JSWORD_HOME/../bibledesktop
$ANT_HOME/bin/ant cvsup $PROPERTIES
$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building bibledesktop-web"
cd $JSWORD_HOME/../bibledesktop-web
$ANT_HOME/bin/ant cvsup $PROPERTIES
$ANT_HOME/bin/ant incremental $PROPERTIES

echo "Building biblemapper"
cd $JSWORD_HOME/../biblemapper
cvs -q up -d -P
#$ANT_HOME/bin/ant incremental $PROPERTIES
