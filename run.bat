@echo off
setlocal
if not exist out mkdir out
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)
java -cp out app.Main
