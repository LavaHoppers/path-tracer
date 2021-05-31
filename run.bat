@ECHO OFF
TITLE Path Tracer
COLOR 0C

:: Runtime flags:
:: -m <int>     enable multithreading
:: -a <int=1>   set the antialiasing
:: -o           enable image file output
:: -d           enable realtime image Display
SET args= 1920 1080 -v -d -m 100

:: Set the location for the *.class files
SET bin=bin

:: The dir containing the *.java files
SET src=src

:: Clear the previous class files
DEL %bin%\*.class

:: Compile all the java files in scr and put the resulting
:: class files into bin
javac -d %bin% %src%\*.java

:: Check if the *.java files were compiled successfully
IF %ERRORLEVEL%==1 (
    PAUSE
) ELSE (
    :: Run the latest compiled *.class files found in bin
    java -cp %bin% PathTracer %args%
)


