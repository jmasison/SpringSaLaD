/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.springsalad.langevinsetup.Global;

public class EditSimulationFrame extends JFrame implements ActionListener{
    
    private final Simulation simulation;
    private final JButton okButton;
    private final EditTimesTablePanel simulationTimesTablePanel;
    private final EditGeometryTablePanel simulationGeometryTablePanel;
    private final EditInitialConditionsTablePanel simulationICPanel;
    private final EditReactionsTabbedPane reactionPane;
    
    public EditSimulationFrame(Global g, Simulation simulation){
        super("Edit Simulation Parameters");
        okButton = new JButton("Finish");
        this.simulation = simulation;
        
        simulationTimesTablePanel = new EditTimesTablePanel(g, simulation);
        simulationGeometryTablePanel = new EditGeometryTablePanel(g, simulation);
        simulationICPanel = new EditInitialConditionsTablePanel(g, simulation);
        reactionPane = new EditReactionsTabbedPane(g, simulation);
        
        Container c = this.getContentPane();
        
        JTabbedPane pane = new JTabbedPane();
        pane.insertTab("System Times", null, simulationTimesTablePanel, null, 0);
        pane.insertTab("System Geometry", null, simulationGeometryTablePanel, null, 1);
        pane.insertTab("Initial Conditions", null, simulationICPanel, null, 2);
        pane.insertTab("Reaction Parameters", null, reactionPane, null, 3);
        
        c.add(pane, "Center");
        
        JPanel southPanel = new JPanel();
        southPanel.add(okButton);
        c.add(southPanel, "South");
        
        okButton.addActionListener(this);
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        simulation.writeFile();
        this.setVisible(false);
        this.dispose();
    }
    
}
