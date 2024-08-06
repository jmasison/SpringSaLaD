/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.PopUp;

public class TransitionReactionTopPanel extends JPanel 
                                implements ListSelectionListener, ItemListener {
    
    private final ArrayList<Molecule> molecules;
    private final TransitionReaction reaction;
    
    private final DefaultListModel moleculeModel = new DefaultListModel();
    private final DefaultListModel typeModel = new DefaultListModel();
    private final DefaultListModel stateModel = new DefaultListModel();
    
    private final DefaultListModel conditionalTypeModel = new DefaultListModel();
    private final DefaultListModel conditionalStateModel = new DefaultListModel();
    
    private final JList moleculeList;
    private final JList typeList;
    private final JList [] stateList = new JList[2];
    
    private final JList conditionalMoleculeList;
    private final JList conditionalTypeList;
    private final JList conditionalStateList;
    
    private final DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
    private final JComboBox conditionalBox;
    
    public TransitionReactionTopPanel(Global g, TransitionReaction reaction){
        this.reaction = reaction;
        this.molecules = g.getMolecules();
        
        moleculeList = new JList(moleculeModel);
        moleculeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moleculeList.addListSelectionListener(this);
        
        typeList = new JList(typeModel);
        typeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        typeList.addListSelectionListener(this);
        
        for(int i=0;i<2;i++){
            stateList[i] = new JList(stateModel);
            stateList[i].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            stateList[i].addListSelectionListener(this);
        }
        
        conditionalMoleculeList = new JList(moleculeModel);
        conditionalMoleculeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conditionalMoleculeList.addListSelectionListener(this);
        
        conditionalTypeList = new JList(conditionalTypeModel);
        conditionalTypeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conditionalTypeList.addListSelectionListener(this);
        
        conditionalStateList = new JList(conditionalStateModel);
        conditionalStateList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conditionalStateList.addListSelectionListener(this);
        
        moleculeModel.clear();
        for(Molecule molecule: molecules){
            moleculeModel.addElement(molecule);
        }
        
        boxModel.addElement(TransitionReaction.NO_CONDITION);
        boxModel.addElement(TransitionReaction.FREE_CONDITION);
        boxModel.addElement(TransitionReaction.BOUND_CONDITION);
        
        conditionalBox = new JComboBox(boxModel);
        conditionalBox.setSelectedIndex(0);
        conditionalBox.addItemListener(this);
        
        initialSetUp();
        
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2,1));
        p.add(makeReactionPanel());
        p.add(makeConditionalPanel());
        
        this.setLayout(new FlowLayout());
        this.add(p);
        
        
    }
    
    /* ****************** INITIAL SETUP *****************************/
    
    private void initialSetUp(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Molecule molecule = reaction.getMolecule();
        if(molecule != null){
            moleculeList.setSelectedValue(molecule, true);
            updateTypeList();
            SiteType type = reaction.getType();
            if(type != null){
                typeList.setSelectedValue(type, true);
                updateStateLists();
                State initialState = reaction.getInitialState();
                State finalState = reaction.getFinalState();
                if(initialState != null){
                    stateList[0].setSelectedValue(initialState, true);
                }
                if(finalState != null){
                    stateList[1].setSelectedValue(finalState, true);
                }
            }
        }
        
        if(reaction.getCondition() != null){
            String condition = reaction.getCondition();
            conditionalBox.setSelectedItem(condition);
            if(!condition.equals(TransitionReaction.BOUND_CONDITION)){
                enableConditionalLists(false);
            } else {
                enableConditionalLists(true);
                Molecule condMol = (Molecule)reaction.getConditionalMolecule();
                if(condMol != null){
                    conditionalMoleculeList.setSelectedValue(condMol, true);
                    updateConditionalTypeList();
                    SiteType condType = (SiteType)reaction.getConditionalType();
                    if(condType != null){
                        conditionalTypeList.setSelectedValue(condType, true);
                        updateConditionalStateList();
                        State condState = (State)reaction.getConditionalState();
                        if(condState != null){
                            conditionalStateList.setSelectedValue(condState, true);
                        }
                    }
                }
            }
        }
        // </editor-fold>
    }
    
    /* **************** LIST MANAGEMENT **********************/
    
    private void updateTypeList(){
        typeModel.clear();
        Molecule molecule = (Molecule)moleculeList.getSelectedValue();
        if(molecule != null){
            for(SiteType type : molecule.getTypeArray()){
                typeModel.addElement(type);
            }
        }
    }
    
    private void updateStateLists(){
        stateModel.clear();
        SiteType type = (SiteType)typeList.getSelectedValue();
        if(type != null){
            if(type.getName().equals(SiteType.ANCHOR)){
                PopUp.warning("Membrane anchors may not participate in reactions.");
                typeList.clearSelection();
            } else {
                for(State state : type.getStates()){
                    stateModel.addElement(state);
                }
            }
        }
    }
    
    private void updateConditionalTypeList(){
        conditionalTypeModel.clear();
        Molecule molecule = (Molecule)conditionalMoleculeList.getSelectedValue();
        if(molecule != null){
            for(SiteType type : molecule.getTypeArray()){
                conditionalTypeModel.addElement(type);
            }
        }
    }
    
    private void updateConditionalStateList(){
        conditionalStateModel.clear();
        SiteType type = (SiteType)conditionalTypeList.getSelectedValue();
        if(type != null){
            if(type.getName().equals(SiteType.ANCHOR)){
                PopUp.warning("Membrane anchors may not participate in reactions.");
                conditionalTypeList.clearSelection();
            } else {
                conditionalStateModel.addElement(TransitionReaction.ANY_STATE);
                for(State state : type.getStates()){
                    conditionalStateModel.addElement(state);
                }
            }
        }
    }
    
    private void enableConditionalLists(boolean bool){
        conditionalMoleculeList.setEnabled(bool);
        conditionalTypeList.setEnabled(bool);
        conditionalStateList.setEnabled(bool);
    }
    
    /* ***************** MAKE THE PANELS *****************************/
    
    private JPanel makeReactionPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int listWidth = 150;
        int listHeight = 100;
        
        JLabel titleLabel = new JLabel("Select the reaction's states.", JLabel.CENTER);
        titleLabel.setPreferredSize(new Dimension(6*(listWidth + 15) + 40, titleLabel.getPreferredSize().height));
        titleLabel.setFont(Fonts.SUBTITLEFONT);
        
        JLabel moleculeLabel = new JLabel("Molecule", JLabel.CENTER);
        JLabel typeLabel = new JLabel("Site Type", JLabel.CENTER);
        JLabel [] stateLabel = new JLabel[2];
        stateLabel[0] = new JLabel("Initial State", JLabel.CENTER);
        stateLabel[1] = new JLabel("Final State", JLabel.CENTER);
        
        JScrollPane moleculePane = new JScrollPane(moleculeList);
        moleculePane.setPreferredSize(new Dimension(listWidth, listHeight));
        moleculePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        moleculePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JScrollPane typePane = new JScrollPane(typeList);
        typePane.setPreferredSize(new Dimension(listWidth, listHeight));
        typePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        typePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JScrollPane [] statePane = new JScrollPane[2];
        for(int i=0;i<2;i++){
            statePane[i] = new JScrollPane(stateList[i]);
            statePane[i].setPreferredSize(new Dimension(listWidth, listHeight));
            statePane[i].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            statePane[i].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        
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
        
        JPanel [] statePanel = new JPanel[2];
        for(int i = 0;i<2;i++){
            statePanel[i] = new JPanel();
            statePanel[i].setLayout(new FlowLayout());
            statePanel[i].setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
            statePanel[i].add(stateLabel[i]);
            statePanel[i].add(statePane[i]);
        }
        
        JLabel arrowLabel = new JLabel(" --> ", JLabel.CENTER);
        arrowLabel.setPreferredSize(new Dimension(40,listHeight + 30));
        arrowLabel.setVerticalAlignment(JLabel.CENTER);
        
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout());
        innerPanel.setPreferredSize(new Dimension(6*(listWidth+15) + 60, listHeight + 60));
        innerPanel.add(titleLabel);
        innerPanel.add(moleculePanel);
        innerPanel.add(typePanel);
        innerPanel.add(statePanel[0]);
        innerPanel.add(arrowLabel);
        innerPanel.add(statePanel[1]);
        
        JPanel p = new JPanel();
        p.add(innerPanel);
        return p;
        // </editor-fold>
    }
    
    private JPanel makeConditionalPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int listWidth = 150;
        int listHeight = 100;
        
        JLabel conditionLabel = new JLabel("Select reaction condition: ", JLabel.RIGHT);
        conditionLabel.setFont(Fonts.SUBTITLEFONT);
        JPanel selectionPanel = new JPanel();
        selectionPanel.add(conditionLabel);
        selectionPanel.add(conditionalBox);
        
        JLabel moleculeLabel = new JLabel("Conditional Molecule", JLabel.CENTER);
        JLabel typeLabel = new JLabel("Conditional Site Type", JLabel.CENTER);
        JLabel stateLabel = new JLabel("Conditional State", JLabel.CENTER);
        
        JScrollPane moleculePane = new JScrollPane(conditionalMoleculeList);
        moleculePane.setPreferredSize(new Dimension(listWidth, listHeight));
        moleculePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        moleculePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JScrollPane typePane = new JScrollPane(conditionalTypeList);
        typePane.setPreferredSize(new Dimension(listWidth, listHeight));
        typePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        typePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JScrollPane statePane = new JScrollPane(conditionalStateList);
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
        listPanel.setPreferredSize(new Dimension(3*(listWidth + 15), listHeight+60));
        
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
        p.add(selectionPanel, "North");
        p.add(listPanel, "Center");
        
        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new FlowLayout());
        returnPanel.add(p);
        
        return returnPanel;
        // </editor-fold>
    }
    
    /* ***************  ASSIGN THE TRANSITION REACTION FIELDS ***********/
    
    private void assignMolecule(){
        reaction.setMolecule((Molecule)moleculeList.getSelectedValue());
    }
    
    private void assignType(){
        reaction.setType((SiteType)typeList.getSelectedValue());
    }
    
    private void assignInitialState(){
        reaction.setInitialState((State)stateList[0].getSelectedValue());
    }
    
    private void assignFinalState(){
        reaction.setFinalState((State)stateList[1].getSelectedValue());
    }
    
    private void assignConditionalMolecule(){
        reaction.setConditionalMolecule((Molecule)conditionalMoleculeList.getSelectedValue());
    }
    
    private void assignConditionalType(){
        reaction.setConditionalType((SiteType)conditionalTypeList.getSelectedValue());
    }
    
    private void assignConditionalState(){
        reaction.setConditionalState((State)conditionalStateList.getSelectedValue());
    }
    
    /* ************** SET THE REACTION CONDITION *********************/
    
    private void assignCondition(String condition){
        reaction.setCondition(condition);
    }
    
    /* ***************** LIST SELECTION LISTENER METHOD *************/
    
    @Override
    public void valueChanged(ListSelectionEvent event){
        if(!event.getValueIsAdjusting()){
            JList source = (JList)event.getSource();
            
            if(source == moleculeList){
                assignMolecule();
                updateTypeList();
            }
            if(source == typeList){
                assignType();
                updateStateLists();
            }
            if(source == stateList[0]){
                assignInitialState();
            }
            if(source == stateList[1]){
                assignFinalState();
            }
            if(source == conditionalMoleculeList){
                assignConditionalMolecule();
                updateConditionalTypeList();
            }
            if(source == conditionalTypeList){
                assignConditionalType();
                updateConditionalStateList();
            }
            if(source == conditionalStateList){
                assignConditionalState();
            }
        }
    }
    
    /* ******* LISTEN FOR CHANGES TO THE CONDITIONAL COMBOBOX *******/
    
    @Override
    public void itemStateChanged(ItemEvent event){
        if(event.getStateChange() == ItemEvent.SELECTED){
            String condition = (String)conditionalBox.getSelectedItem();
            assignCondition(condition);
            if(condition.equals(TransitionReaction.BOUND_CONDITION)){
                enableConditionalLists(true);
            } else {
                enableConditionalLists(false);
            }
        }
    }
    
}
