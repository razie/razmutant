REM prepare the classpath and run the local agent

@set MUTANTDIR=C:/Video/razmutant
@set CLAP=

@if NOT EXIST %MUTANTDIR%\razmutant.jar (
   @echo "ERROR_DIR Please make sure you edit mutant.cmd and set MUTANTDIR properly"
   @goto quit
)

for %%f in (%MUTANTDIR%\lib\*.jar) do call set CLAP=%%CLAP%%;%%~ff

:st

java -cp ".;%MUTANTDIR%\razmutant.jar;%MUTANTDIR%\razpub.jar;%CLAP%" -Xmx150m -Djava.io.tmpdir="%TMP%" com.razie.mutant.MutantMain

REM if an update is available, update and restart instead of exiting

if EXIST %MUTANTDIR%\upgrade\razmutant.jar (
   move %MUTANTDIR%\upgrade\*.jar %MUTANTDIR%\
   move %MUTANTDIR%\upgrade\*.xml %MUTANTDIR%\
   move %MUTANTDIR%\upgrade\*.txt %MUTANTDIR%\
   move %MUTANTDIR%\upgrade\lib\*.jar %MUTANTDIR%\lib
   move %MUTANTDIR%\upgrade\plugins\* %MUTANTDIR%\plugins
   goto st
) 

:quit