/**
 *  Just stores the time and the clusters at that time.
 */
package org.springsalad.dataprocessor.clusterdata;

import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;

@Deprecated
public class SingleRun {

    private final ArrayList<ClusterSnapShot> clusterSnapShots;
    private final ArrayList<Double> times;
    
    public SingleRun(){
        clusterSnapShots = new ArrayList<>();
        times = new ArrayList<>();
    }
    
    public ArrayList<ClusterSnapShot> getClusterSnapShots(){
        return clusterSnapShots;
    }
    
    public ArrayList<Double> getTimes(){
        return times;
    }
    
    public int getTotalTimePoints(){
        return times.size();
    }
    
    public void loadAllSnapShots(File directory){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        File [] files = directory.listFiles();
        Arrays.sort(files);
        for(File file : files){
            if(file.getName().startsWith("Clusters_Time_")){
                ClusterSnapShot snapShot = new ClusterSnapShot();
                snapShot.readFile(file);
                clusterSnapShots.add(snapShot);
                times.add(snapShot.getTime());
            }
        }
        // </editor-fold>
    }
    
    // TESTING
    public static void main(String [] args){
        File dir = new File("C:/Users/pmich/Documents/LangevinFolder/ClusterTestModel_SIMULATIONS/"
                + "ClusterTest1_SIM_FOLDER/data/Run0");
        SingleRun srss = new SingleRun();
        srss.loadAllSnapShots(dir);
        for(Double time : srss.getTimes()){
            System.out.println(time);
        }
    }
}
