@echo off
call mvn -e clean compile install package -DskipTests
cd ..\WebQuoteRpt_jpEar
call mvn wagon:upload-single wagon:sshexec
cd ..\webquote
pause
 