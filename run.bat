:: Clear the previous class files
@echo off
DEL bin\*.class
:: Compile all the java files in ./scr and put the resulting
:: class files into ./bin
javac -d bin src\*.java
pause
:: Run the latest compiled .class files found in ./bin
:: Runtime flags:
:: -m enable multithreading
:: -a enable antialiasing
java -cp bin Main -m