/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springsalad.dataprocessor.clusterdata;

import javax.swing.table.AbstractTableModel;

@Deprecated
public class CDHistogramTableModel extends AbstractTableModel {
    
    private final ClusterDataHistogram histogram;
    private int timeIndex;  // Used to get the correct array from the histogram
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit;
    private double unitScale;
    
    public CDHistogramTableModel(ClusterDataHistogram histogram){
        this.histogram = histogram;
        timeIndex = 0;
        determineTimeUnit();
    }
    
    /* *****************  GET THE BUILDER ********************************/
    public ClusterDataHistogram getHistogram(){
        return histogram;
    }
    
    /* ***************** UPDATE WITH NEW BUILDER DATA ********************/
    public void setTimeIndex(int timeIndex){
        this.timeIndex = timeIndex;
        fireTableStructureChanged();
    }

    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(histogram.getTimes().length > 1){
            double dt = histogram.getTime(1);
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
    
    public String getTimeUnit(){
        return unit;
    }
    
    public double getUnitScale(){
        return unitScale;
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return histogram.getTotalBins();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }
    
    @Override
    public String getColumnName(int col){
        switch(col){
            case 0:
                return "Size Range";
            case 1:
                return "Average Number";
            case 2:
                return "St. Dev.";
            default:
                return "Col out of range";
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        switch(col){
            case 0:
                return histogram.binName(row);
            case 1:
                return histogram.getAverageSizes(timeIndex, row);
            case 2:
                return histogram.getStDevSizes(timeIndex, row);
            default:
                return "Col out of bounds.";
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        if(c == 0){
            return String.class;
        } else {
            return Double.class;
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
