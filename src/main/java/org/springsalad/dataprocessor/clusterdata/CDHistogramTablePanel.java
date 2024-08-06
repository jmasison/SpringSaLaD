/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springsalad.dataprocessor.clusterdata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.springsalad.helpersetup.IOHelp;

@Deprecated
public class CDHistogramTablePanel extends JPanel implements ItemListener {
    
    /* *****************  Top Panel Component ****************************/
    private JComboBox timePickerBox;
    
    /* **************** Jtable and model *********************************/
    private JTable histogramTable;
    private final CDHistogramTableModel histogramModel;
    private final ClusterDataHistogram histogram;
    
    /* ******************* Constructor ***********************************/
    
    public CDHistogramTablePanel(CDHistogramTableModel histogramModel){
        this.histogramModel = histogramModel;
        this.histogram = histogramModel.getHistogram();
        setLayout(new BorderLayout());
        this.add(buildTopPanel(), "North");
        this.add(buildCenterPanel(), "South");
    }
    
    /* ********************* Build top panel ******************************/
    
    private JPanel buildTopPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel timePickerLabel = new JLabel("Select Time (" + histogramModel.getTimeUnit() + "): ", JLabel.RIGHT);
        
        double [] times = histogram.getTimes();
        String [] timeStrings = new String[times.length];
        for(int i=0;i<times.length;i++){
            timeStrings[i] = IOHelp.DF[3].format(times[i]*histogramModel.getUnitScale());
        }
        
        timePickerBox = new JComboBox(timeStrings);
        timePickerBox.setSelectedIndex(0);
        timePickerBox.addItemListener(this);
        
        JPanel p = new JPanel();
        p.add(timePickerLabel);
        p.add(timePickerBox);
        return p;
        // </editor-fold>
    }
    
    /* ***************** BUILD THE CENTER PANEL ************************/
    
    private JPanel buildCenterPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        histogramTable = new JTable(histogramModel);
        histogramTable.setFillsViewportHeight(true);
        
        JScrollPane pane = new JScrollPane(histogramTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        JPanel p0 = new JPanel();
        p0.setLayout(new BorderLayout());
        p0.add(pane,"Center");
        
        return p0;
        // </editor-fold>
    }
    
    /* *********** Item Listener Method ***********************************/
    @Override
    public void itemStateChanged(ItemEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Object source = event.getSource();
        if(source == timePickerBox && event.getStateChange() == ItemEvent.SELECTED){
            histogramModel.setTimeIndex(timePickerBox.getSelectedIndex());
        }
        // </editor-fold>
    }
}
