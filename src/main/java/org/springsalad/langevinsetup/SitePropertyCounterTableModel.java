/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.ArrayList;
import javax.swing.table.*;

public class SitePropertyCounterTableModel extends AbstractTableModel {
    
    private final ArrayList<Molecule> molecules;
    private final ArrayList<Site> sites = new ArrayList<>();
    private final ArrayList<SitePropertyCounter> sitePropertyCounters = new ArrayList<>();
    
    private final ArrayList<String> moleculeNames = new ArrayList<>();
    
    private final String [] columnNames = {"Molecule", "Site Index",
                                    "Site Type", "Track Properties"};
    
    public SitePropertyCounterTableModel(Global g){
        molecules = g.getMolecules();
        for(Molecule molecule : molecules){
            ArrayList<Site> mSites = molecule.getSiteArray();
            for(int i=0;i<mSites.size();i++){
                if(i==0){
                    moleculeNames.add(molecule.getName());
                } else {
                    moleculeNames.add("");
                }
                sites.add(mSites.get(i));
                sitePropertyCounters.add(mSites.get(i).getPropertyCounter());
            }
        }
    }
    
    @Override
    public int getRowCount() {
        return sites.size();
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
        if(columnIndex == 0){
            return moleculeNames.get(rowIndex);
        } else if (columnIndex == 1){
            return sites.get(rowIndex).getIndex();
        } else if (columnIndex == 2){
            return sites.get(rowIndex).getTypeName();
        } else if (columnIndex == 3){
            return sitePropertyCounters.get(rowIndex).isTracked();
        } else {
            return "Index out of bounds.";
        }
    }
    
     // Override this method so the table knows the columns that contain
    // boolean values.
    @Override
    public Class getColumnClass(int c){
        return getValueAt(0,c).getClass();
    }
    
    // Only the tracking boolean is editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col > 2;
    }
    
     @Override
    public void setValueAt(Object value, int row, int col){
        switch(col){
            case 3:
                sitePropertyCounters.get(row).setTracked((Boolean)value);
                break;
            default:
                System.out.println("Unexpected column index: " + col);
        }
        
        fireTableCellUpdated(row, col);
    }
    
}
