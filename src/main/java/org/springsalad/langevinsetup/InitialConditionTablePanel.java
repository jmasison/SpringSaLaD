/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.JScrollPane;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.PopUp;

import java.awt.event.*;

public class InitialConditionTablePanel extends JPanel implements ActionListener {
    
    private final JButton initialPositionsButton;
    private final JTable table;
    private final Global g;
    
    public InitialConditionTablePanel(Global g){
        this.setLayout(new BorderLayout());
        this.g = g;
        table = new JTable(new InitialConditionTableModel(g));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.NONNEGATIVE, false));
        table.setDefaultEditor(Integer.class, new ValueCellEditor(ValueTextField.INTEGER, Constraints.NONNEGATIVE, false));

        JScrollPane scrollPane = new JScrollPane(table);

        JLabel label = new JLabel("Initial Conditions", JLabel.CENTER);
        label.setFont(Fonts.TITLEFONT);

        this.add(label, "North");

        this.add(scrollPane,"Center");
        
        initialPositionsButton = new JButton("Set Initial Positions");
        initialPositionsButton.addActionListener(this);
        JPanel southPanel = new JPanel();
        southPanel.add(initialPositionsButton);
        this.add(southPanel, "South");
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int index = table.getSelectedRow();
        if(index == -1){
            PopUp.warning("No molecule selected.");
            return;
        }
        InitialCondition ic = g.getMolecule(index).getInitialCondition();
        if(ic.usingRandomInitialPositions()){
            PopUp.warning("This molecule is currently set to use\n"
                    + "random initial positions.");
        } else {
            InitialPositionFrame ipf = new InitialPositionFrame(ic);
        }
        // </editor-fold>
    }
    
}
