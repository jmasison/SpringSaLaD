package org.springsalad.clusteranalysis;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

class DataSource {
	private final String dataFolder;
	private final int nf;
	private final String clusterInputFileFormat;
	
	private final List<String> molNames;
	private final String molNamesStr;
	
	public final List<Integer> runNumberList;
	public final List<Double> timePointValueList;
	
	public final List<Iterable<RunTimePointSample>> byTimeThenByRun;
	public final List<Iterable<RunTimePointSample>> byRunThenByTime;
	
	private final FileOperationExceptionLogger logger;
	
	DataSource (FileOperationExceptionLogger logger,
			String dataFolder, List<String> molNames,
			double startTime, double endTimeInclusive, double timeStep,
			int firstRunIndex, int lastRunIndexInclusive) {
		this.logger = logger;
		
		this.dataFolder = dataFolder;
		nf = Math.max(0,BigDecimal.valueOf(timeStep).stripTrailingZeros().scale());
		clusterInputFileFormat = "Run%d" + File.separator + "Clusters_Time_%."+nf+"f.csv";
		
		this.molNames = molNames;
		StringBuilder moleculeTypeHeaderSB = molNames.stream().collect(StringBuilder::new, (sb,str)->sb.append(str).append(","), StringBuilder::append);
		moleculeTypeHeaderSB.insert(0,"\"").replace(moleculeTypeHeaderSB.length()-1, moleculeTypeHeaderSB.length(), "\"");
		molNamesStr = moleculeTypeHeaderSB.toString();
		
		long totRunNum = lastRunIndexInclusive - firstRunIndex + 1;
		runNumberList = IntStream.iterate(firstRunIndex, i -> i + 1).limit(totRunNum).boxed().collect(toList());
		long totTPVNum = (long)((endTimeInclusive - startTime)/timeStep) +1;
		timePointValueList = DoubleStream.iterate(startTime, tpv -> tpv + timeStep).limit(totTPVNum).boxed().collect(toList());
		
		byTimeThenByRun = timePointValueList.stream().map(tpv -> new SingleTimeAllRunsIterable(tpv)).collect(toList());
		byRunThenByTime = runNumberList.stream().map(run -> new SingleRunAllTimeIterable(run)).collect(toList());
	}
	
	// if data comes from somewhere else, extract an abstract datasource class with this method as the only abstract method
	protected RunTimePointSample getRunTimePointSample(int run, double tpv) {
		String relativeFilePathStr = String.format(clusterInputFileFormat, run, tpv);
		Path path = Paths.get(dataFolder, relativeFilePathStr);
		try {			
			DataFrame rawFileDF = CSVHandler.readCSV(path);
			RunTimePointSample rtps = parseRawFileDF(run, tpv, rawFileDF);
			return rtps;
		}
		catch(IOException | UnexpectedFileContentException exception) {
			logger.logFileReadParseException(relativeFilePathStr, exception);
			return new FaultyRunTimePointSample(String.format(ClusterStatsProducer.SINGLE_RUN_STR, run), 
												tpv, String.format("%."+nf+"f",tpv), molNamesStr, exception);
		}
		catch (ClassCastException exception) {
        	Exception e = new UnexpectedFileContentException("File data does not match expected format of String,Integer: " + path, exception);
        	logger.logFileReadParseException(relativeFilePathStr, e);
        	return new FaultyRunTimePointSample(String.format(ClusterStatsProducer.SINGLE_RUN_STR, run), 
					tpv, String.format("%."+nf+"f",tpv), molNamesStr, exception);
        }
		catch (ClusterBuilder.MalformedClusterException exception) {
			Exception e =  new UnexpectedFileContentException("File contains contradicting information about clusters: " + path, exception);
			logger.logFileReadParseException(relativeFilePathStr, e);
			return new FaultyRunTimePointSample(String.format(ClusterStatsProducer.SINGLE_RUN_STR, run), 
					tpv, String.format("%."+nf+"f",tpv), molNamesStr, exception);
        }
		catch (ClusterBuilder.IllegalMoleculeNameException exception) {
			Exception e =  new UnexpectedFileContentException("File contains unexpected molecule name: " + path, exception);
			logger.logFileReadParseException(relativeFilePathStr, e);
			return new FaultyRunTimePointSample(String.format(ClusterStatsProducer.SINGLE_RUN_STR, run), 
					tpv, String.format("%."+nf+"f",tpv), molNamesStr, exception);
        }
	}
	
	RunTimePointSample parseRawFileDF(int run, double tpv, DataFrame rawFileDF) {
		List<Integer> clusterSizeList = new ArrayList<>();
        List<Cluster> clusterCompList = new ArrayList<>();

        Iterator<Object[]> rawFileDFIterator = rawFileDF.iterator();

        //assemble clusterSizeList and clusterCompList
        Object[] dfRow;
        int numClusters = 0;
        while (rawFileDFIterator.hasNext()){
            dfRow = rawFileDFIterator.next();

            String label = (String)dfRow[0];
            if (label.equals("Total clusters")){
                numClusters = (Integer)dfRow[1];
            }
            if (label.equals("Size")) {
                int size = (Integer) dfRow[1];
                clusterSizeList.add(size);
                ClusterBuilder clusterBuilder = new ClusterBuilder(molNames, size);
                while (rawFileDFIterator.hasNext()) {
                    dfRow = rawFileDFIterator.next();
                    if (dfRow[0].equals("Cluster Index")){
                        break;
                    }
                    String molecule = (String) dfRow[0];
                    int count = (Integer) dfRow[1];
                    clusterBuilder.addOnToComposition(molecule, count);
                }
                Cluster c = clusterBuilder.produceCluster();
                clusterCompList.add(c);
            }
        }
        IntStream.generate(()->1).limit(numClusters-clusterSizeList.size()).forEach(clusterSizeList::add);
		return new RunTimePointSample(String.format(ClusterStatsProducer.SINGLE_RUN_STR, run), 
										tpv, String.format("%."+nf+"f",tpv),
										molNamesStr,
										clusterSizeList, clusterCompList);
	}

	
	
	public FileOperationExceptionLogger getLogger() {
		return logger;
	}
	public String getDataFolder() {
		return dataFolder;
	}
	
	public Iterable<RunTimePointSample> getSingleRunAllTimes(int run) {
		int runIndex = runNumberList.indexOf(run);
		if (runIndex == -1) {
			throw new IllegalArgumentException("Invalid run number: " + run);
		}
		return byRunThenByTime.get(runIndex);
	}
	
	public Iterable<RunTimePointSample> getSingleTimeAllRuns(double tpv) {
		int timeIndex = timePointValueList.indexOf(tpv);
		if (timeIndex == -1) {
			throw new IllegalArgumentException("Invalid time point: " + tpv);
		}
		return byTimeThenByRun.get(timeIndex);
	}
	
	private class SingleTimeAllRunsIterable implements Iterable<RunTimePointSample>{
		private double tpv;
		private SingleTimeAllRunsIterable(double tpv) {
			this.tpv = tpv;
		}
		
		@Override
		public SingleTimeAllRunsIterator iterator() {
			return new SingleTimeAllRunsIterator(tpv);
		}
	}
	
	private class SingleTimeAllRunsIterator implements Iterator<RunTimePointSample>{
		private double tpv;
		private Iterator<Integer> runIterator;
		
		private SingleTimeAllRunsIterator(double tpv) {
			this.tpv = tpv ;
			runIterator = runNumberList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return runIterator.hasNext();
		}
		
		@Override
		public RunTimePointSample next() {
			int run = runIterator.next();
			return getRunTimePointSample(run, tpv);
		}
	}
	
	private class SingleRunAllTimeIterable implements Iterable<RunTimePointSample>{
		private int run;
		private SingleRunAllTimeIterable(int run) {
			this.run = run;
		}
		
		@Override
		public SingleRunAllTimeIterator iterator() {
			return new SingleRunAllTimeIterator(run);
		}
	}
	
	private class SingleRunAllTimeIterator implements Iterator<RunTimePointSample>{
		private int run;
		private Iterator<Double> timeIterator;
		
		private SingleRunAllTimeIterator(int run) {
			this.run = run;
			timeIterator = timePointValueList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return timeIterator.hasNext();
		}
		
		@Override
		public RunTimePointSample next() {
			double tpv = timeIterator.next();
			return getRunTimePointSample(run, tpv);			
		}
	}
	
	
}
