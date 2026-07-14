@echo off
rem Shared helper: locates a JDK and sets JAVA_HOME for the other scripts.
rem Priority: existing JAVA_HOME > newest jdk-17* under %USERPROFILE%\.jdks

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" goto :found

for /f "delims=" %%d in ('dir /b /ad /o-n "%USERPROFILE%\.jdks\jdk-17*" 2^>nul') do (
    set "JAVA_HOME=%USERPROFILE%\.jdks\%%d"
    goto :found
)

echo [ERROR] No JDK found. Set JAVA_HOME or install a JDK into %%USERPROFILE%%\.jdks
exit /b 1

:found
echo Using JAVA_HOME=%JAVA_HOME%
exit /b 0
