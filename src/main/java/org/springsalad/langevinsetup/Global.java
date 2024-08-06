/*
 *                        GLOBAL CLASS
 *  This is the top level data structure.  It holds references to all 
 *  of the molecules, the system geometry, the initial conditions, and 
 *  the binding reactions. (Transition reactions are accessed through the 
 *  state they belong to, and creation/destruction reactions are accessed
 *  through the molecule they belong to.)
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.prefs.Preferences;

import org.springsalad.helpersetup.IOHelp;
import org.springsalad.helpersetup.PopUp;

public class Global {
    
    /* ********** Strings to represent the different object categories ****/
    public final static String SPATIAL_INFORMATION = "SYSTEM INFORMATION";
    public final static String TIME_INFORMATION = "TIME INFORMATION";
    public final static String MOLECULES = "MOLECULES";
    public final static String MOLECULE_FILES = "MOLECULE FILES";
    public final static String DECAY_REACTIONS = "CREATION/DECAY REACTIONS";
    public final static String TRANSITION_REACTIONS = "STATE TRANSITION REACTIONS";
    public final static String ALLOSTERIC_REACTIONS = "ALLOSTERIC REACTIONS";
    public final static String BINDING_REACTIONS = "BIMOLECULAR BINDING REACTIONS";
    public final static String MOLECULE_COUNTERS = "MOLECULE COUNTERS";
    public final static String STATE_COUNTERS = "STATE COUNTERS";
    public final static String BOND_COUNTERS = "BOND COUNTERS";
    public final static String SITE_PROPERTY_COUNTERS = "SITE PROPERTY COUNTERS";
    public final static String CLUSTER_COUNTERS = "CLUSTER COUNTERS";
    public final static String SYSTEM_ANNOTATION = "SYSTEM ANNOTATIONS";
    public final static String MOLECULE_ANNOTATIONS = "MOLECULE ANNOTATIONS";
    public final static String REACTION_ANNOTATIONS = "REACTION ANNOTATIONS";
    
    /* ************ KEYS FOR PREFERENCES *****************************/
    public final static String DEFAULT_FOLDER = "Default Folder";
    
    // The system name (same as the file name, usually)
    private String systemName;
    public final static String DEFAULT_SYSTEM_NAME = "New Model";
    
    // The array of molecules
    private ArrayList<Molecule> molecules = new ArrayList<>();
    
    // The array of binding reactions
    private ArrayList<BindingReaction> bindingReactions = new ArrayList<>();
    
    // The array of transition reactions
    private ArrayList<TransitionReaction> transitionReactions = new ArrayList<>();
    
    // The array of allosteric reactions
    private ArrayList<AllostericReaction> allostericReactions = new ArrayList<>();
    
    // The geometry data
    private final BoxGeometry boxGeometry;
    
    // The time data
    private final SystemTimes systemTimes;
    
    // The file we save the data to
    private File file;
    
    // The default folder
    private File defaultFolder;
    
    // User preferences
    private final Preferences pref;
    
    // System annotation
    private final Annotation systemAnnotation;
    
    // Boolean switch to decide if system should track cluster sizes
    private boolean trackClusters;
    
    public Global(String systemName){
        this.systemName = systemName;
        boxGeometry = new BoxGeometry();
        systemTimes = new SystemTimes();
        systemAnnotation = new Annotation();
        pref = Preferences.userRoot();
        
        String defaultFolderLocation = pref.get(DEFAULT_FOLDER, null);
        if(defaultFolderLocation != null){
            defaultFolder = new File(defaultFolderLocation);
        } else {
            defaultFolder = null;
        }
        
        trackClusters = true;
    }
    
    public Global(){
        this.systemName = DEFAULT_SYSTEM_NAME;
        boxGeometry = new BoxGeometry();
        systemTimes = new SystemTimes();
        systemAnnotation = new Annotation();
        pref = Preferences.userRoot();
        
        String defaultFolderLocation = pref.get(DEFAULT_FOLDER, null);
        if(defaultFolderLocation != null){
            defaultFolder = new File(defaultFolderLocation);
        } else {
            defaultFolder = null;
        }
        
        trackClusters = true;
    }
    
    /* **************  METHODS TO SET AND GET THE SYSTEM NAME *************/
    
    public void setSystemName(String name){
        this.systemName = name;
    }
    
    public String getSystemName(){
        return systemName;
    }
    
    /* **************  METHODS RELATED TO THE MOLECULE ARRAY  *************/
    
    // Add a single molecule to the array
    public void addMolecule(Molecule molecule){
        molecules.add(molecule);
    }
    
    // Remove a molecule from the array
    public void removeMolecule(Molecule molecule){
        molecules.remove(molecule);
    }
    
    // Remove molecule by index
    public void removeMolecule(int index){
        molecules.remove(index);
    }
    
    // Retrieve the entire arraylist of molecules
    public ArrayList<Molecule> getMolecules(){
        return molecules;
    }
    
    // Retrieve a single molcule by its index
    public Molecule getMolecule(int index){
        return molecules.get(index);
    }
    
    // Retrieve a single molecule by its name. Returns null if no molecules has the given name.
    public Molecule getMolecule(String name){
        Molecule molecule = null;
        for(Molecule mol : molecules){
            if(mol.getName().equals(name)){
                molecule = mol;
                break;
            }
        }
        return molecule;
    }
    
    public ArrayList<String> getMoleculeNames(){
        ArrayList<String> names = new ArrayList<>();
        for(Molecule molecule : molecules){
            names.add(molecule.getName());
        }
        return names;
    }
    
    /* ********* METHODS RELATED TO THE BINDING REACTION ARRAY **********/
    
    // Add a single reaction
    public void addBindingReaction(BindingReaction reaction){
        bindingReactions.add(reaction);
    }
    
    // Remove a single binding reaction
    public void removeBindingReaction(BindingReaction reaction){
        bindingReactions.remove(reaction);
    }
    
    // Remove a binding reaction based on its index
    public void removeBindingReaction(int index){
        bindingReactions.remove(index);
    }
    
    // Get the entire binding reaction array
    public ArrayList<BindingReaction> getBindingReactions(){
        return bindingReactions;
    }
    
    // Get a binding reaction by its index
    public BindingReaction getBindingReaction(int index){
        return bindingReactions.get(index);
    }
    
    // Get a binding reaction by name
    public BindingReaction getBindingReaction(String name){   
        BindingReaction bindingReaction = null;
        for(BindingReaction reaction : bindingReactions){
            if(reaction.getName().equals(name)){
                bindingReaction = reaction;
            }
        }
        return bindingReaction;
    }
    
    /* ******** METHODS RELATED TO THE TRANSITION REACTION ARRAY ********/
    
    // Add a single transition reaction
    public void addTransitionReaction(TransitionReaction reaction){
        transitionReactions.add(reaction);
    }
    
    // Remove a single transition reaction
    public void removeTransitionReaction(TransitionReaction reaction){
        transitionReactions.remove(reaction);
    }
    
    // Remove a single reaction based on its index
    public void removeTransitionReaction(int index){
        transitionReactions.remove(index);
    }
    
    // Get the entire transition reaction array
    public ArrayList<TransitionReaction> getTransitionReactions(){
        return transitionReactions;
    }
    
    // Get transition reaction by index
    public TransitionReaction getTransitionReaction(int index){
        return transitionReactions.get(index);
    }
    
    // Get transition reaction by name
    public TransitionReaction getTransitionReaction(String name){
        TransitionReaction transitionReaction = null;
        for(TransitionReaction reaction : transitionReactions){
            if(reaction.getName().equals(name)){
                transitionReaction = reaction;
                break;
            }
        }
        return transitionReaction;
    }
    
    /* ******** METHODS RELATED TO THE ALLOSTERIC REACTION ARRAY ********/
    
    // Add a single allosteric reaction
    public void addAllostericReaction(AllostericReaction reaction){
        allostericReactions.add(reaction);
    }
    
    // Remove allosteric reaction
    public void removeAllostericReaction(AllostericReaction reaction){
        allostericReactions.remove(reaction);
    }
    
    // Remove allosteric reaction based on index
    public void reamoveAllostericReaction(int index){
        allostericReactions.remove(index);
    }
    
    // Get entire allosteric reaction array
    public ArrayList<AllostericReaction> getAllostericReactions(){
        return allostericReactions;
    }
    
    // Get allosteric reaction by index
    public AllostericReaction getAllostericReaction(int index){
        return allostericReactions.get(index);
    }
    
    // Get allosteric reaction by name
    public AllostericReaction getAllostericReaction(String name){
        AllostericReaction reaction = null;
        for(AllostericReaction rxn : allostericReactions){
            if(rxn.getName().equals(name)){
                reaction = rxn;
                break;
            }
        }
        return reaction;
    }

    /* *************** METHODS RELATED TO GEOMETRY **********************/
    
    public BoxGeometry getBoxGeometry(){
        return boxGeometry;
    }
    
    /* *************** METHODS RELATED TO THE SYSTEM TIMES ***************/
    
    public SystemTimes getSystemTimes(){
        return systemTimes;
    }
    
    /* ****************** METHODS RELATED TO CLUSTER TRACKING ***************/
    public void setTrackClusters(boolean bool){
        this.trackClusters = bool;
    }
    
    public boolean isTrackingClusters(){
        return trackClusters;
    }
    
    /* ***** CHECK IF A MOLECULE PARTICIPATES IN A BINDING REACTION *****/
        
    public boolean moleculeInBindingReaction(Molecule molecule){
        boolean inReaction = false;
        for(BindingReaction reaction : bindingReactions){
            if(molecule == reaction.getMolecule(0) || molecule == reaction.getMolecule(1)){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    /* ***** CHECK IF A MOLECULE OR SITE TYPE IS IN TRANSITION REACTION **/
    
    public boolean moleculeInTransitionReaction(Molecule molecule){
        boolean inReaction = false;
        for(TransitionReaction reaction : transitionReactions){
            if(molecule == reaction.getMolecule() || molecule == reaction.getConditionalMolecule()){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    public boolean typeInTransitionReaction(SiteType type){
        boolean inReaction = false;
        for(TransitionReaction reaction: transitionReactions){
            if(type == reaction.getType() || type == reaction.getConditionalType()){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    public boolean stateInTransitionReaction(State state){
        boolean inReaction = false;
        for(TransitionReaction reaction : transitionReactions){
            if(state == reaction.getConditionalState() || state == reaction.getFinalState() 
                    || state == reaction.getInitialState()){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    /* ***** CHECK IF A SITE OR STATE IS IN AN ALLOSTERIC REACTION *****/
   
    public boolean moleculeInAllostericReaction(Molecule molecule){
        boolean inReaction = false;
        for(AllostericReaction reaction : allostericReactions){
            if(molecule == reaction.getMolecule()){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    public boolean siteInAllostericReaction(Site site){
        boolean inReaction = false;
        for(AllostericReaction reaction : allostericReactions){
            if(reaction.getSite() == site || reaction.getAllostericSite() == site){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    public boolean stateInAllostericReaction(State state){
        boolean inReaction = false;
        for(AllostericReaction reaction : allostericReactions){
            if(reaction.getInitialState() == state || reaction.getFinalState() == state
                    || reaction.getAllostericState() == state){
                inReaction = true;
                break;
            }
        }
        return inReaction;
    }
    
    /* *********************************************************************\
     *              METHODS TO CHECK SYSTEM CORRECTNESS                    *
     * Before saving the system, we want to check that certain conditions  * 
     * are satisfied.  In particular, we want to make sure that all the    *
     * sites of each molecule are connected to each other, and that the    *
     * binding reactions have well-defined molecules, types, and states.   * 
    \***********************************************************************/
    
    // Check if each molecule is fully connected
    public boolean moleculesFullyConnected(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean allConnected = true;
        for(Molecule molecule : molecules){
            if(molecule.getSiteArray().isEmpty() || molecule.getTypeArray().isEmpty()){
            	PopUp.error("Molecule " + molecule.getName()
                + " is not complete. Add at least 1 site and type to save.");
            	allConnected = false;
                break;
            }
            else if(!molecule.sitesConnected()){
                PopUp.error("Some sites in molecule " + molecule.getName()
                                    + " are not connected to the others.");
                allConnected = false;
                break;
            }
        }
        return allConnected;
        // </editor-fold>
    }
    
    // Check if membrane molecules have anchor sites
    public boolean membraneMoleculesHaveAnchors(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean ok = true;
        for(Molecule molecule : molecules){
            if(molecule.getLocation().equals(SystemGeometry.MEMBRANE)){
                if(!molecule.hasAnchorSites()){
                    PopUp.error("The membrane molecule " + molecule.getName()
                                        + " is not anchored to the membrane.");
                    ok = false;
                    break;
                }
            }
        }
        return ok;
        // </editor-fold>
    }
    
    // Check if each transition reaction is well defined
    public boolean transitionReactionsWellDefined(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean wellDefined = true;
        for(TransitionReaction reaction : transitionReactions){
            if(reaction.getName().equals("")){
                wellDefined = false;
                PopUp.error("One or more transition reactions are not assigned reaction names."
                        + " All reactions must have a reaction name.");
                break;
            }
            if(reaction.getMolecule() == null || reaction.getType() == null ||
                    reaction.getInitialState() == null || reaction.getFinalState() == null){
                wellDefined = false;
            }
            if(reaction.getCondition().equals(TransitionReaction.BOUND_CONDITION)){
                if(reaction.getConditionalMolecule() == null || reaction.getConditionalType() == null ||
                        reaction.getConditionalState() == null){
                    wellDefined = false;
                }
            }
            if(!wellDefined){
                PopUp.error("The transition reaction " + reaction.getName() + " is not well-defined.\n"
                        + " Make sure that the reaction has initial and final states and "
                        + "a conditional state if appropriate.");
                break;
            }
        }
        
        return wellDefined;
        // </editor-fold>
    }
    
    // Check if allosteric reactions are well defined
    public boolean allostericReactionsWellDefined(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean wellDefined = true;
        for(AllostericReaction reaction : allostericReactions){
            if(reaction.getName().equals("")){
                wellDefined = false;
                PopUp.error("One or more allosteric reactions are not assigned reaction names."
                        + " All reactions must have a reaction name.");
                break;
            }
            
            if(reaction.getMolecule() == null || reaction.getSite() == null
                    || reaction.getInitialState() == null || reaction.getFinalState() == null
                    || reaction.getAllostericSite() == null || reaction.getAllostericState() == null){
                wellDefined = false;
                PopUp.error("The allosteric reaction " + reaction.getName() + " is not well-defined.\n"
                        + " Make sure that the reaction has initial, final,"
                        + " and allosteric states.");
                break;
            }
        }
        return wellDefined;
        // </editor-fold>
    }
    
    // Check to see if each binding reaction is well defined
    public boolean bindingReactionsWellDefined(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean wellDefined = true;
        for(BindingReaction reaction : bindingReactions){
            if(reaction.getName().equals("")){
                wellDefined = false;
                PopUp.error("One or more binding reactions are not assigned reaction names."
                        + " All reactions must have a reaction name.");
                break;
            }
            if(reaction.getMolecule(0) == null || reaction.getMolecule(1) == null){
                wellDefined = false;
            } else if(reaction.getType(0) == null || reaction.getType(1) == null){
                wellDefined = false;
            } else if(reaction.getState(0) == null || reaction.getState(1) == null){
                wellDefined = false;
            }
            
            if(!wellDefined){
                PopUp.error("The binding reaction " + reaction.getName() + " is not well defined."
                        + " Please make sure the reaction defines both molecules, site types, and states.");
                break;
            }
        }
        
        return wellDefined;
        // </editor-fold>
    }
    
    /* ********  METHODS RELATED TO THE SYSTEM ANNOTATION ************/
    public Annotation getSystemAnnotation(){
        return systemAnnotation;
    }
    
    /* ***************  FILE MANAGEMENT METHODS **********************/
    
    public File getFile(){
        return file;
    }
    
    public void setFile(File file){
        this.file = file;
        String filename = file.getName();
        this.setSystemName(filename.substring(0, filename.length()-4));
    }
    
    public File getDefaultFolder(){
        return defaultFolder;
    }
    
    public void setDefaultFolder(File folder){
        defaultFolder = folder;
        writeDefaultFolder();
    }
    
    private void writeDefaultFolder(){
        pref.put(DEFAULT_FOLDER, defaultFolder.getAbsolutePath());
    }
    
    /* ************ ANNOTATION MANAGEMENT ********************************/
    private void loadMoleculeAnnotations(String string){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(string);
        sc.useDelimiter("Annotation:");
        while(sc.hasNext()){
            String moleculeInput = sc.next();
            if(moleculeInput != null && moleculeInput.length() != 0){
                Scanner molSc = new Scanner(moleculeInput);
                Molecule molecule = this.getMolecule(IOHelp.getNameInQuotes(molSc));
                Annotation a = molecule.getAnnotation();
                StringBuilder sb = new StringBuilder();
                // finish line
                molSc.nextLine();
                // skip "{"
                molSc.nextLine();
                while(molSc.hasNextLine()){
                    String line = molSc.nextLine();
                    if(line.equals("}")){
                        break;
                    } else {
                        sb.append(line).append("\n");
                    }
                }
                a.setAnnotation(sb.toString());
                molSc.close();
            }
        }
        sc.close();
        // </editor-fold>
    }
    
    private void writeMoleculeAnnotations(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        for(Molecule molecule : molecules){
            Annotation a = molecule.getAnnotation();
            p.println("Annotation: '" + molecule.getName() + "'");
            p.println("{");
            a.printAnnotation(p);
            p.println("}");
            p.println();
        }
        // </editor-fold>
    }
    
    private void writeReactionAnnotations(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        for(Reaction r : transitionReactions){
            Annotation a = r.getAnnotation();
            p.println("Annotation: '" + r.getName() + "'");
            p.println("{");
            a.printAnnotation(p);
            p.println("}");
            p.println();
        }
        for(Reaction r : allostericReactions){
            Annotation a = r.getAnnotation();
            p.println("Annotation: '" + r.getName() + "'");
            p.println("{");
            a.printAnnotation(p);
            p.println("}");
            p.println();
        }
        for(Reaction r : bindingReactions){
            Annotation a = r.getAnnotation();
            p.println("Annotation: '" + r.getName() + "'");
            p.println("{");
            a.printAnnotation(p);
            p.println("}");
            p.println();
        }
        // </editor-fold>
    }
    
    private void loadReactionAnnotations(String string){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(string);
        sc.useDelimiter("Annotation:");
        while(sc.hasNext()){
            String rInput = sc.next();
            if(rInput != null && rInput.length() != 0){
                Scanner rSc = new Scanner(rInput);
                String rxnName = IOHelp.getNameInQuotes(rSc);
                Reaction rxn = this.getTransitionReaction(rxnName);
                if(rxn == null){
                    rxn = this.getAllostericReaction(rxnName);
                    if(rxn == null){
                        rxn = this.getBindingReaction(rxnName);
                        if(rxn == null){
                            System.out.println("ERROR: Tried to read in annotation a non-existent reaction.");
                        }
                    }
                }
                if(rxn != null){
                    Annotation a = rxn.getAnnotation();
                    StringBuilder sb = new StringBuilder();
                    rSc.nextLine();
                    rSc.nextLine();
                    while(rSc.hasNextLine()){
                        String line = rSc.nextLine();
                        if(line.equals("}")){
                            break;
                        } else {
                            sb.append(line).append("\n");
                        }
                    }
                    a.setAnnotation(sb.toString());
                }
                rSc.close();
            }
        }
        sc.close();
        // </editor-fold>
    }
    
    /* *************** WRITE SYSTEM DATA TO FILE *************************/
    public void copyPDBtoNewLocation(){
    	for (Molecule m: molecules) {
    		if(m.getFilename() != null) {
    			File source = new File(m.getFilename());
    			String name = source.getName();
    			File targetDir = new File(file.getParent() + File.separator + "structure_files");
    			File target = new File(file.getParent() + File.separator + "structure_files" + File.separator + name);
   			
    			if (targetDir.exists() && targetDir.isDirectory() && !target.exists() && !target.isDirectory()) {
    				//if target does not exist, open writer
    				try(PrintWriter p = new PrintWriter(new FileWriter(target), true)){
    					//open reader of source
    					try (BufferedReader br = new BufferedReader(new FileReader(source))) {
    					    String line;
    					    while ((line = br.readLine()) != null) {
    					       p.println(line);
    					    }
    					}

    					m.setFile(target.toString());
    				}catch(IOException ioe){
    		            ioe.printStackTrace(System.out);
    		        }
    			}
    		}
    	}
    }
    
    public void writeFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        try(PrintWriter p = new PrintWriter(new FileWriter(file), true)){
            /* ********* BEGIN BY WRITING THE TIMES *********/
            p.println("*** " + TIME_INFORMATION + " ***");
            systemTimes.writeData(p);
            p.println();
            
            /* ********* WRITE THE SPATIAL INFORMATION **********/
            p.println("*** " + SPATIAL_INFORMATION + " ***");
            boxGeometry.writeData(p);
            p.println();
            
            /* ******* WRITE THE SPECIES INFORMATION ***********/
            p.println("*** " + MOLECULES + " ***");
            p.println();
            for(Molecule molecule : molecules){
                molecule.writeMolecule(p);
            }
            /* ******* WRITE THE SPECIES INFORMATION ***********/
            p.println("*** " + MOLECULE_FILES + " ***");
            p.println();
            for(Molecule molecule : molecules){
                p.println("MOLECULE: " + molecule.getName() + " " + molecule.getFilename());
            }
            p.println();
            
            /* ******* WRITE THE DECAY REACTIONS ***************/
            p.println("*** " + DECAY_REACTIONS + " ***");
            p.println();
            for(Molecule molecule : molecules){
                p.println(molecule.getDecayReaction().writeReaction());
            }
            p.println();
            
            /* ******* WRITE THE TRANSITION REACTIONS **********/
            p.println("*** " + TRANSITION_REACTIONS + " ***");
            p.println();
            for(TransitionReaction reaction : transitionReactions){
                p.println(reaction.writeReaction());
            }
            p.println();
            
            /* ******* WRITE THE ALLOSTERIC REACTIONS **********/
            p.println("*** " + ALLOSTERIC_REACTIONS + " ***");
            p.println();
            for(AllostericReaction reaction: allostericReactions){
                p.println(reaction.writeReaction());
            }
            p.println();
            
            /* ******* WRITE THE BINDING REACTIONS ************/
            p.println("*** " + BINDING_REACTIONS + " ***");
            p.println();
            for(BindingReaction reaction : bindingReactions){
                p.println(reaction.writeReaction());
            }
            p.println();
            
            /* ****** WRITE THE MOLECULE COUNTERS **********/
            p.println("*** " + MOLECULE_COUNTERS + " ***");
            p.println();
            for(Molecule molecule: molecules){
                molecule.getMoleculeCounter().writeMoleculeCounter(p);
            }
            p.println();
            
            /* ******  WRITE THE STATE COUNTERS *************/
            p.println("*** " + STATE_COUNTERS + " ***");
            p.println();
            for(Molecule molecule : molecules){
                for(SiteType type : molecule.getTypeArray()){
                    for(State state : type.getStates()){
                        state.getStateCounter().writeStateCounter(p);
                    }
                }
            }
            p.println();
            
            /* ***** WRITE THE BOND COUNTERS ***************/
            p.println("*** " + BOND_COUNTERS + " ***");
            p.println();
            for(BindingReaction reaction: bindingReactions){
                reaction.getBondCounter().writeBondCounter(p);
            }
            p.println();
            
            /* ********  WRITE THE SITE PROPERTY COUNTERS ************/
            p.println("*** " + SITE_PROPERTY_COUNTERS + " ***");
            p.println();
            for(Molecule molecule : molecules){
                ArrayList<Site> sites = molecule.getSiteArray();
                for(Site site : sites){
                    site.getPropertyCounter().writeSitePropertyCounter(p);
                }
            }
            p.println();
            
            /* *************** WRITE THE TRACK CLUSTERS BOOLEAN ***********/
            p.println("*** " + CLUSTER_COUNTERS + " ***");
            p.println();
            p.println("Track_Clusters: " + trackClusters);
            p.println();
            
            /* ****** WRITE THE SYSTEM ANNOTATION ********************/
            p.println("*** " + SYSTEM_ANNOTATION + " ***");
            p.println();
            systemAnnotation.printAnnotation(p);
            p.println();
            
            /* ****** WRITE THE MOLECULE ANNOTATIONS *****************/
            p.println("*** " + MOLECULE_ANNOTATIONS + " ***");
            p.println();
            writeMoleculeAnnotations(p);
            
            /* ****** WRITE THE REACTION ANNOTATIONS *****************/
            p.println("*** " + REACTION_ANNOTATIONS + " ***");
            p.println();
            writeReactionAnnotations(p);
            
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }
        // </editor-fold>
    }
    
    /* ************** LOAD SYSTEM DATA ***********************************/
    
    public void loadFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            sc.useDelimiter("\\*\\*\\*");
            while(sc.hasNext()){
                String next = sc.next().trim();
                switch(next){
                    case TIME_INFORMATION:
                        systemTimes.loadData(sc.next().trim());
                        break;
                    case SPATIAL_INFORMATION:
                        boxGeometry.loadData(sc.next().trim());
                        break;
                    case MOLECULES:
                        molecules = Molecule.loadMolecules(sc.next().trim());
                        break;
                    case MOLECULE_FILES:
                        Molecule.loadMoleculesFiles(this, sc.next().trim(), molecules);
                        break;
                    case DECAY_REACTIONS:
                        DecayReaction.loadReactions(this, new Scanner(sc.next().trim()));
                        break;
                    case TRANSITION_REACTIONS:
                        transitionReactions = TransitionReaction.loadReactions(this, new Scanner(sc.next().trim()));
                        break;
                    case ALLOSTERIC_REACTIONS:
                        allostericReactions = AllostericReaction.loadReactions(this, new Scanner(sc.next().trim()));
                        break;
                    case BINDING_REACTIONS:
                        bindingReactions = BindingReaction.loadReactions(this, new Scanner(sc.next().trim()));
                        break;
                    case MOLECULE_COUNTERS:
                        MoleculeCounter.loadCounters(this, new Scanner(sc.next().trim()));
                        break;
                    case STATE_COUNTERS:
                        StateCounter.loadCounters(this, new Scanner(sc.next().trim()));
                        break;
                    case BOND_COUNTERS:
                        BondCounter.loadCounters(this, new Scanner(sc.next().trim()));
                        break;
                    case SITE_PROPERTY_COUNTERS:
                        SitePropertyCounter.loadCounters(this, new Scanner(sc.next().trim()));
                        break;
                    case CLUSTER_COUNTERS:
                        Scanner xsc = new Scanner(sc.next().trim());
                        // Skip "Track_Clusters: "
                        xsc.next();
                        this.trackClusters = xsc.nextBoolean();
                        xsc.close();
                        break;
                    case SYSTEM_ANNOTATION:
                        systemAnnotation.setAnnotation(sc.next().trim());
                        break;
                    case MOLECULE_ANNOTATIONS:
                        loadMoleculeAnnotations(sc.next().trim());
                        break;
                    case REACTION_ANNOTATIONS:
                        loadReactionAnnotations(sc.next().trim());
                        break;
                }
            }
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    bioe.printStackTrace(System.out);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    fioe.printStackTrace(System.out);
                }
            }
        }
        // </editor-fold>
    }
    
    /* ************* RESET SYSTEM ****************************************/
    
    public void reset(){
        systemName = DEFAULT_SYSTEM_NAME;
        file = null;
        molecules.clear();
        transitionReactions.clear();
        allostericReactions.clear();
        bindingReactions.clear();
        systemTimes.reset();
        boxGeometry.reset();
        systemAnnotation.setAnnotation("");
    }
    
}
