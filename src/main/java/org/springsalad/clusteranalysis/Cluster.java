package org.springsalad.clusteranalysis;

import java.util.Iterator;
import java.util.List;

class Cluster implements Comparable<Cluster> {
	List<String> molNames;
    List<Integer> composition;
    int size;

    Cluster(List<String> molNames, List<Integer> composition){
        if (composition.size() != molNames.size()){
            throw new IllegalArgumentException("Cannot understand the cluster composition given");
        }
        this.molNames = molNames;
        this.composition = composition;
        size = 0;
        for (int count: composition){
        	if (count < 0) {
        		throw new IllegalArgumentException("Cannot have a negative number of a certain molecule");
        	}
            size += count;
        }
    }

    public int compareTo(Cluster c){
        int sizeDiff = this.size - c.size;
        if (sizeDiff != 0) {
        	return sizeDiff;
        }
        Iterator<Integer> thisIterator = this.composition.iterator();
        Iterator<Integer> cIterator = c.composition.iterator();
        while (thisIterator.hasNext() && cIterator.hasNext()) {
        	int componentDiff = thisIterator.next() - cIterator.next();
        	if (componentDiff != 0) {
        		return componentDiff;
        	}
        }
        return this.composition.size() - c.composition.size();
    }

    //surrounded with quotes
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < composition.size(); i++){
            sb.append(composition.get(i));
            if (i != composition.size() -1 ) {
            	sb.append(",");
            }
        }
        return sb.append("\"").toString();
    }
}
