:: New:
:: Wyvern launcher .bat version.
@echo off
set "RUNTIME_PATH=core files/jre1.8.0_191/bin/java.exe"
echo Starting: %RUNTIME_PATH%
"%RUNTIME_PATH%" -jar Wyvern.jar

:: Old:
:: java -jar Wyvern.jar
:: pause

exit
