
package org.springsalad.dataprocessor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.langevinsetup.ValueCellEditor;
import org.springsalad.langevinsetup.ValueTextField;

public class HistogramTablePanel extends JPanel implements ItemListener,
                                                            ActionListener {
    
    /**************** COMPONENTS OF TOP PANEL *****************************/
    private JComboBox rowPickerBox;
    private HistogramOptionsTableModel optionsModel;
    
    /**************** JTABLE AND MODEL ************************************/
    private JTable histogramTable;
    private final HistogramBuilder builder;
    private final HistogramTableModel histogramModel;
    
    /* ****************** CONSTRUCTOR ************************************/
    public HistogramTablePanel(HistogramTableModel histogramModel){
        this.histogramModel = histogramModel;
        this.builder = histogramModel.getHistogramBuilder();
        setLayout(new BorderLayout());
        this.add(buildCenterPanel(), "Center");
        this.add(buildTopPanel(), "North");
    }
    
    /* *****************  BUILD TOP PANEL ***************************/
    
    private JPanel buildTopPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel rowPickerLabel = new JLabel("Bins as ", JLabel.RIGHT);
        rowPickerBox = new JComboBox(new String[]{"Rows", "Columns"});
        rowPickerBox.addItemListener(this);
        JPanel rowPickerPanel = new JPanel();
        rowPickerPanel.add(rowPickerLabel);
        rowPickerPanel.add(rowPickerBox);
        
        optionsModel = new HistogramOptionsTableModel(histogramModel);
        JTable optionsTable = new JTable(optionsModel);
        optionsTable.setFillsViewportHeight(true);
        optionsTable.setDefaultEditor(Integer.class, new ValueCellEditor(ValueTextField.INTEGER, Constraints.NO_CONSTRAINT, false));
        
        JScrollPane pane = new JScrollPane(optionsTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(200,100));
        
        JPanel p0 = new JPanel();
        p0.add(pane);
        
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,2));
        p1.add(rowPickerPanel);
        p1.add(p0);
        
        JPanel p = new JPanel();
        p.add(p1);

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
    
    /* ***************** UPDATE THE OPTIONS PANEL **********************/
    public void updateOptionsTable(){
        optionsModel.fireTableStructureChanged();
    }
    
    /* *****************  ITEM LISTENER METHOD ************************/
    
    @Override
    public void itemStateChanged(ItemEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        
        Object source = event.getSource();
        
        if(source == rowPickerBox){
            if(event.getStateChange() == ItemEvent.SELECTED){
                int index = rowPickerBox.getSelectedIndex();
                if(index == 0){
                    histogramModel.setBinsAsRow(true);
                } else {
                    histogramModel.setBinsAsRow(false);
                }
            }
        }
        
        
        // </editor-fold>
    }
    
    /* **************** ACTION LISTENER METHOD ************************/
    @Override 
    public void actionPerformed(ActionEvent event){
        
    }
}
