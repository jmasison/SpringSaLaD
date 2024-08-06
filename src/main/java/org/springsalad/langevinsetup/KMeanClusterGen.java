package org.springsalad.langevinsetup;

/*
 * Algorithm from:
 * 
 * https://radixcode.com/k-mean-clustering-algorithm-implementation-in-c-java/
 * 
 * */


/*
 * This class is built to generate a list of k clusters around k centers. It takes as
 *  input an int k and a list of AtomPDBs to cluster. Its then calculates and stores 
 *  the centers (centers) and groups of AtomPDBs (groups) for each center.
 * 
 * */

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class KMeanClusterGen {

    int k;
    ArrayList<AtomPDB> list;
    ArrayList<AtomPDB> centers;
    private ArrayList<AtomPDB> oldCenters;
    private ArrayList<Double> row;
    ArrayList<ArrayList<AtomPDB>> groups;
    
    public KMeanClusterGen(int k, ArrayList<AtomPDB> list, ArrayList<AtomPDB> fixedCenters) {
        this.k = k;
        this.list = list;
        centers = new ArrayList<>();
        oldCenters = new ArrayList<>();
        row = new ArrayList<>();
        groups = new ArrayList<>(); //list of atoms in each centroid
        
        //initialize group of atoms in centroids
        for (int i = 0; i < k; i++) {
            groups.add(new ArrayList<>());
        }
        
        //pick k number of random atoms to serve as initial centers
        for(AtomPDB a: fixedCenters){ //after setting unmovable centers
        	centers.add(a);
        }
        
        for (int i = 0; i < k - fixedCenters.size(); i++) {
        	centers.add(list.get(ThreadLocalRandom.current().nextInt(0, list.size())));
        	//centers.add(list.get(ThreadLocalRandom.current().nextInt(0, list.size() + 1))); 
        }           
        
        int iter = 1;
        do {
            
        	//assign all atoms to a group
            for (AtomPDB atom : list) {
                for (AtomPDB c : centers) {
                    row.add(c.distanceToEU(atom));
                }
                //add to the center groups of the closet center
                groups.get(row.indexOf(Collections.min(row))).add(atom);
                row.removeAll(row);
            }
            
            //calculate centers
            for (int i = 0; i < k; i++) {
                if (iter == 1) {
                    oldCenters.add(centers.get(i));
                } else {
                    oldCenters.set(i, centers.get(i));
                }
                //set center to center of mass of points ion that center's group
                if (!groups.get(i).isEmpty()) {
                	//check if center is movable
                	if(!fixedCenters.contains(centers.get(i))){
                		centers.set(i, average(groups.get(i)));
                	}
                }
            }
            //clear groups, unless last iteration
            if (!centers.equals(oldCenters) && iter < 100) { 
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).removeAll(groups.get(i));
                }
            }
            iter++;
        } while (!centers.equals(oldCenters) && iter < 101); //limit 100 iterations TODO could make user value
    }
       
    public int getK(){
    	return k;
    }
    
    public ArrayList<AtomPDB> getCenters(){
    	return centers;
    }
    
    public ArrayList<ArrayList<AtomPDB>> getGroups(){
    	return groups;
    }
    
    public static AtomPDB average(ArrayList<AtomPDB> list) {
    	int size = list.size();
    	double x = 0;
        double y = 0;
        double z = 0;
        
        for (AtomPDB a : list) {
            x += a.getX();
            y += a.getY();
            z += a.getZ();
        }
        
        AtomPDB temp = new AtomPDB(-1, x/size, y/size, z/size);
        return temp;
    }
    
    
}












