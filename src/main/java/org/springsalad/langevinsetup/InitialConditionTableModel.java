/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.table.AbstractTableModel;

public class InitialConditionTableModel extends AbstractTableModel {

    private final Global g;
    
    private final String [] columnNames = {"Molecule", "Number",
        "Concentration (uM)", "Random Init. Positions"};
    
    public InitialConditionTableModel(Global g){
        this.g = g;
    }

    @Override
    public int getRowCount() {
        return g.getMolecules().size();
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
        InitialCondition ic = g.getMolecule(rowIndex).getInitialCondition();
        if(columnIndex == 0){
            return ic.getMoleculeName();
        } else if (columnIndex == 1){
            return ic.getNumber();
        } else if (columnIndex == 2){
            double volume;
            BoxGeometry boxGeometry = g.getBoxGeometry();
            if(ic.getMoleculeLocation().equals(SystemGeometry.INSIDE)){
                volume = boxGeometry.getVolumeIn();
            } else if (ic.getMoleculeLocation().equals(SystemGeometry.OUTSIDE)){
                volume = boxGeometry.getVolumeOut();
            } else {
                volume = boxGeometry.getVolumeTotal();
            }
            return ic.getConcentration(volume);
        } else if(columnIndex == 3){
            return ic.usingRandomInitialPositions();
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
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col != 0;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        InitialCondition ic = g.getMolecule(row).getInitialCondition();
        switch(col){
            case 1:
                ic.setNumber((Integer)value);
                break;
            case 2: 
                double volume;
                BoxGeometry boxGeometry = g.getBoxGeometry();
                if(ic.getMoleculeLocation().equals(SystemGeometry.INSIDE)){
                    volume = boxGeometry.getVolumeIn();
                } else if(ic.getMoleculeLocation().equals(SystemGeometry.OUTSIDE)){
                    volume = boxGeometry.getVolumeOut();
                } else {
                    volume = boxGeometry.getVolumeTotal();
                }
                ic.setConcentration((Double)value, volume);
                break;
            case 3:
                ic.setUsingRandomInitialPositions((Boolean) value);
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        
        fireTableCellUpdated(row, 1);
        fireTableCellUpdated(row, 2);
        
    }
}
