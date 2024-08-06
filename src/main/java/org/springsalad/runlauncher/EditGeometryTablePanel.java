/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.awt.BorderLayout;
import javax.swing.*;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.Fonts;
import org.springsalad.langevinsetup.Global;
import org.springsalad.langevinsetup.ValueCellEditor;
import org.springsalad.langevinsetup.ValueTextField;

public class EditGeometryTablePanel extends JPanel {
    
    public EditGeometryTablePanel(Global g, Simulation simulation){
        
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new EditGeometryTableModel(g, simulation));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.POSITIVE, true));
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JLabel label = new JLabel("System Geometry", JLabel.CENTER);
        label.setFont(Fonts.TITLEFONT);
        
        this.add(label, "North");
        this.add(scrollPane, "Center");
        
    }
    
}
