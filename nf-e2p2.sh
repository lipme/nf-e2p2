#!/bin/bash

ARGS="$*"
CONF=''
SEQUENCES=''
MD5CONF=''
MD5SEQUENCES=''
SEQNAME=''


if [ $# -lt 1 ]; then
  echo -e "\nUsage:\n$0 [--parameters] [--slurm] --proteome <path to fasta file>"
  echo -e "\nMore details:\n$0 --parameters\n"
  exit 0
fi

PURPLE='\033[0;35m'
NC='\033[0m'
TITLE='\033[1;91m'
ERROR='\033[0;31m'
INFO='\033[0;32m'

while [ $# -gt 0 ]; do

   if [[ $1 == *"--help"* ]]; then
        echo -e "\nUsage:\n$0 [--parameters] [--slurm] --proteome <path to fasta file> \n"
        exit 0
   elif [[ $1 == *"--parameters"* ]]; then
        nextflow run ./main.nf --help
        exit 0
   elif [[ $1 == *"--proteome"* ]]; then
        SEQUENCES=$2
        MD5SEQUENCES=`md5sum $2 | cut -d ' ' -f1`
        SEQNAME=`basename $2 | rev | cut -d. -f2- | rev`
   elif [[ $1 == *"--slurm"* ]]; then
        SLURM=true
   fi

  shift
done


if [[ $SEQUENCES == '' ]]; then
  echo -e "${ERROR}[ERROR] --proteome parameter is missing${NC}"
  exit 1
else
  RUNDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/" >/dev/null 2>&1 && realpath . )"
  DATE=`date +%Y%m%d-%s`
  RUNID=`echo "$MD5SEQUENCES" | cut -d ' ' -f1| sed -E 's/\w{24}$//'`
  RESULTID=`echo "$MD5SEQUENCES-$MD5CONF" | cut -d ' ' -f1| sed -E 's/\w{24}$//'`
  LOCKED=`find  $RUNDIR/runs/$SEQNAME-$RUNID/.nextflow/cache/ -name LOCK -exec lsof {} \;`
  
  if [[ $LOCKED == '' ]]; then
     export NXF_SINGULARITY_CACHEDIR=$RUNDIR/runs/$SEQNAME-$RUNID/singularity
     mkdir -p $RUNDIR/runs/$SEQNAME-$RUNID/singularity
     mkdir -p $RUNDIR/runs/$SEQNAME-$RUNID/logs
     rm -f $RUNDIR/runs/$SEQNAME-$RUNID/logs/latest.*
	 echo -e "${PURPLE}\n[RUNID]\t >>>>  $SEQNAME-$RUNID  <<<< ${NC}\n\n"
     echo -e "${INFO}[INFO] Launch:  ${NC}\n\tnextflow run $RUNDIR/main.nf  -resume $ARGS \n\n"
     echo -e "${INFO}[INFO] Results directory\t./runs/$SEQNAME-$RUNID/results-$DATE${NC}"
     echo -e "${INFO}[INFO] Logs    directory\t./runs/$SEQNAME-$RUNID/logs${NC}"
     echo -e "${INFO}[INFO] Work    directory\t./runs/$SEQNAME-$RUNID/work${NC}"
     echo -e "${INFO}[INFO] Apps    directory\t./runs/$SEQNAME-$RUNID/singularity${NC}"
     cd $RUNDIR/runs/$SEQNAME-$RUNID/logs/
     ln -s $DATE.nextflow.log latest.nextflow.log
     ln -s $DATE.stdout latest.stdout
     ln -s $DATE.stderr latest.stderr
     cd $RUNDIR/runs/$SEQNAME-$RUNID/
     mkdir results-$DATE
     rm -f latest && ln -s results-$DATE latest
     if [[ ! $SLURM == '' ]]; then
          echo -e "${INFO}[INFO] Submitted to slurm${NC}"
          (cd $RUNDIR/runs/$SEQNAME-$RUNID/ && nextflow \
          -log $RUNDIR/runs/$SEQNAME-$RUNID/logs/$DATE.nextflow.log \
          run $RUNDIR/main.nf -ansi-log false -resume --output_prefix $SEQNAME $ARGS \
          -work-dir $RUNDIR/runs/$SEQNAME-$RUNID/work \
          --outdir $RUNDIR/runs/$SEQNAME-$RUNID/results-$DATE \
          > $RUNDIR/runs/$SEQNAME-$RUNID/logs/$DATE.stdout ) 2> $RUNDIR/runs/$SEQNAME-$RUNID/logs/$DATE.stderr
     else
          echo -e "${INFO}[INFO] Run in bash mode (nohup and background)${NC}"
          (cd $RUNDIR/runs/$SEQNAME-$RUNID/ && nohup nextflow \
          -log $RUNDIR/runs/$SEQNAME-$RUNID/logs/$DATE.nextflow.log \
          run $RUNDIR/main.nf -ansi-log false -resume --output_prefix $SEQNAME $ARGS \
          -work-dir $RUNDIR/runs/$SEQNAME-$RUNID/work \
          --outdir $RUNDIR/runs/$SEQNAME-$RUNID/results-$DATE \
          > $RUNDIR/runs/$SEQNAME-$RUNID/logs/$DATE.stdout ) 2> $RUNDIR/runs/$SEQNAME-$RUNID/logs/$DATE.stderr&
          echo -e "${NC}[TIP] Kill Main Process\t\$ ./utils/killRun.sh $SEQNAME-$RUNID${NC}"
     
     fi
     cd $RUNDIR
     echo -e "${NC}[TIP] Follow execution\t\$ tail -f ./runs/$SEQNAME-$RUNID/logs/latest.stdout${NC}"
     echo -e "${NC}[TIP] Failed Tasks\t\$ ./utils/failedTasks.sh $SEQNAME-$RUNID${NC}"
     echo -e "${NC}[TIP] Count Tasks\t\$ ./utils/countTasks.sh $SEQNAME-$RUNID${NC}"
     

  else
     echo -e "${ERROR}[ERROR] There's an already running job for this sequence${NC}"
     echo -e "$LOCKED"
     exit 3
  fi
fi

