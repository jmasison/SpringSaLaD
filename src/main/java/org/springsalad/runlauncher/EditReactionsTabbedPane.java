/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import javax.swing.*;

import org.springsalad.langevinsetup.Global;

public class EditReactionsTabbedPane extends JTabbedPane {
    
    private final EditDecayReactionsTablePanel editDecayReactionsPanel;
    private final EditTransitionReactionsTablePanel editTransitionReactionsPanel;
    private final EditBindingReactionsTablePanel editBindingReactionsPanel;
    
    public EditReactionsTabbedPane(Global g, Simulation simulation){
        editDecayReactionsPanel = new EditDecayReactionsTablePanel(g, simulation);
        editTransitionReactionsPanel = new EditTransitionReactionsTablePanel(g, simulation);
        editBindingReactionsPanel = new EditBindingReactionsTablePanel(g, simulation);
        
        this.insertTab("Creation/Decay Reactions", null, 
                                        editDecayReactionsPanel, null, 0);
        this.insertTab("Transition Reactions", null,
                                    editTransitionReactionsPanel, null, 1);
        this.insertTab("Binding Reactions", null, 
                                    editBindingReactionsPanel, null, 2);
    }
}
