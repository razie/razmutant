
MYDIR=/host/Video/razmutant
CLAP=

for ff in `ls $MYDIR/lib/*.jar` ; do CLAP=$CLAP:$ff ; done

GIGI=

:st
while [[ -z $GIGI ]]
do

GIGI="nonzero"

#java -cp ".:$MYDIR/razmutant.jar:$CLAP" -Xmx150m -Djava.io.tmpdir="%TMP%" com.razie.mutant.MutantMain
echo java -cp ".:$MYDIR/razmutant.jar:$CLAP" -Xmx150m com.razie.mutant.MutantMain
java -cp ".:$MYDIR/razmutant.jar:$CLAP" -Xmx150m com.razie.mutant.MutantMain

if [[ -e $MYDIR/upgrade/razmutant.jar ]] 
then
   mv $MYDIR/upgrade/razmutant.jar $MYDIR/razmutant.jar
   mv $MYDIR/upgrade/*.xml $MYDIR/
   mv $MYDIR/upgrade/lib/*.xml $MYDIR/lib
   GIGI=
fi

done

