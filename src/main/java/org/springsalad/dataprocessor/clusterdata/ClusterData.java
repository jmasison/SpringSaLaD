/**
 *  Stores all the SingleRunSnapShots for the simulation and has methods
 *  to get various statistics.
 */
package org.springsalad.dataprocessor.clusterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

@Deprecated
public class ClusterData {
    
    public static final String CLUSTER_SIZE = "Cluster Size";
    
    /* ***************  The single run snap shots. *************************/
    private final ArrayList<SingleRun> singleRuns;
    
    /* ***** Lots of arrays to store various time point averages  *********/
    /* Want to calculate and store them so we're not recalculating on the fly.*/
    // NOTE THAT THESE ARE AVERAGES TAKEN OVER THE DIFFERENT RUNS. For each 
    // run we calculate an average, and then we take the average of the 
    // individual averages.  Thus, THE ST DEV IS ACTUALLY THE STANDARD ERROR
    // OF THE MEAN. 
    private double [] times; // Only need one array to store the times
    private double [] avClusterSizes; // Includes all molecules
    private double [] av2ClusterSizes;
    private double [] stDevClusterSizes; 
    // We want an array holding the average number of each time of molecule
    // bound in clusters at time t.  To keep these organized, make a hashmap
    // of the arrays.  The keys are the molecule names.
    private final HashMap<String, double []> avNumberBound;
    private final HashMap<String, double []> av2NumberBound;
    private final HashMap<String, double []> stDevNumberBound;
    
    /* *********** LIST OF ALL THE MOLECULE NAMES **************************/
    private final ArrayList<String> names;
    
    public ClusterData(ArrayList<String> names){
        this.names = names;
        singleRuns = new ArrayList<>();
        avNumberBound = new HashMap<>(100);
        av2NumberBound = new HashMap<>(100);
        stDevNumberBound = new HashMap<>(100);
    }
    
    /* ************* METHODS TO MANIPULATE SINGLERUNS ARRAY ******************/
    
    public ArrayList<SingleRun> getSingleRuns(){
        return singleRuns;
    }
    
    public void loadSingleRuns(File directory, int totalRuns){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        for(int i=0;i<totalRuns;i++){
            SingleRun srss = new SingleRun();
            srss.loadAllSnapShots(new File(directory,"Run"+i));
            singleRuns.add(srss);
        }
        calculateAverages();
        // </editor-fold>
    }
    
    public void loadSingleRuns(String directoryName, int totalRuns){
        loadSingleRuns(new File(directoryName), totalRuns);
    }
    
    /* *****************  GET THE NAMES *************************************/
    
    public ArrayList<String> getNames(){
        return names;
    }
    
    /* ************* Calculate all of the averages we want ******************/
    
    private void calculateAverages(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        
        // First determine the total number of time points by looking at the 
        // first single run snapshot object.
        int totalTimePoints = singleRuns.get(0).getTotalTimePoints();
        times = new double[totalTimePoints];
        avClusterSizes = new double[totalTimePoints];
        av2ClusterSizes = new double[totalTimePoints];
        stDevClusterSizes = new double[totalTimePoints];
        // Initialize the arrays in the hashmaps
        for(String name : names){
            avNumberBound.put(name, new double[totalTimePoints]);
            av2NumberBound.put(name, new double[totalTimePoints]);
            stDevNumberBound.put(name, new double[totalTimePoints]);
        }
        
        for(int i=0;i<totalTimePoints;i++){
            times[i] = singleRuns.get(0).getTimes().get(i);
            avClusterSizes[i] = 0;
            stDevClusterSizes[i] = 0;
            for(String name : names){
                avNumberBound.get(name)[i] = 0;
                av2NumberBound.get(name)[i] = 0;
                stDevNumberBound.get(name)[i] = 0;
            }
        }
        
        // Now loop over the time points
        for(int i=0;i<totalTimePoints;i++){
            // Loop over all of the singleruns to get the right clustersnapshot
            int runsWithClusters = 0; // Needed for computing the averages. 
            for(SingleRun srss : singleRuns){
                ClusterSnapShot css = srss.getClusterSnapShots().get(i);
                // As a sanity check, we'll make sure the times agree
                if(Math.abs(times[i] - css.getTime()) > 1e-5){
                    System.out.println("TIMES DO NOT AGREE!");
                    System.out.println("times[" + i + "] = " + times[i] + ", css.getTime() = " + css.getTime());
                    System.exit(1);
                }
                
                double avSize = 0;
                HashMap<String, Double> molTotal = new HashMap<>(30);
                for(String name : names){
                    molTotal.put(name, 0.0);
                }
                for(Cluster cluster : css.getClusters()){
                    avSize += cluster.getTotalMolecules();
                    for(String name : names){
                        double c = molTotal.get(name);
                        c += (double)cluster.getNumberOfType(name);
                        molTotal.put(name, c);
                    }
                }

                for(String name : names){
                    double v = molTotal.get(name);
                    avNumberBound.get(name)[i] += v;
                    av2NumberBound.get(name)[i] += v*v;
                }
                // If there are NO clusters, then we don't want to include
                // this simulation in the average.  We're only averaging 
                // over clusters with >=2 molecules, so this simulation
                // doesn't contribute.  (Otherwise I end up getting average
                // cluser sizes <2, which makes no sense.)
                if(avSize > 0){
                    runsWithClusters += 1;
                    avSize /= css.getTotalClustersSizeGreaterThan2();
                    avClusterSizes[i] += avSize;
                    av2ClusterSizes[i] += avSize*avSize;
                }
            }
            
            // Now we're finished looping over the runs, so we can compute the average
            if(runsWithClusters != 0){
                avClusterSizes[i] /= runsWithClusters;
                av2ClusterSizes[i] /= runsWithClusters;
                stDevClusterSizes[i] = Math.sqrt(av2ClusterSizes[i] - avClusterSizes[i]*avClusterSizes[i]);
            }
            for(String name : names){
                double size = (double)singleRuns.size();
                avNumberBound.get(name)[i] /= size;
                av2NumberBound.get(name)[i] /= size;
                // Having some rounding issues apparently, because sometimes 
                // av2 < av*av, and then the sqrt is imaginary. This just 
                // happens when the std dev should be zero, so check for this.
                double av = avNumberBound.get(name)[i];
                double av2 = av2NumberBound.get(name)[i];
                double variance = (av2 - av*av);
                // Rescale the variance so it agrees with the usual statistical definition (N-1 degrees of freedom)
                if(size > 1){
                    variance = (size/(size-1))*variance;
                }
                if(variance > 0){
                    stDevNumberBound.get(name)[i] = Math.sqrt(variance);
                } 
            }
        }
        // </editor-fold>
    }
    
    /* ************  Get the various values **********************************/
    public double [] getTimes(){
        return times;
    }
    
    public double getTime(int i){
        return times[i];
    }
    
    public double [] getAvClusterSizes(){
        return avClusterSizes;
    }
    
    public double getAvClusterSize(int i){
        return avClusterSizes[i];
    }
    
    public double [] getStDevClusterSizes(){
        return stDevClusterSizes;
    }
    
    public double getStDevClusterSize(int i){
        return stDevClusterSizes[i];
    }
    
    public double getAvNumberBound(String name, int i){
        return avNumberBound.get(name)[i];
    }
    
    public double getStDevNumberBound(String name, int i){
        return stDevNumberBound.get(name)[i];
    }
}
