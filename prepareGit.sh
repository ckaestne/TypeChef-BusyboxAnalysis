cd KBuildMiner
mvn scala:run -q -DmainClass=gsd.buildanalysis.linux.KBuildMinerMain
cd ..

grep -v libunarchive gitbusybox/pc.txt | grep -v Unknown > gitbusybox/pc_clean.txt

cat KBuildMiner/pc.txt | sed s/\\.c:.*// | grep -v libunarchive > gitbusybox/filelist

./run.sh de.fosd.typechef.busybox.ProcessFileList gitbusybox/pc_clean.txt gitbusybox/

./run.sh de.fosd.typechef.busybox.KConfigReader gitbusybox/ gitbusybox/featureModel gitbusybox/header.h
