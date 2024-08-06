/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class SimulationTableModel extends AbstractTableModel 
                                                implements SimulationListener {

    private final ArrayList<Simulation> simulations;
    private final String [] columnNames = {"Simulation Name", "Total Runs",
                            "Parallel", "Number of Simultaneous Runs", "Status"};
    
    public SimulationTableModel(SimulationManager sm){
        this.simulations = sm.getSimulations();
        for(Simulation sim : simulations){
            sim.addSimulationListener(this);
        }
    }
    
    @Override
    public int getRowCount() {
        return simulations.size();
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
            return simulations.get(rowIndex).getSimulationName();
        } else if (columnIndex == 1){
            return simulations.get(rowIndex).getRunNumber();
        } else if (columnIndex == 2){
            return simulations.get(rowIndex).isParallel();
        } else if (columnIndex == 3){
            return simulations.get(rowIndex).getNumberSimultaneousRuns();
        } else if (columnIndex == 4){
            return simulations.get(rowIndex).getStatus();
        } else {
            return "Index out of bounds.";
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // The all cells are editable
    @Override
    public boolean isCellEditable(int row, int col){
        if(simulations.get(row).isRunning()){
            return false;
        } else {
            if(col < 3){
                return true;
            } else if(col == 3){
                return simulations.get(row).isParallel();
            } else {
                return false;
            }
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        Simulation sim = simulations.get(row);
        switch(col){
            case 0:
                sim.setSimulationName((String)value);
                break;
            case 1: 
                sim.setRunNumber((Integer)value);
                break;
            case 2:
                boolean val = (Boolean) value;
                sim.setParallel(val);
                if(!val){
                    sim.setNumberSimultaneousRuns(1);
                }
                break;
            case 3:
                sim.setNumberSimultaneousRuns((Integer) value);
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        this.fireTableRowsUpdated(row, row);
    }
    
    /* *************** SIMULATION LISTENER METHOD *************************/
    @Override
    public void simulationChanged(SimulationEvent event){
        this.fireTableDataChanged();
    }
    
    
}
