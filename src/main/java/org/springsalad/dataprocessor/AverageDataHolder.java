/**
 * The data processor class makes the files, but I don't want it to actually 
 * keep references to all of the data.  This class will read in one of the 
 * time point average files created by the data processor, and will store the
 * data in a hash map. The keys to the map will be the names of the columns.
 */

package org.springsalad.dataprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class AverageDataHolder {

    private final ArrayList<String> names;
    private final ArrayList<Double> times;
    private final HashMap<String, ArrayList<Double>> averages;
    private final HashMap<String, ArrayList<Double>> stdevs;
    
    /* ******************* CONSTRUCTOR ********************************/
    public AverageDataHolder(ArrayList<String> nameArray, File file){
        names = new ArrayList<>();
        for(String name : nameArray){
            names.add(name);
        }
        times = new ArrayList<>();
        averages = new HashMap<>(5*nameArray.size());
        stdevs = new HashMap<>(5*nameArray.size());
        readData(file);
    }
    
    /* ******** READ THE DATA FILE TO FILL THE ARRAYS AND MAPS ********/
    private void readData(File file){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        ArrayList<Double> [] avLists = new ArrayList[names.size()];
        ArrayList<Double> [] stdevLists = new ArrayList[names.size()];
        for(int i=0;i<names.size();i++){
            avLists[i] = new ArrayList<>();
            stdevLists[i] = new ArrayList<>();
        }
        
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // skip first and second lines, which just contain headers
            sc.nextLine();
            sc.nextLine();
            while(sc.hasNextLine()){
                Scanner lineScanner = new Scanner(sc.nextLine());
                lineScanner.useDelimiter(",");
                // First entry is the time.
                times.add(Double.parseDouble(lineScanner.next().trim()));
                int counter = 0;
                while(counter < names.size()){
                    avLists[counter].add(Double.parseDouble(lineScanner.next().trim()));
                    counter++;
                }
                // Skip the blank space, then start again for the stdevs
                lineScanner.next();
                counter = 0;
                while(counter < names.size()){
                    stdevLists[counter].add(Double.parseDouble(lineScanner.next().trim()));
                    counter++;
                }
                lineScanner.close();
            }
            // Now that we've made the arrays, let's add them to the hashmaps
            for(int i=0;i<names.size();i++){
                String key = names.get(i);
                averages.put(key, avLists[i]);
                stdevs.put(key, stdevLists[i]);
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
    
    /* **************** GET TIMES AND NAMES ***************************/
    public ArrayList<Double> getTimes(){
        return times;
    }
    
    public ArrayList<String> getNames(){
        return names;
    }
    
    /* *************** GET AVERAGES AND STDEVS *************************/
    
    public ArrayList<Double> getAverage(String name){
        return averages.get(name);
    }
    
    public ArrayList<Double> getStDevs(String name){
        return stdevs.get(name);
    }
    
}
