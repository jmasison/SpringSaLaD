/**
 * It seems silly to have this class which does little more than store the 
 * name of the state (although it might do more later).  I originally just 
 * stored the states as strings inside the SiteType class, but strings are 
 * immutable and thus it was complicated to notify all of the relevant
 * classes about changes to the state name.  By defining this as a separate
 * class I can just treat state names as any other object. 
 */

package org.springsalad.langevinsetup;

public class State {
    
    private String name;
    
    // Give a reference to the site type
    private final SiteType type;
    
    // Each state has a state counter
    private final StateCounter stateCounter;
    
    /* ************** CONSTRUCTORS ***************************/
    
    public State(SiteType type, String name){
        this.name = name;
        this.type = type;
        stateCounter = new StateCounter(this);
    }
    
    /* ***********  GET AND SET THE STATE NAME *******************/
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    /* ************ GET THE SITE TYPE ******************************/
    
    public SiteType getType(){
        return type;
    }
    
    public String getTypeName(){
        return type.getName();
    }
    
    /* *********** GET THE MOLECULE ******************************/
    
    public Molecule getMolecule(){
        return type.getMolecule();
    }
    
    public String getMoleculeName(){
        return type.getMoleculeName();
    }
    
    /* ************ GET THE STATE COUNTER **************************/
    
    public StateCounter getStateCounter(){
        return stateCounter;
    }
    
}
