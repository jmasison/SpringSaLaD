/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import javax.swing.table.*;

public class BondCounterTableModel extends AbstractTableModel {
    
    private final ArrayList<BondCounter> bondData = new ArrayList<>();
    
    private final String [] columnNames = {"Reaction Name", "Count Bonds"};

    public BondCounterTableModel(Global g){
        for(BindingReaction reaction: g.getBindingReactions()){
            bondData.add(reaction.getBondCounter());
        }
    }
    
    @Override
    public int getRowCount() {
        return bondData.size();
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
            return bondData.get(rowIndex).getReactionName();
        } else if(columnIndex == 1){
            return bondData.get(rowIndex).isCounted();
        } else {
            return "Index out of bounds.";
        }
    }
    
    // Override this method so the table knows the one column contains 
    // boolean values.
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // The reaction names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col != 0;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        BondCounter data = bondData.get(row);
        if(col == 1){
            data.setCounted((Boolean)value);
        }
        
        fireTableCellUpdated(row, col);
    }
    
    
}
