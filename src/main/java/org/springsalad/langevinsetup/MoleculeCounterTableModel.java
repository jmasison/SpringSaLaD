/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import javax.swing.table.*;

public class MoleculeCounterTableModel extends AbstractTableModel {
    
    private final ArrayList<MoleculeCounter> countData = new ArrayList<>();
    
    private final String [] columnNames = {"Molecule", "Count Total", 
                                                "Count Free", "Count Bound"};
    
    public MoleculeCounterTableModel(Global g){
        for(Molecule molecule : g.getMolecules()){
            countData.add(molecule.getMoleculeCounter());
        }
    }

    @Override
    public int getRowCount() {
        return countData.size();
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
        MoleculeCounter data = countData.get(rowIndex);
        if(columnIndex == 0){
            return data.getMoleculeName();
        } else if (columnIndex == 1){
            return data.countTotal();
        } else if (columnIndex == 2){
            return data.countFree();
        } else if (columnIndex == 3){
            return data.countBound();
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
        MoleculeCounter data = countData.get(row);
        switch(col){
            case 1:
                data.setMeasurement(MoleculeCounter.TOTAL, (Boolean)value);
                break;
            case 2: 
                data.setMeasurement(MoleculeCounter.FREE, (Boolean)value);
                break;
            case 3:
                data.setMeasurement(MoleculeCounter.BOUND, (Boolean)value);
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
