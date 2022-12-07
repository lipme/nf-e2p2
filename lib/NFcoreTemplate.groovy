//
// This file holds several functions used within the nf-core pipeline template.
//

class NfcoreTemplate {

   
    //
    // ANSII Colours used for terminal logging
    //
    public static Map logColours(Boolean monochrome_logs) {
        Map colorcodes = [:]

        // Reset / Meta
        colorcodes['reset']      = monochrome_logs ? '' : "\033[0m"
        colorcodes['bold']       = monochrome_logs ? '' : "\033[1m"
        colorcodes['dim']        = monochrome_logs ? '' : "\033[2m"
        colorcodes['underlined'] = monochrome_logs ? '' : "\033[4m"
        colorcodes['blink']      = monochrome_logs ? '' : "\033[5m"
        colorcodes['reverse']    = monochrome_logs ? '' : "\033[7m"
        colorcodes['hidden']     = monochrome_logs ? '' : "\033[8m"

        // Regular Colors
        colorcodes['black']      = monochrome_logs ? '' : "\033[0;30m"
        colorcodes['red']        = monochrome_logs ? '' : "\033[0;31m"
        colorcodes['green']      = monochrome_logs ? '' : "\033[0;32m"
        colorcodes['yellow']     = monochrome_logs ? '' : "\033[0;33m"
        colorcodes['blue']       = monochrome_logs ? '' : "\033[0;34m"
        colorcodes['purple']     = monochrome_logs ? '' : "\033[0;35m"
        colorcodes['cyan']       = monochrome_logs ? '' : "\033[0;36m"
        colorcodes['white']      = monochrome_logs ? '' : "\033[0;37m"

        // Bold
        colorcodes['bblack']     = monochrome_logs ? '' : "\033[1;30m"
        colorcodes['bred']       = monochrome_logs ? '' : "\033[1;31m"
        colorcodes['bgreen']     = monochrome_logs ? '' : "\033[1;32m"
        colorcodes['byellow']    = monochrome_logs ? '' : "\033[1;33m"
        colorcodes['bblue']      = monochrome_logs ? '' : "\033[1;34m"
        colorcodes['bpurple']    = monochrome_logs ? '' : "\033[1;35m"
        colorcodes['bcyan']      = monochrome_logs ? '' : "\033[1;36m"
        colorcodes['bwhite']     = monochrome_logs ? '' : "\033[1;37m"

        // Underline
        colorcodes['ublack']     = monochrome_logs ? '' : "\033[4;30m"
        colorcodes['ured']       = monochrome_logs ? '' : "\033[4;31m"
        colorcodes['ugreen']     = monochrome_logs ? '' : "\033[4;32m"
        colorcodes['uyellow']    = monochrome_logs ? '' : "\033[4;33m"
        colorcodes['ublue']      = monochrome_logs ? '' : "\033[4;34m"
        colorcodes['upurple']    = monochrome_logs ? '' : "\033[4;35m"
        colorcodes['ucyan']      = monochrome_logs ? '' : "\033[4;36m"
        colorcodes['uwhite']     = monochrome_logs ? '' : "\033[4;37m"

        // High Intensity
        colorcodes['iblack']     = monochrome_logs ? '' : "\033[0;90m"
        colorcodes['ired']       = monochrome_logs ? '' : "\033[0;91m"
        colorcodes['igreen']     = monochrome_logs ? '' : "\033[0;92m"
        colorcodes['iyellow']    = monochrome_logs ? '' : "\033[0;93m"
        colorcodes['iblue']      = monochrome_logs ? '' : "\033[0;94m"
        colorcodes['ipurple']    = monochrome_logs ? '' : "\033[0;95m"
        colorcodes['icyan']      = monochrome_logs ? '' : "\033[0;96m"
        colorcodes['iwhite']     = monochrome_logs ? '' : "\033[0;97m"

        // Bold High Intensity
        colorcodes['biblack']    = monochrome_logs ? '' : "\033[1;90m"
        colorcodes['bired']      = monochrome_logs ? '' : "\033[1;91m"
        colorcodes['bigreen']    = monochrome_logs ? '' : "\033[1;92m"
        colorcodes['biyellow']   = monochrome_logs ? '' : "\033[1;93m"
        colorcodes['biblue']     = monochrome_logs ? '' : "\033[1;94m"
        colorcodes['bipurple']   = monochrome_logs ? '' : "\033[1;95m"
        colorcodes['bicyan']     = monochrome_logs ? '' : "\033[1;96m"
        colorcodes['biwhite']    = monochrome_logs ? '' : "\033[1;97m"

        return colorcodes
    }

    //
    // Does what is says on the tin
    //
    public static String dashedLine(monochrome_logs) {
        Map colors = logColours(monochrome_logs)
        return "-${colors.dim}----------------------------------------------------${colors.reset}-"
    }

    //
    // nf-core logo
    //
    public static String logo(workflow, monochrome_logs) {
        Map colors = logColours(monochrome_logs)
        String.format(
            """\n
            ${dashedLine(monochrome_logs)}
            ${colors.blue}    _        _____   _____    __  __   ______ 
            ${colors.blue}   | |      |_   _| |  __ \\  |  \\/  | |  ____|
            ${colors.blue}   | |        | |   | |__) | | \\  / | | |__   
            ${colors.blue}   | |        | |   |  ___/  | |\\/| | |  __|  
            ${colors.blue}   | |____   _| |_  | |      | |  | | | |____ 
            ${colors.blue}   |______| |_____| |_|      |_|  |_| |______|
            ${colors.blue}
            ${colors.purple}  ${workflow.manifest.name} v${workflow.manifest.version}${colors.reset}
            ${dashedLine(monochrome_logs)}
            """.stripIndent()
        )
    }

    //
    // pipeline help
    //
    public static String paramsHelp(workflow, params, monochrome_logs) {
        Map colors = logColours(monochrome_logs)
        String.format(
            """
            ${colors.blue}Mandatory :${colors.reset} 
            ${colors.purple}  --proteome${colors.reset}                         Path to proteome sequence
            ${colors.blue}
            ${colors.blue}Optionnal :${colors.reset}
            ${colors.iyellow}   Defaults are provided by conf/params/defaults.config file 
            ${colors.purple}  --e2p2_version${colors.reset}                           Result directory (default : ${params.e2p2_version})
            ${colors.purple}  --outdir${colors.reset}                           Result directory (default : ${params.outdir})
            ${colors.purple}  --output_prefix${colors.reset}                    Output files prefix (default : ${params.output_prefix})
            ${colors.purple}  --evalue ${colors.reset}                                         Blast e-value (default:${params.evalue})
            ${colors.purple}  --slice_size${colors.reset} Proteome slice size(default:${params.slice_size})
            ${colors.blue}
            ${colors.blue}Nextflow :${colors.reset}                   
            ${colors.purple}  --max_cpus${colors.reset}                        Set the number of max allocated cpus per process (default: ${params.max_cpus})
            ${colors.purple}  -resume${colors.reset}                           Rerun the pipeline
            ${colors.purple}  -profile${colors.reset}                          Profile Nextflow for configuration (basic usage : lipme,debug)
            ${colors.purple}  -bg${colors.reset}                               Run in background
            ${colors.purple}  --help${colors.reset}                            Print this help
            ${colors.reset}
            """.stripIndent()
        )
    }

    
    //
    // Print help to screen if required
    //
    public static String help(workflow, params, log) {
        Map colors = logColours(params.monochrome_logs)
        def command = "${colors.blue}nextflow run ${workflow.projectDir}/main.nf --proteome /path/to/proteome.fasta  -profile debug${colors.reset}"
        def help_string = ''
        help_string += logo(workflow, params.monochrome_logs)
        help_string += '\n' + command + '\n'
        help_string += paramsHelp(workflow, params, params.monochrome_logs)
        help_string += '\n' //+ citation(workflow) + '\n'
        help_string += dashedLine(params.monochrome_logs)
        help_string += dashedLine(params.monochrome_logs)
        return help_string
    }
}
