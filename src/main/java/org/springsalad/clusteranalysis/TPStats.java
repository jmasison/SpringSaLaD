package org.springsalad.clusteranalysis;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

abstract class TPStats {
    
    final String runStr;
    final double tpv;
    final String tpvStr;
    final String molNamesStr;
    final double acs;
    final double sd;
    final double aco;

	final SortedMap<Integer,Double> sizeFreqMap;
	final SortedMap<Integer,Double> sizeFotmMap; 
	final SortedMap<Cluster,Double> compFreqMap;
	    
    TPStats(String runStr, double tpv, String tpvStr, String molNamesStr,
    		double acs, double sd, double aco, 
    		SortedMap<Integer,Double> sizeFreqMap, 
    		SortedMap<Integer,Double> sizeFotmMap, SortedMap<Cluster,Double> compFreqMap){
    	this.runStr = runStr;
    	this.tpv = tpv;
    	this.tpvStr = tpvStr;
    	this.molNamesStr = molNamesStr;
    	this.acs = acs;
    	this.sd = sd;
    	this.aco = aco;
    	this.sizeFreqMap = sizeFreqMap;
    	this.sizeFotmMap = sizeFotmMap; 
    	this.compFreqMap = compFreqMap;
    }

    abstract void writeClusterSizeDistribution(DataDestination dataDestination);
    abstract void writeClusterCompositionDistribution(DataDestination dataDestination);
}
class MeanTPStats extends TPStats{ // basically the same as TPStats, but this class is not abstract
	static final String[] sizeDistributionOutputHeaders = new String[] {"Size", "Frequency", "Fraction of total molecules"};
	final String[] compositionDistributionOutputHeaders;
	
	MeanTPStats(String runStr, double tpv, String tpvStr, String molNamesStr,
			double acs, double sd, double aco, 
			SortedMap<Integer,Double> sizeFreqMap, SortedMap<Integer,Double> sizeFotmMap, 
    		SortedMap<Cluster,Double> compFreqMap){
		super(runStr, tpv, tpvStr, molNamesStr, acs, sd, aco, sizeFreqMap, sizeFotmMap, compFreqMap);
		compositionDistributionOutputHeaders = new String[] {"Size", molNamesStr, "Frequency in clusters of the same size"};
	}

	@Override
	void writeClusterSizeDistribution(DataDestination dataDestination) {
		dataDestination.writeClusterSizeDistribution(runStr, tpvStr, 
				sizeDistributionOutputHeaders,
				sizeFreqMap.keySet(),
				sizeFreqMap.values(), 
				sizeFotmMap.values());
	}

	@Override
	void writeClusterCompositionDistribution(DataDestination dataDestination) {
		List<Integer> sizeList = compFreqMap.keySet().stream().map(cluster -> cluster.size).collect(toList());
		dataDestination.writeClusterCompositionDistribution(runStr, tpvStr, 
				compositionDistributionOutputHeaders, 
				sizeList, 
				compFreqMap.keySet(),
				compFreqMap.values());
	}
}

class NormalTPStats extends TPStats{
	static final String[] sizeDistributionOutputHeaders = new String[] {"Size", "Count", "Frequency", "Fraction of total molecules"};
	final String[] compositionDistributionOutputHeaders;
	final SortedMap<Integer,Integer> sizeCountMap;
	final SortedMap<Cluster,Integer> compCountMap;
	
	NormalTPStats(String runStr, double tpv, String tpvStr, String molNamesStr,
			double acs, double sd, double aco, 
			SortedMap<Integer,Integer> sizeCountMap, SortedMap<Integer,Double> sizeFreqMap, SortedMap<Integer,Double> sizeFotmMap, 
			SortedMap<Cluster,Integer> compCountMap, SortedMap<Cluster,Double> compFreqMap){
		super(runStr, tpv, tpvStr, molNamesStr, acs, sd, aco, sizeFreqMap, sizeFotmMap, compFreqMap);
		compositionDistributionOutputHeaders = new String[] {"Size", molNamesStr, "Count", "Frequency in clusters of the same size"};
		this.sizeCountMap = sizeCountMap;
		this.compCountMap = compCountMap;
	}
	
	@Override
	void writeClusterSizeDistribution(DataDestination dataDestination) {
		dataDestination.writeClusterSizeDistribution(runStr, tpvStr, 
				sizeDistributionOutputHeaders, 
				sizeCountMap.keySet(), 
				sizeCountMap.values(), 
				sizeFreqMap.values(), 
				sizeFotmMap.values());
	}
	
	@Override
	void writeClusterCompositionDistribution(DataDestination dataDestination) {
		List<Integer> sizeList = compCountMap.keySet().stream().map(cluster -> cluster.size).collect(toList());
		dataDestination.writeClusterCompositionDistribution(runStr, tpvStr, 
				compositionDistributionOutputHeaders, 
				sizeList, 
				compCountMap.keySet(), 
				compCountMap.values(), 
				compFreqMap.values());
	}
}

class FaultyNormalTPStats extends NormalTPStats{
	Exception exception;
	String[] exceptionHeader;
	List<String> singleColumn;
	FaultyNormalTPStats(String runStr, double tpv, String tpvStr, String molNamesStr, Exception exception) {
		super(runStr, tpv, tpvStr, molNamesStr,
				Double.NaN, Double.NaN, Double.NaN, 
				null, null, null, null, null);
		this.exception = exception;
		exceptionHeader = new String[]{"\"Cannot calculate cluster stats because raw cluster data file cannot be read or parsed\""};
		StringBuilder ehSB = new StringBuilder("\"");
		Throwable t = exception;
		while (true) {
			ehSB.append(t.toString());
			if (t.getCause() != null) {
				ehSB.append(System.lineSeparator()).append("Caused by: ");
				t = t.getCause();
			}
			else {
				break;
			}
		}
		ehSB.append("\"");
		singleColumn = new ArrayList<>();
		singleColumn.add(ehSB.toString());
	}
	
	@Override
	void writeClusterSizeDistribution(DataDestination dataDestination) {
		dataDestination.writeClusterSizeDistribution(runStr, tpvStr, exceptionHeader, singleColumn);
	}
	
	@Override
	void writeClusterCompositionDistribution(DataDestination dataDestination) {
		dataDestination.writeClusterCompositionDistribution(runStr, tpvStr, exceptionHeader, singleColumn);
	}
}

