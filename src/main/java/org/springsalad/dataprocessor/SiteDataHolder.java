
package org.springsalad.dataprocessor;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.*;

public class SiteDataHolder {
    
    private final DataProcessor processor;
    
    private final ArrayList<String> stateNames;
    private final ArrayList<String> bondNames;
    
    private final ArrayList<Double> times;
    private final ArrayList<Double> avFree;
    private final ArrayList<Double> stdevFree;
    private final ArrayList<Double> avBound;
    private final ArrayList<Double> stdevBound;
    
    private HashMap<String, ArrayList<Double>> averageStates;
    private HashMap<String, ArrayList<Double>> stdevStates;
    
    private HashMap<String, ArrayList<Double>> averageBonds;
    private HashMap<String, ArrayList<Double>> stdevBonds;
    
    public SiteDataHolder(DataProcessor processor){
        this.processor = processor;
        stateNames = new ArrayList<>();
        bondNames = new ArrayList<>();
        
        times = new ArrayList<>();
        avFree = new ArrayList<>();
        stdevFree = new ArrayList<>();
        avBound = new ArrayList<>();
        stdevBound = new ArrayList<>();

        
    }
    
    /* ***************  CLEAR DATA *************************************/
    private void clearData(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        stateNames.clear();
        bondNames.clear();
        times.clear();
        avFree.clear();
        stdevFree.clear();
        avBound.clear();
        stdevBound.clear();
        
        averageStates = null;
        stdevStates = null;
        averageBonds = null;
        stdevBonds = null;
        // </editor-fold>
    }
    
    /* **************  READ FILE ****************************************/
    public void setSiteData(String siteName){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        clearData();
        ArrayList<String> allBondNames = processor.getBondNames();
        File file = processor.getSiteFile(siteName);
        if(file == null){
            System.out.println("Site file was null for site name " + siteName);
        } else {
            BufferedReader br = null;
            FileReader fr = null;
            Scanner sc = null;
             try{
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                sc = new Scanner(br);
                // skip the first line
                sc.nextLine();
                // Use the second line to get the state and binding reaction names
                Scanner secondLine = new Scanner(sc.nextLine());
                secondLine.useDelimiter(",");
                // Skip "Time", "Free", and "Bound"
                secondLine.next();
                secondLine.next();
                secondLine.next();
                // Read in entries until we hit the blank
                while(secondLine.hasNext()){
                    String name = secondLine.next().trim();
                    if(name.length() == 0){
                        break;
                    } else {
                        boolean foundBond = false;
                        // See if we found a bond name
                        for(String bondName : allBondNames){
                            if(bondName.equals(name)){
                                bondNames.add(name);
                                foundBond = true;
                                break;
                            }
                        }
                        // If not, we found a state name
                        if(!foundBond){
                            stateNames.add(name);
                        }
                    }
                }
                secondLine.close();
                // Create arrays to hold the state and bond data
                ArrayList<Double> [] avStates = new ArrayList[stateNames.size()];
                ArrayList<Double> [] sdStates = new ArrayList[stateNames.size()];
                ArrayList<Double> [] avBonds = new ArrayList[bondNames.size()];
                ArrayList<Double> [] sdBonds = new ArrayList[bondNames.size()];
                for(int i =0;i<avStates.length;i++){
                    avStates[i] = new ArrayList<>();
                    sdStates[i] = new ArrayList<>();
                }
                for(int i=0;i<avBonds.length;i++){
                    avBonds[i] = new ArrayList<>();
                    sdBonds[i] = new ArrayList<>();
                }
                // Now read in the lines with data
                while(sc.hasNextLine()){
                    Scanner lineScanner = new Scanner(sc.nextLine());
                    lineScanner.useDelimiter(",");
                    // First entry is time
                    times.add(Double.parseDouble(lineScanner.next().trim()));
                    // Next entry is the free count
                    avFree.add(Double.parseDouble(lineScanner.next().trim()));
                    // Next entry is the bound count
                    avBound.add(Double.parseDouble(lineScanner.next().trim()));
                    // Now read in the states and bonds
                    for(int i=0;i<avStates.length;i++){
                        avStates[i].add(Double.parseDouble(lineScanner.next().trim()));
                    }
                    for(int i=0;i<avBonds.length;i++){
                        avBonds[i].add(Double.parseDouble(lineScanner.next().trim()));
                    }
                    // Skip the blank
                    lineScanner.next();
                    // Next entry is sdFree
                    stdevFree.add(Double.parseDouble(lineScanner.next().trim()));
                    // Next entry is sdBound
                    stdevBound.add(Double.parseDouble(lineScanner.next().trim()));
                    // Finish with states and bonds
                    for(int i=0;i<sdStates.length;i++){
                        sdStates[i].add(Double.parseDouble(lineScanner.next().trim()));
                    }
                    for(int i=0;i<sdBonds.length;i++){
                        sdBonds[i].add(Double.parseDouble(lineScanner.next().trim()));
                    }
                    lineScanner.close();
                }
                
                averageStates = new HashMap<>(5*stateNames.size());
                stdevStates = new HashMap<>(5*stateNames.size());
                averageBonds = new HashMap<>(5*bondNames.size());
                stdevBonds = new HashMap<>(5*bondNames.size());
                
                for(int i=0;i<stateNames.size();i++){
                    averageStates.put(stateNames.get(i), avStates[i]);
                    stdevStates.put(stateNames.get(i), sdStates[i]);
                }
                
                for(int i=0;i<bondNames.size();i++){
                    averageBonds.put(bondNames.get(i), avBonds[i]);
                    stdevBonds.put(bondNames.get(i), sdBonds[i]);
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
    
    /* *******************  GET METHODS ********************************/
    
    public ArrayList<Double> getTimes(){
        return times;
    }
    
    public double getTime(int i){
        return times.get(i);
    }
    
    public ArrayList<Double> getAverageFree(){
        return avFree;
    }
    
    public ArrayList<Double> getStDevFree(){
        return stdevFree;
    }
    
    public ArrayList<Double> getAverageBound(){
        return avBound;
    }
    
    public ArrayList<Double> getStDevBound(){
        return stdevBound;
    }
    
    public ArrayList<String> getStateNames(){
        return stateNames;
    }
    
    public String getStateName(int i){
        return stateNames.get(i);
    } 
               
    public ArrayList<String> getBondNames(){
        return bondNames;
    }
    
    public String getBondName(int i){
        return bondNames.get(i);
    }
    
    public ArrayList<Double> getAverageStateData(String key){
        return averageStates.get(key);
    }
    
    public ArrayList<Double> getStDevStateData(String key){
        return stdevStates.get(key);
    }
    
    public ArrayList<Double> getAverageBondData(String key){
        return averageBonds.get(key);
    }
    
    public ArrayList<Double> getStDevBondData(String key){
        return stdevBonds.get(key);
    }
}
