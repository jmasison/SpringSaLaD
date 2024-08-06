
package org.springsalad.dataprocessor;

import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

public class SiteFreeBoundTableModel extends AbstractTableModel {
    
    private final SiteDataHolder dataHolder;
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit = "s";
    private double unitScale = 1;
    
    public SiteFreeBoundTableModel(SiteDataHolder dataHolder){
        this.dataHolder = dataHolder;
        determineTimeUnit();
    }
    
    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(dataHolder.getTimes().size() > 1){
            double dt = dataHolder.getTime(1);
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
    
    /* ****************** UPDATE DATA *************************************/
    public void updateData(){
        determineTimeUnit();
        fireTableStructureChanged();
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return dataHolder.getTimes().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }
    
    @Override
    public String getColumnName(int col){
        switch(col){
            case 0:
                return "Time (" + unit + ")";
            case 1:
                return "Average Free";
            case 2:
                return "Average Bound";
            case 3:
                return "St Dev Free";
            case 4:
                return "St Dev Bound";
            default:
                return "Index out of bounds";
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if(col == 0){
            return IOHelp.DF[3].format(unitScale*dataHolder.getTime(row));
        } else if(col == 1){
            return dataHolder.getAverageFree().get(row);
        } else if(col == 2){
            return dataHolder.getAverageBound().get(row);
        } else if(col == 3){
            return dataHolder.getStDevFree().get(row);
        } else if(col == 4){
            return dataHolder.getStDevBound().get(row);
        } else {
            return "Index out of bounds";
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        if(c==0){
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
