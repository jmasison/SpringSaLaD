package org.springsalad.clusteranalysis;

import java.util.ArrayList;
import java.util.List;

class ClusterBuilder {
	
	private final int supposedSize;
	private final List<String> molNames;
	private final List<Integer> composition;
	public ClusterBuilder(List<String> molNames, int supposedSize) {
		this.supposedSize = supposedSize;
		this.molNames = molNames;
		this.composition = new ArrayList<>(molNames.size());
	}

	public void addOnToComposition(String molecule, int count) {
		int index = molNames.indexOf(molecule);
		if (index == -1) {
			throw new IllegalMoleculeNameException("Cluster builder received an invalid molecule name: " + molecule);
		}
		composition.add(index, count);		
	}

	public Cluster produceCluster() {
		try {
			Cluster c = new Cluster(molNames, composition);
			if (c.size != supposedSize) {
				throw new IllegalArgumentException();
			}		
			return c;
		}
		catch (IllegalArgumentException iae) {
			throw new MalformedClusterException("A cluster of the specified size " + supposedSize + " cannot be constructed from the given composition: " 
					+ "\n" + molNames
					+ "\n" + composition);
		}
	}

    public static class MalformedClusterException extends RuntimeException{
		private static final long serialVersionUID = 3241828733000206058L;
		public MalformedClusterException () {
    		super();
    	}
		public MalformedClusterException (String message) {
		    super(message);
		}
		public MalformedClusterException (String message, Throwable cause) {
			super(message, cause);
		}
		public MalformedClusterException (Throwable cause) {
			super(cause);
		}
    }
    
    public static class IllegalMoleculeNameException extends IllegalArgumentException{
		private static final long serialVersionUID = 3241828733000206058L;
		public IllegalMoleculeNameException () {
    		super();
    	}
		public IllegalMoleculeNameException (String message) {
		    super(message);
		}
		public IllegalMoleculeNameException (String message, Throwable cause) {
			super(message, cause);
		}
		public IllegalMoleculeNameException (Throwable cause) {
			super(cause);
		}
    }
	
}
