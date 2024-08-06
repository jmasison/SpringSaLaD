/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import javax.swing.table.AbstractTableModel;

import org.springsalad.langevinsetup.BoxGeometry;
import org.springsalad.langevinsetup.Global;
import org.springsalad.langevinsetup.InitialCondition;
import org.springsalad.langevinsetup.SystemGeometry;

public class EditInitialConditionsTableModel extends AbstractTableModel {
    
    private final InitialCondition [] defaultICs;
    private final BoxGeometry defaultGeometry;
    private final InitialCondition [] simulationICs;
    private final BoxGeometry simulationGeometry;
    
    private final String [] columnNames = {"Molecule", "Default Number",
        "Default Conc. (uM)", "New Number", "New Conc. (uM)"};
    
    private final String [] moleculeNames;
    
    public EditInitialConditionsTableModel(Global g, Simulation simulation){
        defaultICs = new InitialCondition[g.getMolecules().size()];
        defaultGeometry = g.getBoxGeometry();
        
        simulationICs = new InitialCondition[defaultICs.length];
        simulationGeometry = simulation.getBoxGeometry();
        
        moleculeNames = new String[defaultICs.length];
        
        for(int i=0;i<defaultICs.length;i++){
            moleculeNames[i] = g.getMolecule(i).getName();
            defaultICs[i] = g.getMolecule(i).getInitialCondition();
            simulationICs[i] = simulation.getMolecule(i).getInitialCondition();
        }
    }
    
    @Override
    public int getRowCount() {
        return moleculeNames.length;
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
        int defaultNumber = defaultICs[rowIndex].getNumber();
        double defaultConc = getConcentration(defaultICs[rowIndex], defaultGeometry);
        int simNumber = simulationICs[rowIndex].getNumber();
        double simConc = getConcentration(simulationICs[rowIndex], simulationGeometry);
        if(columnIndex == 0){
            return moleculeNames[rowIndex];
        } else if (columnIndex == 1){
            return defaultNumber;
        } else if (columnIndex == 2){
            return defaultConc;
        } else if (columnIndex == 3){
            if(simNumber == defaultNumber){
                return null;
            } else {
                return simNumber;
            }
        } else if (columnIndex == 4){
            if(defaultConc == simConc){
                return null;
            } else {
                return simConc;
            }
        } else {
            return "Index out of bounds.";
        }
    }
    
    private Double getConcentration(InitialCondition ic, BoxGeometry geometry){
        double volume;
        String location = ic.getMoleculeLocation();
        switch(location){
            case SystemGeometry.INSIDE:{
                volume = geometry.getVolumeIn();
                break;
            }
            case SystemGeometry.OUTSIDE:{
                volume = geometry.getVolumeOut();
                break;
            }
            case SystemGeometry.MEMBRANE:{
                volume = geometry.getVolumeTotal();
                break;
            }
            default:
                volume = -100;
                System.out.println("getConcentration received unexpected input " + location);
        }
        return (Double)ic.getConcentration(volume);
    }
    
    @Override
    public Class getColumnClass(int c){
        switch(c){
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Double.class;
            case 3:
                return Integer.class;
            case 4:
                return Double.class;
            default:
                System.out.println("Unexpected value in "
                                + "EditICTableModel.getColumnClass: " + c);
                return String.class;
        }
    }
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col > 2;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        InitialCondition simIC = simulationICs[row];
        InitialCondition defaultIC = defaultICs[row];
        switch(col){
            case 3:
                if(value == null){
                    simIC.setNumber(defaultIC.getNumber());
                } else {
                    simIC.setNumber((Integer)value);
                }
                break;
            case 4:
                double defaultVolume;
                double simVolume;
                switch (simIC.getMoleculeLocation()) {
                    case SystemGeometry.INSIDE:
                        defaultVolume = defaultGeometry.getVolumeIn();
                        simVolume = simulationGeometry.getVolumeIn();
                        break;
                    case SystemGeometry.OUTSIDE:
                        defaultVolume = defaultGeometry.getVolumeOut();
                        simVolume = simulationGeometry.getVolumeOut();
                        break;
                    default:
                        defaultVolume = defaultGeometry.getVolumeTotal();
                        simVolume = simulationGeometry.getVolumeTotal();
                        break;
                }
                if(value == null){
                    simIC.setConcentration(defaultIC.getConcentration(defaultVolume), simVolume);
                } else {
                    simIC.setConcentration((Double)value, simVolume);
                }
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        
        fireTableCellUpdated(row, 3);
        fireTableCellUpdated(row, 4);
        
    }
    
    
}
