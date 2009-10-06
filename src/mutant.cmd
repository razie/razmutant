
set MYDIR=c:/video/razmutant
set CLAP=

for %%f in (%MYDIR%\lib\*.jar) do call set CLAP=%%CLAP%%;%%~ff

:st

java -cp ".;%MYDIR%\razmutant.jar;%CLAP%" -Xmx150m -Djava.io.tmpdir="%TMP%" com.razie.mutant.MutantMain

if EXIST %MYDIR%\upgrade\razmutant.jar (
   move %MYDIR%\upgrade\razmutant.jar %MYDIR%\razmutant.jar
   move %MYDIR%\upgrade\*.xml %MYDIR%\
   move %MYDIR%\upgrade\lib\*.xml %MYDIR%\lib
   goto st
) 

