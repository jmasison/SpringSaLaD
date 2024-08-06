/*
 *  A "simulation" is a specific instance of the global model.  It has 
 *  it's own copy of the main global file, which it can operate on. 
 */

package org.springsalad.runlauncher;

import org.springsalad.clusteranalysis.DataProcessor2;
import org.springsalad.dataprocessor.DataProcessor;
import org.springsalad.helpersetup.IOHelp;
import org.springsalad.langevinsetup.Global;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class Simulation extends Global implements ChangeListener{
    
    /* *************  STRING TO INDICATE SIMULATION DATA IN FILE ************/
    public final static String SIMULATION_STATE = "SIMULATION STATE";
    public final static String PROCESSOR_FILES = "PROCESSOR FILES";
    public final static String RAW_DATA_FILES = "RAW DATA FILES";
    public final static String SITE_DATA_FILES = "SITE DATA FILES";
    
    /* *************  STRINGS TO INDICATE STATUS ************************/
    public final static String NEVER_RAN = "Never Ran";
    public final static String ABORTED = "Aborted";
    public final static String RUNNING = "Running";
    public final static String FINISHED = "Finished";
    
    /* ***  STATIC BOOLEAN TO INDICATE IF SIMULATION ENGINE IS WARMED UP **/
    public static boolean ENGINE_WARMED_UP = false;
    
    private String simulationName;
    
    private int runNumber;
    
    private Process [] process;
    
    private boolean isRunning = false;
    private boolean hasResults = false;
    private boolean aborted = false;
    
    private ProgressPanel [] progressPanel;
    private ProgressPanelFrame progressPanelFrame;
    
    private boolean parallel;
    private int numberSimultaneousRuns = 1;
    
    // I'd like the ability to run a simulation on the cluster, then move the
    // data files to the expected location, then open them with the gui and 
    // have the gui compute all of the statistics.  It'll be a little clunky
    // because first I'll have to load a model into the gui so the _SIMULATIONS
    // folder is created, then move a sim file into that folder (which must
    // be named SimName_SIM.txt) along with a folder named SimName_SIM_FOLDER
    // which contains all of the data I want to use.  For now I'm just going to
    // have the program look for the "run on cluster" flag and know to recalculate
    // all of the statistics.  A better way might be to let the user define
    // how many runs they'd like to average, or provide them more control.  
    // Right now this is just for me so I'm not going to worry about it too much.
    // I do not provide a getter or setter for this.  Only I should touch it,
    // and only by editing the file.
    private boolean runOnCluster;  // ALSO NEED TO MANUALLY SET THE HASRESULTS FLAG
    
    private final DataProcessor processor;
    
    private final ArrayList<SimulationListener> listeners;
    
    /* ***************  CONSTRUCTORS *******************************/
    
    // This constructor accepts a pre-existing simulation file
    public Simulation(File simFile){
        super("SYSTEM NAME NOT USED");
        String name = simFile.getName();
        simulationName = name.substring(0,simFile.getName().length()-8);
        super.setFile(simFile);
        // Must make processor before loading the file
        processor = new DataProcessor2(simFile.getParent(), name.substring(0,name.length()-4));
        // set some defaults
        parallel = false;
        runNumber = 1;
        
        runOnCluster = false;
        // now load the file
        this.loadFile();
        
        if(runOnCluster && hasResults){
            this.calculateStatistics();
        }
        
        listeners = new ArrayList<>();
    }
    
    
    /* ***************** GET AND SET RUN NUMBER **************************/
    
    public void setRunNumber(int n){
        runNumber = n;
        hasResults = false;
        aborted = false;
        isRunning = false;
        writeFile();
    }
    
    public int getRunNumber(){
        return runNumber;
    }
    
    /* ********* METHODS TO EDIT THE FILE AND FOLDER ************************/
    
    public void deleteFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        deleteSimulationData();
        File f = super.getFile();
        try{
            Files.delete(f.toPath());
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }
        // </editor-fold>
    }
    
    private void deleteSimulationData(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        String folderName = simulationName + "_SIM_FOLDER";
        File folder = new File(super.getFile().getParent(), folderName);
        try{
            IOHelp.removeRecursive(folder.toPath());
        } catch(IOException ioe){
            System.out.println(ioe.getMessage());
            System.out.println(ioe.getCause());
            ioe.printStackTrace(System.out);
        }
        // </editor-fold>
    }

    /* ********************************************************************\
     *               GET AND SET THE FILE NAME                            *
    \**********************************************************************/
    
    public String getSimulationName(){
        return simulationName;
    }
    
    public void setSimulationName(String name){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(!simulationName.equals(name)){
            simulationName = name;
            processor.setSimulationName(name + "_SIM");
            File currentFile = super.getFile();
            Path newPath = null;
            try{
                newPath = Files.move(currentFile.toPath(), currentFile.toPath().resolveSibling(simulationName + "_SIM.txt"));
            } catch(IOException ioe){
                ioe.printStackTrace(System.out);
            }
            if(newPath != null){
                super.setFile(newPath.toFile());
            }
            processor.setDataFolder(currentFile.getParent() + "/" +  processor.getSimulationName() + "_FOLDER/data/");
        }
        // </editor-fold>
    }
    
    /* ****************  GET THE DATA PROCESSOR **************************/
    public DataProcessor getDataProcessor(){
        return processor;
    }
    
    /* *************** CLEAN UP PREVIOUS OUTSTREAM FILES ***************/
    
    private void cleanOutStreamFiles(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        File parent = new File(super.getFile().getParent());
        File [] files = parent.listFiles();
        for(File f : files){
            if(f.getName().contains("_OutStream_") && f.getName().contains(simulationName)){
                try{
                    Files.delete(f.toPath());
                } catch(IOException ioe){
                    ioe.printStackTrace(System.out);
                }
            }
        }
        // </editor-fold>
    }
    
    /* *************** CHECK IF SIMULATION IS RUNNING *******************/
    public boolean isRunning(){
        return isRunning;
    }
    
    public boolean wasAborted(){
        return aborted;
    }
    
    public boolean hasResults(){
        return hasResults;
    }
    
    /* **********************  GET STATUS *********************************/
    public String getStatus(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(!isRunning && !aborted && !hasResults){
            return NEVER_RAN;
        } else if(!isRunning && aborted && !hasResults){
            return ABORTED;
        } else if(isRunning && !aborted && !hasResults){
            return RUNNING;
        } else if(!isRunning && !aborted && hasResults){
            return FINISHED;
        } else {
            return "Unexpected boolean flag combination.";
        }
        // </editor-fold>
    }
    
    /* **************** METHODS INVOLVING LISTENERS ******************/
    public void addSimulationListener(SimulationListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    
    public void removeSimulationListener(SimulationListener listener){
        listeners.remove(listener);
    }
    
    private void notifyListeners(){
        SimulationEvent event = new SimulationEvent(this);
        for(SimulationListener listener : listeners){
            listener.simulationChanged(event);
        }
    }
    
    /* ************** GET AND SET PARALLEL FLAG ***************************/
    
    public boolean isParallel(){
        return parallel;
    }
    
    public void setParallel(boolean bool){
        parallel = bool;
        writeFile();
    }
    
    public int getNumberSimultaneousRuns(){
        return numberSimultaneousRuns;
    }
    
    public void setNumberSimultaneousRuns(int number){
        numberSimultaneousRuns = number;
        writeFile();
    }
    
    /* ***************  KILL THIS SIMULATION *****************************/
    public void abortSimulation(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(isRunning){
            if(process != null){
                for(Process p : process){
                    p.destroy();
                }
            }
            
            isRunning = false;
            aborted = true;
            hasResults = false;
            closeProgressFrame();
            notifyListeners();
            writeFile();
            cleanOutStreamFiles();
        }
        // </editor-fold>
    }
    
    /* *************** RUN THIS SIMULATION *******************************/
    
    public void runSimulationWithProcessBuilder(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        cleanOutStreamFiles();
        // Delete old folder
        if(aborted || hasResults){
            deleteSimulationData();
        }
        String separator = System.getProperty("file.separator");
        String classPath = System.getProperty("java.class.path");
        String javaHome = System.getProperty("java.home");
        String javaPath = javaHome + separator + "bin" + separator + "java";
        isRunning = true;
        aborted = false;
        hasResults = false;
        File f = super.getFile();
        // System.out.println(f.getAbsolutePath());
        String parent = f.getParent();
        File [] outputFile = new File[runNumber];
        String [] outputFileName = new String[runNumber];
        progressPanel = new ProgressPanel[runNumber];
        process = new Process[runNumber];
        // Construct the files and progress panels.
        for(int i=0;i<runNumber;i++){
            String intString = Integer.toString(i);
            outputFileName[i] = parent + "/" + simulationName  + "_OutStream_" 
                                                    + intString + ".txt";
            outputFile[i] = new File(outputFileName[i]);
            
            try(PrintWriter p = new PrintWriter(new FileWriter(outputFile[i]),true)){
                p.println("Simulation 0% complete. Elapsed time: 0.000 sec.");
            }catch(FileNotFoundException fne){
                fne.printStackTrace(System.out);
            } catch(IOException ioe){
                ioe.printStackTrace(System.out);
            }
            
            progressPanel[i] = new ProgressPanel(simulationName, i, outputFile[i]);
            progressPanel[i].startProgressBar();
            progressPanel[i].addProgressBarListener(this);
        }
        
        RunLauncher sruns = new RunLauncher(process, f, outputFile, parallel, numberSimultaneousRuns);
        sruns.start();
        
        // Last thing we do is start the progress panel
        progressPanelFrame = new ProgressPanelFrame(progressPanel);
        notifyListeners();
        writeFile();
        // </editor-fold>
    }

    /* *************** SHOW PROGRESS ************************************/
    
    public JFrame getProgressFrame(){
        return progressPanelFrame;
    }
    
    public void closeProgressFrame(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(progressPanelFrame != null){
            progressPanelFrame.setVisible(false);
            progressPanelFrame.dispose();
            if(progressPanel != null){
                for(ProgressPanel panel : progressPanel){
                    if(panel != null){
                        panel = null;
                    }
                }
            }
        }
        progressPanel = null;
        // </editor-fold>
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Just see if all of the progress panels are finished
        boolean done = true;
        if(progressPanel != null){
            for (ProgressPanel panel : progressPanel) {
                if(panel != null){
                    done = done&&panel.isFinished();
                }
            }
        }
        isRunning = !done;
        
        if(!isRunning && !aborted){
            if(!processor.hasData()){
                processor.grabNames();
            }
            hasResults = true;
            // Close the progress frame
            closeProgressFrame();
            notifyListeners();
            // Must make files before writing the new simulation file
            calculateStatistics();
            writeFile();
            cleanOutStreamFiles();
        }
        // </editor-fold>
    }
    
    public void calculateStatistics(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        processor.calculateStatistics(0, runNumber-1);
        if(runOnCluster){
            writeFile();
        }
        // </editor-fold>
    }
    
    /* *************** METHODS TO WRITE SIMULATION SPECIFIC DATA ***********/
    private void writeSimulationState(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        p.println("Runs: " + runNumber); 
        p.println("Parallel: " + parallel);
        p.println("SimultaneousRuns: " + numberSimultaneousRuns);
        p.println("Aborted: " + aborted);
        p.println("IsRunning: " + isRunning);
        p.println("HasResults: " + hasResults);
        p.println("RunOnCluster: " + runOnCluster);
        // </editor-fold>
    }
    
    private void writeAverageFileLocations(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(!hasResults){
            p.println("MoleculeAverages: 'null'");
            p.println("BondAverages: 'null'");
            p.println("StateAverages: 'null'");
            p.println("RunningTimes: 'null'");
        } else {
            if(processor.getMoleculeAverageDataFile() != null){
                p.println("MoleculeAverages: '" + processor.getMoleculeAverageDataFile().getName() + "'");
            } else {
                p.println("MoleculeAverages: 'null'");
            }
            if(processor.getBondAverageDataFile() != null){
                p.println("BondAverages: '" + processor.getBondAverageDataFile().getName() + "'");
            } else {
                p.println("BondAverages: 'null'");
            }
            if(processor.getStateAverageDataFile() != null){
                p.println("StateAverages: '" + processor.getStateAverageDataFile().getName() + "'");
            } else {
                p.println("StateAverages: 'null'");
            }
            if(processor.getRunningTimesFile() != null){
                p.println("RunningTimes: '" + processor.getRunningTimesFile().getName() + "'");
            } else {
                p.println("RunningTimes: 'null'");
            }
        }
        // </editor-fold>
    }
    
    private void writeRawDataFileLocations(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(hasResults){
            for(String name : processor.getMoleculeNames()){
                p.println("'" + name + "' LOCATION '" + 
                        processor.getRawDataFile(name).getName() + "'");
            }
            for(String name : processor.getBondNames()){
                p.println("'" + name + "' LOCATION '" + 
                        processor.getRawDataFile(name).getName() + "'");
            }
            for(String name : processor.getStateNames()){
                p.println("'" + name + "' LOCATION '" + 
                        processor.getRawDataFile(name).getName() + "'");
            }
        } else {
            p.println("'null'");
        }
        // </editor-fold>
    }
    
    private void writeSiteDataFileLocations(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(hasResults){
            for(String name : processor.getSiteNames()){
                p.println("'" + name + "' LOCATION '" + 
                        processor.getSiteFile(name).getName() + "'");
            }
        } else {
            p.println("null");
        }
        // </editor-fold>
    }
    
    /* *************** METHODS TO LOAD SPECIFIC SIMULATION DATA *************/
    private void loadSimulationState(String stateData){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sSc = new Scanner(stateData);
        sSc.next();
        runNumber = sSc.nextInt();
        sSc.next();
        parallel = sSc.nextBoolean();
        String option = sSc.next();
        String value = sSc.next();
        // If we see "Aborted: " here, then we're reading in an old file
        // written before I added the number of simultaneous runs option.
        if(option.equals("Aborted:")){
            if(parallel){
                numberSimultaneousRuns = 2; // Just setting a default
            } else {
                numberSimultaneousRuns = 1;
            }
            aborted = Boolean.parseBoolean(value);
        } else {
            numberSimultaneousRuns = Integer.parseInt(value);
            sSc.next();
            aborted = sSc.nextBoolean();
        }
        sSc.next();
        isRunning = sSc.nextBoolean();
        sSc.next();
        hasResults = sSc.nextBoolean();
        
        // Check to see if we have more data. Files created before I 
        // defined the "runOnCluster" flag will not have this field.
        if(sSc.hasNext()){
            if(sSc.next().equals("RunOnCluster:")){
                runOnCluster = sSc.nextBoolean();
            }
        } else {
            runOnCluster = false;
        }
        sSc.close();
        // </editor-fold>
    }
    
    private void loadAverageFiles(String fileData){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner fsc = new Scanner(fileData);
        fsc.next();
        processor.setMoleculeAverageDataFile(IOHelp.getNameInQuotes(fsc));
        fsc.nextLine();
        fsc.next();
        processor.setBondAverageDataFile(IOHelp.getNameInQuotes(fsc));
        fsc.nextLine();
        fsc.next();
        processor.setStateAverageDataFile(IOHelp.getNameInQuotes(fsc));
        fsc.nextLine();
        fsc.next();
        processor.setRunningTimesFile(IOHelp.getNameInQuotes(fsc));
        fsc.close();
        // </editor-fold>
    }
    
    private void loadRawDataFiles(String fileData){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(fileData);
        while(sc.hasNextLine()){
            Scanner lineScanner = new Scanner(sc.nextLine());
            String name = IOHelp.getNameInQuotes(lineScanner);
            // Skip "LOCATION"
            lineScanner.next();
            String fileName = IOHelp.getNameInQuotes(lineScanner);
            processor.setRawDataFile(name, fileName);
            lineScanner.close();
        }
        sc.close();
        // </editor-fold>
    }
    
    private void loadSiteDataFiles(String fileData){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(fileData);
        while(sc.hasNextLine()){
            Scanner lineScanner = new Scanner(sc.nextLine());
            String name = IOHelp.getNameInQuotes(lineScanner);
            // Skip "LOCATION"
            lineScanner.next();
            String fileName = IOHelp.getNameInQuotes(lineScanner);
            processor.setSiteFile(name, fileName);
            lineScanner.close();
        }
        sc.close();
        // </editor-fold>
    }
    
    /* *****  OVERRIDE THE SAVE AND LOAD FILE TO INCLUDE SIMULATION DATA **/
    
    @Override
    public void writeFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        super.writeFile();
        // Open the file to append
        try(PrintWriter p = new PrintWriter(new FileWriter(super.getFile(),true), true)){
            p.println();
            
            p.println("*** " + SIMULATION_STATE + " ***");
            p.println();
            writeSimulationState(p);
            p.println();
            
            p.println("*** " + PROCESSOR_FILES + " ***");
            p.println();
            writeAverageFileLocations(p);
            p.println();
            
            p.println("*** " + RAW_DATA_FILES + " ***");
            p.println();
            writeRawDataFileLocations(p);
            p.println();
            
            p.println("*** " + SITE_DATA_FILES + " ***");
            p.println();
            writeSiteDataFileLocations(p);
            p.println();
            
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }
        // </editor-fold>
    }
    
    @Override
    final public void loadFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        super.loadFile();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(super.getFile());
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            sc.useDelimiter("\\*\\*\\*");
            while(sc.hasNext()){
                String next = sc.next().trim();
                switch (next) {
                    case SIMULATION_STATE:
                        loadSimulationState(sc.next().trim());
                        break;
                    case PROCESSOR_FILES:
                        if(hasResults){
                            processor.grabNames();
                            if(!runOnCluster){
                                System.out.println("Loading " + super.getFile().getName());
                                loadAverageFiles(sc.next().trim());
                            }
                        }
                        break;
                    case RAW_DATA_FILES:
                        if(hasResults && !runOnCluster){
                            loadRawDataFiles(sc.next().trim());
                        }
                        break;
                    case SITE_DATA_FILES:
                        if(hasResults && !runOnCluster){
                            loadSiteDataFiles(sc.next().trim());
                        }
                }
            }
        } catch(FileNotFoundException fnfe){
            fnfe.printStackTrace(System.out);
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
}
