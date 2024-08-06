/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import javax.swing.*;

import org.springsalad.helpersetup.Constraints;

public class DecayReactionTablePanel extends JPanel {
    
    public DecayReactionTablePanel(Global g){
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new DecayReactionTableModel(g));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.NONNEGATIVE, false));
        JScrollPane scrollPane = new JScrollPane(table);
        
        this.add(scrollPane, "Center");

    }
    
}
