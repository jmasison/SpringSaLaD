/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

public class DecayReaction extends Reaction {

    private final Molecule molecule;
    
    private double kcreate; // Units uM/s
    private double kdecay;  // Units 1/s
    
    public DecayReaction(Molecule molecule){
        this.molecule = molecule;
        kcreate = 0;
        kdecay = 0;
    }
    
    public void setDecayRate(double rate){
        kdecay = rate;
    }
    
    public void setCreationRate(double rate){
        kcreate = rate;
    }
    
    public double getDecayRate(){
        return kdecay;
    }
    
    public double getCreationRate(){
        return kcreate;
    }
    
    @Override
    public String getName(){
        return molecule.getName();
    }
    
    @Override
    public String toString(){
        return molecule.getName();
    }
    
    // Only need this because we extend Reaction class.  Does nothing here.
    @Override
    public void setName(String name){}
    
    /* ****************      FILE IO  ******************************/

    @Override
    public String writeReaction(){
        return "'" + this.getName() + "' : kcreate " + getCreationRate()
                                                + " kdecay " + getDecayRate();
    }
    
    @Override
    public void loadReaction(Global g, Scanner sc){
        // Skip kcreate
        sc.next();
        kcreate = sc.nextDouble();
        sc.next();
        kdecay = sc.nextDouble();
        sc.close();
    }
    
    public static void loadReactions(Global g, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            String []  nextLine = dataScanner.nextLine().split(":");
            Molecule molecule = g.getMolecule(IOHelp.getNameInQuotes(new Scanner(nextLine[0])));
            DecayReaction reaction = molecule.getDecayReaction();
            reaction.loadReaction(g, new Scanner(nextLine[1].trim()));
        }
        dataScanner.close();
    }
}
