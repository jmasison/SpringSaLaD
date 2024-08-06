
package org.springsalad.dataprocessor;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

public class AverageDataTableModel extends AbstractTableModel {
    
    private final ArrayList<String> namesToShow;
    private final ArrayList<Double> times;
    private final AverageDataHolder dataHolder;
    
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit;
    private double unitScale;
    
    public AverageDataTableModel(AverageDataHolder dataHolder){
        namesToShow = new ArrayList<>();
        times = dataHolder.getTimes();
        determineTimeUnit();
        this.dataHolder = dataHolder;
    }
    
    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(times.size() > 1){
            double dt = times.get(1);
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
    
    /* ****************** MODIFY THE NAMES TO SHOW LIST ******************/
    public void addNameToShow(String name){
        if(!namesToShow.contains(name)){
            namesToShow.add(name);
            fireTableStructureChanged();
        }
    }
    
    public void removeNamesToShow(String name){
        namesToShow.remove(name);
        fireTableStructureChanged();
    }
    
    public void clearNamesToShow(){
        namesToShow.clear();
        fireTableStructureChanged();
    }
    
    public ArrayList<String> getNamesToShow(){
        return namesToShow;
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return times.size();
    }

    @Override
    public int getColumnCount() {
        return 1+2*namesToShow.size();
    }
    
    @Override
    public String getColumnName(int col){
        if(col == 0){
            return "Time (" + unit + ")";
        } else if(col <= namesToShow.size()){
            return "AV: " + namesToShow.get(col-1);
        } else {
            return "ST DEV: " + namesToShow.get(col-namesToShow.size()-1);
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        String key;
        if(col == 0){
            return IOHelp.DF[3].format(unitScale*times.get(row));
        } else if(col <= namesToShow.size()){
            key = namesToShow.get(col-1);
            return dataHolder.getAverage(key).get(row);
        } else if(col <= 2*namesToShow.size()){
            key = namesToShow.get(col-1-namesToShow.size());
            return dataHolder.getStDevs(key).get(row);
        } else {
            return "Index out of bounds!";
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
