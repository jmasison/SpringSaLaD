/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;

public class MoleculeSelectionEvent {
    
    // Just pass arrays of the currently selected sites and links
    private final ArrayList<Site> selectedSites;
    private final ArrayList<Link> selectedLinks;
    
    public MoleculeSelectionEvent(ArrayList<Site> selectedSites, ArrayList<Link> selectedLinks){
        this.selectedSites = selectedSites;
        this.selectedLinks = selectedLinks;
    }
    
    public ArrayList<Site> getSelectedSites(){
        return selectedSites;
    }
    
    public ArrayList<Link> getSelectedLinks(){
        return selectedLinks;
    }
}
