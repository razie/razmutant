# prepare the classpath and run the local agent

MUTANTDIR=/host/Video/razmutant
CLAP=

for ff in `ls $MUTANTDIR/lib/*.jar` ; do CLAP=$CLAP:$ff ; done

GIGI=

while [[ -z $GIGI ]]
do

   GIGI="nonzero"

   echo java -cp ".:$MUTANTDIR/razmutant.jar:$MUTANTDIR/razpub.jar:$CLAP" -Xmx150m com.razie.mutant.MutantMain
   java -cp ".:$MUTANTDIR/razmutant.jar:$MUTANTDIR/razpub.jar:$CLAP" -Xmx150m com.razie.mutant.MutantMain 

   # if an update is available, update and restart instead of exiting

   if [[ -e $MUTANTDIR/upgrade/razmutant.jar ]] 
   then
      mv $MUTANTDIR/upgrade/*.jar $MUTANTDIR/
      mv $MUTANTDIR/upgrade/cfg/*.xml $MUTANTDIR/cfg/
      mv $MUTANTDIR/upgrade/*.xml $MUTANTDIR/
      mv $MUTANTDIR/upgrade/*.txt $MUTANTDIR/
      mv $MUTANTDIR/upgrade/lib/*.jar $MUTANTDIR/lib/
      mv $MUTANTDIR/upgrade/plugins/* $MUTANTDIR/plugins/
      mkdir -p $MUTANTDIR/log

      GIGI=
   else
      echo "DID NOT COPY FROM UPGRADE...!!!"
   fi

done

