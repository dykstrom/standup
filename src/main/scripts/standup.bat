@echo off

setlocal

set ORIGINAL_DIR=%CD%
set SCRIPT_DIR=%~dp0

cd %SCRIPT_DIR%

image\bin\java --enable-native-access=javafx.graphics -m se.dykstrom.standup/se.dykstrom.standup.StandUp %*

cd %ORIGINAL_DIR%

endlocal
