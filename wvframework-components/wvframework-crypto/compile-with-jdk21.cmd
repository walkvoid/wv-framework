@echo off
REM 在 wv-framework 根目录执行 reactor 编译本模块（需 JDK 21）
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0..\.."
echo JAVA_HOME=%JAVA_HOME%
java -version
mvn -pl wvframework-components/wvframework-crypto -am clean compile test %*
