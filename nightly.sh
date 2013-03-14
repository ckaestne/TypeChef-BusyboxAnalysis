cd gitbusybox
git pull > gitstatus
if [ "$1" =  "--force" ]
then
	echo Skipping git check.
else 
	grep "Already up-to-date" gitstatus > /dev/null
	if [ $? == 0 ]; 
	then
		echo "No git updates. Quitting."
		exit
	else
		echo "Starting checking..."
	fi
fi

cd ..

./prepareGit.sh
./cleanBusyboxGit.sh
./analyzeBusyboxGit.sh

cat dbg.config | java -jar DBGFileChecker.jar
grep FAIL gitbusybox/out > gitbusybox/outfail


#linker checks
mv gitbusybox/busyboxfinal.interface gitbusybox/busyboxprev.interface
mv gitbusybox/busyboxfinal.dbginterface gitbusybox/busyboxprev.dbginterface
./run.sh de.fosd.typechef.busybox.BusyboxLinker
mv busyboxfinal.interface gitbusybox/busyboxfinal.interface
mv busyboxfinal.dbginterface gitbusybox/busyboxfinal.dbginterface
./run.sh de.fosd.typechef.busybox.InterfaceDiff gitbusybox/busyboxprev.interface gitbusybox/busyboxfinal.interface > gitbusybox/interfacediff
cp gitbusybox/interfacediff gitbusybox/interfacediff.txt
cp gitbusybox/outfail gitbusybox/outfail.txt

echo Finished nightly with `wc -l gitbusybox/outfail` and `wc -l gitbusybox/interfacediff` | mail -s "busybox nightly" -a gitbusybox/outfail.txt -a gitbusybox/interfacediff.txt ckaestne
