/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import javax.swing.table.AbstractTableModel;

import org.springsalad.langevinsetup.DecayReaction;
import org.springsalad.langevinsetup.Global;

import java.util.ArrayList;

public class EditDecayReactionsTableModel extends AbstractTableModel {
    
    private final ArrayList<DecayReaction> defaultReactions;
    private final ArrayList<DecayReaction> simulationReactions;
    
    private final String [] columnNames = {"Molecule", 
        "Default Creation Rate (uM/s)", "Default Decay Rate (1/s)",
        "New Creation Rate (uM/s)", "New Decay Rate (1/s)"};
    
    public EditDecayReactionsTableModel(Global g, Simulation simulation){
        defaultReactions = new ArrayList<>();
        simulationReactions = new ArrayList<>();
        
        for(int i=0;i<g.getMolecules().size();i++){
            defaultReactions.add(g.getMolecule(i).getDecayReaction());
            simulationReactions.add(simulation.getMolecule(i).getDecayReaction());
        }
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
        DecayReaction dRxn = defaultReactions.get(rowIndex);
        DecayReaction sRxn = simulationReactions.get(rowIndex);
        if(columnIndex == 0){
            return dRxn.getName();
        } else if (columnIndex == 1){
            return dRxn.getCreationRate();
        } else if (columnIndex == 2){
            return dRxn.getDecayRate();
        } else if (columnIndex == 3){
            double sRate0 = sRxn.getCreationRate();
            if(dRxn.getCreationRate() == sRate0){
                return null;
            } else {
                return sRate0;
            }
        } else if(columnIndex == 4){
            double sRate1 = sRxn.getDecayRate();
            if(dRxn.getDecayRate() == sRate1){
                return null;
            } else {
                return sRate1;
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
        return col > 2;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        DecayReaction dRxn = defaultReactions.get(row);
        DecayReaction sRxn = simulationReactions.get(row);
        if(col == 3){
            if(value == null){
                sRxn.setCreationRate(dRxn.getCreationRate());
            } else {
                sRxn.setCreationRate(Double.parseDouble(value.toString()));
            }
        } else if(col == 4){
            if(value == null){
                sRxn.setDecayRate(dRxn.getDecayRate());
            } else {
                sRxn.setDecayRate(Double.parseDouble(value.toString()));
            }
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
