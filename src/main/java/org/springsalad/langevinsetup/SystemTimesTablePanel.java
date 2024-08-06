/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import javax.swing.*;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.ScientificCellRenderer;

public class SystemTimesTablePanel extends JPanel {
    
    public SystemTimesTablePanel(SystemTimes systemTimes){
        
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new SystemTimesTableModel(systemTimes));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.POSITIVE, false));
        table.setDefaultRenderer(Double.class, ScientificCellRenderer.getRendererInstance());
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JLabel label = new JLabel("Time Specifications", JLabel.CENTER);
        label.setFont(Fonts.TITLEFONT);
        
        this.add(label, "North");
        this.add(scrollPane, "Center");
    }
    
}
