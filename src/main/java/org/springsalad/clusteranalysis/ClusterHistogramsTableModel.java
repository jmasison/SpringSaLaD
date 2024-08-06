package org.springsalad.clusteranalysis;

import org.springsalad.helpernovis.IOHelp;

import javax.swing.table.AbstractTableModel;
import java.util.Set;
import java.util.SortedMap;

class ClusterHistogramsTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -7651404506470682025L;

	private SortedMap<String, SortedMap<String, DataFrame>> dataFrameGrid;
	private DataFrame selectedDF;
	ClusterHistogramsTableModel(SortedMap<String, SortedMap<String, DataFrame>> dataFrameGrid) {
		this.dataFrameGrid = dataFrameGrid;
		SortedMap<String, DataFrame> tmp = dataFrameGrid.get(dataFrameGrid.firstKey());
		selectedDF = tmp.get(tmp.firstKey());
	}
	
	public Set<String> getRunStrSet(){
		return dataFrameGrid.keySet();
	}
	
	public Set<String> getTimeStrSet() {
		return dataFrameGrid.get(dataFrameGrid.firstKey()).keySet();
	}
	
	public void setDFToShow(String runStr, String tpvStr) {
		selectedDF = dataFrameGrid.get(runStr).get(tpvStr);
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		if (selectedDF == null) {
			return 0;
		}
		else {
			return selectedDF.getSeries(0).size();
		}
	}

	@Override
	public int getColumnCount() {
		if (selectedDF == null) {
			return 1;
		}
		else {
			return selectedDF.headers.length;
		}
	}
	
	@Override
	public String getColumnName(int col) {
		if (selectedDF == null) {
			return "No data available";
		}
		else {
			return selectedDF.headers[col];
		}
	}
	
	@Override
	public Class getColumnClass(int col) {
		return String.class;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object tmp = selectedDF.getSeries(columnIndex).get(rowIndex);
		if (tmp instanceof Double) {
			return IOHelp.DF[3].format((Double) tmp);
		}
		else {
			return tmp.toString();
		}
	}

	
}
