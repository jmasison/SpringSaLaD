package org.springsalad.clusteranalysis;

import java.io.IOException;

import org.springsalad.dataprocessor.DataProcessor;

/*
	THIS IS A MAIN CLASS:
	To add more calculations of statistics, 
	write your own calculation class and call it from this class.

*/

public class DataProcessor2 extends DataProcessor{
	private ClusterAnalyzer clusterAnalyzer;
	
	public DataProcessor2(String allSimsFolder, String simulationName) {
		super(allSimsFolder, simulationName);
	}
	
	@Override
	public void computeAllTimePointAverages(int startIndex, int endIndexInclusive) {
		super.computeAllTimePointAverages(startIndex, endIndexInclusive);
		
		try {
			clusterAnalyzer = new ClusterAnalyzer(logger, dataFolder, startIndex, endIndexInclusive);
			clusterAnalyzer.calculateAndWriteClusterStats();
		}
		catch(IOException | IllegalArgumentException exception) {
			FileOperationExceptionLogger.justDisplayException("Unable to calculate cluster stats.", exception);
		}
	}

}
