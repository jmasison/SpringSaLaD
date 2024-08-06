/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

import java.util.ArrayList;

public class AllostericReaction extends Reaction {
    
    private String name;
    
    // Molecule and site undergoing transition
    private Molecule molecule;
    private Site site;
    private State initialState;
    private State finalState;
    
    // Reaction only proceed when allostericSite is in the right state
    private Site allostericSite;
    private State allostericState;
    
    // Reaction rate
    private double rate; // Units s-1
    
    /* ************** CONSTRUCTORS ***********************************/
    
    public AllostericReaction(){
        this.name = null;
        
        molecule = null;
        site = null;
        initialState = null;
        finalState = null;
        
        allostericSite = null;
        allostericState = null;
        
        rate = 0;
    }
    
    public AllostericReaction(String name){
        this.name = name;
        
        molecule = null;
        site = null;
        initialState = null;
        finalState = null;
        
        allostericSite = null;
        allostericState = null;
        
        rate = 0;
    }
    
    public AllostericReaction(String name, Molecule molecule, Site site){
        this.name = name;
        
        this.molecule = molecule;
        this.site = site;
        this.initialState = null;
        this.finalState = null;
        
        this.allostericSite = null;
        this.allostericState = null;
        
        rate = 0;
    }
    
    /* ************** GET AND SET NAME ******************************/
    
    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public void setName(String name){
        this.name = name;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    /* *********   GET AND SET THE MOLECULE *****************/
    
    public void setMolecule(Molecule molecule){
        this.molecule = molecule;
    }
    
    public Molecule getMolecule(){
        return molecule;
    }
    
    /* ********** GET AND SET THE SITE *********************/
    
    public void setSite(Site site){
        this.site = site;
    }
    
    public Site getSite(){
        return site;
    }
    
    /* ********* GET AND SET THE INITIAL AND FINAL STATES ***********/
    
    public void setInitialState(State state){
        initialState = state;
    }
    
    public void setFinalState(State state){
        finalState = state;
    }
    
    public State getInitialState(){
        return initialState;
    }
    
    public State getFinalState(){
        return finalState;
    }
    
    /* ********** GET AND SET THE ALLOSTERIC SITE AND STATE ***********/
    
    public void setAllostericSite(Site site){
        this.allostericSite = site;
    }
    
    public void setAllostericState(State state){
        this.allostericState = state;
    }
    
    public Site getAllostericSite(){
        return allostericSite;
    }
    
    public State getAllostericState(){
        return allostericState;
    }
    
    /* ************ GET AND SET THE REACTION RATE **********************/
    
    public void setRate(double rate){
        this.rate = rate;
    }
    
    public double getRate(){
        return rate;
    }
    
    /* ***************** PRINT A REACTION REPRESENTATION ******************/
    
    @Override
    public String writeReaction(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(name).append("' ::     ");
        if(molecule != null && site != null && initialState != null && finalState != null){
            sb.append("'").append(molecule.getName()).append("' : ");
            sb.append("Site ").append(site.getIndex()).append(" : '");
            sb.append(initialState.getName()).append("'");
            sb.append(" --> ");
            sb.append("'").append(finalState.getName()).append("' ");
            sb.append(" Rate ").append(Double.toString(rate));
            sb.append(" Allosteric_Site ").append(allostericSite.getIndex());
            sb.append(" State '").append(allostericState.getName()).append("'");
        }
        return sb.toString();
        // </editor-fold>
    }
    
    /* **************** LOAD SINGLE REACTION ****************************/
    
    @Override
    public void loadReaction(Global g, Scanner sc){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        name = IOHelp.getNameInQuotes(sc);
        sc.next();
        molecule = g.getMolecule(IOHelp.getNameInQuotes(sc));
        sc.next();
        sc.next();
        site = molecule.getSite(sc.nextInt());
        sc.next();
        SiteType type = site.getType();
        initialState = type.getState(IOHelp.getNameInQuotes(sc));
        sc.next();
        finalState = type.getState(IOHelp.getNameInQuotes(sc));
        sc.next();
        rate = sc.nextDouble();
        sc.next();
        allostericSite = molecule.getSite(sc.nextInt());
        sc.next();
        SiteType alloType = allostericSite.getType();
        allostericState = alloType.getState(IOHelp.getNameInQuotes(sc));
        // </editor-fold>
    }
    
    /* *************** LOAD FULL ARRAY ***********************************/
    
    public static ArrayList<AllostericReaction> loadReactions(Global g, Scanner sc){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<AllostericReaction> allostericReactions = new ArrayList<>();
        AllostericReaction reaction;
        while(sc.hasNextLine()){
            reaction= new AllostericReaction();
            reaction.loadReaction(g, new Scanner(sc.nextLine()));
            allostericReactions.add(reaction);
        }
        sc.close();
        return allostericReactions;
        // </editor-fold>
    }
    
}
