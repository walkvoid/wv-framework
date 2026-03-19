@echo off
REM 可选：使用 JDK 21（与工程一致）
REM set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"

cd /d "%~dp0"
call gradlew.bat %*
