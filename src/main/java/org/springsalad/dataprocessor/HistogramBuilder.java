
package org.springsalad.dataprocessor;

import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class HistogramBuilder {
    
    /* ***************  RAW DATA FILE ********************************/
    private File rawDataFile = null;
    
    /* **************** HISTOGRAM PARAMETERS **************************/
    private int minimum;
    private int maximum;
    private int actualMinimum;
    private int actualMaximum;
    private int binSize;
    private int totalBins;
    
    private boolean autoMaximum = true;
    private boolean autoMinimum = true;
    private boolean autoBinSize = true;
    
    /* ********** RELEVANT LINES OF THE RAW DATA FILE *****************/
    // I only use this so I can just follow the createHistogram method from
    // the dataProcessor class.
    private final ArrayList<String> lines;
    
    /* ****************   HISTOGRAM RESULTS ***************************/
    // A map between an integer (the bin number) and an
    // array list of the results for different times. 
    private HashMap<Integer, ArrayList<Integer>> histogramResults;
    
    /* ****************  ARRAYLIST OF THE TIMES ***********************/
    private final ArrayList<Double> times;
    
    /* ******************* CONSTRUCTOR *******************************/
    public HistogramBuilder(){
        binSize = 1;
        lines = new ArrayList<>();
        times = new ArrayList<>();
    }
    
    /* ***********  GET AND SET HISTOGRAM PARAMETERS *****************/
    
    public void setMinimum(int minimum){
        this.minimum = minimum;
    }
    
    public int getMinimum(){
        return minimum;
    }
    
    public void setMaximum(int maximum){
        this.maximum = maximum;
    }
    
    public int getMaximum(){
        return maximum;
    }
    
    public void setToActualMaximum(){
        maximum = actualMaximum;
    }
    
    public void setToActualMinimum(){
        minimum = actualMinimum;
    }
    
    public void setBinSize(int binSize){
        this.binSize = binSize;
    }
    
    public int getBinSize(){
        return binSize;
    }
    
    public int getTotalBins(){
        return totalBins;
    }
    
    /* ************** GET AND SET AUTO FLAGS *****************************/
    
    public void setAutoMinimum(boolean bool){
        autoMinimum = bool;
        if(bool){
            minimum = actualMinimum;
        }
    }
    
    public boolean getAutoMinimum(){
        return autoMinimum;
    }
    
    public void setAutoMaximum(boolean bool){
        autoMaximum = bool;
        if(bool){
            maximum = actualMaximum;
        }
    }
    
    public boolean getAutoMaximum(){
        return autoMaximum;
    }
    
    public void setAutoBinSize(boolean bool){
        autoBinSize = bool;
        if(bool){
            binSize = 1;
        }
    }
    
    public boolean getAutoBinSize(){
        return autoBinSize;
    }
    
    /* ************** SET AND GET RAW DATA FILE **************************/
    public void setRawDataFile(File file){
        rawDataFile = file;
        setFileLines();
        determineActualMaxAndMin();
        setToActualMaximum();
        setToActualMinimum();
    }
    
    public File getRawDataFile(){
        return rawDataFile;
    }
    
    /* **************  SET THE RAW DATA *********************************/
    
    private void setFileLines(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        lines.clear();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(rawDataFile);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // Skip the first line, just has species name
            sc.nextLine();
            // Skip next line, just has run numberr
            sc.nextLine();
            while(sc.hasNextLine()){
                lines.add(sc.nextLine());
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
    
    /* ******** AUTOMATICALLY DETERMINE THE MAXIMUM NUMBER **************/
    
    private void determineActualMaxAndMin(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        int maxValue = 0;
        int minValue = 1000000;
        for(String line : lines){
            Scanner sc = new Scanner(line);
            sc.useDelimiter(",");
            // Skip the time
            sc.next();
            while(sc.hasNext()){
                String s = sc.next();
                if(!s.equals(" ")){
                    int value = (int)Math.round(Double.parseDouble(s));
                    if(value > maxValue){
                        maxValue = value;
                    }
                    if(value < minValue){
                        minValue = value;
                    }
                }
            }
            sc.close();
            actualMaximum = maxValue;
            actualMinimum = minValue;
        }
        // </editor-fold>
    }
   
    /* ************  CONSTRUCT HISTOGRAM ******************************/
    
    public void constructHistogram(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        totalBins = (maximum-minimum)/binSize + 1;
        
        times.clear();
        histogramResults = new HashMap<>(5*totalBins);
        ArrayList<Integer> [] lists = new ArrayList[totalBins];
        for(int i=0;i<lists.length;i++){
            lists[i] = new ArrayList<>();
        }
        
        int [][] counts = new int[lines.size()][totalBins];
        for (int[] count : counts) {
            for (int j = 0; j<count.length; j++) {
                count[j] = 0;
            }
        }
 
        for(int i=0;i<lines.size();i++){
            String line = lines.get(i);
            Scanner sc = new Scanner(line);
            sc.useDelimiter(",");
            times.add(sc.nextDouble());
            while(sc.hasNext()){
                String s = sc.next();
                if(!s.equals(" ")){
                    int value = (int)Math.round(Double.parseDouble(s));
                    int binNumber = (value - minimum)/binSize;
                    if(binNumber >= 0 && binNumber < totalBins){
                        counts[i][binNumber]++;
                    }
                }
            }
            sc.close();
        }
        
        for (int[] count : counts) {
            for (int j = 0; j<count.length; j++) {
                lists[j].add(count[j]);
            }
        }
        
        for(int i=0;i<totalBins;i++){
            histogramResults.put(i, lists[i]);
        }
        // </editor-fold>
    }
    
    /* *****************  GET TIMES ************************************/
    public ArrayList<Double> getTimes(){
        return times;
    }
    
    /* *****************   GET BIN NAME ********************************/
    
    public String binName(int i){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        if(i < totalBins){
            if(binSize == 1){
                return Integer.toString(minimum + binSize*i);
            } else {
                int lowerBound = minimum + binSize*i;
                int upperBound = lowerBound + binSize - 1;
                return lowerBound + " - " + upperBound;
            }
        } else {
            return "Out of bounds.";
        }
        // </editor-fold>
    }
    
    /* ***************** GET THE HISTOGRAM RESULT **********************/
    
    public int getHistogramResult(int bin, int time){
        return histogramResults.get(bin).get(time);
    }
    
}
