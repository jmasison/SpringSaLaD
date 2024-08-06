/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import javax.swing.table.*;

public class StateCounterTableModel extends AbstractTableModel {
    
    private final ArrayList<StateCounter> stateCounters = new ArrayList<>();
    private final ArrayList<Molecule> molecules;
    
    private final ArrayList<String> moleculeNames = new ArrayList<>();
    private final ArrayList<String> typeNames = new ArrayList<>();
    private final ArrayList<String> stateNames = new ArrayList<>();
    
    private final String [] columnNames = {"Molecule", "Site Type", "State",
                            "Count Total", "Count Free", "Count Bound"};
    
    public StateCounterTableModel(Global g){
        molecules = g.getMolecules();
        for(Molecule molecule : molecules){
            ArrayList<SiteType> tempTypes = molecule.getTypeArray();
            for(int i=0;i<tempTypes.size();i++){
                ArrayList<State> tempStates = tempTypes.get(i).getStates();
                for(int j=0;j<tempStates.size();j++){
                    
                    if(i == 0 && j ==0){
                        moleculeNames.add(molecule.getName());
                    } else {
                        moleculeNames.add("");
                    }
                    
                    if(j==0){
                        typeNames.add(tempTypes.get(i).getName());
                    } else {
                        typeNames.add("");
                    }
                    
                    stateNames.add(tempStates.get(j).getName());
                    stateCounters.add(tempStates.get(j).getStateCounter());
                }
            }
        }
    }

    @Override
    public int getRowCount() {
        return stateCounters.size();
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
        StateCounter counter = stateCounters.get(rowIndex);
        if(columnIndex == 0){
            return moleculeNames.get(rowIndex);
        } else if (columnIndex == 1){
            return typeNames.get(rowIndex);
        } else if (columnIndex == 2){
            return stateNames.get(rowIndex);
        } else if (columnIndex == 3){
            return counter.countTotal();
        } else if (columnIndex == 4){
            return counter.countFree();
        } else if (columnIndex == 5){
            return counter.countBound();
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
        return col > 2;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        StateCounter counter = stateCounters.get(row);
        switch(col){
            case 3:
                counter.setMeasurement(MoleculeCounter.TOTAL, (Boolean)value);
                break;
            case 4: 
                counter.setMeasurement(MoleculeCounter.FREE, (Boolean)value);
                break;
            case 5:
                counter.setMeasurement(MoleculeCounter.BOUND, (Boolean)value);
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
