package org.springsalad.clusteranalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class MeanTPStatsBuilder {
	
	private double tpv;
	private String tpvStr;
	private String molNamesStr;
	private boolean haveNotEncounteredFirstTPYet;
	private double acsSum, acoSum;
	private SortedMap<Integer,Double> sizeFreqMap, sizeFotmMap; 
	private SortedMap<Cluster,Double> compFreqMap;
	private Map<Integer,Integer> sizeSampleNumMap;
	private List<Double> acsList;
	
	MeanTPStatsBuilder() {
		// defaults
		haveNotEncounteredFirstTPYet = true;
		acsSum = acoSum = 0;
		sizeFreqMap = new TreeMap<>();
		sizeFotmMap = new TreeMap<>();
		compFreqMap = new TreeMap<>();
		sizeSampleNumMap = new HashMap<>();
		acsList = new ArrayList<>();
	}
	
	void addTPStats(TPStats tpStats) {
		if (haveNotEncounteredFirstTPYet) {
			tpv = tpStats.tpv;
			tpvStr = tpStats.tpvStr;
			molNamesStr = tpStats.molNamesStr;
			haveNotEncounteredFirstTPYet = false;
		}
		else {
			assert tpStats.tpv == tpv;
		}
		if (Double.isNaN(tpStats.acs)) { // we exclude empty samples from the mean stats
			return;
		}
		acsSum += tpStats.acs;
		acsList.add(tpStats.acs);
		acoSum += tpStats.aco;
		tpStats.sizeFreqMap.forEach((size,freq) -> sizeFreqMap.merge(size,freq, (oldFreq, newFreq) -> oldFreq + newFreq));
		tpStats.sizeFotmMap.forEach((size,fotm) -> sizeFotmMap.merge(size,fotm, (oldFotm, newFotm) -> oldFotm + newFotm));
		int currentSize = -1;
		for (Map.Entry<Cluster,Double> entry: tpStats.compFreqMap.entrySet()) {
			if (entry.getKey().size != currentSize){
				currentSize = entry.getKey().size;
				sizeSampleNumMap.merge(currentSize,1,(oldNum,newNum)->oldNum+newNum);
			}
			compFreqMap.merge(entry.getKey(),entry.getValue(), (oldFreq,newFreq)->oldFreq+newFreq);
		}
		//tpStats.compFreqMap.forEach((cluster,freq) -> compFreqMap.merge(cluster,freq, (oldFreq, newFreq) -> oldFreq + newFreq));
	}
	
	MeanTPStats getMeanTPStats() {
		int numOfTP = acsList.size();
		double meanACS = acsSum/numOfTP;
		double meanACO = acoSum/numOfTP;
		double varianceOfACS = acsList.stream().mapToDouble(acs->acs*acs).sum()/numOfTP - meanACS*meanACS;
		double scaledVariance = varianceOfACS * numOfTP / (numOfTP-1);
		double sdOfACS = Math.sqrt(scaledVariance);
		if (Double.isNaN(sdOfACS)) {
			sdOfACS = 0.0;
        }
		
		sizeFreqMap.forEach((size,freqSum)->sizeFreqMap.put(size,freqSum/numOfTP));
		SortedMap<Integer,Double> sizeMeanFreqMap = sizeFreqMap;
		sizeFotmMap.forEach((size,fotmSum)->sizeFotmMap.put(size,fotmSum/numOfTP));
		SortedMap<Integer,Double> sizeMeanFotmMap = sizeFotmMap;
		for (Map.Entry<Cluster,Double> entry: compFreqMap.entrySet()) {
			Cluster cluster = entry.getKey();
			Double sumFreq = entry.getValue();
			compFreqMap.put(cluster, sumFreq / sizeSampleNumMap.get(cluster.size));
		}
		return new MeanTPStats(ClusterStatsProducer.MEAN_RUN_STR, tpv, tpvStr, molNamesStr, 
				meanACS, sdOfACS, meanACO, sizeMeanFreqMap, sizeMeanFotmMap, compFreqMap);
	}
}
