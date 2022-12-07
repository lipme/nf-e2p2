#!/bin/bash

RUNID=$1
SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]:-$0}"; )" &> /dev/null && realpath .. 2> /dev/null; )";
echo -e "FILE\t$SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt"
HEADER=`head -1 $SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt | cut -f3,4,5,6,8`
FAILED=`grep FAILED $SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt | cut -f3,4,5,6,8`
echo -e "$HEADER\n$FAILED"
echo -e "\n\n\n"
grep FAILED $SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt| cut -f2 | xargs -i echo "cd $SCRIPT_DIR/runs/$RUNID/work/{}*" > /tmp/$$.wd.tmp
grep FAILED $SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt| cut -f4 | cut -d  ' ' -f1| xargs -i echo "# {}" > /tmp/$$.tk.tmp
paste /tmp/$$.tk.tmp /tmp/$$.wd.tmp
rm /tmp/$$.*.tmp
