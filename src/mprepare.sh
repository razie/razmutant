
JARDIR=/host/home/razvanc/workspace7u4/razmutant/bin
MYDIR=/host/Video/razmutant

date  > $JARDIR/timestamp.txt
#time /t >> $JARDIR/timestamp.txt

rm -r $MYDIR/upgrade
mkdir $MYDIR/upgrade
mkdir $MYDIR/upgrade/lib
mkdir $MYDIR/upgrade/plugins

cd $JARDIR

cd ../../razpub/bin
jar cvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razpubs/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razupnp/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razassets/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp plugins/plugin_*.xml $MYDIR/upgrade/plugins
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razagent/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razmedia/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp plugins/plugin_*.xml $MYDIR/upgrade/plugins
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razmutant/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../razplay/bin
jar uvf $MYDIR/upgrade/razmutant.jar *
cp *.xml $MYDIR/upgrade/
cp ../lib/*.jar $MYDIR/upgrade/lib

cd ../../blinkie/bin
jar cvf $MYDIR/upgrade/plugins/plugin_blinkie.jar *
cp plugins/plugin_blinkie.xml $MYDIR/upgrade/plugins

cd $JARDIR
jar uvf $MYDIR/upgrade/razmutant.jar *
cp $JARDIR/*.xml $MYDIR/upgrade/
cp $JARDIR/README.txt $MYDIR/upgrade/README.txt

cp $MYDIR/upgrade/razmutant.jar $MYDIR/razmutant.jar
cp $MYDIR/upgrade/*.xml $MYDIR/
cp $MYDIR/upgrade/plugins/*.xml $MYDIR/plugins
#cp $MYDIR/lib/*.jar $MYDIR/upgrade/lib/
cp $SCALA_HOME/lib/scala-library.jar $MYDIR/upgrade/lib/
cp $SCALA_HOME/lib/scala-compiler.jar $MYDIR/upgrade/lib/
cp $JARDIR/README.txt $MYDIR/upgrade/README.txt

cd $MYDIR

