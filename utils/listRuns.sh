
#!/bin/bash

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]:-$0}"; )" &> /dev/null && realpath .. 2> /dev/null; )";

NC='\033[0m'
INFO='\033[0;32m'


echo -e "${INFO}\n--------------------------\n LIST OF RUN IDS \n--------------------------\n${NC}"
dirname $SCRIPT_DIR/runs/*/.nextflow |xargs -i basename {}

echo -e "${INFO}\n--------------------------\n LIST OF RUNNING RUNS \n--------------------------\n${NC}"
ls $SCRIPT_DIR/runs/*/.nextflow/cache/*/db/LOCK | xargs -i lsof {} | grep ^java | sed -E 's/.+ //;s,\.nextflow.+,,' | xargs -i basename {}
