find busybox-1.18.5/ -name "*.dbg*" |while read i; do echo $i; rm $i; done 
find busybox-1.18.5/ -name "*.err" |while read i; do echo $i; rm $i; done 
find busybox-1.18.5/ -name "*.interface" |while read i; do echo $i; rm $i; done 
find busybox-1.18.5/ -name "*.pi" |while read i; do echo $i; rm $i; done 
find busybox-1.18.5/ -name "*.time" |while read i; do echo $i; rm $i; done 
find busybox-1.18.5/ -name "*.c.xml" |while read i; do echo $i; rm $i; done 
