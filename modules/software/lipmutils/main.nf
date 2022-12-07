nextflow.enable.dsl=2

process LIPMUTILS_splitSingleDB {
    
    label "process_high_memory"

    tag("SPLIT ${file} in ${num_per_slice}")

    input:
    path(file)
    val(num_per_slice)
    
    output:
    path("${file.baseName}.slice.*")
    
    script:
    """
    lipm_fastasplitter.pl --in ${file} --num_per_slice ${num_per_slice} --outprefix ${file.baseName}.slice.
    """
}


process LIPMUTILS_softwaresInfo {
	output:
	path("lipmutils.softwares_info.tsv")

	script:
	"""
	softwares_info > lipmutils.softwares_info.tsv
	"""
}


process LIPMUTILS_mergeFiles {

    publishDir "${params.outdir}/", mode: 'copy'
    
    input:
    path(e2p2_files)
    val(outprefix)
    val(suffix)


    output:
    path("${outprefix}/${outprefix}.*")

    script:
    """
    mkdir ${outprefix}
    cat *.${suffix}.pf > ${outprefix}/${outprefix}.${suffix}.pf
    cat *.${suffix}.orxn.pf > ${outprefix}/${outprefix}.${suffix}.orxn.pf
    ( grep -h '^# Ensemble' *.${suffix} | sort -u  && grep -h -v ^# *.${suffix} | sort ) > ${outprefix}/${outprefix}.${suffix}
    ( grep -h '^# Ensemble' *.${suffix}.long | sort -u  && grep -h -v ^# *.${suffix}.long ) > ${outprefix}/${outprefix}.${suffix}.long
    """
}
