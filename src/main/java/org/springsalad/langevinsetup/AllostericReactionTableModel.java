/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.table.AbstractTableModel;

public class AllostericReactionTableModel extends AbstractTableModel {
    
    private final AllostericReaction reaction;
    
    private final String [] columnNames = {"Reaction Name", "Rate (s-1)"};
    
    public AllostericReactionTableModel(AllostericReaction reaction){
        this.reaction = reaction;
    }
    
    @Override
    public int getRowCount() {
        return 1;
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
            return reaction.getName();
        } else if (columnIndex == 1){
            return reaction.getRate();
        } else {
            return "Index out of bounds.";
        }
    }
    
    // Override this method so the table knows the columns that contain
    // double values.
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // The all cells are editable
    @Override
    public boolean isCellEditable(int row, int col){
        return true;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        switch(col){
            case 0:
                reaction.setName((String)value);
                break;
            case 1: 
                reaction.setRate((Double)value);
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        
        fireTableCellUpdated(row, col);
    }
}
