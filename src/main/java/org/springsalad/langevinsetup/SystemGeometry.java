/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;


public class SystemGeometry {
    
    // Flags to indicate location relative to the membrane
    public static final String INSIDE = "Intracellular";
    public static final String OUTSIDE = "Extracellular";
    public static final String MEMBRANE = "Membrane";
    
    // Get all flags
    public static String [] getLocations(){
        return new String[]{INSIDE, MEMBRANE, OUTSIDE};
    }
    
}
