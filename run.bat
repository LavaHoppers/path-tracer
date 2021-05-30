@ECHO OFF

:: Runtime flags:
:: -m     enable multithreading
:: -a2    enable 2x antialiasing
:: -a4    enable 4x antialiasing
:: -a8    enable 8x antialiasing
:: -o     enable image file output
:: -d     enable realtime image Display
:: -hd    set resolution to 1080p
:: -4k    set resolution to 3840p

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
    java -cp %bin% Main %1 %2 %3 %4 %5 %6 %7 %8 %9

)


