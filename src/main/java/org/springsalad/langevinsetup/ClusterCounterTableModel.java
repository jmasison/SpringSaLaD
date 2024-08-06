
package org.springsalad.langevinsetup;

import javax.swing.table.AbstractTableModel;

public class ClusterCounterTableModel extends AbstractTableModel {
    
    private final Global g;
    private final String [] columnNames = {"",""};
    
    public ClusterCounterTableModel(Global g){
        this.g = g;
    }
    
    @Override
    public int getRowCount() {
        return 1;
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
        if(columnIndex==0){
            return "Track Clusters?";
        } else if(columnIndex == 1){
            return g.isTrackingClusters();
        } else {
            return "Index out of bounds (1) in cluster counter table model.";
        }
    }
    
    // Override this method so the table knows the columns that contain
    // boolean values.
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col > 0;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        if(row==0 && col == 1){
            g.setTrackClusters((Boolean)value);
        } else {
            System.out.println("Index out of bounds (2) in cluster counter table model.");
        }
        fireTableCellUpdated(row, col);
    }
    
}
