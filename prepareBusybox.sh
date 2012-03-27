#this script creates the necessary .pc files describing file presence conditions.
#
#run "sbt mkrun" to create run.sh the first time
#(potentially you will need to compile and publish the main TypeChef project first with "sbt publish-local")
#
#create a directory  busybox-1.18.5 and download busybox sources there

./run.sh de.fosd.typechef.busybox.ProcessFileList busybox/busybox_pcs.txt busybox-1.18.5/

