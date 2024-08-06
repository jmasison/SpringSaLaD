/**
 * The SiteType class holds information about the various site types.  
 * I wanted to call the class Type, but then this class would interfere with 
 * the Java superclass Type. 
 * Types have a name, a radius, a diffusion constant, and a color. They 
 * define the possible states for a site of this type. 
 *
 * They also store information on the reactions describing transitions between
 * the states of the SiteType. 
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.springsalad.helpersetup.Colors;
import org.springsalad.helpersetup.IOHelp;
import org.springsalad.helpersetup.NamedColor;

public class SiteType {

    private String typeName;
    private double radius; // radius in nm
    private double D;      // diffusion constant in um^2/s
    private NamedColor color;
    
    /*
     * Keep a reference to the molecule this type belongs to.
     */
    
    private final Molecule molecule;
    
    /** 
     * Types can have internal states. For example, a catalytic site type might
     * have two states, active and inactive.  Phosphorylation sites would have
     * two states, phospho and dephos. All types have at least one state. 
     * Here I define an arraylist which just stores the names of those 
     * states. The names could just be state1, state2, etc, or the user can 
     * define the states. 
     */
    
    private ArrayList<State> states = new ArrayList<>();

    
    /**
     *                  MEMBRANE ANCHOR DESIGNATION
     * The membrane-anchor is a special, built-in type used to indicate that
     * a site is permanently bound to the membrane and only undergoes 2D
     * diffusion in the membrane.  Also, these sites are forbidden from having 
     * any reactions (other than molecule-level creation/decay reactions). 
     * The site is otherwise treated as any other site.  For example, the user
     * could define several membrane anchor site for one molecule.  However,
     * there can only ever be one membrane anchor type for a given molecule. If
     * this were to be a limitation, I could relax this later.
     */
    
    public static String ANCHOR = "Anchor";
    
    /******************************************************************\
     *                         CONSTRUCTORS                           *
     *  @param typeName The name of this type.                        *
     *  @param molecule The molecule this site type belongs to        *
    \******************************************************************/
    
    public SiteType(Molecule molecule, String typeName){
        //Just give some defaults
        this.typeName = typeName;
        this.molecule = molecule;
        radius = 1;
        D = 1;
        color = Colors.RED;
        states.add(new State(this, "State0"));
    }
    
    /*/*****************************************************************\                                                            * 
     *          SET AND GET METHODS FOR PRIMARY VARIABLES              *
    \*******************************************************************/
    
    public void setName(String typeName){
        this.typeName = typeName;
    }
    
    public void setRadius(double r){
        radius = r;
    }
    
    public void setD(double D){
        this.D = D;
    }
    
    public void setColor(String colorName){
        color = Colors.getColorByName(colorName);
    }
    
    public void setColor(NamedColor color){
        this.color = color;
    }
    
    public String getName(){
        return typeName;
    }
    
    public double getRadius(){
        return radius;
    }
    
    public double getReactionRadius() {
    	// assumes radius in nanometers
    	// Either 1.5x radius or radius+2 nm, whichever is smaller, but not smaller than radius+0.5 nm
    	double minRadius = Math.max(0.5+radius,  1.5*radius);
    	double reactionRadius = Math.min(minRadius, radius+2);
    	return reactionRadius;
    }
    
    public double getD(){
        return D;
    }
    
    public Color getColor(){
        return color.getColor();
    }
    
    public String getColorName(){
        return color.getName();
    }
    
    @Override
    public String toString(){
        return typeName;
    }
    
    /* ********** GET THE MOLECULE ***************************************/
    
    public Molecule getMolecule(){
        return molecule;
    }
    
    public String getMoleculeName(){
        return molecule.getName();
    }
    
    /*/********************************************************************\
     *            SET AND GET METHODS RELATING TO STATES                  *
    \**********************************************************************/
    // Get all states
    public ArrayList<State> getStates(){
        return states;
    }
    // Get state by name
    public State getState(String name){
        State s = null;
        for (State state : states) {
            if (name.equals(state.getName())) {
                s = state;
                break;
            }
        }
        return s;
    }
    // Get state by index
    public State getState(int index){
        return states.get(index);
    }
    // Set the entire state array
    public void setStateArray(ArrayList<State> stateArray){
        states = stateArray;
    }
    // Add a single state
    public void addState(State state){
        states.add(state);
    }
    // Remove a single state
    public void removeState(State state){
        states.remove(state);
    }
    
    /* ******************************************************************\
     *                           FILE IO                                *
    \********************************************************************/
    
    public void writeType(PrintWriter p){
        p.print("TYPE: Name \"" + getName() + "\"");
        p.print(" Radius " + IOHelp.DF[5].format(getRadius()) + " D " + IOHelp.DF[3].format(getD()) + " Color " + getColorName());
        p.print(" STATES ");
        for (State state : states) {
            p.print("\"" + state + "\"" + " ");
        }
        p.println();
    }
    
    public static SiteType readType(Molecule mol, String s){
        SiteType tempType = new SiteType(mol, "TempName");
        Scanner sc = new Scanner(s);
        if(!sc.next().equals("TYPE:")){
            System.out.println("ERROR: Type line did not begin with \"TYPE:\"");
        }
        while(sc.hasNext()){
            String scnext = sc.next();
            switch(scnext){
                case "Name":{
                    tempType.setName(IOHelp.getNameInQuotes(sc));
                    break;
                }
                case "Radius":{
                    tempType.setRadius(Double.parseDouble(sc.next()));
                    break;
                }
                case "D":{
                    tempType.setD(Double.parseDouble(sc.next()));
                    break;
                }
                case "Color":{
                    tempType.setColor(sc.next());
                    break;
                }
                case "STATES":{
                    ArrayList<State> tempStates = new ArrayList<>();
                    while(sc.hasNext()){
                        tempStates.add(new State(tempType, IOHelp.getNameInQuotes(sc)));
                    }
                    tempType.setStateArray(tempStates);
                    break;
                }
                default:{
                    System.out.println("Type reader received unexpected input: " + scnext);
                }
            }
        }
        sc.close();
        return tempType;
    }
    
    
    
}
