/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.PopUp;

public class BindingReactionTopPanel extends JPanel 
                                            implements ListSelectionListener{
    
    private final ArrayList<Molecule> molecules;
    private final BindingReaction reaction;
    
    private final DefaultListModel [] moleculeModel = new DefaultListModel[2];
    private final DefaultListModel [] typeModel = new DefaultListModel[2];
    private final DefaultListModel [] stateModel = new DefaultListModel[2];
    
    private final JList [] moleculeList = new JList[2];
    private final JList [] typeList = new JList[2];
    private final JList [] stateList = new JList[2];
    
    public BindingReactionTopPanel(Global g, BindingReaction reaction){
        this.reaction = reaction;
        this.molecules = g.getMolecules();
        
        Molecule [] mol = reaction.getMolecules();
        SiteType [] type = reaction.getTypes();
        State [] state = reaction.getStates();
        
        for(int i=0;i<2;i++){
            moleculeModel[i] = new DefaultListModel();
            typeModel[i] = new DefaultListModel();
            stateModel[i] = new DefaultListModel();
            
            moleculeList[i] = new JList(moleculeModel[i]);
            typeList[i] = new JList(typeModel[i]);
            stateList[i] = new JList(stateModel[i]);
            
            moleculeList[i].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            typeList[i].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            stateList[i].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            moleculeModel[i].clear();
            for(Molecule molecule : molecules){
                moleculeModel[i].addElement(molecule);
            }
            
            moleculeList[i].clearSelection();
            if(mol[i] != null){
                moleculeList[i].setSelectedValue(mol[i], true);
                updateTypeList(i);
                if(type[i] != null){
                    typeList[i].setSelectedValue(type[i], true);
                    updateStateList(i);
                    if(state[i] != null){
                        stateList[i].setSelectedValue(state[i], true);
                    }
                }
            }
            
            moleculeList[i].addListSelectionListener(this);
            typeList[i].addListSelectionListener(this);
            stateList[i].addListSelectionListener(this);
        }
        
        this.setLayout(new GridLayout(2,1));
        this.add(makePanel(0));
        this.add(makePanel(1));
       
    }
    
    /* *********** MAKE THE PANEL  ***********/
    
    private JPanel makePanel(int i){
        
        int listWidth = 150;
        int listHeight = 100;
        
        JLabel titleLabel;
        if(i==0){
            titleLabel = new JLabel("Select First Site", JLabel.CENTER);
        } else {
            titleLabel = new JLabel("Select Second Site", JLabel.CENTER);
        }
        titleLabel.setFont(Fonts.SUBTITLEFONT);
        
        JLabel moleculeLabel = new JLabel("Molecule", JLabel.CENTER);
        JLabel typeLabel = new JLabel("Site Type", JLabel.CENTER);
        JLabel stateLabel = new JLabel("State", JLabel.CENTER);
        
        JScrollPane moleculePane = new JScrollPane(moleculeList[i]);
        JScrollPane typePane = new JScrollPane(typeList[i]);
        JScrollPane statePane = new JScrollPane(stateList[i]);
        
        moleculePane.setPreferredSize(new Dimension(listWidth, listHeight));
        moleculePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        moleculePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        typePane.setPreferredSize(new Dimension(listWidth, listHeight));
        typePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        typePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        statePane.setPreferredSize(new Dimension(listWidth, listHeight));
        statePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        statePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JPanel moleculePanel = new JPanel();
        moleculePanel.setLayout(new FlowLayout());
        moleculePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        moleculePanel.add(moleculeLabel);
        moleculePanel.add(moleculePane);
        
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        typePanel.add(typeLabel);
        typePanel.add(typePane);
        
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new FlowLayout());
        statePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        statePanel.add(stateLabel);
        statePanel.add(statePane);
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1,3));
        listPanel.setPreferredSize(new Dimension(3*(listWidth + 15), listHeight+30));
        
        JPanel moleculeContainer = new JPanel();
        moleculeContainer.setLayout(new FlowLayout());
        moleculeContainer.add(moleculePanel);
        
        JPanel typeContainer = new JPanel();
        typeContainer.setLayout(new FlowLayout());
        typeContainer.add(typePanel);
        
        JPanel stateContainer = new JPanel();
        stateContainer.setLayout(new FlowLayout());
        stateContainer.add(statePanel);
        
        listPanel.add(moleculeContainer);
        listPanel.add(typeContainer);
        listPanel.add(stateContainer);
        
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(listPanel.getPreferredSize().width, listPanel.getPreferredSize().height + 20));
        p.setLayout(new BorderLayout());
        p.add(titleLabel, "North");
        p.add(listPanel, "Center");
        
        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new FlowLayout());
        returnPanel.add(p);
        
        return returnPanel;
    }
    
    /* ************** LIST MANAGEMENT ***************/
    
    private void updateTypeList(int i){
        typeModel[i].clear();
        Molecule molecule = (Molecule)moleculeList[i].getSelectedValue();
        if(molecule != null){
            for(SiteType type : molecule.getTypeArray()){
                typeModel[i].addElement(type);
            }
        }
    }
    
    private void updateStateList(int i){
        stateModel[i].clear();
        SiteType type = (SiteType)typeList[i].getSelectedValue();
        if(type != null){
            if(type.getName().equals(SiteType.ANCHOR)){
                PopUp.warning("Membrane anchors may not participate in binding reactions.");
                typeList[i].clearSelection();
            } else {
                // Add an option for any state
                stateModel[i].addElement(BindingReaction.ANY_STATE);
                for(State state : type.getStates()){
                    stateModel[i].addElement(state);
                }
            }
        }
    }
    
    /* *********** ASSIGN BINDING REACTION FIELDS **************/
    
    private void assignMolecule(int i){
        Molecule molecule = (Molecule)moleculeList[i].getSelectedValue();
        reaction.setMolecule(i, molecule);
    }
    
    private void assignType(int i){
        SiteType type = (SiteType)typeList[i].getSelectedValue();
        reaction.setType(i, type);
    }
    
    private void assignState(int i){
        State state = (State)stateList[i].getSelectedValue();
        reaction.setState(i, state);
    }
    
    /* ************ LIST SELCTION LISTENER METHOD *********************/

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList source = (JList)e.getSource();
        for(int i=0;i<2;i++){
            if(source == moleculeList[i]){
                assignMolecule(i);
                updateTypeList(i);
                break;
            } else if(source == typeList[i]){
                assignType(i);
                updateStateList(i);
                break;
            } else if(source == stateList[i]){
                assignState(i);
            }
        }
    }
    
}