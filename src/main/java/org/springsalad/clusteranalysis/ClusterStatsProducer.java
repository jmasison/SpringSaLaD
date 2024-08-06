package org.springsalad.clusteranalysis;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

/*
 	THIS IS A MAIN CLASS:
	It coordinates the calculating of the cluster stats, by calling the rest of the functions.
	
 */

public class ClusterStatsProducer {
	// use these when creating time series holders, run time point samples and tp stats
	public static final String MEAN_RUN_STR = "MEAN_Run";
	public static final String OVERALL_RUN_STR = "OVERALL_Run";
	public static final String SINGLE_RUN_STR = "Run%d";
	
	public static void calculateAndWriteClusterStats(DataSource dataSource, DataDestination dataDestination) {
		List<Integer> runNumList = dataSource.runNumberList;
		List<Double> timeInSeconds = dataSource.timePointValueList;
		Map<String,TimeSeriesHolder> timeSeriesHolderMap = runNumList.stream().collect(toMap(i->String.format(SINGLE_RUN_STR,i), 
																							i -> new TimeSeriesHolder(String.format(SINGLE_RUN_STR,i), 
																														timeInSeconds)));
		timeSeriesHolderMap.put(MEAN_RUN_STR, new TimeSeriesHolder(MEAN_RUN_STR, timeInSeconds));
		timeSeriesHolderMap.put(OVERALL_RUN_STR, new TimeSeriesHolder(OVERALL_RUN_STR, timeInSeconds));
		
		for(Iterable<RunTimePointSample> singleTPAllRuns: dataSource.byTimeThenByRun) {
			OverallTPBuilder overallTPBuilder = new OverallTPBuilder();
			MeanTPStatsBuilder meanTPStatsBuilder = new MeanTPStatsBuilder(); 
			
			for (RunTimePointSample runTimePointSample: singleTPAllRuns) {
				overallTPBuilder.addTP(runTimePointSample);
				NormalTPStats normalTPStats = runTimePointSample.calculateClusterStats();
				normalTPStats.writeClusterSizeDistribution(dataDestination);
				normalTPStats.writeClusterCompositionDistribution(dataDestination);
				timeSeriesHolderMap.get(normalTPStats.runStr).addAveragesToLists(normalTPStats);
				meanTPStatsBuilder.addTPStats(normalTPStats);
			}
			MeanTPStats meanTPStats = meanTPStatsBuilder.getMeanTPStats();
			meanTPStats.writeClusterSizeDistribution(dataDestination);
			meanTPStats.writeClusterCompositionDistribution(dataDestination);
			timeSeriesHolderMap.get(MEAN_RUN_STR).addAveragesToLists(meanTPStats);
			
			RunTimePointSample overallTP = overallTPBuilder.getOverallTP();
			NormalTPStats overallTPStats = overallTP.calculateClusterStats();
			overallTPStats.writeClusterSizeDistribution(dataDestination);
			overallTPStats.writeClusterCompositionDistribution(dataDestination);
			timeSeriesHolderMap.get(OVERALL_RUN_STR).addAveragesToLists(overallTPStats);
		}
		
		for(TimeSeriesHolder timeSeriesHolder: timeSeriesHolderMap.values()) {
			timeSeriesHolder.writeTo(dataDestination);
		}
	}	
}

