#!/bin/sh

export LOGFILE=$WEB_HOME/buildlog.txt
export XALAN=$JSWORD_HOME/jar/xalan25d1

export ANT_OPTS="-Xmx512m"
#export ANT_OPTS="-Xmx512m -Xbootclasspath/p:$XALAN/xalan.jar:$XALAN/xercesImpl.jar:$XALAN/xml-apis.jar"

export PROPERTIES=
export PROPERTIES="$PROPERTIES -Dtarget.web=$WEB_HOME"
export PROPERTIES="$PROPERTIES -Djnlp.hostname=$JNLP_HOST"
export PROPERTIES="$PROPERTIES -Dlocalprefix=$FTP_BASE"
export PROPERTIES="$PROPERTIES -Dwebprefix=$WEB_PREFIX"

