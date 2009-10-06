
rem set JARDIR=C:\home\razvanc\myprojects\mutant\out
set JARDIR=E:\home\razvanc\workspace2\mutant\out

set MYDIR=c:\video\razmutant

date /t  > %JARDIR%\timestamp.txt
time /t >> %JARDIR%\timestamp.txt

del %MYDIR%\upgrade\razmutant.jar 

cd %JARDIR%
e:

cd ..\..\razpub\out
"%JAVA_HOME%\bin\jar" cvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razpubs\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razupnp\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razassets\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razagent\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razmedia\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razmutant\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
cd ..\..\razdidi\out
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\

cd %JARDIR%
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *

copy %JARDIR%\*.xml %MYDIR%\upgrade\
copy %JARDIR%\README.txt %MYDIR%\upgrade\README.txt

copy %MYDIR%\upgrade\razmutant.jar %MYDIR%\razmutant.jar
copy %MYDIR%\upgrade\*.xml %MYDIR%\
copy %MYDIR%\lib\*.jar %MYDIR%\upgrade\lib\
copy %JARDIR%\README.txt %MYDIR%\upgrade\README.txt

cd %MYDIR%
c:

