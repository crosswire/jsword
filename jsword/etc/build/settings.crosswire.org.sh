#!/bin/sh

export WEBAPP_JSWORD=/home/jsword/html
export WEBAPP_BIBLEDESKTOP=/home/jsword/bibledesktop/html
export FTP_BASE=/home/jsword/ftp
export FTP_PREFIX=http://www.crosswire.org/ftpmirror/pub/jsword
export JNLP_HOMEPAGE=http://www.crosswire.org/bibledesktop
export JNLP_CODEBASE=http://www.crosswire.org/bibledesktop/nightly
export JNLP_INSTALLED=$WEBAPP_BIBLEDESKTOP/nightly

export EMAIL=joe@eireneh.com
export ANT_HOME=/home/joe/local/ant
export JAVA_HOME=/usr/local/java

export PATH=$PATH:/usr/local/bin
export PATH=$PATH:/bin
export PATH=$PATH:/usr/bin
export PATH=$PATH:/home/joe/local/bin
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$ANT_HOME/bin
