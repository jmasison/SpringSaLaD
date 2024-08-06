package org.springsalad.clusteranalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;

import javax.swing.table.AbstractTableModel;

import org.springsalad.helpersetup.IOHelp;

class ClusterAveragesTableModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 2805397188769564278L;
	
	
	private final List<String> namesToShow;
    private final NavigableMap<String,DataFrame> dataFrameMap;
    private boolean mapHasNoNormalDFs;
    private DataFrame firstNormalDF;
    
	public ClusterAveragesTableModel(NavigableMap<String, DataFrame> dataFrameMap) {
		namesToShow = new ArrayList<>();
		this.dataFrameMap = dataFrameMap;
		
		mapHasNoNormalDFs = true;
		firstNormalDF = null;
		for (DataFrame df: this.dataFrameMap.values()) {
			if (df != null && df.getSeries(0).size() > 1 && df.getSeries(0).get(0).getClass() == Double.class) {
				mapHasNoNormalDFs = false;
				firstNormalDF = df;
				break;
			}
		}
		
		determineTimeUnit();
	}
	
    // For display purposes it helps to pick a convenient unit for the time,
    // for example, ms instead of seconds.
    private String unit;
    private double unitScale;


    /* ****************** DETERMINE UNIT AND SCALE ***********************/
    private void determineTimeUnit(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
    	if (mapHasNoNormalDFs) {
    		unit = "s";
    		unitScale = 1;
    	}
    	else {
    		List<Object> times = firstNormalDF.getSeries(0);
	        if(times.size() > 1){
	            double dt = (Double)times.get(1);
	            if(dt >= 0.1){
	                unit = "s";
	                unitScale = 1;
	            } else if(1E3*dt >= 0.1){
	                unit = "ms";
	                unitScale = 1E3;
	            } else if(1E6*dt >= 0.1){
	                unit = "us";
	                unitScale = 1E6;
	            } else if(1E9*dt >= 0.1){
	                unit = "ns";
	                unitScale = 1E9;
	            } else {
	                unit = "ps";
	                unitScale = 1E12;
	            }
	        }
    	}       
        // </editor-fold>
    }

    public Iterable<String> getNames(){
    	return dataFrameMap.keySet();
    }
    
    public void replaceAllNamesToShow(List<String> newNamesToShow){
        namesToShow.clear();
        namesToShow.addAll(newNamesToShow);
        fireTableStructureChanged();
    }

    /* *****************  TABLE METHODS **********************************/

    @Override
    public int getRowCount() {
    	if (mapHasNoNormalDFs) {
    		return 1;
    	}
    	else {
    		return firstNormalDF.getSeries(0).size();
    	}
    }

    @Override
    public int getColumnCount() {
        return 1 + 3*namesToShow.size();
    }

    @Override
    public String getColumnName(int col){
        if(col == 0){
            return "Time (" + unit + ")";
        }
        else{
        	String dfName = namesToShow.get((col-1)/3);
        	DataFrame df = dataFrameMap.get(dfName);
        	String shortHeader;
        	if (df == null) {
        		shortHeader = "";
        	}
        	else {
        		shortHeader = df.headers[((col-1) % 3) + 1];
        	}
            return dfName + " " + shortHeader;
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
    	if(col == 0){
    		if (mapHasNoNormalDFs) {
        		return "NA";
        	}
    		else {
    			return IOHelp.DF[3].format(unitScale * (Double) firstNormalDF.getSeries(0).get(row));
    		}
        }
    	DataFrame df = dataFrameMap.get(namesToShow.get((col-1)/3));
    	if (df == null) {
    		return "NA";
    	}
        else{    
    		if (row >= df.getSeries(0).size()) {
    			return "";
    		}
    		Object element = df.getSeries((col-1)%3+1).get(row);
    		if (element.getClass() == Double.class) {
    			return IOHelp.DF[3].format((Double) element);
    		}
    		else {
    			return element.toString();
    		}
        }
    	
    }

    @Override
    public Class getColumnClass(int c){
        return String.class;
    }

}
