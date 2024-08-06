/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.dataprocessor;

import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

public class RawDataTableModel extends AbstractTableModel{

    private RawDataHolder rawData;
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit;
    private double unitScale;
    
    public RawDataTableModel(RawDataHolder rawData){
        this.rawData = rawData;
        determineTimeUnit();
    }
    
    /* ***************** CHANGE THE RAW DATA *****************************/
    
    public void setRawData(RawDataHolder rawData){
        this.rawData = rawData;
        determineTimeUnit();
        fireTableStructureChanged();
    }
    
    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(rawData != null){
            double dt = rawData.getTimes().get(1);
            if(dt > 0.1){
                unit = "s";
                unitScale = 1;
            } else if(1E3*dt > 0.1){
                unit = "ms";
                unitScale = 1E3;
            } else if(1E6*dt > 0.1){
                unit = "us";
                unitScale = 1E6;
            } else if(1E9*dt > 0.1){
                unit = "ns";
                unitScale = 1E9;
            } else {
                unit = "ps";
                unitScale = 1E12;
            }
        }
        // </editor-fold>
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        if(rawData != null){
            return rawData.getTimes().size();
        } else {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        if(rawData != null){
            return rawData.getRunNumber() + 1;
        } else {
            return 1;
        }
    }
    
    @Override
    public String getColumnName(int col){
        if(rawData != null){
            if(col == 0){
                return "Time (" + unit + ")";
            } else {
                return rawData.getRunsHeaders().get(col-1);
            }
        } else {
            return "NO DATA TO DISPLAY";
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if(rawData != null){
            if(col == 0){
                return IOHelp.DF[3].format(unitScale*rawData.getTimes().get(row));
            } else {
                return rawData.getRunData(col-1).get(row);
            }
        } else {
            return null;
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        if(c == 0){
            return String.class;
        } else {
            return Integer.class;
        }
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
