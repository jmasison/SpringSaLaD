/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.dataprocessor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RunningTimesTablePanel extends JPanel implements ItemListener{
    
    /* *********** COMBOBOX TO SET THE UNITS ******************************/
    private JComboBox<String> unitBox;
    private RunningTimesTableModel tableModel;
    
    public RunningTimesTablePanel(RunningTimesDataHolder runData){
        super();
        this.setLayout(new BorderLayout());
        
        this.add(buildTopPanel(), "North");
        this.add(buildCenterPanel(runData),"Center");
    }
    
    /* ***************  TOP PANEL HOLDS THE UNIT COMBOBOX **************/
    private JPanel buildTopPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel unitLabel = new JLabel("Select Time Unit: ", JLabel.RIGHT);
        unitBox = new JComboBox<>(RunningTimesTableModel.ALL_UNITS);
        unitBox.addItemListener(this);
        
        JPanel p0 = new JPanel();
        p0.add(unitLabel);
        p0.add(unitBox);
        
        JPanel p = new JPanel();
        p.add(p0);
        
        return p0;
        // </editor-fold>
    }
    
    /* ************* MIDDLE PANEL HOLDS THE TABLE **********************/
    private JPanel buildCenterPanel(RunningTimesDataHolder runData){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        tableModel = new RunningTimesTableModel(runData);
        
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        
        JScrollPane pane = new JScrollPane(table);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        JPanel p0 = new JPanel();
        p0.setLayout(new BorderLayout());
        p0.add(pane,"Center");
        
        return p0;
        // </editor-fold>
    }
    
    @Override
    public void itemStateChanged(ItemEvent event){
        if(event.getStateChange() == ItemEvent.SELECTED){
            tableModel.setUnitScale((String)unitBox.getSelectedItem());
        }
    }
}
