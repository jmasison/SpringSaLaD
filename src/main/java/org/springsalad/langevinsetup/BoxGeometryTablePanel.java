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

public class BoxGeometryTablePanel extends JPanel {
    
    
    public BoxGeometryTablePanel(BoxGeometry boxGeometry){
        
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new BoxGeometryTableModel(boxGeometry));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Integer.class, new ValueCellEditor(ValueTextField.INTEGER, Constraints.POSITIVE, false));
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.POSITIVE, false));
        
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JLabel label = new JLabel("Geometry Specifications", JLabel.CENTER);
        label.setFont(Fonts.TITLEFONT);
        
        this.add(label, "North");
        this.add(scrollPane, "Center");
        
    }
    
}
