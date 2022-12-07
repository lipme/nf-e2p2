#!/usr/bin/env nextflow

nextflow.enable.dsl=2

include {LIPMUTILS_splitSingleDB;LIPMUTILS_mergeFiles} from "./modules/software/lipmutils/main.nf"
include {E2P2V4_run} from "./modules/software/e2p2v4/main.nf"
include {E2P2V3_run} from "./modules/software/e2p2v3/main.nf"

workflow {
	main:
		if (params.help) {
			def help_message = NFcoreTemplate.help(workflow, params, log)
			log.info help_message
			exit 0
		}

		if(! params.output_prefix) { exit 1, 'output_prefix not specified' }
		
		
		if (params.proteome) { 
			fasta_ch = Channel.fromPath(params.proteome)
			slices_ch = LIPMUTILS_splitSingleDB(fasta_ch, params.slice_size).flatten()
			
			if (params.e2p2_version == 3){
				e2p2v3_slice_outfiles = E2P2V3_run(slices_ch)
				LIPMUTILS_mergeFiles(e2p2v3_slice_outfiles.flatten().collect(), params.output_prefix, 'e2p2v3')
			}
			else {
				e2p2v4_slice_outfiles = E2P2V4_run(slices_ch)
				LIPMUTILS_mergeFiles(e2p2v4_slice_outfiles.flatten().collect(), params.output_prefix,'e2p2v4')
			}
		} else { exit 1, 'Input proteome file not specified!' }

}
