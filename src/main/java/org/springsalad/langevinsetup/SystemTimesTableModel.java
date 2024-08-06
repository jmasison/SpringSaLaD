/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import javax.swing.table.AbstractTableModel;

public class SystemTimesTableModel extends AbstractTableModel { 
    
    private final SystemTimes systemTimes;
    
    private final String [] columnNames = {"Time", "Value (s)"};
    
    private final String [] timeString;
    
    
    public SystemTimesTableModel(SystemTimes systemTimes){
        this.systemTimes = systemTimes;
        this.timeString = SystemTimes.ALL_TIME_LABELS;
    }

    @Override
    public int getRowCount() {
        return timeString.length;
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
            return timeString[rowIndex];
        } else if (columnIndex == 1){
            switch(rowIndex){
                case 0:
                    return systemTimes.getTotalTime();
                case 1:
                    return systemTimes.getdt();
                case 2:
                    return systemTimes.getdtspring();
                case 3:
                    return systemTimes.getdtdata();
                case 4:
                    return systemTimes.getdtimage();
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
    
    // Override this method so the table knows the columns that contain
    // double values.
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // Time names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col != 0;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        switch(row){
            case 0:
                systemTimes.setTotalTime((Double)value);
                break;
            case 1:
                systemTimes.setdt((Double)value);
                break;
            case 2:
                systemTimes.setdtspring((Double) value);
                break;
            case 3:
                systemTimes.setdtdata((Double)value);
                break;
            case 4:
                systemTimes.setdtimage((Double)value);
                break;
        }
            
        
        fireTableCellUpdated(row, col);
    }
}
