
REM you probably need to change this line
set JSWORD=%~dp0\..
echo "JSWORD=%JSWORD%"

set CP=""
set CP=%CP%;%JSWORD%\resource
set CP=%CP%;%JSWORD%\lib\crimson.jar
set CP=%CP%;%JSWORD%\lib\jaxp.jar
set CP=%CP%;%JSWORD%\lib\jdom.jar
set CP=%CP%;%JSWORD%\lib\jlfgr-1_0.jar
set CP=%CP%;%JSWORD%\lib\jsword.jar
set CP=%CP%;%JSWORD%\lib\xalan.jar
set CP=%CP%;%JSWORD%\lib\log4j.jar

java -cp %CP% org.crosswire.jsword.view.swing.desktop.Desktop
