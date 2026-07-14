@echo off
rem Builds the backend (compile + tests).
setlocal
call "%~dp0_java-env.cmd" || exit /b 1
pushd "%~dp0..\backend\scheduler"
call mvnw.cmd clean verify
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%
