class Utils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    
    static void info(String message, Boolean verbose) {
        if (verbose) {
            println(ANSI_GREEN + "[INFO]\t${message}" + ANSI_RESET)
        }
    }
    static void error(String message) {
        println(ANSI_RED + "[ERROR]\t${message}"+ ANSI_RESET)
        System.exit(1)
    }

    static void warn(String message, Boolean verbose) {
        if (verbose) {
            println(ANSI_YELLOW + "[WARN]\t${message}" + ANSI_RESET)
        }
    }

    static void dumpEstCfg(File f,hashmap) {
        f.append "\n#est_${hashmap.id}\n"
        for (key in hashmap.keySet()) {
            def val = hashmap.get(key)
            f.append "est_${hashmap.id}_${key}=${val}\n"
        }
    }

    static void   dumpProteinCfg(File f,hashmap)    {
        f.append "\n#blastx_${hashmap.id}\n"
        for (key in hashmap.keySet()) {
            def val = hashmap.get(key)
            f.append "blastx_${hashmap.id}_${key}=${val}\n"
        }
    }

    static void   dumpParams(filepath,params)    {
        File dumpFile = new File(filepath)
        def date = new Date()
        dumpFile.write "\n//Run parameters ${date}\n"
        dumpFile.append "params {\n"
        params.keySet().sort().each { k -> 
            if (params[k] instanceof String){
                dumpFile.append "${k}='${params[k]}'\n" 
            }
            else {
                dumpFile.append "${k}=${params[k]}\n" 
            }
        }
        dumpFile.append "}\n"
    }

    static void assertFileExists(paramname,path) {
        File file = new File(path)
        if (! file.exists())    {
            this.error("File ${path} does not exists - check your configuration file for paramerter ${paramname}")
        }
    }



    static void checkSequenceIds(sequences) {
        def p = ['/bin/bash', '-c', /grep '^>' ${sequences} | cut -f 1 -d ' ' | grep -c '\\.'/].execute()
        p.waitFor()
        def test = p.text.trim().toInteger()
        if (test > 0 ) {
            this.error("File ${sequences} : your sequence ids must not contain '.' character")
        }
        return
    }
    
    static double getFastaSize(sequences) {
        def p = ['/bin/bash', '-c', /grep -v '^>' ${sequences} | tr -d "\\n" | wc -c/].execute()
        p.waitFor()
        def fastaSize = p.text.trim().toDouble()
         return fastaSize
    }
    
    static String getGenomeMode(sequences) {
        def mode = 'normal'
        def size = this.getFastaSize(sequences) 
        if ( size > 2**32 ) {
	     mode = 'big'
        }
        return mode
    }
    
    static String getFastaHash(sequences) {
        def p = ['/bin/bash', '-c', /md5sum ${sequences} | cut -d ' ' -f1/].execute()
        p.waitFor()
        def fastaHash = p.text.trim()
        return fastaHash
    }


    static int getFastaSequenceNumber(sequences) {
        def p = ['/bin/bash', '-c', /grep -c '^>' ${sequences}/].execute()
        p.waitFor()
        return p.text.trim().toDouble()
    }


    static ArrayList readParams (params) {

        //this.info("Override params with ${paramFile} values", params.verbose)
        //params = overrideParams(params, paramFile)

        def estDBs = this.paramsGetEstDBList(params)
        info("estDBs [ok]",params.verbose)
        def proteinDBs = this.paramsGetProteinDBList(params)
        info("proteinDBs [ok]",params.verbose)
        def additionalDBs = this.paramsGetAdditionalList(params)
        info("additionalDBs [ok]",params.verbose)
        def repeatconf = this.initRepeatSearch(params)
        info("repeatconf [ok]",params.verbose)
        
        def EGNconf = [:]
        EGNconf["est_list"] = estDBs[0]
        EGNconf["est"] = estDBs[1]
        //@a_preservfiles, "$seq.est$nb.rm_unspliced_$MIN_LEN_SHORT_UNSPLICED.gff3") if ($o_param->Get("est_$nb\_remove_unspliced") == 2);
        EGNconf["est_preserved"] = estDBs[2]
        EGNconf["train_est_db"] = estDBs[1].find {    element -> element.training == 1}
        EGNconf["protein_list"] = proteinDBs[0]
        EGNconf["protein"] = proteinDBs[1]
        EGNconf["protein_preserved"] = proteinDBs[2]
        EGNconf["additional"] = additionalDBs[1]
        EGNconf["additional_list"] = additionalDBs[1]
        EGNconf["repeat"] = repeatconf
        EGNconf["train_prot_db"] = proteinDBs[1].find  { element -> element.training }
        return [params, EGNconf]
    }

    static ArrayList paramsGetEstDBList(params) {
        String [] estList = params.est_list.split(" ").sort()
        String [] estSuffix = ['file','pcs','pci','remove_unspliced','preserve','training','priority','trim']
        def estDBList = []
        def estPreservedDBList = []

        estList.sort().each {
            String estID = it
            if (estList.contains(estID)) {
                def estDB = [id: estID, name: "est${estID}"]
                estSuffix.sort().each {
                    String suffix = it
                    String key = 'est_' + estID +'_' + suffix
                    String value = params[key]
                    estDB[suffix] = value.isInteger() ? value as Integer : value
                    if (suffix == 'preserve' && value as Integer == 1) {
                        estPreservedDBList.add("est${estID}")
                    }
                    if (suffix == 'remove_unspliced' && value as Integer == 2) {
                        estPreservedDBList.add("est${estID}.rm_unspliced_${properties.gmap_MIN_LEN_SHORT_UNSPLICED}")
                    }
                    if (suffix == 'file') {
                        assertFileExists(key, value)
                        estDB["entries"] = getFastaSequenceNumber(value)
                    }

                }
                estDBList.add(estDB)
            }
        }
        return [estList,estDBList.sort(),estPreservedDBList.sort()]
    }

    static ArrayList paramsGetProteinDBList(params) {

        String [] proteinList = params.blastx_db_list.split(" ").sort()
        String [] proteinSuffix = ['file','weight','pcs','pci','remove_repeat','preserve','training','activegaps'].sort()
        def proteinDBList = []
        def proteinPreservedDBList = []

        proteinList.each {
            String proteinID = it
            if (proteinList.contains(proteinID)) {
                def proteinDB = [id: proteinID, name: "blast${proteinID}"]
                proteinSuffix.each {
                String suffix = it
                String key = 'blastx_db_' + proteinID +'_' + suffix
                String value = params[key]
                proteinDB[suffix] = value.isInteger() ? value as Integer : value

                    if (suffix == 'file') {
                        assertFileExists(key,value);
                        def mode = "high"
                        def fastaSize = getFastaSize(value)
                        if (fastaSize < params.dbsize_switch_resource_low) {
                            mode = "low"
                        } else if ( fastaSize > params.dbsize_switch_resource_low && fastaSize < params.dbsize_switch_resource_high) {
                            mode = "medium"
                        }
                        this.info("${value} : ${fastaSize} letters / resource allocation -> ${mode}", params.verbose)
                        proteinDB["mode"] = mode
                        proteinDB["size"] = fastaSize
                        proteinDB["entries"] = getFastaSequenceNumber(value)


                    }
                    if (suffix == 'preserve' && value as Integer == 1) {
                        proteinPreservedDBList.add("blast${proteinID}")
                    }
                }
                proteinDBList.add(proteinDB)


            }
        }
        return [proteinList,proteinDBList.sort(),proteinPreservedDBList.sort()]
    }

    static ArrayList paramsGetAdditionalList(params) {


        def additionalList = []
        def additionalDBList = []
        if (params.containsKey('additional_list')) {
            additionalList = params.additional_list.split(" ")
            if (additionalList.size() > 0) {
                additionalList.each {
                    String additionalID = it
                    if (additionalList.contains(additionalID)) {
                        def additionalDB = [id: additionalID]
                        String filekey = 'additional_' + additionalID +'_file'
                        String file = params[filekey]
                        String templatekey = 'additional_' + additionalID +'_cfg_template'
                        String cfg_template = params[templatekey]
                        String name = file.split('\\/').last().replaceAll("\\.gff3","").split('\\.').last()
                        additionalDB['file'] = file
                        additionalDB['name'] = name
                        additionalDB['cfg_template'] = cfg_template
                        additionalDBList.add(additionalDB)
                    }
                }
            }
        }
        return [additionalList,additionalDBList]
    }

    static HashMap initRepeatSearch(params) {
        def EGNrepeatconf = [:]
        def repbase_is_defined = 0
        def repeat_sequence_db = null
        
        if (params.containsKey('dfam_consensus_db')) {
            String dbpath = params.dfam_consensus_db
            assertFileExists('dfam_consensus_db',dbpath)
            EGNrepeatconf['dfam_consensus_db'] = dbpath
        }
        if (params.containsKey('transposonpsi_db')) {
            String dbpath = params.transposonpsi_db
            assertFileExists('transposonpsi_db',dbpath)
            EGNrepeatconf['transposonpsi_db'] = dbpath
        }
        
        if (params.containsKey('repeat_sequence_db')) {
            String dbpath = params.repeat_sequence_db
                assertFileExists('repeat_sequence_db',dbpath)
                repbase_is_defined = 1
                repeat_sequence_db = dbpath
        }
        def search_species_specific_repeat_domains =  0
        def species_repeat_domains = null
        if (params.containsKey('species_repeat_domains')) {
            //# Index the species_repeat_domains database
            //# and use it to clean the proteic databases: so replace 'repeat_sequence_db'
            String dbpath = params.species_repeat_domains
             assertFileExists('species_repeat_domains',dbpath)
            species_repeat_domains = dbpath
            repeat_sequence_db =      dbpath
            
            if (repbase_is_defined == 1) {
                this.warn("'repeat_sequence_db' is ignored. species_repeat_domains is used instead.", params.verbose)
            } 
        }
         else {
            //# If RepBase is available => Launch the step "Search species specific repeat domains"
            if (repbase_is_defined == 1) {
                search_species_specific_repeat_domains = 1
            }
         }
        EGNrepeatconf['search_species_specific_repeat_domains'] = search_species_specific_repeat_domains
        EGNrepeatconf['repeat_sequence_db'] = repeat_sequence_db
        EGNrepeatconf['repbase_is_defined'] = repbase_is_defined
        EGNrepeatconf['species_repeat_domains'] = species_repeat_domains
        
        return EGNrepeatconf
    }


    static HashMap overrideParams(params, paramFile) {
        Properties properties = new Properties()
        File propertiesFile = new File(paramFile)
        propertiesFile.withInputStream {
            properties.load(it)
        }
        properties.keySet().each {k -> {
                def value = properties.get(k)
                params.remove(k)
                params.put(k,value)
        }
    }
        return params
}
}
