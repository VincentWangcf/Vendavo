@echo off
call mvn -e clean compile install package -DskipTests
cd ..\WebQuoteEar
call mvn wagon:upload-single wagon:sshexec
cd ..\webquote
pause
 