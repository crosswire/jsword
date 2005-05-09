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
for PROJECT in common jsword-support jsword jsword-sword jsword-web biblemapper bibledesktop bibledesktop-web
do
  echo "Updating $PROJECT"
  cd $JSWORD_HOME/../$PROJECT
  cvs -q up -d -P
  # Ensure that directory permissions and ownership are as expected
  find . -type d -name CVS -prune -o -type d ! -perm -2775 -print | xargs chmod 2775
  find . -type d -name CVS -prune -o -type d ! -group jsword | xargs chgrp jsword
done
chmod 755 $JSWORD_HOME/etc/build/*.sh

echo ""
echo "=============================================================================="
echo "Building jsword-web at `date`"
cd $JSWORD_HOME/../jsword-web
$ANT_HOME/bin/ant incremental $PROPERTIES
