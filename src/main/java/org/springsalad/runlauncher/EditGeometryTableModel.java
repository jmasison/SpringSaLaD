/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import javax.swing.table.AbstractTableModel;

import org.springsalad.langevinsetup.BoxGeometry;
import org.springsalad.langevinsetup.Global;

public class EditGeometryTableModel extends AbstractTableModel {

    private final BoxGeometry defaultGeometry;
    private final BoxGeometry simulationGeometry;
    private final String [] columnNames = {"Direction", "Default Size (nm)",
                                                "New Size (nm)"};
    private final String [] rowNames = {"X", "Y", "Z intracellular",
                                                "Z extracellular"};
    
    public EditGeometryTableModel(Global g, Simulation simulation){
        defaultGeometry = g.getBoxGeometry();
        simulationGeometry = simulation.getBoxGeometry();
    }
    
    @Override
    public int getRowCount() {
        return rowNames.length;
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
        double defaultValue = returnSize(defaultGeometry, rowIndex);
        double simValue = returnSize(simulationGeometry, rowIndex);
        if(columnIndex == 0){
            return rowNames[rowIndex];
        } else if (columnIndex == 1){
            return defaultValue;
        } else if (columnIndex == 2){
            if(defaultValue == simValue){
                return null;
            } else {
                return simValue;
            }
        } else {
            return "Index out of bounds.";
        }
    }
    
    private Double returnSize(BoxGeometry boxGeometry, int rowIndex){
        switch(rowIndex){
                case 0:{
                    return boxGeometry.getX();
                }
                case 1:{
                    return boxGeometry.getY();
                }
                case 2:{
                    return boxGeometry.getZin();
                }
                case 3:{
                    return boxGeometry.getZout();
                }
                default:
                    return null;
            }
    }
    
    @Override
    public Class getColumnClass(int c){
        switch(c){
            case 0:
                return String.class;
            case 1:
                return Double.class;
            case 2:
                return Double.class;
            default:
                System.out.println("Unexpected index in EditGeometryTableModel"
                        + " getColumnClass");
                return String.class;
        }
    }
    
    // Only the tracking boolean is editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col > 1;
    }
    
     @Override
    public void setValueAt(Object value, int row, int col){
        double defaultValue = (Double)this.getValueAt(row, col-1);
        switch(row){
            case 0:
                if(value == null){
                    simulationGeometry.setX(defaultValue);
                } else {
                    simulationGeometry.setX((Double)value);
                }
                break;
            case 1:
                if(value == null){
                    simulationGeometry.setY(defaultValue);
                } else {
                    simulationGeometry.setY((Double)value);
                }
                break;
            case 2:
                if(value == null){
                    simulationGeometry.setZin(defaultValue);
                } else {
                    simulationGeometry.setZin((Double)value);
                }
                break;
            case 3:
                if(value == null){
                    simulationGeometry.setZout(defaultValue);
                } else {
                    simulationGeometry.setZout((Double)value);
                }
                break;
            default:
                System.out.println("Unexpected column index: " + col);
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
