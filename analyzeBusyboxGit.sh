#!/bin/bash -e
#!/bin/bash -vxe

filesToProcess() {
  local listFile=gitbusybox/filelist
  cat $listFile
  #awk -F: '$1 ~ /.c$/ {print gensub(/\.c$/, "", "", $1)}' < linux_2.6.33.3_pcs.txt
}


flags="-U HAVE_LIBDMALLOC -U ENABLE_NC_110_COMPAT -D_GNU_SOURCE"
srcPath="gitbusybox"
export partialPreprocFlags="\
  --bdd \
  --openFeat=gitbusybox/features \
  --include gitbusybox/header.h \
  --include mheader.h \
  --featureModelDimacs gitbusybox/featureModel.dimacs \
  -I $srcPath/include  \
  --writePI --recordTiming --parserstatistics --lexdebug \
  --dumpcfg -t --interface --debugInterface --errorXML"
echo $partialPreprocFlags
## Reset output
filesToProcess|while read i; do
  if [ ! -f $srcPath/$i.dbg ]; then
    touch $srcPath/$i.dbg
    ./jcpp.sh $srcPath/$i.c $flags
  else
    echo "Skipping $srcPath/$i.c"
  fi
done

