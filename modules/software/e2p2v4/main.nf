nextflow.enable.dsl=2

process E2P2V4_run {

    label "process_medium"
    tag("E2P2V4 ${fasta}")
    input:
    path(fasta)

    output:
    path("${fasta}.e2p2v4*")

    script:
    """
    E2P2 -be "${params.evalue}" -n ${task.cpus} -i ${fasta}
    """
}

