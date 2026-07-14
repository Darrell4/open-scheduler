@echo off
rem Starts the local PostgreSQL container.
docker compose -f "%~dp0..\docker-compose.yml" up -d
