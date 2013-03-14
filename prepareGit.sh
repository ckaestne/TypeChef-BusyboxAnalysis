#this script extracts the 
#* presence condition for files
#* the feature model
#* the header file


sbt mkrun

#create a directory gitbusybox and download busybox sources there

#you'll need KBuildMiner
#http://code.google.com/p/variability/source/browse/KBuildMiner/
#and Maven (mvn) to generate the list of presence conditions per files
cd gitbusybox
make gen_build_files
cd ..

#copy an old file pc in case KBuildMiner is not installed
cp KBuildMiner/pc.txt gitbusybox/pc.txt

# the following tries to extract presence conditions from the build system
# you need to install KBuildMiner to get the latest conditions (see KBuildMiner directory)
cd KBuildMiner
mvn scala:run -q -DmainClass=gsd.buildanalysis.linux.KBuildMinerMain
cd ..
grep -v libunarchive gitbusybox/pc.txt | grep -v Unknown > gitbusybox/pc_clean.txt

# extract a list of all relevant files
cat gitbusybox/pc.txt | sed s/\\.c:.*// | grep -v libunarchive | grep -v "/tc$" | grep -v "appletlib" > gitbusybox/filelist

# generate .pc files from the presence condition list
./run.sh de.fosd.typechef.busybox.ProcessFileList gitbusybox/pc_clean.txt gitbusybox/

# read the feature model and create corresponding files
./run.sh de.fosd.typechef.busybox.KConfigReader gitbusybox/ gitbusybox/featureModel gitbusybox/header.h gitbusybox/features

# translate the feature model into a .dimacs file for faster processing
./run.sh de.fosd.typechef.busybox.CreateDimacs gitbusybox/featureModel gitbusybox/featureModel.dimacs
