
REM you probably need to change this line
set JSWORD=%~dp0
echo "JSWORD=%JSWORD%"
REM set JSWORD=.

java -cp %JSWORD%\resource;%JSWORD%\log4j.jar;%JSWORD%\crimson.jar;%JSWORD%\jaxp.jar;%JSWORD%\jdom.jar;%JSWORD%\jlfgr-1_0.jar;%JSWORD%\jsword.jar;%JSWORD%\xalan.jar dtools
