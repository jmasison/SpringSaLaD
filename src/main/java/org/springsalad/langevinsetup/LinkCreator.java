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

import org.springsalad.helpersetup.PopUp;

public class LinkCreator extends JFrame implements ActionListener, 
                                        ListSelectionListener {
    
    private final Molecule molecule;
    private final JList<Site> [] list = new JList[2];
    
    private final JButton createButton = new JButton("Create Link");
    private final JButton cancelButton = new JButton("Cancel");
    
    private final int [] currentIndex = new int[2];
    
    public LinkCreator(Molecule molecule){
        super("Link Creator");
        this.molecule = molecule;
        
        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        c.setPreferredSize(new Dimension(350,280));
        
        c.add(makePanel("First Site", 0));
        c.add(makePanel("Second Site", 1));
        c.add(createButton);
        c.add(cancelButton);
        
        createButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    
    private JPanel makePanel(String labelText, int i){
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultListModel listModel = new DefaultListModel();
        for(Site site : molecule.getSiteArray()){
            listModel.addElement(site);
        }
        list[i] = new JList<>(listModel);
        list[i].setSelectedIndex(i);
        currentIndex[i] = i;
        list[i].addListSelectionListener(this);
        
        JScrollPane scrollPane = new JScrollPane(list[i]);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(150,200));
        
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
        p.setPreferredSize(new Dimension(160,230));
        
        p.add(label);
        p.add(scrollPane);
        
        return p;
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton)e.getSource();
        if(source == createButton){
            Site site0 = list[0].getSelectedValue();
            Site site1 = list[1].getSelectedValue();
            if(site0 == null || site1 == null){
                PopUp.error("One or both sites are not selected.");
            } else {
                Link link = new Link(site0, site1);
                molecule.addLink(link);
                this.setVisible(false);
                this.dispose();
            }
        }
        if(source == cancelButton){
            this.setVisible(false);
            this.dispose();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // All we have to do is check to see if the indices are the same, 
        // and if so, we change them back to the old indices.
        int [] newIndex = {list[0].getSelectedIndex(), list[1].getSelectedIndex()};
        if(newIndex[0] == newIndex[1]){
            PopUp.error("A link cannot connect a site to itself.");
            list[0].setSelectedIndex(currentIndex[0]);
            list[1].setSelectedIndex(currentIndex[1]);
        } else {
            currentIndex[0] = newIndex[0];
            currentIndex[1] = newIndex[1];
        }
        
        
    }
    
    public static void main(String [] args){
        Molecule mol = new Molecule("new mol");
        SiteType type0 = new SiteType(mol, "Type0");
        SiteType type1 = new SiteType(mol, "Type1");
        mol.addType(type0);
        mol.addType(type1);
        Site site0 = new Site(mol, type0);
        Site site1 = new Site(mol, type1);
        mol.addSite(site0);
        mol.addSite(site1);
        mol.addSite(new Site(mol, type0));
        mol.addSite(new Site(mol, type1));
        new LinkCreator(mol);
    }
    
}
