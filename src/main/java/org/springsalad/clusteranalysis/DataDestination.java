package org.springsalad.clusteranalysis;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import static java.util.stream.Collectors.toList;

class DataDestination {	
	private final String dataFolder;
	private final FileOperationExceptionLogger logger;
	
	DataDestination(FileOperationExceptionLogger logger, String dataFolder){
		this.dataFolder = dataFolder;
		this.logger = logger;
	}
	
	void writeClusterSizeDistribution(String runStr, String tpvStr, String[] headers, Collection<?> ... columns) {
		try {
			Path filePath = PathCreator.clusterSizeDistributionPath(dataFolder,runStr, tpvStr);
			CSVHandler.writeCSV(filePath, headers, columns);
		}
		catch(IOException ioe) {
			logger.logFileWriteException(ioe);
		}
	}
	
	void writeClusterCompositionDistribution(String runStr, String tpvStr, String[] headers, Collection<?> ... columns) {
		try {
			Path filePath = PathCreator.clusterCompositionDistributionPath(dataFolder,runStr, tpvStr);
			CSVHandler.writeCSV(filePath, headers, columns);
		}
		catch(IOException ioe) {
			logger.logFileWriteException(ioe);
		}
	}
	
	void writeTrajectoryTimeSeries(String runStr, String[] headers, Collection<?> ... columns) {
		try {
			Path filePath = PathCreator.trajectoryTimeSeriesPath(dataFolder,runStr);
			CSVHandler.writeCSV(filePath, headers, columns);
		}
		catch(IOException ioe) {
			logger.logFileWriteException(ioe);
		}
	}
	
}
