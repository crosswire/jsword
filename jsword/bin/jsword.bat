
REM STEP 1 - Initial setup
REM @echo off
if "%OS%"=="Windows_NT" @setlocal

REM STEP 2 - Check we know where we are installed
set DEFAULT_JSWORD=%~dp0\..
if "%JSWORD%"=="" set JSWORD=%DEFAULT_JSWORD%
set DEFAULT_JSWORD=
if exist "%JSWORD%" goto DoneFindJSword
REM have a blind guess ...
if not exist "%SystemDrive%\Program Files\JSword" goto FailedFindJSword
set JSWORD=%SystemDrive%\Program Files\JSword
:DoneFindJSword
echo "Using JSWORD=%JSWORD%"

REM STEP 3 - Setup the classpath
set LOCALCLASSPATH=%CLASSPATH%
for %%i in ("%JSWORD%\lib\*.jar") do call "%JSWORD%\bin\lcp.bat" %%i

REM STEP 4 - Run JSword
REM we might need to get extra memory?
REM set JSWORD_OPTS=-Xmx256M -classpath "%LOCALCLASSPATH%"
%JAVA_HOME%\bin\java.exe "-Djava.endorsed.dirs=%JSWORD%\lib" -classpath "%JSWORD%\resource" "-Djsword.bible.dir=%JSWORD%\resource" %JSWORD_OPTS% org.crosswire.jsword.view.swing.desktop.Desktop
goto End

:FailedFindJSword
echo "Can't find install directory. Please use %SystemDrive%\Program Files\JSword or set the JSWORD variable"

:End
set LOCALCLASSPATH=
set _JAVACMD=
if "%OS%"=="Windows_NT" @endlocal
