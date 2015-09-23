#!/bin/bash

set -x

export BUILD_HOME=~jsword/build
export WEB_HOME=~jsword/html
export PATH=/home/jsword/bin:$PATH:/usr/bin:/opt/java/bin
export SVNROOT=http://www.crosswire.org/svn/jsword/trunk
export GITROOT=https://github.com/crosswire

umask -S u=rwx,g=rwx,o=rx > /dev/null

# do a complete build every Saturday
case $(date +%u) in
  6) operations=all ;;
  *) operations=incremental ;;
esac

# If the user passed in arguments then use that
if (( $# ))
then
    # The "magic" quoting here with the @ preserves the arguments
    # The () creates an array
    operations=("${@}")
fi

echo > $WEB_HOME/scm.log
for p in jsword
do
    # Git pull can only be done from within the local clone
    # Create a sub-shell and do the git command from it
    (
	cd $BUILD_HOME/$p
	git pull
    ) >> $WEB_HOME/scm.log 2>&1
done

for p in bibledesktop bibledesktop-web biblemapper common-aqua common-swing incubator javatar jsword-limbo jsword-support jsword-sword jsword-web
do
    svn update $BUILD_HOME/$p >> $WEB_HOME/scm.log 2>&1
done

find $BUILD_HOME -type f -name "*.sh" ! -executable -ls -exec chmod 0770 {} \;

$BUILD_HOME/jsword/etc/build/build.sh "${operations[@]}"
