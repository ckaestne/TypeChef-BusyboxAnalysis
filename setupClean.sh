#!/bin/bash

#This script downloads and/or extracts the busybox 1.18.5 source files, the redhat system property files.
#It also creates the run.sh file and starts it in order to create the presence condition files.
#
#The TypeChef project has to be in the same folder as the TypeChef-BusyboxAnalysis folder:
#/someFolder/TypeChef
#/someFolder/TypeChef-BusyboxAnalysis

path=$(cd "$(dirname "$0")"; pwd)

srcPath="busybox-1.18.5"
srcPathArchive="busybox-1.18.5.tar.bz2"
mkRun="run.sh"
systemSourceArchive="includes-redhat.tar.bz2"
systemSourceFolder="systems/redhat/usr"
redhatFolder="systems/redhat"
typeChef="../TypeChef"


# Check for busybox source files
if [ ! -d $srcPath ]; then
	if [ ! -f $srcPathArchive ]; then
		# Busybox archive does not exisist -> download source files
		echo "Downloading BusyBox source files."
		wget http://www.busybox.net/downloads/busybox-1.18.5.tar.bz2
	fi
	# Extract source files
	tar xvjf busybox-1.18.5.tar.bz2
fi

# Check system source files
if [ ! -d $systemSourceFolder ]; then
	if [ ! -f $systemSourceArchive ]; then
		# System file archive does not exisist -> download system files
		echo "Downloading system source files."
		wget http://www.cs.cmu.edu/%7Eckaestne/tmp/includes-redhat.tar.bz2
	fi
	# Create systems/redhat folder
	if [ ! -d $redhatFolder ]; then
		mkdir -p $redhatFolder
	fi
	# Extract system files
	tar xvjf $systemSourceArchive -C $redhatFolder
fi

# Check for run.sh file
if [ ! -f $mkRun ]; then
	java -Xmx1024M -Xss256M -XX:PermSize=256M -XX:MaxPermSize=512M -jar $typeChef/sbt-launch.jar mkrun
fi

# Prepare busybox script, creates presence condition files
./run.sh de.fosd.typechef.busybox.ProcessFileList busybox/busybox_pcs.txt busybox-1.18.5/
