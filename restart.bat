@echo off
if "%1"=="kill" goto kill
if "%1"=="restart" goto restart
echo Usage: run.bat [kill/restart]
goto end

:kill
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /PID %%a /F
goto end

:restart
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /PID %%a /F
mvn clean package exec:exec
goto end

:end