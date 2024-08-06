/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import javax.swing.table.*;

public class DecayReactionTableModel extends AbstractTableModel {
    
    private final ArrayList<DecayReaction> reactions = new ArrayList<>();
    
    private final String [] columnNames = {"Molecule", "Creation Rate (uM/s)", "Decay Rate (1/s)"};
    
    public DecayReactionTableModel(Global g){
        for(Molecule molecule: g.getMolecules()){
            reactions.add(molecule.getDecayReaction());
        }
    }
    
    @Override
    public int getRowCount() {
        return reactions.size();
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
        if(columnIndex == 0){
            return reactions.get(rowIndex).getName();
        } else if (columnIndex == 1){
            return reactions.get(rowIndex).getCreationRate();
        } else if (columnIndex == 2){
            return reactions.get(rowIndex).getDecayRate();
        } else {
            return "Index out of bounds!";
        }
    }
    
    // Override this method so the table knows that two of the columns contain 
    // double values.
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col != 0;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        DecayReaction reaction = reactions.get(row);
        if(col == 1){
            reaction.setCreationRate(Double.parseDouble(value.toString()));
        } else if(col == 2){
            reaction.setDecayRate((Double)value);
        }
        
        fireTableCellUpdated(row, col);
    }
    
    
    
}
