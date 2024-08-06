/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springsalad.helpersetup.Colors;
import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.PopUp;


public class DrawPanel3DPanel extends JPanel implements ActionListener {
    
    private DrawPanel3D drawPanel;
    
    private final ValueTextField [] shiftTF = new ValueTextField[3];
    private JButton shift;
    private JButton addLink;
    private JButton removeLink;
    
    private JButton flipX;
    private JButton flipY;
    private JButton flipZ;
    
    private ValueTextField newRadius;
    private JButton enterRadius;
       
    public DrawPanel3DPanel(DrawPanel3D drawPanel){
        this.setLayout(new BorderLayout());
        this.drawPanel = drawPanel;
        drawPanel.systemSetup();
               
        this.add(drawPanel, "Center");
        this.add(makeButtonPanel(), "East");
        
        shift.addActionListener(this);
        addLink.addActionListener(this);
        removeLink.addActionListener(this);
        enterRadius.addActionListener(this);
        
        flipX.addActionListener(this);
        flipY.addActionListener(this);
        flipZ.addActionListener(this);
    }
    
    private JPanel makeButtonPanel(){
        
    	JPanel parent = new JPanel();
    	parent.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.weightx = 0.5;
    	
    	//make translation panel
    
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(5,1));
        p.setPreferredSize(new Dimension(150,160));
        
        JLabel translationLabel = new JLabel("Set Translation (nm)", JLabel.CENTER);
        p.add(translationLabel);
        
        JLabel [] label = new JLabel[]{new JLabel("dx: ", JLabel.RIGHT), 
            new JLabel("dy: ", JLabel.RIGHT), new JLabel("dz: ", JLabel.RIGHT)};
        
        JPanel [] shiftPanel = new JPanel[3];
        for(int i=0;i<3;i++){
            shiftPanel[i] = new JPanel();
            shiftPanel[i].add(label[i]);
            shiftTF[i] = new ValueTextField("0.0", 5, ValueTextField.DOUBLE, Constraints.NO_CONSTRAINT, false);
            shiftPanel[i].add(shiftTF[i]);
            p.add(shiftPanel[i]);
        }
        
        JPanel buttonPanel = new JPanel();
        shift = new JButton("Translate");
        buttonPanel.add(shift);
        p.add(buttonPanel);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        
        //add translation panel to grid     
        parent.add(p, c);
        
        //addLink
        addLink = new JButton("Add Link");
        JPanel pan = new JPanel();
        pan.add(addLink);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(40, 0, 0, 0);
        parent.add(pan, c);
        
        //removeLink
        removeLink = new JButton("Remove Link");
        JPanel pan2 = new JPanel();
        pan2.add(removeLink);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        parent.add(pan2, c);
       
        JLabel radiiLabel = new JLabel("Set Radius (nm):", JLabel.CENTER);       
        newRadius = new ValueTextField("0.0", 5, ValueTextField.DOUBLE, Constraints.NO_CONSTRAINT, false);
        JPanel pan3 = new JPanel();
        pan3.add(radiiLabel);
        pan3.add(newRadius);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(50, 0, 0, 0);
        parent.add(pan3, c);
        
        enterRadius = new JButton("Apply");
        JPanel pan4 = new JPanel();
        pan4.add(enterRadius);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 5;
        c.insets = new Insets(0, 0, 0, 0);
        parent.add(pan4, c);
        
        flipX = new JButton("Flip X");
        JPanel pan5 = new JPanel();
        pan5.add(flipX);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 6;
        c.insets = new Insets(20, 0, 0, 0);
        parent.add(pan5, c);
        
        flipY = new JButton("Flip Y");
        JPanel pan6 = new JPanel();
        pan6.add(flipY);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 7;
        c.insets = new Insets(0, 0, 0, 0);
        parent.add(pan6, c);
        
        flipZ = new JButton("Flip Z");
        JPanel pan7 = new JPanel();
        pan7.add(flipZ);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 8;
        c.insets = new Insets(0, 0, 0, 0);
        parent.add(pan7, c);
        
        JPanel rPanel = new JPanel();
        rPanel.add(parent);
        return rPanel;
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
    	JButton source = (JButton)event.getSource();
        if(source == shift){
        	drawPanel.shiftSites(shiftTF[0].getDouble(), shiftTF[1].getDouble(), shiftTF[2].getDouble());
        }
        else if(source == addLink){
        	//limit links to two sites
        	ArrayList<Site> sites = drawPanel.getSelectedSites();
        	if(sites.size() > 2){
        		PopUp.information("Please select only 2 sites");
        	}else if(sites.size() < 2){
        		PopUp.information("Please select 2 sites");
        	}else{
        		drawPanel.addLinkToMol(new Link(sites.get(0), sites.get(1)));
        	}
        }
        else if(source == removeLink){       	
        	if(drawPanel.getSelectedLinks().size() > 1){
        		PopUp.information("Please remove 1 link at a time");
        	}else if(drawPanel.getSelectedLinks().size() < 1){
        		PopUp.information("Please select a link");
        	}else{
        		drawPanel.removeLinkToMol(drawPanel.getSelectedLinks().get(0));
        	}
        }
        else if(source == enterRadius){
        	drawPanel.updateRadius(drawPanel.getSelectedSites(), newRadius.getDouble());
        }
        else if(source == flipX){
        	drawPanel.flip(0);
        }
        else if(source == flipY){
        	drawPanel.flip(1);
        }
        else if(source == flipZ){
        	drawPanel.flip(2);
        }
        
    }
        
    
    public static void main(String [] args){
        JFrame frame = new JFrame();
        
        Molecule molecule = new Molecule("H");
        SiteType [] type = new SiteType[Colors.COLORARRAY.length];
        for(int i=0;i<type.length;i++){
            type[i] = new SiteType(molecule, "Type " + i);
            type[i].setRadius(1 + 0.5*(i%3));
            type[i].setColor(Colors.COLORARRAY[i]);
            molecule.addType(type[i]);
        }
        Site [] site = new Site[3*type.length];
        for(int i=0;i<site.length;i++){
            site[i] = new Site(molecule, type[i%type.length]);
            site[i].setX(30*Math.cos(2*Math.PI*i/site.length));
            site[i].setY(30*Math.sin(2*Math.PI*i/site.length));
            site[i].setZ(6);
            site[i].setLocation(SystemGeometry.INSIDE);
            molecule.addSite(site[i]);
        }
        Link [] link = new Link[site.length -1];
        for(int i=0;i<link.length;i++){
            link[i] = new Link(site[i], site[i+1]);
            molecule.addLink(link[i]);
        }
        
        DrawPanel3D panel3d = new DrawPanel3D(molecule);
        
        DrawPanel3DPanel panel = new DrawPanel3DPanel(panel3d);
        
        
        Container c = frame.getContentPane();
        c.add(panel);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(700,500);
        frame.setVisible(true);
    }
    
}
