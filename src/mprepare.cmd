
rem set JARDIR=C:\home\razvanc\myprojects\mutant\bin
set JARDIR=E:\home\razvanc\workspace2\mutant\bin

set MYDIR=c:\video\razmutant

date /t  > %JARDIR%\timestamp.txt
time /t >> %JARDIR%\timestamp.txt

mkdir %MYDIR%\upgrade
del %MYDIR%\upgrade\*
mkdir %MYDIR%\upgrade\lib
mkdir %MYDIR%\upgrade\lib

cd %JARDIR%
e:

cd ..\..\razpub\bin
"%JAVA_HOME%\bin\jar" cvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razpubs\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razupnp\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razassets\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy plugins/*.xml %MYDIR%\upgrade\plugins\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razagent\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razmedia\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy plugins/*.xml %MYDIR%\upgrade\plugins\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razmutant\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy ..\lib\*.jar %MYDIR%\upgrade\lib
cd ..\..\razplay\bin
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *
copy *.xml %MYDIR%\upgrade\
copy ..\lib\*.jar %MYDIR%\upgrade\lib

cd %JARDIR%
"%JAVA_HOME%\bin\jar" uvf %MYDIR%\upgrade\razmutant.jar *

copy %JARDIR%\*.xml %MYDIR%\upgrade\
copy %JARDIR%\README.txt %MYDIR%\upgrade\README.txt

copy %MYDIR%\upgrade\razmutant.jar %MYDIR%\razmutant.jar
copy %MYDIR%\upgrade\*.xml %MYDIR%\
copy %MYDIR%\upgrade\plugins\*.xml %MYDIR%\plugins\
copy %SCALA_HOME%\lib/scala-library.jar %MYDIR%/upgrade/lib/
copy %SCALA_HOME%\lib/scala-compiler.jar %MYDIR%/upgrade/lib/
copy %MYDIR%\lib\*.jar %MYDIR%\upgrade\lib\
copy %JARDIR%\README.txt %MYDIR%\upgrade\README.txt

cd %MYDIR%
c:

