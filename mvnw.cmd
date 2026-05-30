@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script - EFD Parser
@REM Requires Java 17+. If JAVA_HOME is not set, falls back to the JDK
@REM bundled with the VS Code Java extension (Temurin 21).
@REM ----------------------------------------------------------------------------
@echo off
setlocal

pushd "%~dp0"

set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
set PROJECT_DIR=%CD%
set JAVA_EXE=

@REM Detect Java: explicit JAVA_HOME > VS Code Temurin 21 > system java
if defined JAVA_HOME (
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
    goto :run_wrapper
)

set VSCODE_JDK=%USERPROFILE%\.vscode\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64
if exist "%VSCODE_JDK%\bin\java.exe" (
    set JAVA_EXE=%VSCODE_JDK%\bin\java.exe
    goto :run_wrapper
)

@REM Fall back to system java - must be Java 17+
for /f "delims=" %%i in ('where java 2^>nul') do (
    set JAVA_EXE=%%i
    goto :run_wrapper
)

echo ERROR: Java 17+ not found. Install Java 17+ and set JAVA_HOME.
exit /B 1

:run_wrapper
if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven wrapper JAR...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%WRAPPER_JAR%' -UseBasicParsing"
    if ERRORLEVEL 1 ( echo Download failed. && exit /B 1 )
)

"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%PROJECT_DIR%" -cp "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*
if ERRORLEVEL 1 goto :error
goto :end
:error
set ERROR_CODE=%ERRORLEVEL%
:end
popd
exit /B %ERROR_CODE%
