#!/bin/sh

export SUPPORT_HOME=$JSWORD_HOME-support
export WEB_HOME=/var/tomcat4/webapps/jsword
export FTP_BASE=/home/ftp/pub/jsword
export ANT_HOME=/home/joe/local/ant
export JAVA_HOME=/usr/local/java
export WEB_PREFIX=http://www.crosswire.org/ftpmirror/pub/jsword
export JNLP_HOST=www.crosswire.org

export PATH=$PATH:/usr/local/bin
export PATH=$PATH:/bin
export PATH=$PATH:/usr/bin
export PATH=$PATH:/usr/X11R6/bin
export PATH=$PATH:/home/joe/local/bin
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$ANT_HOME/bin

# extssh makes cvs get the dir perms wrong, but we can fix it
chgrp -R -c jswordcvs /cvs/jsword/*
