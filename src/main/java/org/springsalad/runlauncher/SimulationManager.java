/*
 * This class stores the reference to the original file and stores the 
 * list of simulations.
 */

package org.springsalad.runlauncher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.springsalad.helpersetup.ClassLauncher;
import org.springsalad.langevinsetup.Global;

public class SimulationManager {

    // A reference to the global
    private final Global g;
    // A reference to the main file describing this system
    private final File globalFile;
    // A reference to the folder where all of the simulation files will be stored
    private final File simulationFolder;
    // The list of simulations
    private final ArrayList<Simulation> simulations = new ArrayList<>();
    // Tells us if we've warmed up the simulation class
    private static boolean warmedUp = false;
    
    public SimulationManager(Global g, File globalFile){
        this.g = g;
        this.globalFile = globalFile;
        this.simulationFolder = getSimulationFolder();
        loadExistingSimulations();
    }

    /*************************************************************************\
     *                     CREATE SIMULATION FOLDER                          *
     *                                                                       *
     *  @return The folder where simulations are stored.                     *
    \*************************************************************************/
    
    private File getSimulationFolder(){
        String fileName = globalFile.getName();
        // Strip off the .txt
        fileName = fileName.substring(0, fileName.length()-4);
        String folderName = fileName + "_SIMULATIONS";
        String parentFolder = globalFile.getParent();
        File folder = new File(parentFolder, folderName);
        folder.mkdir();
        return folder;
    }
    
    /*************************************************************************\
     *                      LOAD EXISTING SIMULATIONS                        *
     *  All simulations end with "_SIM", so just look for this suffix.       *
    \*************************************************************************/
    
    private void loadExistingSimulations(){
        File [] files = simulationFolder.listFiles();
        for(File file : files){
            String filename = file.getName();
            if(filename.length() > 8){
                if(filename.substring(filename.length()-8).equals("_SIM.txt")){
                    simulations.add(new Simulation(file));
                }
            }
        }
    }
    
    /************************************************************************\
     *                   CREATE NEW SIMULATION                              *
     *  Creates a new simulation.  The new simulation is named              *
     *  "SimulationX", where "X" is a numerical value.  The method first    *
     *  tries X = simulations.size(), but if that name is already used,     *
     *  then it increments X until it finds a name not in use.              *
    \************************************************************************/
    
    public void createNewSimulation(){
        // Make the new name.
        int X = simulations.size();
        String name = "Simulation";
        String newName = name + X;
        while(nameInUse(newName)){
            X++;
            newName = name + X;
        }
        // Copy the global file to a new file with the expected name
        Path globalPath = globalFile.toPath();
        File newFile = new File(simulationFolder, newName + "_SIM.txt");
        Path newFilePath = newFile.toPath();
        try{
            Files.copy(globalPath, newFilePath);
        } catch (IOException ioe){
            ioe.printStackTrace(System.out);
        }
        simulations.add(new Simulation(newFile));
    }
    
    /* ************* WARM UP SIMULATION ENGINE **************************/
    
    public static boolean isWarmedUp(){
        return warmedUp;
    }
    
    public static void warmUp(String input, String output){
        warmedUp = true;
        try{
            ClassLauncher.start("langevinnovis01.Global", input, "0", output);
        } catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    
    /* *************  GET SIMULATIONS ***********************************/
    
    public ArrayList<Simulation> getSimulations(){
        return simulations;
    }
    
    public Simulation getSimulation(int index){
        return simulations.get(index);
    }
    
    public void removeSimulation(int index){
        Simulation sim = simulations.get(index);
        sim.deleteFile();
        simulations.remove(sim);
    }
    
    public void removeSimulation(Simulation sim){
        sim.deleteFile();
        simulations.remove(sim);
    }
    
    private boolean nameInUse(String name){
        boolean inUse = false;
        for(Simulation simulation : simulations){
            if(simulation.getSimulationName().equals(name)){
                inUse = true;
                break;
            }
        }
        return inUse;
    }
    
    /* ************** RETURN THE GLOBAL OBJECT ************************/
    
    public Global getGlobal(){
        return g;
    }
    
}
