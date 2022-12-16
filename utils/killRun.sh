#!/bin/bash

RUNID=$1
SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]:-$0}"; )" &> /dev/null && realpath .. 2> /dev/null; )";
LOCKED=`lsof $SCRIPT_DIR/runs/$RUNID/.nextflow/cache/*/db/LOCK | grep java | sed -E 's/^java +//' | cut -d ' ' -f1`

  
if [[ $LOCKED == '' ]]; then
	echo "$CMD This run is not running on this server"
else
	echo "Kill main process $LOCKED - check cluster jobs"
	kill -9 $LOCKED
fi
