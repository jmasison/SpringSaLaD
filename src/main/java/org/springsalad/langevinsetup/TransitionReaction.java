/*
 * All transition reactions are one-way. If the reaction is actually 
 * reversible, then a second transition reaction must be defined. 
 */

package org.springsalad.langevinsetup;

import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

import java.util.ArrayList;

public class TransitionReaction extends Reaction {
    
    // The name of the reaction
    private String name;
    
    // The molecule, site type, intial state, and final state
    private Molecule molecule;
    private SiteType type;
    private State initialState;
    private State finalState;
    
    // The reaction might depend on binding to another site.  I call this the
    // "conditional" site (and molecule, etc.).
    private Molecule conditionalMolecule;
    private SiteType conditionalType;
    private State conditionalState;
    
    // The conditional state could be any state of the conditional type
    public final static String ANY_STATE_STRING = "Any_State";
    public final static State ANY_STATE = new State(null, ANY_STATE_STRING);
    
    // There might be no conditions on the reaction, or maybe it must be free, etc.
    public final static String NO_CONDITION = "None";
    public final static String FREE_CONDITION = "Free";
    public final static String BOUND_CONDITION = "Bound";
    
    // The condition on this reaction
    private String condition; 
    
    // Each transition reaction has a single rate
    private double rate;  // Units s-1
    
    /* ************ CONSTRUCTORS ******************/
    
    public TransitionReaction(){
        name = "New Transition Reaction";
        
        molecule = null;
        type = null;
        initialState = null;
        finalState = null;
        
        conditionalMolecule = null;
        conditionalType = null;
        conditionalState = null;
        
        condition = NO_CONDITION;
        rate = 0;
    }
    
    public TransitionReaction(String name){
        this.name = name;
        
        molecule = null;
        type = null;
        initialState = null;
        finalState = null;
        
        conditionalMolecule = null;
        conditionalType = null;
        conditionalState = null;
        
        condition = NO_CONDITION;
        rate = 0;
    }
    
    public TransitionReaction(String name, Molecule molecule, SiteType type){
        this.name = name;
        
        this.molecule = molecule;
        this.type = type;
        initialState = null;
        finalState = null;
        
        conditionalMolecule = null;
        conditionalType = null;
        conditionalState = null;
        
        condition = NO_CONDITION;
        rate = 0;
    }
    
    /* ********* GET AND SET THE REACTION NAME ***************/
    
    @Override
    public void setName(String name){
        this.name = name;
    }
    
    @Override
    public String getName(){
        return name;
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
    
    /* ********** GET AND SET THE SITE TYPE *********************/
    
    public void setType(SiteType type){
        this.type = type;
    }
    
    public SiteType getType(){
        return type;
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
    
    /* ******** GET AND SET THE CONDITIONALS ************************/
    
    public void setConditionalMolecule(Molecule molecule){
        conditionalMolecule = molecule;
    }
    
    public void setConditionalType(SiteType type){
        conditionalType = type;
    }
    
    public void setConditionalState(State state){
        conditionalState = state;
    }
    
    public Molecule getConditionalMolecule(){
        return conditionalMolecule;
    }
    
    public SiteType getConditionalType(){
        return conditionalType;
    }
    
    public State getConditionalState(){
        return conditionalState;
    }
    
    /* ********* GET AND SET THE REACTION CONDITION *********************/
    
    public void setCondition(String condition){
        this.condition = condition;
    }
    
    public String getCondition(){
        return condition;
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
        if(molecule != null && type != null && initialState != null && finalState != null){
            sb.append("'").append(molecule.getName()).append("' : '");
            sb.append(type.getName()).append("' : '");
            sb.append(initialState.getName()).append("'");
            sb.append(" --> ");
            sb.append("'").append(finalState.getName()).append("' ");
            sb.append(" Rate ").append(Double.toString(rate)).append(" ");
            sb.append(" Condition ").append(condition);
            if(condition.equals(BOUND_CONDITION)){
                sb.append(" '").append(conditionalMolecule.getName()).append("' : '");
                sb.append(conditionalType.getName()).append("' : '");
                sb.append(conditionalState.getName()).append("'");
            }
        }
        return sb.toString();
        // </editor-fold>
    }
    
    /* **************** LOAD SINGLE REACTION ****************************/
    
    @Override
    public void loadReaction(Global g, Scanner dataScanner){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        name = IOHelp.getNameInQuotes(dataScanner);
        dataScanner.next();
        molecule = g.getMolecule(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        type = molecule.getType(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        initialState = type.getState(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        finalState = type.getState(IOHelp.getNameInQuotes(dataScanner));
        dataScanner.next();
        rate = dataScanner.nextDouble();
        dataScanner.next();
        condition = dataScanner.next();
        if(!condition.equals(BOUND_CONDITION)){
            conditionalMolecule = null;
            conditionalType = null;
            conditionalState = null;
        } else {
            conditionalMolecule = g.getMolecule(IOHelp.getNameInQuotes(dataScanner));
            dataScanner.next();
            conditionalType = conditionalMolecule.getType(IOHelp.getNameInQuotes(dataScanner));
            dataScanner.next();
            String condState = IOHelp.getNameInQuotes(dataScanner);
            if(condState.equals(TransitionReaction.ANY_STATE_STRING)){
                conditionalState = TransitionReaction.ANY_STATE;
            } else {
                conditionalState = conditionalType.getState(condState);
            }
        }
        // </editor-fold>
    }
    
    /* *************** LOAD FULL ARRAY *********************************/
    
    public static ArrayList<TransitionReaction> loadReactions(Global g, Scanner sc){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<TransitionReaction> transitionReactions = new ArrayList<>();
        TransitionReaction reaction;
        while(sc.hasNextLine()){
            reaction = new TransitionReaction();
            reaction.loadReaction(g, new Scanner(sc.nextLine()));
            transitionReactions.add(reaction);
        }
        sc.close();
        return transitionReactions;
        // </editor-fold>
    }
}
