
JARDIR=/host/home/razvanc/workspace7/mutant/out
MYDIR=/host/Video/razmutant

date  > $JARDIR/timestamp.txt
#time /t >> $JARDIR/timestamp.txt

rm $MYDIR/upgrade/razmutant.jar 

cd $JARDIR

cd ../../razpub/out
jar cvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cd ../../razpubs/out
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cd ../../razupnp/out
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cd ../../razassets/out
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cd ../../razagent/out
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cd ../../razmedia/out
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cd ../../razmutant/out
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/

cd $JARDIR
jar uvf $MYDIR/upgrade/razmutant.jar *
cp $JARDIR/*.xml $MYDIR/upgrade/
cp $JARDIR/README.txt $MYDIR/upgrade/README.txt

cp $MYDIR/upgrade/razmutant.jar $MYDIR/razmutant.jar
cp $MYDIR/upgrade/*.xml $MYDIR/
cp $MYDIR/lib/*.jar $MYDIR/upgrade/lib/
cp $JARDIR/README.txt $MYDIR/upgrade/README.txt

cd $MYDIR

