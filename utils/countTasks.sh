#!/bin/bash

RUNID=$1
SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]:-$0}"; )" &> /dev/null && realpath .. 2> /dev/null; )";
echo -e "FILE\t$SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt"
cut -f4 $SCRIPT_DIR/runs/$RUNID/latest/08_pipeline_info/execution_trace.txt  | cut -f1 -d ' ' | sort | uniq -c
