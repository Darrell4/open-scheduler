@echo off
rem Runs the backend test suite.
setlocal
call "%~dp0_java-env.cmd" || exit /b 1
pushd "%~dp0..\backend\scheduler"
call mvnw.cmd test
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%
