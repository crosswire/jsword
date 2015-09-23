umask -S u=rwx,g=rwx,o=rx > /dev/null

export FTP_BASE=/home/ftp/pub/jsword
export FTP_PREFIX=http://www.crosswire.org/ftpmirror/pub/jsword
export WEBSITE_JSWORD=/home/jsword/html
export PACKAGE_JSWORD=$FTP_BASE/nightly

export WEBSITE_BIBLEDESKTOP=/home/jsword/bibledesktop/html
export PACKAGE_BIBLEDESKTOP=$WEBSITE_BIBLEDESKTOP/nightly
export JNLP_HOMEPAGE=http://www.crosswire.org/bibledesktop
export JNLP_CODEBASE=$JNLP_HOMEPAGE/nightly/webstart

export KEYSTORE_FILE=../jsword/crosswire.keystore

export EMAIL=dmsmith@crosswire.com
export ANT_HOME=/home/jsword/ant
export JAVA_HOME=/home/jsword/jdk1.6

export PATH=$PATH:/usr/local/bin
export PATH=$PATH:/bin
export PATH=$PATH:/usr/bin
export PATH=$PATH:$ANT_HOME/bin
export PATH=$PATH:/home/jsword/bin
export PATH=$PATH:$JAVA_HOME/bin
