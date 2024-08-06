/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springsalad.langevinsetup;

import javax.swing.table.AbstractTableModel;

public class InitialPositionTableModel extends AbstractTableModel {
    
    private final InitialCondition ic;
    private final String [] columnNames = {"Molecule", "x", "y", "z"};
    
    public InitialPositionTableModel(InitialCondition ic){
        this.ic = ic;
    }
    
    // Override this method so the table knows the columns that contain
    // boolean values.
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
    public int getRowCount() {
        return ic.getNumber();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    
    @Override
    public String getColumnName(int col){
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0){
            return rowIndex;
        } else {
            // This check should be redundant because we I don't allow this
            // table to be created unless we've initialized the initial position
            // array.
            if(ic.hasInitialPositions()){
                switch(columnIndex){
                    case 1:
                        return ic.getInitialX(rowIndex);
                    case 2:
                        return ic.getInitialY(rowIndex);
                    case 3:
                        return ic.getInitialZ(rowIndex);
                    default:
                        return "IP Tabel model out of bounds.";
                }
            } else {
                return 0.0;
            }
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        switch(col){
            case 1:
                ic.setInitialX(row, (Double)value);
                break;
            case 2:
                ic.setInitialY(row, (Double)value);
                break;
            case 3:
                ic.setInitialZ(row, (Double)value);
                break;
        }
    }
    
}
