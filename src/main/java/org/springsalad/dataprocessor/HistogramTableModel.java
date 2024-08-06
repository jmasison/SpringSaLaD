
package org.springsalad.dataprocessor;

import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

public class HistogramTableModel extends AbstractTableModel {
    
    private final HistogramBuilder builder;
    private boolean binsAsRows;
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit;
    private double unitScale;
    
    
    public HistogramTableModel(HistogramBuilder builder){
        this.builder = builder;
        binsAsRows = true;
        determineTimeUnit();
    }
    
    /* *****************  GET THE BUILDER ********************************/
    public HistogramBuilder getHistogramBuilder(){
        return builder;
    }
    
    /* ***************** UPDATE WITH NEW BUILDER DATA ********************/
    public void updateBuilder(){
        determineTimeUnit();
        fireTableStructureChanged();
    }
    
    /* ******************  CHANGE THE BINS AS ROWS FLAG ******************/
    public void setBinsAsRow(boolean bool){
        binsAsRows = bool;
        fireTableStructureChanged();
    }
    
    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(builder.getTimes().size() > 1){
            double dt = builder.getTimes().get(1);
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
        if(binsAsRows){
            return builder.getTotalBins();
        } else {
            return builder.getTimes().size();
        }
    }

    @Override
    public int getColumnCount() {
        if(binsAsRows){
            return builder.getTimes().size() + 1;
        } else {
            return builder.getTotalBins() + 1;
        }
    }
    
    @Override
    public String getColumnName(int col){
        if(binsAsRows){
            if(col == 0){
                return "RANGE";
            } else {
                double value = unitScale*builder.getTimes().get(col-1);
                return IOHelp.DF[3].format(value) + " " + unit;
            }
        } else {
            if(col == 0){
                return "Time (" + unit + ")";
            } else {
                return builder.binName(col-1);
            }
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if(binsAsRows){
            if(col == 0){
                return builder.binName(row);
            } else {
                return builder.getHistogramResult(row, col-1);
            }
        } else {
            if(col == 0){
                double value = unitScale*builder.getTimes().get(row);
                return IOHelp.DF[3].format(value);
            } else {
                return builder.getHistogramResult(col-1, row);
            }
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
