/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.*;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.IOHelp;

import java.awt.*;
import java.awt.event.*;

public class LinkLengthEditor extends JFrame implements ActionListener {
    
    private final static int FIX_SITE_0 = 0;
    private final static int FIX_SITE_1 = 1;
    private final static int FIX_CENTER = 2;
    
    private final Link link;

    private ValueTextField vtf;
    
    private JComboBox box;
    private final String [] boxOptions;
    
    private JButton ok;
    private JButton cancel;
    
    public LinkLengthEditor(Link link){
        super("Edit Link Length");
        this.link = link;
        boxOptions = new String[3];
        boxOptions[0] = "Site " + link.getSite1().getIndex();
        boxOptions[1] = "Site " + link.getSite2().getIndex();
        boxOptions[2] = "Link Midpoint";
        
        layoutComponents();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private void layoutComponents(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel lengthLabel = new JLabel("Link length (nm): ", JLabel.RIGHT);
        // lengthLabel.setFont(Fonts.LABELFONT);
        vtf = new ValueTextField(IOHelp.DF[5].format(link.getLength()),
                10, ValueTextField.DOUBLE, Constraints.POSITIVE, false);
        JPanel lengthPanel = new JPanel();
        lengthPanel.add(lengthLabel);
        lengthPanel.add(vtf);
        
        JLabel boxLabel = new JLabel("Select Fixed Position: ", JLabel.RIGHT);
        // boxLabel.setFont(Fonts.LABELFONT);
        box = new JComboBox(boxOptions);
        JPanel boxPanel = new JPanel();
        boxPanel.add(boxLabel);
        boxPanel.add(box);
        
        ok = new JButton("Finish");
        cancel = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(3,1));
        p.add(lengthPanel);
        p.add(boxPanel);
        p.add(buttonPanel);
        
        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(p);
        // </editor-fold>
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JButton source = (JButton)event.getSource();
        
        if(source == cancel){
            this.setVisible(false);
            this.dispose();
        }
        
        if(source == ok){
            double newLength = vtf.getDouble();
            Site [] site = link.getSites();
            // Unit vector from site[0] to site[1]
            double [] unitVector = link.unitVector();
            
            switch(box.getSelectedIndex()){
                case FIX_SITE_0:{
                    double newX = site[0].getX() + newLength*unitVector[0];
                    double newY = site[0].getY() + newLength*unitVector[1];
                    double newZ = site[0].getZ() + newLength*unitVector[2];
                    site[1].setPosition(newX, newY, newZ);
                }
                case FIX_SITE_1:{
                    double newX = site[1].getX() - newLength*unitVector[0];
                    double newY = site[1].getY() - newLength*unitVector[1];
                    double newZ = site[1].getZ() - newLength*unitVector[2];
                    site[0].setPosition(newX, newY, newZ);
                }
                case FIX_CENTER:{
                    double oldLength = link.getLength();
                    double [] center = new double[3];
                    double [] pos0 = site[0].getPosition();
                    for(int i=0;i<center.length;i++){
                        center[i] = pos0[i] + unitVector[i]*oldLength/2;
                    }
                    double [] newPos0 = new double[3];
                    double [] newPos1 = new double[3];
                    for(int i=0;i<center.length;i++){
                        newPos0[i] = center[i] - unitVector[i]*newLength/2;
                        newPos1[i] = center[i] + unitVector[i]*newLength/2;
                    }
                    site[0].setPosition(newPos0[0], newPos0[1], newPos0[2]);
                    site[1].setPosition(newPos1[0], newPos1[1], newPos1[2]);
                }
            }
            
            this.setVisible(false);
            this.dispose();
        }
        // </editor-fold>
    }
    
}
