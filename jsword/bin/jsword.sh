#!/bin/sh

JSWORD=`dirname $0`/..

CP=""
CP=$CP:$JSWORD/resource
CP=$CP:$JSWORD/lib/crimson.jar
CP=$CP:$JSWORD/lib/jaxp.jar
CP=$CP:$JSWORD/lib/jdom.jar
CP=$CP:$JSWORD/lib/jlfgr-1_0.jar
CP=$CP:$JSWORD/lib/jsword.jar
CP=$CP:$JSWORD/lib/xalan.jar
CP=$CP:$JSWORD/lib/log4j-1.2.7.jar
CP=$CP:$JSWORD/lib/jaxb-ri.jar
CP=$CP:$JSWORD/lib/jaxb-libs.jar
CP=$CP:$JSWORD/lib/jaxb-api.jar

java -cp $CP org.crosswire.jsword.view.swing.desktop.Desktop
