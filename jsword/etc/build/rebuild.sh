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
# we used to use: "$ANT_HOME/bin/ant cvsup $PROPERTIES"
# but this spawned a jvm just to do a "cvs -q up -d -P" which
# seemed like a waste ...

echo ""
echo "=============================================================================="
echo "Updating from crosswire"
cd $JSWORD_HOME/../common
cvs -q up -d -P
cd $JSWORD_HOME/../jsword-support
cvs -q up -d -P
cd $JSWORD_HOME/../jsword
cvs -q up -d -P
cd $JSWORD_HOME/../jsword-sword
cvs -q up -d -P
cd $JSWORD_HOME/../jsword-web
cvs -q up -d -P
cd $JSWORD_HOME/../bibledesktop
cvs -q up -d -P
cd $JSWORD_HOME/../bibledesktop-web
cvs -q up -d -P
cd $JSWORD_HOME/../biblemapper
cvs -q up -d -P
chmod 755 $JSWORD_HOME/etc/build/*.sh


echo ""
echo "=============================================================================="
echo "Building jsword-web"
cd $JSWORD_HOME/../jsword-web
$ANT_HOME/bin/ant incremental $PROPERTIES

echo ""
echo "=============================================================================="
echo "Building bibledesktop-web"
cd $JSWORD_HOME/../bibledesktop-web
$ANT_HOME/bin/ant incremental $PROPERTIES

