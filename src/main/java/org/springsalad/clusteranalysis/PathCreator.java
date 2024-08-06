package org.springsalad.clusteranalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathCreator {
	/** 
	 * cluster data directory structure:
	 * 
	 * data
	 * 		Cluster_stat
	 * 			Averages
	 * 				Time.
	 * 				Series.
	 * 				for.
	 * 				each
	 * 				trajectory.
	 * 			Histograms
	 * 				Size_Freq_Fotm
	 * 					<Trajectory name>
	 * 						Csv file
	 * 						Csv file
	 *  					Csv file
	 *  				<Trajectory name>
	 *  				<Trajectory name>
	 * 				Size_Comp_Fotm
	 * 					<Trajectory name>
	 * 						Csv file
	 *  					Csv file
	 *   					Csv file
	 */
	


	public static final String AvgFolderEXTENSION = "Cluster_stat/Averages",
			HisFolderEXTENSION = "Cluster_stat/Histograms",
			SffFolderEXTENSION = "Cluster_stat/Histograms/Size_Freq_Fotm",
			ScfFolderEXTENSION = "Cluster_stat/Histograms/Size_Comp_Freq";
	
	public static final String averagesFileName = "ACS_SD_ACO.csv", 
								sizeFreqFotmFileName = "Size_Freq_Fotm.csv", 
								sizeCompFreqFileName = "Size_Comp_Freq.csv";
	
	// don't forget to createDirectories()
	public static Path trajectoryTimeSeriesPath(String dataFolder, String runStr) throws IOException{
		Path dir = Paths.get(dataFolder, AvgFolderEXTENSION);
		if (! Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		return Paths.get(dir.toString(), runStr + "_" + averagesFileName);
	}
	
	public static Path clusterSizeDistributionPath(String dataFolder, String runStr, String tpvStr) throws IOException{
		Path dir = Paths.get(dataFolder, SffFolderEXTENSION, runStr);
		if (! Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		return Paths.get(dir.toString(), runStr + "_" + tpvStr + "_" + sizeFreqFotmFileName);
	}
	
	public static Path clusterCompositionDistributionPath(String dataFolder, String runStr, String tpvStr) throws IOException{
		Path dir = Paths.get(dataFolder, ScfFolderEXTENSION, runStr);
		if (! Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		return Paths.get(dir.toString(), runStr + "_" + tpvStr + "_" + sizeCompFreqFileName);
	}
}

@FunctionalInterface
interface HistogramPathCreator{
	Path createPath(String dataFolder, String runStr, String tpvStr) throws IOException;
}
