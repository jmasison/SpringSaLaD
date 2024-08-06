package org.springsalad.clusteranalysis;

import java.util.ArrayList;
import java.util.List;

class OverallTPBuilder {
	double tpv;
	String tpvStr;
	String molNamesStr;
	boolean haveNotEncounteredFirstTPYet;
	List<Integer> clusterSizeList;
	List<Cluster> clusterCompList;
	
	OverallTPBuilder() {
		haveNotEncounteredFirstTPYet = true;
		clusterSizeList = new ArrayList<>();
		clusterCompList = new ArrayList<>();
	}
	
	void addTP(RunTimePointSample runTimePointSample) {
		if (haveNotEncounteredFirstTPYet) {
			tpv = runTimePointSample.timePointValue;
			tpvStr = runTimePointSample.tpvStr;
			molNamesStr = runTimePointSample.molNamesStr;
			haveNotEncounteredFirstTPYet = false;
		}
		else {
			assert runTimePointSample.timePointValue == tpv;
		}
		clusterSizeList.addAll(runTimePointSample.clusterSizeList);
		clusterCompList.addAll(runTimePointSample.clusterCompList);
	}
	
	RunTimePointSample getOverallTP() {
		return new RunTimePointSample(ClusterStatsProducer.OVERALL_RUN_STR, tpv, tpvStr, molNamesStr, clusterSizeList, clusterCompList);
	}
}
