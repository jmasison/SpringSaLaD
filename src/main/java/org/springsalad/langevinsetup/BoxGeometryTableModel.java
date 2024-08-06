/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.table.AbstractTableModel;

public class BoxGeometryTableModel extends AbstractTableModel {
    
    private final BoxGeometry boxGeometry;
    
    private final String [] columnNames = {"Direction", "Size (nm)",
                                "Partition Number", "Partition Size (nm)", ""};
    
    private final String [] direction = {"X", "Y", "Z intracellular",
                                                "Z extracellular"};
    
    
    public BoxGeometryTableModel(BoxGeometry boxGeometry){
        this.boxGeometry = boxGeometry;
    }

    @Override
    public int getRowCount() {
        return 4;
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
            return direction[rowIndex];
        } else if (columnIndex == 1){
            if(rowIndex == 0){
                return boxGeometry.getX();
            } else if(rowIndex == 1){
                return boxGeometry.getY();
            } else if (rowIndex == 2){
                return boxGeometry.getZin();
            } else if (rowIndex == 3){
                return boxGeometry.getZout();
            } else {
                return null;
            }
        } else if (columnIndex == 2){
            if(rowIndex < 3){
                return boxGeometry.getNpart(rowIndex);
            } else {
                return null;
            }
        } else if (columnIndex == 3){
            if(rowIndex < 3){
                return boxGeometry.getDpart(rowIndex);
            } else {
                return null;
            }
        } else {
            return "";
        }
    }
    
    // Override this method so the table knows the columns that contain
    // boolean values.
    @Override
    public Class getColumnClass(int c){
        if(c < columnNames.length - 1){
            return getValueAt(0,c).getClass();
        } else {
            return String.class;
        }
    }
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        if(row != 3){
            return (col != 0 && col != columnNames.length-1);
        } else {
            return col == 1;
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        switch(col){
            case 1:
                if(row == 0){
                    boxGeometry.setX((Double)value);
                } else if(row == 1){
                    boxGeometry.setY((Double)value);
                } else if (row == 2){
                    boxGeometry.setZin((Double)value);
                } else if (row == 3){
                    boxGeometry.setZout((Double)value);
                }
                break;
            case 2: 
                if(row < 3){
                    boxGeometry.setNpart(row, (Integer)value);
                }
                break;
            case 3:
                
                break;
            default:
                System.out.println("Unexpected column index!");
        }
        if(row != 3){
            fireTableCellUpdated(row, 1);
            fireTableCellUpdated(row, 2);
            fireTableCellUpdated(row, 3);
        } else {
            fireTableCellUpdated(row, 1);
            fireTableCellUpdated(row-1, 3);
        }
    }
}
