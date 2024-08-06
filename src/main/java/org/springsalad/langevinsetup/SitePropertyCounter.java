/*
 * This data class keeps track of the properties of a particular site in a 
 * given molecule.  This will let us distinguish between two sites with the 
 * same site type but that may have different statistics because of their
 * location within the molecule.  
 *
 * At this point we'll keep track of the state of the site, whether the 
 * site is bond, and the type and state of its binding partner. 
 */

package org.springsalad.langevinsetup;

import java.io.*;
import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

public class SitePropertyCounter {
    
    /* ********  The site whose properties we're measuring  *********/
    private final Site site;
    
    private boolean trackData;
    
    public SitePropertyCounter(Site site){
        this.site = site;
        trackData = true;
    }
    
    /* ********  Set and get trackData *************/
    
    public boolean isTracked(){
        return trackData;
    }
    
    public void setTracked(boolean bool){
        trackData = bool;
    }
    
    /* ************** WRITE PROPERTY COUNTER **********************/
    public void writeSitePropertyCounter(PrintWriter p){
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(site.getMolecule().getName()).append("' Site ").append(site.getIndex());
        sb.append(" :  Track Properties ").append(trackData);
        p.println(sb.toString());
    }
    
    /* ***************  LOAD ALL COUNTERS *****************/
    // Since there is only a single data field to read in, it doesn't make 
    // sense to define a method to load a single counter.
    public static void loadCounters(Global g, Scanner dataScanner){
        while(dataScanner.hasNextLine()){
            Scanner sc = new Scanner(dataScanner.nextLine());
            Molecule molecule = g.getMolecule(IOHelp.getNameInQuotes(sc));
            // Skip "Site"
            sc.next();
            int index = sc.nextInt();
            Site mSite = molecule.getSite(index);
            SitePropertyCounter propertyCounter = mSite.getPropertyCounter();
            // Skip ":"
            sc.next();
            // Skip "Track"
            sc.next();
            // Skip "Properties"
            sc.next();
            // Read in the boolean
            propertyCounter.setTracked(sc.nextBoolean());
            sc.close();
        }
        dataScanner.close();
    }
    
}
