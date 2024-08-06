/**
 *  This class stores all of the clusters at one instant in time. It can 
 *  parse the data in the cluster file to construct the clusters.
 */
package org.springsalad.dataprocessor.clusterdata;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

@Deprecated
public class ClusterSnapShot {
    
    private double time;
    private final ArrayList<Cluster> clusters; // Only clusters >= 2 molecules
    private int totalClusters;  // Includes "clusters" of size 1
    
    public ClusterSnapShot(){
        time = 0;
        clusters = new ArrayList<>();
        totalClusters = 0;
    }
    
    public double getTime(){
        return time;
    }
    
    public ArrayList<Cluster> getClusters(){
        return clusters;
    }
    
    public int getTotalClusters(){
        return totalClusters;
    }
    
    public int getTotalClustersSizeGreaterThan2(){
        return clusters.size();
    }
    
    public int getTotalUnboundMolecules(){
        return totalClusters - clusters.size();
    }
    
    public int getTotalClustersOfSize(int size){
        int total = 0;
        for(Cluster cluster : clusters){
            if(cluster.getTotalMolecules() == size){
                total++;
            }
        }
        return total;
    }
    
    public void readFile(File file){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // To get the time we need to parse the file name.  The file name is 
        // always Clusters_Time_xxx.csv.
        String fileName = file.getName();
        time = Double.parseDouble(fileName.substring(15, fileName.length()-4));
        
        FileReader fr = null;
        BufferedReader br = null;
        Scanner sc;
        Scanner fileScanner = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            fileScanner = new Scanner(br);
            // First line has the total number of clusters, written as "Total Clusters, xx"
            fileScanner.next(); fileScanner.next();
            this.totalClusters = Integer.parseInt(fileScanner.next().trim());
            // Now change the delimiter to "Cluster Index,"
            fileScanner.useDelimiter("Cluster Index,");
            fileScanner.next(); // Have to skip the two newline characters
            while(fileScanner.hasNext()){
                Cluster cluster = new Cluster();
                sc = new Scanner(fileScanner.next());
                // Skip the cluster index , which is meaningless
                sc.next();
                // Get the total size
                sc.next();
                cluster.setTotalMolecules(Integer.parseInt(sc.next().trim()));
                while(sc.hasNextLine()){
                    String [] pair = sc.nextLine().split(",");
                    if(pair.length == 2){
                        cluster.addName(pair[0]);
                        cluster.addNumberOfType(pair[0], Integer.parseInt(pair[1].trim()));
                    }
                }
                clusters.add(cluster);
                sc.close();
            }
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        } finally {
            if(fileScanner != null){
                fileScanner.close();
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
    
    // FOR TESTING  
    @Override
    public String toString(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder();
        sb.append("Time\t").append(this.time).append("\n");
        sb.append("Total Clusters\t").append(this.totalClusters).append("\n");
        sb.append("\n");
        for(Cluster cluster : clusters){
            sb.append(cluster.toString()).append("\n");
        }
        return sb.toString();
        // </editor-fold>
    }
    
    // FOR TESTING
    public static void main(String [] args){
        
        ClusterSnapShot snapShot = new ClusterSnapShot();
        File file = new File("C:/Users/pmich/Documents/LangevinFolder/ClusterTestModel_SIMULATIONS/ClusterTest1_SIM_FOLDER/"
                + "data/Run0/Clusters_Time_0.13.csv");
        snapShot.readFile(file);
        System.out.println(snapShot.toString());
    }
}
