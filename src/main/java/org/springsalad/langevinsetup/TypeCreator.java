/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.springsalad.helpersetup.Colors;
import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.NamedColor;
import org.springsalad.helpersetup.PopUp;

import java.util.ArrayList;
import java.util.Enumeration;

public class TypeCreator extends JFrame implements ActionListener {
    
    // The site type that we are creating or editing.
    private final SiteType type;

    // Global for checking against reactions
    private final Global g;
    
    // The textfields for the name, radius, and diffusion constant
    private final NameTextField nameTF;
    private final ValueTextField radiusTF;
    private final ValueTextField diffusionTF;
    
    // The combobox for the colors
    private final JComboBox<NamedColor> colorBox;
    
    // The checkbox to tell us if this is a membrane anchor
//    private final JCheckBox anchorCheckBox;
    
    // Button to indicate we are done editing
    private final JButton finishButton = new JButton("Finish");
    
    // The list of states
    private final JList<State> stateList;
    private final DefaultListModel stateModel = new DefaultListModel();
    
    // The buttons for state creation, editing, and removal
    private final JButton addState = new JButton("Add State");
    private final JButton editState = new JButton("Edit State");
    private final JButton removeState = new JButton("Remove State");
    
    public TypeCreator(Global g, Molecule molecule, SiteType type){
        super("Site Type Editor");
        this.type = type;
        this.g = g;
        
        Container c = getContentPane();
        
        // CREATE THE LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(6,1));
        
        // Construct the panel to hold the name label and textfield.
        JPanel namePanel = createLabeledPanel("Name: ", 80);
        String typeName = type.getName();
        nameTF = new NameTextField(typeName, 7);
             // Add in all of the disallowed names
        ArrayList<SiteType> types = molecule.getTypeArray();
        for(SiteType tempType : types){
            if(!typeName.equals(tempType.getName())){
                nameTF.addDisallowedName(tempType.getName());
            }
        }
        nameTF.addDisallowedName(SiteType.ANCHOR);
        // Don't allow an empty name
        nameTF.addDisallowedName("");
        namePanel.add(nameTF);
        if(typeName.equals(SiteType.ANCHOR)){
            nameTF.setEnabled(false);
            addState.setEnabled(false);
            removeState.setEnabled(false);
        }
        
        // Construct the panel to hold the radius label and textfield.
        JPanel radiusPanel = createLabeledPanel("Radius (nm): ", 80);
        radiusTF = new ValueTextField(Double.toString(type.getRadius()), 7, 
                                ValueTextField.DOUBLE, Constraints.POSITIVE, false);
        radiusPanel.add(radiusTF);
        
        // Construct the panel to hold the diffusion constant label and tf.
        JPanel diffusionPanel = createLabeledPanel("D (um^2/s): ", 80);
        diffusionTF = new ValueTextField(Double.toString(type.getD()), 7, 
                                ValueTextField.DOUBLE, Constraints.POSITIVE, false);
        diffusionPanel.add(diffusionTF);
        
        // Construct the panel to hold the color combobox
        JPanel colorPanel = createLabeledPanel("Color: ", 70);
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        NamedColor [] colors = Colors.COLORARRAY;
        for(NamedColor color : colors){
            model.addElement(color);
        }
        colorBox = new JComboBox<>(model);
        colorBox.setSelectedItem(Colors.getColorByName(type.getColorName()));
        colorPanel.add(colorBox);
        
        // Construct the panel to hold the anchor checkbox
//        JPanel anchorPanel = new JPanel();
//        anchorPanel.setLayout(new FlowLayout());
//        anchorCheckBox = new JCheckBox("Membrane Anchor");
//        if(molecule.getLocation().equals(SystemGeometry.MEMBRANE)){
//            if(!molecule.hasAnchorType()) {
//                anchorCheckBox.setEnabled(true);
//            } else {
//                if(type.getName().equals(SiteType.ANCHOR)){
//                    nameTF.setEnabled(false);
//                    anchorCheckBox.setEnabled(true);
//                    anchorCheckBox.setSelected(true);
//                } else {
//                    anchorCheckBox.setEnabled(false);
//                }
//            }
//        } else {
//            anchorCheckBox.setEnabled(false);
//        }
//
//        anchorPanel.add(anchorCheckBox);
        
        // Construct a panel to hold the finish button
        JPanel finishPanel = new JPanel();
        finishPanel.setLayout(new FlowLayout());
        finishPanel.add(finishButton);
        
        // Now add all of these panels to the leftPanel
        leftPanel.add(namePanel);
        leftPanel.add(radiusPanel);
        leftPanel.add(diffusionPanel);
        leftPanel.add(colorPanel);
//        leftPanel.add(anchorPanel);
        leftPanel.add(finishPanel);
        
        // CREATE THE RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout());
        // Create the label
        JLabel stateLabel = new JLabel("States");
        stateLabel.setHorizontalAlignment(JLabel.CENTER);
        // Initialize the state list
        for(State state : type.getStates()){
            stateModel.addElement(state);
        }
        stateList = new JList<>(stateModel);
        stateList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stateList.setPreferredSize(new Dimension(100,100));
        
        JScrollPane scrollPane = new JScrollPane(stateList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(110, 120));
        
        rightPanel.add(stateLabel);
        rightPanel.add(scrollPane);
        rightPanel.add(addState);
        rightPanel.add(editState);
        rightPanel.add(removeState);
        
        // Set the size of the right panel to be slightly larger than its largest component
        rightPanel.setPreferredSize(new Dimension(130, 250));
        
        c.setLayout(new FlowLayout());
        c.add(leftPanel);
        c.add(rightPanel);
        
        finishButton.addActionListener(this);
        addState.addActionListener(this);
        editState.addActionListener(this);
        removeState.addActionListener(this);
//        anchorCheckBox.addItemListener(this);
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
        
        resizeStateButtons();
    }
    
    private JPanel createLabeledPanel(String labelText, int labelSize){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setPreferredSize(new Dimension(labelSize, label.getPreferredSize().height));
        p.add(label);
        return p;
        // </editor-fold>
    }
    
    private void resizeStateButtons(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int width = Math.max(addState.getWidth(), Math.max(editState.getWidth(),
                                                removeState.getWidth()));
        addState.setPreferredSize(new Dimension(width, addState.getPreferredSize().height));
        editState.setPreferredSize(new Dimension(width, editState.getPreferredSize().height));
        removeState.setPreferredSize(new Dimension(width, removeState.getPreferredSize().height));
        this.validate();
        // </editor-fold>
    }
    
    /* ********* MANAGE STATE LIST ******************************/
    private void updateStateList(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int index = stateList.getSelectedIndex();
        stateModel.clear();
        for(State state : type.getStates()){
            stateModel.addElement(state);
        }
        if(index < stateModel.size()){
            stateList.setSelectedIndex(index);
        } else {
            stateList.setSelectedIndex(stateModel.size() -1);
        }
        // </editor-fold>
    }
    
    /* ****** CHECK THE STATE NAME AGAINST EXISTING NAMES ****/
    private boolean stateNameOK(State state){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean ok = true;
        for(Enumeration<State> e = stateModel.elements(); e.hasMoreElements();){
            State nextState = e.nextElement();
            if(state != nextState){
                if(state.getName().equals(nextState.getName())){
                    ok = false;
                    PopUp.error(state.getName() + " matches the name of another state.");
                    break;
                }
            }
        }
        return ok;
        // </editor-fold>
    }
    
    /* *****************  MANAGE THE STATE BUTTONS      **********/
    
    private void pressedAddStateButton(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Begin with a default name
        State newState = new State(type, "State" + stateModel.size());
        do{
            String name = PopUp.input("Enter a name for the state.", newState.getName());
            if(name != null){
                newState.setName(name);
            } else {
                return;
            }
        } while(!stateNameOK(newState));
        type.addState(newState);
        updateStateList();
        // </editor-fold>
    }
    
    private void pressedEditStateButton(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Get the state we want to edit
        State state = stateList.getSelectedValue();
        if(state == null){
            PopUp.error("No state selected.");
        } else {
            do{
                String name = PopUp.input("Enter a new name for the state.", state.getName());
                if(name != null){
                    state.setName(name);
                } else {
                    return;
                }
            } while (!stateNameOK(state));
        }
        updateStateList();
        // </editor-fold>
    }
    
    private void pressedRemoveStateButton(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(stateModel.size() == 1){
            PopUp.warning("All site types must have at least one state.");
        } else {
            State state = stateList.getSelectedValue();
            if(state == null){
                PopUp.warning("Please select a state.");
            } else if(g.stateInTransitionReaction(state)){
                PopUp.error("The selected state participates in a transition reaction."
                    + " Please delete that reaction before removing this state.");
            } else {
                type.removeState(state);
                updateStateList();
            }
        }
        // </editor-fold>
    }
    
    private void pressedFinishButton(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        type.setName(nameTF.getText());
        type.setRadius(radiusTF.getDouble());
        type.setD(diffusionTF.getDouble());
        type.setColor((NamedColor)colorBox.getSelectedItem());
        this.setVisible(false);
        this.dispose();
    	for(BindingReaction reaction : g.getBindingReactions()) {
    		if (reaction.getType(0) != type && reaction.getType(1) != type) continue;
            boolean ret = reaction.checkOnRate();
            if(ret == false) {
            	String msg = "Kon is too large (I.e. exceeds the diffusion limited rate) for reaction '" + reaction.getName() + "'.\n";
            	msg += "Please consider increasing Radius or D here, or reducing Kon in the reaction.";
            	JOptionPane.showMessageDialog(null, msg);
            	break;
            }
    	}
        // </editor-fold>
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Object source = event.getSource();
        if(source == addState){
            pressedAddStateButton();
        }
        if(source == editState){
            pressedEditStateButton();
        }
        if(source == removeState){
            pressedRemoveStateButton();
        }
        if(source == finishButton){
            pressedFinishButton();
        }
        // </editor-fold>
    }
    
//    @Override
//    public void itemStateChanged(ItemEvent event){
//        
//        if(event.getStateChange() == ItemEvent.SELECTED){
//            nameTF.setText(SiteType.ANCHOR);
//            nameTF.setEnabled(false);
//            type.getStates().clear();
//            type.addState(new State(type, SiteType.ANCHOR));
//            updateStateList();
//        } else {
//            nameTF.setText("New Type");
//            nameTF.setEnabled(true);
//            type.getStates().clear();
//            type.addState(new State(type, "State0"));
//            updateStateList();
//        }
//    }
    
}
