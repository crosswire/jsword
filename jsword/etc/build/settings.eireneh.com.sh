#!/bin/sh

export WEBAPP_JSWORD=/opt/jsword/webapp
export WEBAPP_BIBLEDESKTOP=/opt/bibledesktop/webapp
export FTP_BASE=/opt/jsword/ftpbase
export FTP_PREFIX=http://www.eireneh.com/no-download-area
export JNLP_HOMEPAGE=http://tameion/bibledesktop

export JNLP_CODEBASE=$JNLP_HOMEPAGE/nightly
export JNLP_INSTALLED=$WEBAPP_BIBLEDESKTOP/nightly

export EMAIL=joe@eireneh.com
export ANT_HOME=/opt/ant
export JAVA_HOME=/usr/java/j2sdk

export PATH=$PATH:/usr/local/bin
export PATH=$PATH:/bin
export PATH=$PATH:/usr/bin
export PATH=$PATH:/usr/X11R6/bin
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$ANT_HOME/bin

