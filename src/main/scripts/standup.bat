@echo off

setlocal

set ORIGINAL_DIR=%CD%
set SCRIPT_DIR=%~dp0

cd %SCRIPT_DIR%

image\bin\java -m se.dykstrom.standup/se.dykstrom.standup.StandUp %*

cd %ORIGINAL_DIR%

endlocal
