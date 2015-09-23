#!/bin/bash

set -x

export PATH=/home/jsword/bin:$PATH
umask -S u=rwx,g=rwx,o=rx > /dev/null

export SVNROOT=http://www.crosswire.org/svn/jsword/trunk
export GITROOT=https://github.com/crosswire
export BUILD_HOME=~jsword/build
export WEB_HOME=~jsword/html

echo > $WEB_HOME/scm.log
for p in jsword
do
    git clone $GITROOT/${p}.git $BUILD_HOME/$p >> $WEB_HOME/scm.log 2>&1
done

for p in bibledesktop bibledesktop-web biblemapper common-aqua common-swing incubator javatar jsword-limbo jsword-support jsword-sword jsword-web
do
    svn checkout $SVNROOT/$p $BUILD_HOME/$p >> $WEB_HOME/scm.log 2>&1
done

find $BUILD_HOME -type f -name "*.sh" ! -executable -ls -exec chmod 0770 {} \;
