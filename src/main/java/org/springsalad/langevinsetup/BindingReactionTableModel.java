/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

public class BindingReactionTableModel extends AbstractTableModel {
    
    private final BindingReaction reaction;
    
    private final String [] columnNames = {"Reaction Name",
                                        "On Rate (uM-1.s-1)", "Off Rate (s-1)",
                                        "Bond Length (nm)"};
    
    public BindingReactionTableModel(BindingReaction reaction){
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
            return reaction.getkon();
        } else if (columnIndex == 2){
            return reaction.getkoff();
        } else if (columnIndex == 3){
            return reaction.getBondLength();
        } else {
            return "Index out of bounds.";
        }
    }
    
    // Override this method so the table knows the columns that contain
    // boolean values.
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
                reaction.setkon((Double)value);
                boolean ret = reaction.checkOnRate();
                System.out.println(ret + " ");
                if(ret == false) {
                	String msg = "Kon is too large (I.e. exceeds the diffusion limited rate) for this reaction.\n";
                	msg += "Please consider reducing Kon or increasing the Radius or D of the participating Site Types.";
                	JOptionPane.showMessageDialog(null, msg);
                }
                break;
            case 2:
                reaction.setkoff((Double)value);
                break;
            case 3:
                reaction.setBondLength((Double)value);
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
