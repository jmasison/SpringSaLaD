/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.io.*;
import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

public class MoleculeCounter {
    
    private final Molecule molecule;
    
    public static final String FREE = "Free";
    public static final String BOUND = "Bound";
    public static final String TOTAL = "Total";
    public static final String NONE = "None";
    
    private boolean countFree = true;
    private boolean countBound = true;
    private boolean countTotal = true;
    
    public MoleculeCounter(Molecule molecule){
        this.molecule = molecule;
    }
    
    // SET METHODS
    
    public void setMeasurement(String type, boolean bool){
        switch(type){
            case FREE:{
                countFree = bool;
                break;
            }
            case BOUND:{
                countBound = bool;
                break;
            }
            case TOTAL:{
                countTotal = bool;
                break;
            }
            case NONE:{
                countFree = false;
                countBound = false;
                countTotal = false;
                break;
            }
            default:{
                System.out.println("CountData setMeasurement() received the following unexpected input: " + type);
            }
        }
    }
    
    // GET METHODS
    
    public boolean countTotal(){
        return countTotal;
    }
    public boolean countFree(){
        return countFree;
    }
    public boolean countBound(){
        return countBound;
    }
    
    public String getMoleculeName(){
        return molecule.getName();
    }
    
    /* ************** WRITE COUNTER ******************************/
    
    public void writeMoleculeCounter(PrintWriter p){
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(molecule.getName()).append("'");
        sb.append(" : Measure ");
        if(!countBound && !countFree && !countTotal){
            sb.append(NONE).append(" ");
        } else {
            if(countTotal){
                sb.append(TOTAL).append(" ");
            }
            if(countFree){
                sb.append(FREE).append(" ");
            }
            if(countBound){
                sb.append(BOUND).append(" ");
            }
        }
        p.println(sb.toString());
    }
     
    /* ************ SET SINGLE COUNTER **************************/
    
    public void loadCounter(Scanner dataScanner){
        // Skip the word "measure"
        dataScanner.next();
        while(dataScanner.hasNext()){
            this.setMeasurement(dataScanner.next(), true);
        }
        dataScanner.close();
    }
    
    /* *********** LOAD ALL MOLECULE COUNTERS ********************/
    
    public static void loadCounters(Global g, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            String [] next = dataScanner.nextLine().split(":");
            Molecule mol = g.getMolecule(IOHelp.getNameInQuotes(new Scanner(next[0])));
            MoleculeCounter counter = mol.getMoleculeCounter();
            counter.loadCounter(new Scanner(next[1].trim()));
        }
        dataScanner.close();
    }
    
}
