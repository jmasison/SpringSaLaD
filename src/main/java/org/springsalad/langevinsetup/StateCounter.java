/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.io.*;
import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

public class StateCounter {

    private final State state;
    
    public static final String FREE = "Free";
    public static final String BOUND = "Bound";
    public static final String TOTAL = "Total";
    public static final String NONE = "None";
    
    private boolean countFree = true;
    private boolean countBound = true;
    private boolean countTotal = true;
    
    public StateCounter(State state){
        this.state = state;
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
    
    public String getStateName(){
        return state.getName();
    }
    
    /* ********  WRITE STATE COUNTER ***********************************/

    public void writeStateCounter(PrintWriter p){
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(state.getMoleculeName()).append("' : '");
        sb.append(state.getTypeName()).append("' : '");
        sb.append(state.getName()).append("'");
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
    
    /* ******** LOAD SINGLE STATE COUNTER ******************************/
    
    public void loadCounter(Scanner sc){
        sc.next();
        while(sc.hasNext()){
            this.setMeasurement(sc.next(), true);
        }
    }
    
    /* ******* LOAD ALL STATE COUNTERS **********************************/
    
    public static void loadCounters(Global g, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            Scanner sc = new Scanner(dataScanner.nextLine());
            Molecule mol = g.getMolecule(IOHelp.getNameInQuotes(sc));
            sc.next();
            SiteType type = mol.getType(IOHelp.getNameInQuotes(sc));
            sc.next();
            State state = type.getState(IOHelp.getNameInQuotes(sc));
            sc.next();
            state.getStateCounter().loadCounter(sc);
            sc.close();
        }
        dataScanner.close();
    }

}
