

package org.springsalad.runlauncher;

import javax.swing.table.AbstractTableModel;

import org.springsalad.langevinsetup.Global;
import org.springsalad.langevinsetup.SystemTimes;

public class EditTimesTableModel extends AbstractTableModel {
    
    private final SystemTimes defaultTimes;
    private final SystemTimes simulationTimes;
    private final String [] columnNames = {"Time", "Default", "New Value"};
    private final String [] rowNames;
    
    public EditTimesTableModel(Global g, Simulation simulation){
        this.defaultTimes = g.getSystemTimes();
        this.simulationTimes = simulation.getSystemTimes();
        this.rowNames = SystemTimes.ALL_TIME_LABELS;
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
        double defaultTime = returnTime(defaultTimes, rowIndex);
        double simulationTime = returnTime(simulationTimes, rowIndex);;
        if(columnIndex == 0){
            return rowNames[rowIndex];
        } else if (columnIndex == 1){
            return defaultTime;
        } else if (columnIndex == 2){
            if(defaultTime == simulationTime){
                return null;
            } else {
                return simulationTime;
            }
        } else {
            return "Index out of bounds.";
        }
    }
    
    private Double returnTime(SystemTimes systemTimes, int rowIndex){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        switch(rowIndex){
                case 0:{
                    return systemTimes.getTotalTime();
                }
                case 1:{
                    return systemTimes.getdt();
                }
                case 2:{
                    return systemTimes.getdtspring();
                }
                case 3:{
                    return systemTimes.getdtdata();
                }
                case 4:{
                    return systemTimes.getdtimage();
                }
                default:
                    return null;
            }
        // </editor-fold>
    }
    
     // Override this method so the table knows the columns that contain
    // boolean values.
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
                System.out.println("Unexpected index in EditTimesTableModel"
                        + " getColumnClass: " + c);
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
        Double defaultValue = (Double)this.getValueAt(row, col-1);
        switch(row){
            case 0:
                if(value == null){
                    simulationTimes.setTotalTime(defaultValue);
                } else {
                    simulationTimes.setTotalTime((Double)value);
                }
                break;
            case 1:
                if(value == null){
                    simulationTimes.setdt(defaultValue);
                } else {
                    simulationTimes.setdt((Double)value);
                }
                break;
            case 2:
                if(value == null){
                    simulationTimes.setdtspring(defaultValue);
                } else {
                    simulationTimes.setdtspring((Double)value);
                }
                break;
            case 3:
                if(value == null){
                    simulationTimes.setdtdata(defaultValue);
                } else {
                    simulationTimes.setdtdata((Double)value);
                }
                break;
            case 4:
                if(value == null){
                    simulationTimes.setdtimage(defaultValue);
                } else {
                    simulationTimes.setdtimage((Double)value);
                }
                break;
            default:
                System.out.println("Unexpected column index: " + col);
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
