package org.springsalad.clusteranalysis;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

class RunTimePointSample {
	
	final String runStr; 
	final double timePointValue; 
	final String tpvStr;
	final String molNamesStr;
	
	final List<Integer> clusterSizeList;
	final List<Cluster> clusterCompList;
	
	public RunTimePointSample(String runStr, double timePointValue, String tpvStr, String molNamesStr,
			List<Integer> clusterSizeList, List<Cluster> clusterCompList) {
		this.runStr = runStr;
		this.timePointValue = timePointValue;
		this.tpvStr = tpvStr;
		this.molNamesStr = molNamesStr;
		this.clusterSizeList = clusterSizeList;
		this.clusterCompList = clusterCompList;
	}
	
	NormalTPStats calculateClusterStats() {
		List<Integer> sizeList = clusterSizeList;
		List<Cluster> compList = clusterCompList;
		
		final double totNumClusters = sizeList.size();
        final double totNumMolecules = sizeList.stream().mapToInt(Integer::intValue).sum();
        double acs = totNumMolecules/totNumClusters;

        final SortedMap<Integer,Integer> sizeCountMap = sizeList.stream().collect(toMap(Function.identity(),
																		                i->1,
																		                (originalCount, one) -> originalCount + one,
																		                TreeMap::new));
        SortedMap<Integer,Double> sizeFreqMap = sizeCountMap.entrySet().stream().collect(toMap(Map.Entry::getKey, me -> me.getValue()/totNumClusters, (v1,v2)->v1, TreeMap::new));
        SortedMap<Integer,Double> sizeFotmMap = sizeCountMap.entrySet().stream().collect(toMap(Map.Entry::getKey, me -> me.getValue()*me.getKey()/totNumMolecules, (v1,v2)->v1, TreeMap::new));
        double aco = sizeFotmMap.entrySet().stream().mapToDouble(me -> me.getKey() * me.getValue()).sum();
        double variance = sizeCountMap.entrySet().stream().mapToDouble(me -> me.getKey() * me.getKey() * me.getValue()).sum()/totNumClusters - acs*acs;
        double scaledSampleVariance = variance * totNumClusters / (totNumClusters-1);
        double sd = Math.sqrt(scaledSampleVariance);
        if (Double.isNaN(sd)) {
        	sd = 0.0;
        }
        SortedMap<Cluster,Integer> compCountMap = compList.stream().collect(toMap(Function.identity(),c->1,(originalCount,one)->originalCount+one,TreeMap::new));
        SortedMap<Cluster,Double> compFreqMap = compCountMap.entrySet().stream().collect(toMap(Map.Entry::getKey,
        																					me->(double) (me.getValue())/(sizeCountMap.get(me.getKey().size)),
        																					(v1,v2NeverOccurs)->v1,
        																					TreeMap::new));
        return new NormalTPStats(runStr, timePointValue, tpvStr, molNamesStr,
        		acs, sd, aco,
        		sizeCountMap, sizeFreqMap, sizeFotmMap, compCountMap, compFreqMap);
	}
	
	
}
