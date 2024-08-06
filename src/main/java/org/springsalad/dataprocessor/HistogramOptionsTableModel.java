
package org.springsalad.dataprocessor;

import javax.swing.table.AbstractTableModel;

public class HistogramOptionsTableModel extends AbstractTableModel {
    
    private final HistogramTableModel histogramModel;
    private final HistogramBuilder builder;
    
    private final String [] columnNames = {"", "Auto", "Value"};
    
    public HistogramOptionsTableModel(HistogramTableModel histogramModel){
        this.histogramModel = histogramModel;
        this.builder = histogramModel.getHistogramBuilder();
    }
    
    /* *****************  TABLE METHODS **********************************/
    
    @Override
    public int getRowCount() {
        return 3;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }
    
    @Override
    public String getColumnName(int col){
        return columnNames[col];
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if(col == 0){
            switch(row){
                case 0:
                    return "Minimum";
                case 1:
                    return "Maximum";
                case 2:
                    return "Bin Size";
                default:
                    return "Out of bounds.";
            }
        } else if(col == 1){
            switch(row){
                case 0:
                    return builder.getAutoMinimum();
                case 1:
                    return builder.getAutoMaximum();
                case 2:
                    return builder.getAutoBinSize();
                default:
                    return null;
            }
        } else if(col == 2){
            switch(row){
                case 0:
                    return builder.getMinimum();
                case 1:
                    return builder.getMaximum();
                case 2:
                    return builder.getBinSize();
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public Class getColumnClass(int c){
        switch(c){
            case 0:
                return String.class;
            case 1:
                return Boolean.class;
            default:
                return Integer.class;
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int col){
        switch(col){
            case 0:
                return false;
            case 1:
                return true;
            case 2:
                if(row == 0){
                    return !builder.getAutoMinimum();
                } else if(row == 1){
                    return !builder.getAutoMaximum();
                } else if(row == 2){
                    return !builder.getAutoBinSize();
                } else {
                    return false;
                }
            default:
                return false;
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        switch(col){
            case 1:
                if(row == 0){
                    builder.setAutoMinimum((Boolean)value);
                } else if(row == 1){
                    builder.setAutoMaximum((Boolean)value);
                } else if(row == 2){
                    builder.setAutoBinSize((Boolean)value);
                }
                builder.constructHistogram();
                histogramModel.fireTableStructureChanged();
                break;
            case 2:
                if(row == 0){
                    builder.setMinimum((Integer)value);
                } else if(row == 1){
                    builder.setMaximum((Integer)value);
                } else if(row == 2){
                    builder.setBinSize((Integer)value);
                }
                builder.constructHistogram();
                histogramModel.fireTableStructureChanged();
                break;
        }
        fireTableDataChanged();
    }
}
