package org.springsalad.dataprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class RawDataHolder {

    private final ArrayList<Double> times;
    private final ArrayList<String> runs;
    private HashMap<Integer, ArrayList<Integer>> runData;
    
    public RawDataHolder(File file){
        times = new ArrayList<>();
        runs = new ArrayList<>();
        readData(file);
    }
    
    /* **************** READ THE FILE ********************************/
    private void readData(File file){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        ArrayList<Integer> [] valueLists;
         try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // Skip the first line
            sc.nextLine();
            // Second line is special
            Scanner secondLine = new Scanner(sc.nextLine());
            secondLine.useDelimiter(",");
            // First entry is just "Time".  Skip it.
            secondLine.next();
            while(secondLine.hasNext()){
                String next = secondLine.next().trim();
                if(next.length()>2){
                    runs.add(next);
                }
            }
            secondLine.close();
            valueLists = new ArrayList[runs.size()];
            for(int i=0;i<valueLists.length;i++){
                valueLists[i] = new ArrayList<>();
            }
            // Now scan the rest of the lines
            while(sc.hasNextLine()){
                Scanner lineScanner = new Scanner(sc.nextLine());
                lineScanner.useDelimiter(",");
                times.add(Double.parseDouble(lineScanner.next().trim()));
                int counter = 0;
                while(counter < runs.size()){
                    double val = Double.parseDouble(lineScanner.next().trim());
                    valueLists[counter].add((int)Math.round(val));
                    counter++;
                }
                lineScanner.close();
            }
            // Now add all the arrays to the hashmap
            runData = new HashMap<>(4*runs.size());
            for(int i=0;i<runs.size();i++){
                runData.put(i, valueLists[i]);
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
    
    /* **************** GET METHODS **********************************/
    
    public ArrayList<Double> getTimes(){
        return times;
    }
    
    public ArrayList<Integer> getRunData(int i){
        return runData.get(i);
    }
    
    public ArrayList<String> getRunsHeaders(){
        return runs;
    }
    
    public int getRunNumber(){
        return runs.size();
    }
    
}
