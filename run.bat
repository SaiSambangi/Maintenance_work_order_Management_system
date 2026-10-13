@echo off
setlocal EnableDelayedExpansion
title Maintenance Work Order Management System
echo ========================================================
echo   Maintenance Work Order Management System
echo ========================================================
echo.

rem Load environment variables from .env file if it exists
if exist "%~dp0.env" (
    echo [*] Loading environment variables from .env ...
    for /f "usebackq tokens=1,* delims==" %%A in ("%~dp0.env") do (
        set "line=%%A"
        if not "!line:~0,1!"=="#" (
            set "%%A=%%B"
        )
    )
)

if "%JAVA_HOME%"=="" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-17"
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [1/2] Starting Spring Boot Backend (Port 8080)...
start "Backend - Spring Boot (Port 8080)" cmd /k "cd /d %~dp0backend && %~dp0backend\mvnw.cmd spring-boot:run"

echo [2/2] Starting React Frontend (Port 5173)...
start "Frontend - React Vite (Port 5173)" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo ========================================================
echo Both servers have been launched in separate windows!
echo - Backend:  http://localhost:8080
echo - Frontend: http://localhost:5173
echo ========================================================
echo.
ping 127.0.0.1 -n 4 > nul
start http://localhost:5173
exit
