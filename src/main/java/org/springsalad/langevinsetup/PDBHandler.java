package org.springsalad.langevinsetup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Set;

//import org.apache.commons.math3.ml.clustering.CentroidCluster;
//import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.springsalad.helpersetup.Colors;
import org.springsalad.helpersetup.PopUp;

public class PDBHandler{
	private int k;
	private String pdbfile = "";
	private KMeanClusterGen kmc;
	private ArrayList<AtomPDB> centers = new ArrayList<>();
	private ArrayList<ArrayList<AtomPDB>> groups = new ArrayList<>();
//	private ArrayList<AtomPDB> centers = new ArrayList<>();
//	private ArrayList<List<AtomPDB>> groups = new ArrayList<>();
	private Molecule mol;
		
	public PDBHandler(String filename, int k, ArrayList<AtomPDB> fCent){
		this.k = k;
		this.pdbfile = filename;
		
		PopUp.information("Please wait for the molecule to load\n(It will appear under Molecules)");
		System.out.println("Begin generating Centroids (Sites)...");		
//optimized from apache
//		KMeansPlusPlusClusterer<AtomPDB> kmcAlpha = new KMeansPlusPlusClusterer<>(this.k, 1000);
//		List<CentroidCluster<AtomPDB>> listCC;
//		if(this.pdbfile.contains(".cif")){
//			listCC = kmcAlpha.cluster(parseCIF(this.pdbfile));
//		}else{
//			listCC = kmcAlpha.cluster(parsePDB(this.pdbfile));
//		}
//			
//		//set centers and groups
//		int indexCent = 0;
//		for(CentroidCluster<AtomPDB> centclust: listCC){
//			this.centers.add(new AtomPDB(indexCent, 
//					centclust.getCenter().getPoint()[0],
//					centclust.getCenter().getPoint()[1],
//					centclust.getCenter().getPoint()[2]));
//			indexCent++;
//			
//			this.groups.add(centclust.getPoints());
//		}
//		//////////////////		
//		//my implementation
		if(this.pdbfile.contains(".cif")){
			kmc = new KMeanClusterGen(this.k, parseCIF(this.pdbfile), fCent);
		}else{
			kmc = new KMeanClusterGen(this.k, parsePDB(this.pdbfile), fCent);
		}
		
		this.centers = kmc.getCenters();
		this.groups = kmc.getGroups();
		for(int i = 0; i < k; i++){
			centers.get(i).setIndex(i);
		}	
		//////////////////
		
//		HashMap<AtomPDB, Double> radii = getRadii(this.centers, this.groups);
		HashMap<AtomPDB, Double> radii = getRadiiAL(this.centers, this.groups);
							
		//format coors
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
				
		//set up center of mass of centers for translation to the origin
		AtomPDB adjust = KMeanClusterGen.average(this.centers);
		double adjX = adjust.getX();
		double adjY = adjust.getY();
		double adjZ = adjust.getZ();
		
		this.mol = new Molecule("Mol$" + (adjX + adjY + adjZ)/3);
		int index;		
		//sites and types
		for (index = 0; index < this.centers.size(); index++){ //we want there to be a direct link (index) between sites and centers
			AtomPDB cent = this.centers.get(index);
			SiteType st = new SiteType(this.mol, "Type" + index);
			if(fCent.contains(cent)){
				st.setColor(Colors.BLUE);
			}
						
			//st.setRadius(Double.parseDouble(df.format(centToCentD.get(cent)/2)));
			st.setRadius(Double.parseDouble(df.format(radii.get(cent))));
			//st.setRadius(radii.get(cent));
			
			Site s = new Site(this.mol);
			s.setIndex(index);
			s.setType(st);
			s.setInitialState(st.getState(0));
			s.setLocation(SystemGeometry.INSIDE);
			s.setX(cent.getX() - adjX);
			s.setY(cent.getY() - adjY);
			s.setZ(cent.getZ() - adjZ);
			
			this.mol.addSite(s);
			this.mol.addType(st);
			
			System.out.println(
					"DEBUG: C" + index +
					" = x( " + df.format(cent.getX()) + 
					" )\ty( " + df.format(cent.getY()) + 
					" )\tz( " + df.format(cent.getZ()) + 
					" )\tR( " + df.format(radii.get(cent)) +
					" )\n\n DONE."					
					);
		}
		
		LinkGen gen = new LinkGen(this.centers, this.mol);
		this.mol = gen.getMol();
	}
	
	public Molecule getMol(){
		return this.mol;
	}

// optimized
//	private HashMap<AtomPDB, Double> getRadii(ArrayList<AtomPDB> centers, ArrayList<List<AtomPDB>> groups) {
//		HashMap<AtomPDB, Double> radii = new HashMap<>();
//				
//		//initialize radii to longest distance to point in cluster
//		int index = 0;
//		for(AtomPDB cent: centers){
//			radii.put(cent, 0.0); //initialize to 0
//			
//			for (AtomPDB atom: groups.get(index)){
//				Double d = atom.distanceToEU(cent);
//				if(d > radii.get(cent)){
//					radii.put(cent, d);
//				}
//			}
//			index++;
//		}
//			
//		//Now check for overlaps
//		// If the distance to the nearest center (d) /2 is less than the radius there is an overlap, so reduce it to d/2
//		HashMap<AtomPDB, Double> c2cD = distanceToClosestCenter(centers, pariedDistancesHelper(centers));
//		for(AtomPDB cent: centers){
//			if((c2cD.get(cent)/2) < radii.get(cent)){
//				radii.put(cent, (c2cD.get(cent)/2));
//			}
//		}	
//		return radii;
//	}
	
	private HashMap<AtomPDB, Double> getRadiiAL(ArrayList<AtomPDB> centers, ArrayList<ArrayList<AtomPDB>> groups) {
		HashMap<AtomPDB, Double> radii = new HashMap<>();
				
		//initialize radii to longest distance to point in cluster
		int index = 0;
		for(AtomPDB cent: centers){
			radii.put(cent, 0.0); //initialize to 0
			
			for (AtomPDB atom: groups.get(index)){
				Double d = atom.distanceToEU(cent);
				if(d > radii.get(cent)){
					radii.put(cent, d);
				}
			}
			index++;
		}
			
		//Now check for overlaps
		// If the distance to the nearest center (d) /2 is less than the radius there is an overlap, so reduce it to d/2
		if(centers.size() > 1){ // make sure we have at least more than one center
			HashMap<AtomPDB, Double> c2cD = distanceToClosestCenter(centers, pariedDistancesHelper(centers));
			for(AtomPDB cent: centers){
				if((c2cD.get(cent)/2) < radii.get(cent)){
					radii.put(cent, (c2cD.get(cent)/2) - 0.0001); //0.0001 so there isnt an overlap
				}
			}	
		}
		return radii;
	}


	private static ArrayList<AtomPDB> parsePDB(String filename){
		ArrayList<AtomPDB> temp = new ArrayList<>();
		BufferedReader bReader;
		
		int c1 = 30;
		int c2 = 38;
		int c3 = 46;
		int c4 = 54;
		
		int i1 = 5;
		int i2 = 11;
		
		try {
			bReader = new BufferedReader(new FileReader(filename));

			String line;
			while ((line = bReader.readLine()) != null) {
				if(!line.startsWith("ATOM")) { //only want Atoms, ignore everything else
					continue;
				}
				
				//find the index position in parts that contains x,y,z
				//x = 31 - 38
				//y = 39 - 46
				//z = 47 - 54
				char[] x = new char[8];
				char[] y = new char[8];
				char[] z = new char[8];
				line.getChars(c1, c2, x, 0); //read 30,31,32,33,34,35,36,37 (or -1 for cif)
				line.getChars(c2, c3, y, 0); //read 38,39,40,41,42,43,44,45 (or -1 for cif)
				line.getChars(c3, c4, z, 0); //read 46,47,48,49,50,51,52,53 (or -1 for cif)
				
				Double xX = Double.parseDouble((new String(x)).trim());
				Double yY = Double.parseDouble((new String(y)).trim());
				Double zZ = Double.parseDouble((new String(z)).trim());			

				char[] idx = new char[6];
				line.getChars(i1, i2, idx, 0); //read 5,6,7,8,9,10 (ALWAYS)
				Integer iDX = Integer.parseInt((new String(idx)).trim());
				
				AtomPDB atom = new AtomPDB(iDX, xX / 10, yY / 10, zZ / 10);//DONE divide by 10, to convert to nm
				temp.add(atom);						
			}

			bReader.close();
			System.out.println("parsePDB of " + filename + " finished.");

			return temp;

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error: could not create reader with filename: " + filename + "\n");
		}
		
		return null;		
	}
	
	private static ArrayList<AtomPDB> parseCIF(String filename){
		ArrayList<AtomPDB> temp = new ArrayList<>();
		String delim = "\\s+";
		BufferedReader bReader;
		
		try {
			bReader = new BufferedReader(new FileReader(filename));

			String line;
			while ((line = bReader.readLine()) != null) {
				if(!line.startsWith("ATOM")) { //only want Atoms, ignore everything else
					continue;
				}
				String[] parts = line.split(delim); //split based on multiple spaces

				temp.add(new AtomPDB(
						Integer.parseInt(parts[1]), 
						Double.parseDouble(parts[10]) / 10, 
						Double.parseDouble(parts[11]) / 10,  
						Double.parseDouble(parts[12]) / 10));//DONE divide by 10, to convert to nm							
			}

			bReader.close();
			System.out.println("parsePDB of " + filename + " finished.");

			return temp;

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error: could not create reader with filename: " + filename + "\n");
		}
		
		return null;		
	}
	
	/*
	 * method for computing distance between all points, to be used in computing 
	 * the distance between centroids in radii assignment
	 * 
	 * */
	
	private static HashMap<ArrayList<AtomPDB>, Double> pariedDistancesHelper(ArrayList<AtomPDB> list){
		HashMap<ArrayList<AtomPDB>, Double> temp = new HashMap<>();
		int numElements = list.size();
		
		for(int i = 1; i < numElements + 1; i++){
			for(int j = i + 1; j < numElements + 1; j++){
				//add both point a to b, and b to a
				int a = i - 1;
				int b = j - 1;
				
				AtomPDB aA = list.get(a);
				AtomPDB bA = list.get(b);
				
				ArrayList<AtomPDB> ab = new ArrayList<>();
				ArrayList<AtomPDB> ba = new ArrayList<>();
				
				ab.add(aA);
				ab.add(bA);
				
				ba.add(bA);
				ba.add(aA);
				
				temp.put(ab, bA.distanceToEU(aA));				
				temp.put(ba, bA.distanceToEU(aA));
			}
		}
		
		return temp;
	}
	
	private static HashMap<AtomPDB, Double> distanceToClosestCenter(ArrayList<AtomPDB> centers, HashMap<ArrayList<AtomPDB>, Double> map){
		HashMap<AtomPDB, Double> temp = new HashMap<AtomPDB, Double>();
		Set<ArrayList<AtomPDB>> keys = map.keySet();
		
		ArrayList<ArrayList<AtomPDB>> toRemove = new ArrayList<>();
		
		for(AtomPDB a: centers){
			for(ArrayList<AtomPDB> al: keys){
				if(al.get(0).equals(a)){
					if(!temp.containsKey(a)){
						temp.put(a, map.get(al));
					}else{
						if(temp.get(a) >= map.get(al)){ //overwrite distance if we find a shorter one
							temp.put(a, map.get(al));
						}//else do not change value
					}
					
					toRemove.add(al);
				}	
			}
			for(ArrayList<AtomPDB> al: toRemove){
				keys.remove(al);
			}
			toRemove.clear();
		}
		return temp;		
	}



	public class LinkGen {
		
		private Molecule mol;
		private ArrayList<AtomPDB> centers;
		private double[][] matrix;
		private int size;
		
		public LinkGen(ArrayList<AtomPDB> centers, Molecule mol){
			this.mol = mol;
			this.centers = centers;
			this.size = this.centers.size();
			this.matrix = new double[this.size][this.size];
			
			for(AtomPDB atomA: this.centers){
				for(AtomPDB atomB: this.centers){
					this.matrix[atomA.getIndex()][atomB.getIndex()] = atomA.distanceToEU(atomB);
				}
			}
		
			
			boolean visited[] = new boolean[this.size];
			for(int i = 0; i < this.size; i++){
				visited[i] = false;
			}	
			//start at 0
			visited[centers.get(0).getIndex()] = true;
						
			//leverage the idea that every node is connected
			int totalLinks = 0;
			int currNodeIdx = 0;
			while(totalLinks < this.size - 1){
				int minIdx = -1;
				double min = Double.MAX_VALUE;
				
				//find node with lowest value edge to current node
				for(int i = 0; i < this.size; i++){
					int trueI = centers.get(i).getIndex();
					if(visited[trueI] == false && this.matrix[currNodeIdx][trueI] < min){
						min = this.matrix[currNodeIdx][trueI];
						minIdx = trueI;
					}
				}
				this.mol.addLink(new Link(this.mol.getSite(currNodeIdx), this.mol.getSite(minIdx)));
				
				currNodeIdx = minIdx;
				visited[minIdx] = true;
				totalLinks++;
				
			}
		}
		
		public Molecule getMol(){
			return this.mol;
		}
		
	}

	
	
	
	
	
	
	






}