@echo off
rem Runs the Spring Boot app locally (expects Postgres, see db-up.cmd).
setlocal
call "%~dp0_java-env.cmd" || exit /b 1
pushd "%~dp0..\backend\scheduler"
call mvnw.cmd spring-boot:run
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%
