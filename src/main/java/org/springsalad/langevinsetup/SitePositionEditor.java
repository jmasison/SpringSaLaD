/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.IOHelp;

public class SitePositionEditor extends JFrame implements ActionListener {
    
    private final Site site;
    
    private ValueTextField xTF;
    private ValueTextField yTF;
    private ValueTextField zTF;
    
    private JButton ok;
    private JButton cancel;
    
    public SitePositionEditor(Site site){
        super("Set Position");
        this.site = site;
        
        layoutComponents();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setSize(210,230);
        this.setVisible(true);
    }
    
    private void layoutComponents(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel siteLabel = new JLabel("Site " + site.getIndex(), JLabel.CENTER);
        siteLabel.setFont(Fonts.SITE_FONT);
        
        JPanel px = new JPanel();
        JLabel xLabel = new JLabel("x (nm): ", JLabel.RIGHT);
        xTF = new ValueTextField(IOHelp.DF[4].format(site.getX()), 6, 
                ValueTextField.DOUBLE, Constraints.NO_CONSTRAINT, false);
        px.add(xLabel);
        px.add(xTF);
        
        JPanel py = new JPanel();
        JLabel yLabel = new JLabel("y (nm): ", JLabel.RIGHT);
        yTF = new ValueTextField(IOHelp.DF[4].format(site.getY()), 6, 
                ValueTextField.DOUBLE, Constraints.NO_CONSTRAINT, false);
        py.add(yLabel);
        py.add(yTF);
        
        JPanel pz = new JPanel();
        JLabel zLabel = new JLabel("z (nm): ", JLabel.RIGHT);
        zTF = new ValueTextField(IOHelp.DF[4].format(site.getZ()), 6, 
                ValueTextField.DOUBLE, Constraints.NO_CONSTRAINT, false);
        pz.add(zLabel);
        pz.add(zTF);
        
        JPanel buttonPanel = new JPanel();
        ok = new JButton("Finish");
        cancel = new JButton("Cancel");
        
        ok.addActionListener(this);
        cancel.addActionListener(this);
        
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(5,1));
        p.add(siteLabel);
        p.add(px);
        p.add(py);
        p.add(pz);
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
            site.setX(xTF.getDouble());
            site.setY(yTF.getDouble());
            site.setZ(zTF.getDouble());
            
            this.setVisible(false);
            this.dispose();
        }
        // </editor-fold>
    }
}
