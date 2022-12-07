# NF-E2P2

Nextflow version of E2P2 Pipeline

## INSTALL

### PRE-REQUISITES

- wget
- gzip
- NextFlow (> 21.04.0)  (Java 8 or later is required) : <https://www.nextflow.io/index.html#GetStarted>
- Singularity container app (> 3.0) : <https://sylabs.io/guides/3.0/user-guide/index.html>

## Usage

```bash
./nf-e2p2.sh --help

#Load mandatory modules/environment (Singularity & NextFlow)

Usage:
./nf-e2p2.sh [--parameters] [--slurm] --proteome <path to fasta file> 

#more details
./nf-e2p2.sh --parameters


#each parameter can be overwritten using command line syntax (--param) or adding it in config file

# --slurm parameter disables nohup/background submission which is incompatible with sbatch submission
```

## Run output directory

```bash
runs
└── <SEQNAME>-<RUNHASH>
    ├── logs
    ├── work
    ├── singularity
    └── results
```

- RUNHASH is computed with sequence file content en config file content (command line are not taken into account)
- `singularity` directory contains aumatically retrieved containers (from <https://lipm-browsers.toulouse.inra.fr/pub/singularity-repository/egnep/$VERSION/> )
- `work` directory contains task directories - WARNING : can become huge in size
- `results` directory contains final files

## Results output subdirectories

| Directory        | Content                                                                                                                             |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| ${proteome}    | E2P2 result file                                                                           |
| 08_pipeline_info    | Execution timeline, resources usage, softwaresersions                                                                            |

## Results output files

```bash
$outdir
├── 08_pipeline_info
│   ├── execution_report.html
│   ├── execution_timeline.html
│   ├── execution_trace.txt
│   └── pipeline_dag.svg
└── ${proteome}
    ├── ${proteome}.e2p2v${e2p2_version}
    ├── ${proteome}.e2p2v${e2p2_version}.long
    ├── ${proteome}.e2p2v${e2p2_version}.orxn.pf
    └── ${proteome}.e2p2v${e2p2_version}.pf


```


## Contact

lipme-bioinfo@inrae.fr

## Credits

- Sébastien Carrere
- Ludovic Cottret

## Citations

https://github.com/carnegie/E2P2

