
set MYDIR=e:/video/razmutant
set CLAP=

for %%f in (%MYDIR%\lib\*.jar) do call set CLAP=%%CLAP%%;%%~ff

:st

java -cp ".;%MYDIR%\razmutant.jar;%CLAP%" -Xmx150m -Djava.io.tmpdir="%TMP%" com.razie.mutant.MutantMain

if EXIST %MYDIR%\upgrade\razmutant.jar (
   move %MYDIR%\upgrade\razmutant.jar %MYDIR%\razmutant.jar
   move %MYDIR%\upgrade\*.xml %MYDIR%\
   move %MYDIR%\upgrade\*.txt %MYDIR%\
   move %MYDIR%\upgrade\lib\*.jar %MYDIR%\lib
   move %MYDIR%\upgrade\plugins\* %MYDIR%\plugins
   goto st
) 

