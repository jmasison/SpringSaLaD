/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springsalad.dataprocessor.clusterdata;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

@Deprecated
public class ClusterDataTableModel extends AbstractTableModel {
    
    private final ArrayList<String> namesToShow;
    private final double [] times;
    private final ClusterData clusterData;
    
    // For display purposes it helps to pick a convenient unit for the time, 
    // for example, ms instead of seconds.
    private String unit;
    private double unitScale;
    
    public ClusterDataTableModel(ClusterData clusterData){
        namesToShow = new ArrayList<>();
        times = clusterData.getTimes();
        determineTimeUnit();
        this.clusterData = clusterData;
    }
    
    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(times.length > 1){
            double dt = times[1];
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
        return times.length;
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
            return IOHelp.DF[3].format(unitScale*times[row]);
        } else if(col <= namesToShow.size()){
            key = namesToShow.get(col-1);
            if(key.equals(ClusterData.CLUSTER_SIZE)){
                return clusterData.getAvClusterSize(row);
            } else {
                return clusterData.getAvNumberBound(key, row);
            }
        } else if(col <= 2*namesToShow.size()){
            key = namesToShow.get(col-1-namesToShow.size());
            if(key.equals(ClusterData.CLUSTER_SIZE)){
                return clusterData.getStDevClusterSize(row);
            } else {
                return clusterData.getStDevNumberBound(key, row);
            }
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
