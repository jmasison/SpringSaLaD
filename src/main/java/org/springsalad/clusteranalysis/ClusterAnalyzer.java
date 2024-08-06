package org.springsalad.clusteranalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	THIS IS A MAIN CLASS:
	It prepares the information needed to retrieve the raw cluster data and write the cluster stats.
	It also delegates the calculations to ClusterStatsProducer.
	
*/

public class ClusterAnalyzer {
    
	public static void main(String[] args) throws IOException{
    	String dataFolder = "C:\\Users\\imt_w\\Documents\\SpringSalad\\Clustering_tutorial_01\\Clustering_tutorial_01_SIMULATIONS\\Test1X4_SIM_FOLDER\\data";
    	FileOperationExceptionLogger logger = new FileOperationExceptionLogger("");
    	ClusterAnalyzer ca = new ClusterAnalyzer(logger, dataFolder, 0, 2);
    	ca.calculateAndWriteClusterStats();
    	logger.displayLogGUI();
    }
    
	private DataSource dataSource;    
    private DataDestination dataDestination;
    private String dataFolder;
	
	public ClusterAnalyzer(FileOperationExceptionLogger logger, String dataFolder, int startIndex, int finishIndexInclusive) throws IOException{
        Pattern simNamePattern = Pattern.compile(".*(_SIM)_FOLDER");
        Matcher simNameMatcher = simNamePattern.matcher(dataFolder);
        if (simNameMatcher.find()){
            String simfile = dataFolder.substring(0,simNameMatcher.end(1)) + ".txt";
            SimFile simFileObj = new SimFile(simfile);
            
            double[] timeStats = simFileObj.getTimeStats();
            final double tTotal = timeStats[0];
            final double dtData = timeStats[3];
            
            final int numOfRuns = simFileObj.getNumRuns();
            if (finishIndexInclusive >= numOfRuns) {
            	throw new IllegalArgumentException("Unable to compute cluster stats: Last run index exceeds total number of runs");
            }           
            List<String> molNames = new ArrayList<>(simFileObj.getMolecules().keySet());
            this.dataFolder = dataFolder;
            dataSource = new DataSource(logger, dataFolder, molNames, 
            		0, tTotal, dtData, startIndex, finishIndexInclusive);
            dataDestination = new DataDestination(logger, dataFolder);
            
        }
        else {
        	throw new IllegalArgumentException(
        			"Datafolder is not located under a SIM_FOLDER that has an accompanying SIM_txt\nDatafolder: " + dataFolder);
        }
    }
    
    public void calculateAndWriteClusterStats() throws IOException{
    	ClusterStatsProducer.calculateAndWriteClusterStats(dataSource, dataDestination);
    }
}
