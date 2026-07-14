@echo off
rem Stops the local PostgreSQL container (data volume is kept).
docker compose -f "%~dp0..\docker-compose.yml" down
