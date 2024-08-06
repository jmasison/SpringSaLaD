/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JTable;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.Fonts;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class BindingReactionBottomPanel extends JPanel {
    
    public BindingReactionBottomPanel(Global g, BindingReaction reaction){
        
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new BindingReactionTableModel(reaction));
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.NONNEGATIVE, false));
        table.setDefaultEditor(String.class, new ReactionNameCellEditor(g, reaction));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300,50));
//        JPanel scrollPanel = new JPanel();
//        scrollPanel.setLayout(new BorderLayout());
//        scrollPanel.add(scrollPane, "Center");
        
        this.add(scrollPane, "North");
        this.add(new AnnotationPanel(reaction.getAnnotation()), "Center");
        
    }
    
}
