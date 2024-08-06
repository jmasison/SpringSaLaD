package org.springsalad.dataprocessor;

import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

public class SiteBondsTableModel extends AbstractTableModel {
    
    private final SiteDataHolder dataHolder;
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit = "s";
    private double unitScale = 1;
    
    public SiteBondsTableModel(SiteDataHolder dataHolder){
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
        return 2*dataHolder.getBondNames().size() + 1;
    }
    
    @Override
    public String getColumnName(int col){
        if(col == 0){
            return "Time (" + unit + ")";
        } else if(col <= dataHolder.getBondNames().size()){
            return "Average " + dataHolder.getBondName(col-1);
        } else {
            return "St Dev " + dataHolder.getBondName(col-1-dataHolder.getBondNames().size());
        }  
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        int totalNames = dataHolder.getBondNames().size();
        if(col == 0){
            return IOHelp.DF[3].format(unitScale*dataHolder.getTime(row));
        } else if(col <= totalNames){
            String name = dataHolder.getBondName(col-1);
            return dataHolder.getAverageBondData(name).get(row);
        } else if(col <= 2*totalNames){
            String name = dataHolder.getBondName(col-1-totalNames);
            return dataHolder.getStDevBondData(name).get(row);
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
