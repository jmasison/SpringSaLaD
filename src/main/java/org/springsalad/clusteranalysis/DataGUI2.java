package org.springsalad.clusteranalysis;

import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.event.ListSelectionEvent;

import org.springsalad.dataprocessor.DataGUI;
import org.springsalad.langevinsetup.SystemTimes;
import org.springsalad.runlauncher.Simulation;

/*
	THIS IS A MAIN CLASS:
	It includes the new cluster stats display.
	
*/

public class DataGUI2 extends DataGUI{
	public static void main(String[] args) {
		Simulation sim = new Simulation(new File("C:\\Users\\imt_w\\Documents\\SpringSalad\\Clustering_tutorial_01\\Clustering_tutorial_01_SIMULATIONS\\TEST1X5_SIM.txt"));
		new DataGUI2("Test", sim);
	}
	
	private static final String CLUSTER_STATS = "CLUSTER STATS";
	
	private DataDisplayManager currentDataDisplayManager;
	private ClusterAveragesDisplayManager clusterAveragesDisplayManager;
	private ClusterHistogramsDisplayManager clusterHistogramsDisplayManager;
	
	public DataGUI2 (String title, Simulation simulation) {
		super(title, simulation);
		
		if (this.simulation.isTrackingClusters()) {
			SystemTimes systemTimes = this.simulation.getSystemTimes();
			int numRuns = this.simulation.getRunNumber();
			clusterAveragesDisplayManager = new ClusterAveragesDisplayManager(processor.getDataFolder(), 
																				0, numRuns-1, 
																				0, systemTimes.getTotalTime(), systemTimes.getdtdata());
			clusterHistogramsDisplayManager = new ClusterHistogramsDisplayManager(processor.getDataFolder(), 
																					0, numRuns-1, 
																					0, systemTimes.getTotalTime(), systemTimes.getdtdata());
			dataClassBox.addItem(CLUSTER_STATS);
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent event) {
		super.itemStateChanged(event);
		if (event.getStateChange() == ItemEvent.SELECTED) {
			if (dataClassBox.getSelectedItem().equals(CLUSTER_STATS)) {
				Object source = event.getSource();
				if (source == dataClassBox) {
					currentPick = CLUSTER_STATS;
					if (dataTypeBox.getItemCount()==3) {
						if (dataTypeBox.getSelectedIndex()==2) {
							dataTypeBox.setSelectedIndex(1);
						}
						dataTypeBox.removeItemAt(2);
					}
				}
				if (dataTypeBox.getSelectedItem().equals(dataTypes[0])) {
					currentDataDisplayManager = clusterAveragesDisplayManager;
				}
				else {
					currentDataDisplayManager = clusterHistogramsDisplayManager;
				}
				currentDataDisplayManager.configureList(listLabel, list, listModel);
				centerPanel.removeAll();
				centerPanel.add(currentDataDisplayManager.getMainPanel());
				centerPanel.validate();
				centerPanel.repaint();
			}
			else {
				currentDataDisplayManager = null;
			}
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent event) {
		super.valueChanged(event);
		if (!event.getValueIsAdjusting()) {
			if (currentPick.equals(CLUSTER_STATS)) {
				currentDataDisplayManager.setNamesToShow(list.getSelectedValuesList());
			}
		}
	}
}
