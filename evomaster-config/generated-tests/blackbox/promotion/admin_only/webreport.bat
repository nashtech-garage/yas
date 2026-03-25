@echo off
setlocal

:: Get the directory where this batch file is located
set "SCRIPT_DIR=%~dp0"

:: Navigate to the script directory
cd /d "%SCRIPT_DIR%"

:: Check if Python is installed
where python >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Error: Python not found in PATH.
    echo Install Python from https://www.python.org/downloads/
    pause
    exit /b 1
)

python "webreport.py"

pause