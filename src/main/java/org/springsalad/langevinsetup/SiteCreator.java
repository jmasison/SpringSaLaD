/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.*;
import javax.swing.event.*;

import org.springsalad.helpersetup.PopUp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class SiteCreator extends JFrame implements ActionListener, 
                                        ItemListener, ListSelectionListener {
    
    private final Site site;
    private final boolean makingNew;
    private final JComboBox locationBox;
    private final JList<SiteType> typeList;
    private final JButton finishButton = new JButton("Finish");

    public SiteCreator(Molecule molecule, Site site){
        super("Site Editor");
        
        if(site == null){
            this.site = new Site(molecule, molecule.getTypeArray().get(0));
            makingNew = true;
        } else {
            this.site = site;
            makingNew = false;
        }
        
        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        
        // Make a label for the type list
        JLabel listLabel = new JLabel("Select a site type");
        
        // Make the type list
        DefaultListModel listModel = new DefaultListModel();
        ArrayList<SiteType> types = molecule.getTypeArray();
        for(SiteType type: types){
            listModel.addElement(type);
        }
        typeList = new JList<>(listModel);
        typeList.setSelectedValue(this.site.getType(), true);
        JScrollPane typePane = new JScrollPane(typeList);
        typePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        typePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        typePane.setPreferredSize(new Dimension(150,100));
        
        // Label for the location combo box
        JLabel locationLabel = new JLabel("Choose a site location.");
        
        // Location box setup
        DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
        for(String location : SystemGeometry.getLocations()){
            boxModel.addElement(location);
        }
        locationBox = new JComboBox(boxModel);
        
        if(molecule.getLocation().equals(SystemGeometry.INSIDE)){
            locationBox.setSelectedIndex(0);
            locationBox.setEnabled(false);
        } else if(molecule.getLocation().equals(SystemGeometry.OUTSIDE)){
            locationBox.setSelectedIndex(2);
            locationBox.setEnabled(false);
        } else {
            if(site == null){
                SiteType selectedType = typeList.getSelectedValue();
                if(selectedType.getName().equals(SiteType.ANCHOR)){
                    locationBox.setSelectedIndex(1);
                } else {
                    locationBox.setSelectedIndex(0);
                }
                locationBox.setEnabled(true);
            } else {
                // We won't allow editing of anchor sites, nor will we allow
                // another site to be edited to an anchor site. So get rid of
                // the anchor option and the membrane option.
                boxModel.removeElement(SystemGeometry.MEMBRANE);
                
                for(Enumeration e = listModel.elements(); e.hasMoreElements();){
                    SiteType type = (SiteType)e.nextElement();
                    if(type.getName().equals(SiteType.ANCHOR)){
                        listModel.removeElement(type);
                        break;
                    }
                }
                
                locationBox.setSelectedItem(site.getLocation());
                
            }
        }
        
        // Enlarge the finish button.  This is just to make the layout easier
        finishButton.setPreferredSize(new Dimension(100,finishButton.getPreferredSize().height));
        c.add(listLabel);
        c.add(typePane);
        c.add(locationLabel);
        c.add(locationBox);
        c.add(finishButton);
        c.setPreferredSize(new Dimension(190,220));
        
        typeList.addListSelectionListener(this);
        locationBox.addItemListener(this);
        finishButton.addActionListener(this);
        
        this.pack();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        SiteType type = typeList.getSelectedValue();
        site.setType(type);
        site.setLocation((String)locationBox.getSelectedItem());
        if(makingNew){
            site.setX(0);
            site.setY(4);
            site.getMolecule().addSite(site);
        }
        site.setInitialState(type.getState(0));
        site.setChecked(false);
        site.setPositionOK(true);
        this.setVisible(false);
        this.dispose();
    }

    
    @Override
    public void itemStateChanged(ItemEvent e) {
        // Make sure that the site type is anchor if "membrane" is selected.
        if(e.getStateChange() == ItemEvent.SELECTED){
            String location = (String)locationBox.getSelectedItem();
            SiteType type = typeList.getSelectedValue();
            if(location.equals(SystemGeometry.MEMBRANE)){
                if(!type.getName().equals(SiteType.ANCHOR)){
                    PopUp.error("Only anchors may be located on the membrane.");
                    locationBox.setSelectedIndex(0);
                }
            } else {
                if(type.getName().equals(SiteType.ANCHOR)){
                    PopUp.error("Anchors must be located on the membrane.");
                    locationBox.setSelectedIndex(1);
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        SiteType type = typeList.getSelectedValue();
        if(type.getName().equals(SiteType.ANCHOR)){
            locationBox.setSelectedIndex(1);
        } else {
            if(locationBox.getSelectedIndex()== 1){
                locationBox.setSelectedIndex(0);
            }
        }
    }
    
    
    
}
