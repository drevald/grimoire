@echo off
echo Loading environment variables from .env file...

if not exist .env (
    echo ERROR: .env file not found!
    echo Please copy .env.example to .env and configure your settings.
    echo Example: copy .env.example .env
    exit /b 1
)

REM Load environment variables from .env file (without setlocal to make them global)
for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
    REM Skip empty lines and comments
    echo %%a | findstr /b /c:"#" >nul
    if errorlevel 1 (
        if not "%%a"=="" (
            set "%%a=%%b"
            echo Set %%a=%%b
        )
    )
)

echo.
echo Starting Grimoire application...
echo.

gradlew.bat bootRun
