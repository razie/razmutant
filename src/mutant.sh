
MYDIR=/host/Video/razmutant
CLAP=

for ff in `ls $MYDIR/lib/*.jar` ; do CLAP=$CLAP:$ff ; done

GIGI=

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
   mv $MYDIR/upgrade/*.txt $MYDIR/
   mv $MYDIR/upgrade/lib/*.jar $MYDIR/lib
   mv $MYDIR/upgrade/plugins/* $MYDIR/plugins
   mkdir -p $MYDIR/log

   GIGI=
fi

done

