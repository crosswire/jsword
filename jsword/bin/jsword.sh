#!/bin/sh

JSWORD=`dirname $0`

CP=""
CP=$CP:$JSWORD/resource
CP=$CP:$JSWORD/crimson.jar
CP=$CP:$JSWORD/jaxp.jar
CP=$CP:$JSWORD/jdom.jar
CP=$CP:$JSWORD/jlfgr-1_0.jar
CP=$CP:$JSWORD/jsword.jar
CP=$CP:$JSWORD/xalan.jar
CP=$CP:$JSWORD/log4j.jar

java -cp $CP org.crosswire.jsword.view.swing.desktop.Desktop
