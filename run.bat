:: Compile all the java files in ./scr and put the resulting
:: class files into ./bin
javac -d bin src/*.java
pause
:: Run the latest compiled .class files found in ./bin
:: The rendering will be run in multithreaded mode
:: -m is added to enable multithreading
java -cp bin Main -m