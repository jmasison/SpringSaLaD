/**
 *  Helps to define a Cluster class to organize the data in a cluster.  The 
 *  data is simply 
 *  1) cluster size (total molecules)
 *  2) names of molecules
 *  3) hashmap between names of molecules and number of that molecule in the cluster
 */
package org.springsalad.dataprocessor.clusterdata;

import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public class Cluster {

    private final ArrayList<String> names;
    private final HashMap<String, Integer> numberOfType;
    private int totalMolecules;
    
    public Cluster(){
        names = new ArrayList<>();
        numberOfType = new HashMap<>();
        totalMolecules = 0;
    }
    
    public void setTotalMolecules(int total){
        this.totalMolecules = total;
    }
    
    public int getTotalMolecules(){
        return totalMolecules;
    }
    
    public void addName(String name){
        names.add(name);
    }
    
    public ArrayList<String> getNames(){
        return names;
    }
    
    public void addNumberOfType(String name, int value){
        numberOfType.put(name, value);
    }
    
    public int getNumberOfType(String name){
        return numberOfType.get(name);
    }
    
    // FOR TESTING
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Size\t").append(totalMolecules).append("\n");
        for(String name : names){
            sb.append(name).append("\t").append(numberOfType.get(name)).append("\n");
        }
        return sb.toString();
    }
}
