#this script extracts the 
#* presence condition for files
#* the feature model
#* the header file


#if run.sh is missing, run "sbt mkrun"

#create a directory gitbusybox and download busybox sources there

#you'll need KBuildMiner
#http://code.google.com/p/variability/source/browse/KBuildMiner/
#and Maven (mvn) to generate the list of presence conditions per files
cd gitbusybox
make gen_build_files
cd ..

cd KBuildMiner
mvn scala:run -q -DmainClass=gsd.buildanalysis.linux.KBuildMinerMain
cd ..

grep -v libunarchive gitbusybox/pc.txt | grep -v Unknown > gitbusybox/pc_clean.txt

cat gitbusybox/pc.txt | sed s/\\.c:.*// | grep -v libunarchive > gitbusybox/filelist

./run.sh de.fosd.typechef.busybox.ProcessFileList gitbusybox/pc_clean.txt gitbusybox/

./run.sh de.fosd.typechef.busybox.KConfigReader gitbusybox/ gitbusybox/featureModel gitbusybox/header.h
