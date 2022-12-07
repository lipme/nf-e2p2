nextflow.enable.dsl=2

process E2P2V3_run {

    label "process_medium"
    tag("E2P2V3 ${fasta}")
    input:
    path(fasta)

    output:
    path("${fasta}.e2p2v3*")

    script:
    """
    E2P2 -e "${params.evalue}" -t ${task.cpus} -i ${fasta} -r \$PWD -o ${fasta}.e2p2v3
    """
}
