/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import org.springsalad.langevinsetup.Global;
import org.springsalad.langevinsetup.TransitionReaction;

public class EditTransitionReactionsTableModel extends AbstractTableModel {
    
    private final ArrayList<TransitionReaction> defaultReactions;
    private final ArrayList<TransitionReaction> simulationReactions;
    
    private final String [] columnNames = {"Reaction", "Default Rate (1/s)", 
                                                    "New Rate (1/s)"};
    
    public EditTransitionReactionsTableModel(Global g, Simulation sim){
        defaultReactions = g.getTransitionReactions();
        simulationReactions = sim.getTransitionReactions();
    }
    
    @Override
    public int getRowCount() {
        return defaultReactions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int col){
        return columnNames[col];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        double defaultRate = defaultReactions.get(rowIndex).getRate();
        double simRate = simulationReactions.get(rowIndex).getRate();
        if(columnIndex == 0){
            return defaultReactions.get(rowIndex).getName();
        } else if (columnIndex == 1){
            return defaultRate;
        } else if (columnIndex == 2){
            if(defaultRate == simRate){
                return null;
            } else {
                return simRate;
            }
        } else {
            return "Index out of bounds!";
        }
    }
    
    // Override this method so the table knows that two of the columns contain 
    // double values.
    @Override
    public Class getColumnClass(int c){
        if(c == 0){
            return String.class;
        } else {
            return Double.class;
        }
    }
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col > 1;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        TransitionReaction dRxn = defaultReactions.get(row);
        TransitionReaction sRxn = simulationReactions.get(row);
        if(col == 2){
            if(value == null){
                sRxn.setRate(dRxn.getRate());
            } else {
                sRxn.setRate(Double.parseDouble(value.toString()));
            }
        }
        
        fireTableCellUpdated(row, col);
    }
    
    
}
