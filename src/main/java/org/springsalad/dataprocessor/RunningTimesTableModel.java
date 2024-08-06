/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.dataprocessor;

import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

public class RunningTimesTableModel extends AbstractTableModel {
    
    public final static String DAYS = "Days";
    public final static String HOURS = "Hours";
    public final static String MINUTES = "Minutes";
    public final static String SECONDS = "Seconds";
    public final static String [] ALL_UNITS = {SECONDS, MINUTES, 
                                    HOURS, DAYS};
    
    private final RunningTimesDataHolder runData;
    private String unit;
    private double unitScale;
    
    public RunningTimesTableModel(RunningTimesDataHolder runData){
        this.runData = runData;
        unit = "s";
        unitScale = 1;
    }
    
    /* *****************   SET THE UNIT SCALE ***************************/
    // Accepts one of the public static strings
    public void setUnitScale(String newUnit){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        switch(newUnit){
            case SECONDS:
                unit = "sec";
                unitScale = 1;
                break;
            case MINUTES:
                unit = "min";
                unitScale = 1/60.0;
                break;
            case HOURS:
                unit = "hr";
                unitScale = 1/3600.0;
                break;
            case DAYS:
                unit = "days";
                unitScale = 1/(24*3600.0);
                break;
            default:
                System.out.println("Unexpected new unit: " + newUnit);
        }
        fireTableDataChanged();
        // </editor-fold>
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return runData.getTimes().size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    
    @Override
    public String getColumnName(int col){
        switch(col){
            case 0:
                return "Run";
            case 1:
                return "Time (" + unit + ")";
            case 2:
                return "Average";
            case 3:
                return "St. Dev.";
            default:
                return "Unexpected column index.";
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if(col == 0){
            return runData.getRunNumber(row);
        } else if(col == 1){
            return IOHelp.DF[3].format(unitScale*runData.getTime(row));
        } else if(col == 2){
            if(row == 0){
                return IOHelp.DF[3].format(unitScale*runData.getAverage());
            } else {
                return "";
            }
        } else if(col == 3){
            if(row == 0){
                return IOHelp.DF[3].format(unitScale*runData.getStDev());
            } else {
                return "";
            }
        } else {
            return "Unexpected column index.";
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        return String.class;
    }
    
    // Nothing is editable
    @Override
    public boolean isCellEditable(int row, int col){
        return false;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        // NO NEED TO IMPLEMENT
    }
    
}
