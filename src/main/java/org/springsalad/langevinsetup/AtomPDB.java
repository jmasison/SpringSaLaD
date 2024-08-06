package org.springsalad.langevinsetup;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class AtomPDB implements Clusterable {

	private int index;
	private double x;
	private double y;
	private double z;
	
	
	public AtomPDB(int index, double x, double y, double z){
		this.index = index;
		this.x = x;
		this.y = y;
		this.z = z;		
	}

	public String toString() {
		return "AtomPDB_" + index;
		//return "AtomPDB_" + index + " [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	public boolean equals(AtomPDB a){
		if(a.x == this.x && a.y == this.y && a.z == this.z){
			return true;
		}else{
			return false;
		}
	}
	
	public int getIndex(){
		return index;
	}
	public void setIndex(int index0){
		this.index = index0;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getZ(){
		return z;
	}
	
	public double distanceToEU(AtomPDB atom){
		return Math.sqrt(
				Math.pow(atom.getX() - this.getX(), 2) + 
				Math.pow(atom.getY() - this.getY(), 2) + 
				Math.pow(atom.getZ() - this.getZ(), 2)
				);
	}
	
	public double distanceToEU(double x, double y, double z){
		return Math.sqrt(
				Math.pow(x - this.getX(), 2) + 
				Math.pow(y - this.getY(), 2) + 
				Math.pow(z - this.getZ(), 2)
				);
	}

	//clusterable-
	public double[] getPoint() {
		return new double[] {this.x, this.y, this.z};
	}
	
	
}
