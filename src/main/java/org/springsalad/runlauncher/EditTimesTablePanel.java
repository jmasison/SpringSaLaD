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
import org.springsalad.helpersetup.ScientificCellRenderer;
import org.springsalad.langevinsetup.Global;
import org.springsalad.langevinsetup.ValueCellEditor;
import org.springsalad.langevinsetup.ValueTextField;

public class EditTimesTablePanel extends JPanel{
    
    public EditTimesTablePanel(Global g, Simulation simulation){
        
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new EditTimesTableModel(g, simulation));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.POSITIVE, true));
        table.setDefaultRenderer(Double.class, ScientificCellRenderer.getRendererInstance());
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JLabel label = new JLabel("System Times", JLabel.CENTER);
        label.setFont(Fonts.TITLEFONT);
        
        this.add(label, "North");
        this.add(scrollPane, "Center");
    }
    
}
