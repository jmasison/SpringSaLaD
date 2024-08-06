/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.dataprocessor;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class RunningTimesDataHolder {
    
    private final ArrayList<Integer> runNumbers;
    private final ArrayList<Double> times;
    private double average;
    private double stdev;
    
    public RunningTimesDataHolder(File file){
        runNumbers = new ArrayList<>();
        times = new ArrayList<>();
        readData(file);
    }
    
    private void readData(File file){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // Skip header
            sc.nextLine();
            // First line is special
            Scanner firstLine = new Scanner(sc.nextLine());
            firstLine.useDelimiter(",");
            runNumbers.add(Integer.parseInt(firstLine.next()));
            times.add(Double.parseDouble(firstLine.next()));
            average = Double.parseDouble(firstLine.next());
            stdev = Double.parseDouble(firstLine.next());
            firstLine.close();
            while(sc.hasNextLine()){
                Scanner lineScanner = new Scanner(sc.nextLine());
                lineScanner.useDelimiter(",");
                runNumbers.add(Integer.parseInt(lineScanner.next()));
                times.add(Double.parseDouble(lineScanner.next()));
                lineScanner.close();
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
                }catch(IOException bioe){
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
    
    /* ****************  JUST NEED SOME GET METHODS ***********************/
    public double getAverage(){
        return average;
    }
    
    public double getStDev(){
        return stdev;
    }
    
    public ArrayList<Integer> getRunNumbers(){
        return runNumbers;
    }
    
    public Integer getRunNumber(int i){
        return runNumbers.get(i);
    }
    
    public ArrayList<Double> getTimes(){
        return times;
    }
    
    public Double getTime(int i){
        return times.get(i);
    }
    
}
