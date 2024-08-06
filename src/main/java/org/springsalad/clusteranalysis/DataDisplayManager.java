package org.springsalad.clusteranalysis;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public interface DataDisplayManager {
	public static final String infoStr = "=== Cluster Analysis Definitions ===\n\nCluster Size = number of individual molecules comprising a cluster\nFractional frequency (freq) = number of clusters of a certain size / total number of clusters\nAverage Cluster Size (ACS) = sum(cluster size*fractional frequency) = (# molecules)/(# clusters).\nStandard Deviation (SD) = standard deviation of the cluster sizes.\nFraction of total molecules (Fotm) = (cluster size * number of such clusters) / total number of molecules.\nAverage Cluster Occupancy (ACO) = sum(cluster size * fotm)\n\nCompositional frequency (comp freq): frequency of a particular cluster composition in clusters of that size\n\n\n-Note: Since ACO is related to the square of the cluster sizes and ACS is related to just the cluster sizes, if the ACO is much larger than the ACS, the sample contains large clusters.\n\n\n=== Sampling Statistics Considerations ===\nOverall run: at each timepoint, combining all single run samples into one sample\nMean run: at each timepoint, each statistic (except SD) is averaged over the single runs. SD is now the standard deviation of the average cluster sizes.\n\n-Note: if a single run sample has no molecules (due to decay reactions), the mean freqs would not add to 1.0, but\nsum (cluster sizes * mean freq) = mean ACS.\nSame for mean fotms.\n\n--Note: if some single run samples do not have clusters of a certain size while other single run samples do, the mean comp freqs for that cluster size would not add to 1.0. Eg:\nRun0\t\t: 1,0,0 -> 0.1; 0,1,0 -> 0.9\nRun1(no monomers)\t: 1,0,0 -> 0.0; 0,1,0 -> 0.0\nMean Run\t\t: 1,0,0 -> 0.05; 0,1,0 -> 0.45.\n\n---Note: the mean ACO and the overall ACO will be the same if the single run samples all have the same number of molecules (eg. if there are no creation or decay reactions).\n\n----Note: the mean ACS and the overall ACS would be the same only if the single run samples all have the same number of clusters (not very likely).";
	
	void configureList(JLabel listLabel, JList<String> list, DefaultListModel listModel);
	
	JPanel getMainPanel();
	
	void setNamesToShow(List<String> selectedValuesList);
	
	DataFrame convertCSVReadParseExceptionToDataFrame(Exception exception);
	
	static String describeReadParseException(Exception exception){
		StringBuilder sb = new StringBuilder();
		Throwable t = exception;
		while (true) {
			sb.append(t.toString());
			if (t.getCause() != null) {
				sb.append(System.lineSeparator()).append("Caused by: ");
				t = t.getCause();
			}
			else {
				break;
			}
		}
		return sb.toString();
	}
	
	
}
