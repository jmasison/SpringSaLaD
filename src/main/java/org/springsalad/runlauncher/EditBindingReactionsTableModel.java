/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.springsalad.langevinsetup.BindingReaction;
import org.springsalad.langevinsetup.Global;

public class EditBindingReactionsTableModel extends AbstractTableModel {
    
    private final ArrayList<BindingReaction> defaultReactions;
    private final ArrayList<BindingReaction> simulationReactions;
    
    private final String [] columnNames = {"Reaction", 
        "Default On Rate (uM-1.s-1)", "Default Off Rate (1/s)", "Default Bond Length (nm)",
        "New On Rate (uM-1.s-1)", "New Off Rate (1/s)", "New Bond Length (nm)"};
    
    public EditBindingReactionsTableModel(Global g, Simulation sim){
        defaultReactions = g.getBindingReactions();
        simulationReactions = sim.getBindingReactions();
    }
    
    @Override
    public int getRowCount() {
        return defaultReactions.size();
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
        BindingReaction dRxn = defaultReactions.get(rowIndex);
        BindingReaction sRxn = simulationReactions.get(rowIndex);
        if(columnIndex == 0){
            return dRxn.getName();
        } else if (columnIndex == 1){
            return dRxn.getkon();
        } else if (columnIndex == 2){
            return dRxn.getkoff();
        } else if (columnIndex == 3){
            return dRxn.getBondLength();
        } else if (columnIndex == 4){
            if(dRxn.getkon() == sRxn.getkon()){
                return null;
            } else {
                return sRxn.getkon();
            }
        } else if (columnIndex == 5){
            if(dRxn.getkoff() == sRxn.getkoff()){
                return null;
            } else {
                return sRxn.getkoff();
            }
        } else if (columnIndex == 6){
            if(dRxn.getBondLength() == sRxn.getBondLength()){
                return null;
            } else {
                return sRxn.getBondLength();
            }
        } else {
            return "Index out of bounds!";
        }
    }
    
    // Override this method so the table knows that two of the columns contain 
    // double values.
    @Override
    public Class getColumnClass(int c){
        if(c == 0){
            return String.class;
        } else {
            return Double.class;
        }
    }
    
    // The molecule names are not editable
    @Override
    public boolean isCellEditable(int row, int col){
        return col > 3;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        BindingReaction dRxn = defaultReactions.get(row);
        BindingReaction sRxn = simulationReactions.get(row);
        if(col == 4){
            if(value == null){
                sRxn.setkon(dRxn.getkon());
            } else {
                sRxn.setkon(Double.parseDouble(value.toString()));
            }
            
            boolean ret = sRxn.checkOnRate();
            System.out.println(ret + " ");
            if(ret == false) {
            	String msg = "The simulation Kon is too large (I.e. exceeds the diffusion limited rate) for this reaction.\n";
            	msg += "Please consider reducing Kon or increasing the Radius or D of the participating Site Types.";
            	JOptionPane.showMessageDialog(null, msg);
            }
        } else if(col == 5){
            if(value == null){
                sRxn.setkoff(dRxn.getkoff());
            } else {
                sRxn.setkoff(Double.parseDouble(value.toString()));
            }
        } else if(col == 6){
            if(value == null){
                sRxn.setBondLength(dRxn.getBondLength());
            } else {
                sRxn.setBondLength(Double.parseDouble(value.toString()));
            }
        }
        
        fireTableCellUpdated(row, col);
    }
}
