/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class DrawPanelPanel extends JPanel implements ChangeListener {

    private final DrawPanel drawPanel;
    
    private final JSpinner pixelSizeSpinner;
    private final JSpinner panelSizeSpinner; 
    
    public DrawPanelPanel(DrawPanel drawPanel){
        this.drawPanel = drawPanel;
        
        SpinnerNumberModel pixelModel 
                = new SpinnerNumberModel(DrawPanel.PIXELS_PER_NM, 2, 100, 1);
        
        SpinnerNumberModel panelModel =
                new SpinnerNumberModel(drawPanel.getPreferredSize().width,
                500, 30000, 500);

        pixelSizeSpinner = new JSpinner(pixelModel);
        panelSizeSpinner = new JSpinner(panelModel);
        
        addComponents();
        
        pixelSizeSpinner.addChangeListener(this);
        panelSizeSpinner.addChangeListener(this);
        
    }
    
    private void addComponents(){
        this.setLayout(new BorderLayout());
        
        JLabel pixelLabel = new JLabel("Pixels per nm: ", JLabel.RIGHT);
        JLabel panelLabel = new JLabel("Canvas size: ", JLabel.RIGHT);
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(pixelLabel);
        topPanel.add(pixelSizeSpinner);
        topPanel.add(panelLabel);
        topPanel.add(panelSizeSpinner);
        
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new FlowLayout());
        outerPanel.add(drawPanel);
        
        JScrollPane scrollPane = new JScrollPane(outerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        this.add(topPanel, "North");
        this.add(scrollPane, "Center");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSpinner source = (JSpinner)e.getSource();
        int value = (Integer)source.getModel().getValue();
        
        if(source == pixelSizeSpinner){
            DrawPanel.setPixelsPerNm(value);
            drawPanel.repaint();
        }
        
        if(source == panelSizeSpinner){
            drawPanel.setPreferredSize(new Dimension(value, value));
            drawPanel.getParent().invalidate();
            this.validate();
            this.repaint();
        }
    }
}
