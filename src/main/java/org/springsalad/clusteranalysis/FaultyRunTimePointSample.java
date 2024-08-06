package org.springsalad.clusteranalysis;

import java.util.ArrayList;
import java.util.List;

class FaultyRunTimePointSample extends RunTimePointSample {
	
	private final Exception exception;
	public FaultyRunTimePointSample(String runStr, double timePointValue, String tpvStr, String molNamesStr, Exception exception) {
		super(runStr, timePointValue, tpvStr, molNamesStr, new ArrayList<Integer>(), new ArrayList<Cluster>());
		this.exception = exception;
	}
	
	@Override
	NormalTPStats calculateClusterStats() {
		return new FaultyNormalTPStats(runStr, timePointValue, tpvStr, molNamesStr, exception);
	}

}
