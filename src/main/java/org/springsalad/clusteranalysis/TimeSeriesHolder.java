package org.springsalad.clusteranalysis;

import java.util.ArrayList;
import java.util.List;

class TimeSeriesHolder {
	final String runStr;
	private int currentListIndex;
	
	final List<Double> timeInSeconds;
	final List<Double> acsList;
	final List<Double> sdList;
	final List<Double> acoList;
	
	static final String[] outputHeaders = new String[] {"Time (s)", "ACS", "SD", "ACO"}; 

    TimeSeriesHolder(String runStr, List<Double> timeInSeconds){
    	this.runStr = runStr;
    	this.timeInSeconds = timeInSeconds;
    	currentListIndex = 0;
    	acsList = new ArrayList<>();
    	sdList = new ArrayList<>();
    	acoList = new ArrayList<>();
    }

    void addAveragesToLists(TPStats tpStats){
    	assert tpStats.tpv == timeInSeconds.get(currentListIndex);
    	acsList.add(tpStats.acs);
    	sdList.add(tpStats.sd);
    	acoList.add(tpStats.aco);
    	currentListIndex++;
    }
    
    void writeTo(DataDestination dataDestination) {
    	dataDestination.writeTrajectoryTimeSeries(runStr, outputHeaders, timeInSeconds, acsList, sdList, acoList);
    }
}
