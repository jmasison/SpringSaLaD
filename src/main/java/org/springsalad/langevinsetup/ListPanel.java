/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.PopUp;

public class ListPanel extends JPanel implements ActionListener, ListSelectionListener  {
    
    public final static int MOLECULE = 0;
    public final static int BINDING_REACTION = 1;
    public final static int TRANSITION_REACTION = 2;
    public final static int ALLOSTERIC_REACTION = 3;
    
    private final Global g;
    private final int type;
    
    private final JButton addButton;
    private final JButton removeButton;
    
    private final DefaultListModel listModel;
    private final JList list;
    
    private final SystemTree treePane;
    
    private final AnnotationPanel annotationPanel;
    
    public ListPanel(int type, Global g, SystemTree treePane, 
                                        AnnotationPanel annotationPanel){
        this.type = type;
        this.g = g;
        this.treePane = treePane;
        this.annotationPanel = annotationPanel;
        annotationPanel.setAnnotation(null);
        JLabel label;
        switch(type){
            case MOLECULE:{
                label = new JLabel("Molecules", JLabel.CENTER);
                break;
            }
            case BINDING_REACTION:{
                label = new JLabel("Binding Reactions", JLabel.CENTER);
                break;
            }
            case TRANSITION_REACTION:{
                label = new JLabel("Transition Reactions", JLabel.CENTER);
                break;
            }
            case ALLOSTERIC_REACTION:{
                label = new JLabel("Allosteric Reactions", JLabel.CENTER);
                break;
            }
            default:
                label = new JLabel("Messed up!");
        }
        label.setFont(Fonts.TITLEFONT);
        
        addButton = new JButton("Add new");
        removeButton = new JButton("Remove");
        
        this.setLayout(new BorderLayout());
        this.add(label, "North");
        
        listModel = new DefaultListModel();
        list = new JList(listModel);
        JScrollPane listPane = new JScrollPane(list);
        listPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        listPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(listPane, "Center");
        
        JPanel p = new JPanel();
        p.add(addButton);
        p.add(removeButton);
        this.add(p, "South");
        
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        list.addListSelectionListener(this);
        
        updateList();
    }
    
    private void updateList(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        listModel.clear();
        switch(type){
            case MOLECULE:{
                for(Molecule molecule : g.getMolecules()){
                    listModel.addElement(molecule);
                }
                break;
            } 
            case BINDING_REACTION:{
                for(BindingReaction reaction :  g.getBindingReactions()){
                    listModel.addElement(reaction);
                }
                break;
            }
            case TRANSITION_REACTION:{
                for(TransitionReaction reaction : g.getTransitionReactions()){
                    listModel.addElement(reaction);
                }
                break;
            }
            case ALLOSTERIC_REACTION:{
                for(AllostericReaction reaction : g.getAllostericReactions()){
                    listModel.addElement(reaction);
                }
                break;
            }
                
            default:
                // do nothing
        }
        // </editor-fold>
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JButton source = (JButton)e.getSource();
        if(source == addButton){
            switch(type){
                case MOLECULE:{
                    Molecule molecule = new Molecule("NewMolecule" + g.getMolecules().size());
                    g.addMolecule(molecule);
                    treePane.addMolecule(molecule);
                    treePane.selectNode(molecule.toString());
                    break;
                }
                case BINDING_REACTION:{
                    BindingReaction reaction = new BindingReaction("BindingReaction" + g.getBindingReactions().size());
                    g.addBindingReaction(reaction);
                    treePane.addBindingReaction(reaction);
                    treePane.selectNode(reaction.getName());
                    break;
                }
                case TRANSITION_REACTION:{
                    TransitionReaction reaction = new TransitionReaction("TransitionReaction" + g.getTransitionReactions().size());
                    g.addTransitionReaction(reaction);
                    treePane.addTransitionReaction(reaction);
                    treePane.selectNode(reaction.getName());
                    break;
                }
                case ALLOSTERIC_REACTION:{
                    AllostericReaction reaction = new AllostericReaction("AllostericReaction" + g.getAllostericReactions().size());
                    g.addAllostericReaction(reaction);
                    treePane.addAllostericReaction(reaction);
                    treePane.selectNode(reaction.getName());
                    break;
                }
                default:{
                    break;
                }
            }
        }
        
        if(source == removeButton){
            switch(type){
                case MOLECULE:{
                    Molecule molecule = (Molecule)list.getSelectedValue();
                    if(molecule == null){
                        PopUp.warning("No molecule selected.");
                    } else if (g.moleculeInBindingReaction(molecule)){
                        PopUp.error("The selected molecule participates in a binding reaction."
                                + " Please delete that reaction before removing this molecule.");
                    } else if (g.moleculeInTransitionReaction(molecule)){
                        PopUp.error("The selected molecule participates in a transition reaction."
                                + " Please delete that reaction before removing this molecule.");
                    } else {
                        int confirm = PopUp.doubleCheck("Are you sure you want to remove " + molecule.getName() + "?");
                        if(confirm == 0){
                            g.removeMolecule(molecule);
                            treePane.removeMolecule(molecule);
                            this.updateList();
                        }
                    }
                    break;
                }
                case BINDING_REACTION:{
                    BindingReaction reaction = (BindingReaction)list.getSelectedValue();
                    if(reaction == null){
                        PopUp.warning("No reaction selected.");
                    } else {
                        int confirm = PopUp.doubleCheck("Are you sure you want to remove " + reaction.getName() + "?");
                        if(confirm == 0){
                            g.removeBindingReaction(reaction);
                            treePane.removeBindingReaction(reaction);
                            this.updateList();
                        }
                    }
                    break;
                }
                case TRANSITION_REACTION:{
                    TransitionReaction reaction = (TransitionReaction)list.getSelectedValue();
                    if(reaction == null){
                        PopUp.warning("No reaction selected.");
                    } else {
                        int confirm = PopUp.doubleCheck("Are you sure you want to remove " + reaction.getName() + "?");
                        if(confirm == 0){
                            g.removeTransitionReaction(reaction);
                            treePane.removeTransitionReaction(reaction);
                            this.updateList();
                        }
                    }
                    break;
                }
                case ALLOSTERIC_REACTION:{
                    AllostericReaction reaction = (AllostericReaction)list.getSelectedValue();
                    if(reaction == null){
                        PopUp.warning("No reaction selected.");
                    } else {
                        int confirm = PopUp.doubleCheck("Are you sure you want to remove " + reaction.getName() + "?");
                        if(confirm == 0){
                            g.removeAllostericReaction(reaction);
                            treePane.removeAllostericReaction(reaction);
                            this.updateList();
                        }
                    }
                    break;
                }
            }
        }
        // </editor-fold>
    }
    
    @Override
    public void valueChanged(ListSelectionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(!event.getValueIsAdjusting()){
            Object o = list.getSelectedValue();
            if(o != null){
                switch(type){
                    case MOLECULE:
                        Molecule mol = (Molecule)o;
                        annotationPanel.setAnnotation(mol.getAnnotation());
                        break;
                    default:
                        Reaction reaction = (Reaction)o;
                        annotationPanel.setAnnotation(reaction.getAnnotation());
                }
            }
        }
        // </editor-fold>
    }
}
