
package org.springsalad.dataprocessor;

import java.util.Scanner;

import org.springsalad.clusteranalysis.FileOperationExceptionLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataProcessor {

    /******   The names of the csv files in each "Run" folder **************/
    public final static String BOND_DATA = "FullBondData.csv";
    public final static String MOLECULE_DATA = "FullCountData.csv";
    public final static String STATE_DATA = "FullStateCountData.csv";
    public final static String SITE_PROPERTY_DATA = "SitePropertyData.csv";
    public final static String LOCATION_DATA = "LocationData.csv";
    public final static String MOLECULE_IDS = "MoleculeIDs.csv";
    public final static String SITE_IDS = "SiteIDs.csv";
    
    public final static String AVERAGE_MOLECULE_DATA = "AverageMoleculeCounts";
    public final static String AVERAGE_BOND_DATA = "AverageBondCounts";
    public final static String AVERAGE_STATE_DATA = "AverageStateCounts";
    public final static String AVERAGE_SITE_PROPERTY_DATA = "AverageSiteProperties";
    public final static String AVERAGE_DISTANCE_DATA = "AverageDistanceData";
    
    public final static String DISTANCE_DATA = "DistanceData";
    public final static String HEAT_MAP_DATA = "HeatMap";
    public final static String ALL_DATA = "AllData";
    
    /******** The name of the "Running Time" file in each run folder ********/
    public final static String RUNNING_TIME = "RunningTime.txt";
    public final static String DAYS = "days";
    public final static String DAY = "day";
    public final static String HOURS = "hours";
    public final static String HOUR = "hour";
    public final static String MINUTES = "min.";
    public final static String SECONDS = "sec.";
    
    /*******  Possible drives to read and write from **********************/
    public final static String C_DESKTOP = "C:/Users/pmichalski.CAM/Documents/LangevinFolder";
    public final static String C_LAPTOP = "C:/Users/Paul/Documents/LangevinFolder";
    public final static String Z_DRIVE = "Z:/LangevinFolder";
    
    /*******  The simulation name ****************************************/
    private String simulationName;
    
    /******  The top level folder name *************************************/
    protected String dataFolder;
    
    /**************  THE FILES FOR VARIOUS DATA **************************/
    private File moleculeDataFile = null;
    private File bondDataFile = null;
    private File stateDataFile = null;
    private File runningTimeFile = null;
    // Hashmap between molecule names, bond names, etc, and raw data files
    // containing time point data from all runs
    private HashMap<String, File> rawDataFiles = null;
    // Hashmap between site names and average site data files
    private HashMap<String, File> siteFiles = null;
    
    /****************** MOLECULE, BOND, STATE NAMES **********************/
    private final ArrayList<String> moleculeNames = new ArrayList<>();
    private final ArrayList<String> bondNames = new ArrayList<>();
    private final ArrayList<String> stateNames = new ArrayList<>();
    private final ArrayList<String> siteNames = new ArrayList<>();
    
    /******** HASHMAP BETWEEN SITE IDS AND MOLECULE NAME AND SITETYPE ******/
    private HashMap<Integer, String> siteIDMap = null;
    
    /**************** TELLS US IF WE SET THE DATA FOLDER YET **************/
    private boolean dataExists;
    
    /* ********************* CONSTRUCTOR ********************************/
    
    protected FileOperationExceptionLogger logger;
    public DataProcessor(String drive, String simulationName){
        this.simulationName = simulationName;
        this.dataFolder = drive + "/" +  simulationName + "_FOLDER/data/";
    }
    
    
    
    
    public void calculateStatistics(int startNum, int endNumIncl) {
    	logger = new FileOperationExceptionLogger("");
    	System.out.println("Computing time point averages.");
        computeAllTimePointAverages(startNum, endNumIncl);
        System.out.println("Computing running times.");
        getRunningTimes(startNum, endNumIncl);
        System.out.println("Computing raw data files.");
        makeAllRawDataFiles(startNum, endNumIncl);
        System.out.println("Finished computing data.");
        
        displayLog();
    }
    
    /* ************* BUILD THE NAMES ONCE WE HAVE DATA ********************/
    public void grabNames(){
        this.saveBondNames();
        this.saveMoleculeNames();
        this.saveSitesOfInterest();
        this.saveStateNames();
        this.buildSiteIDHashMap();
        rawDataFiles = new HashMap<>(10*(moleculeNames.size() + bondNames.size()
                                                        + stateNames.size()));
        siteFiles = new HashMap<>(10*siteNames.size());
        dataExists = true;
    }
    
    /* ****************  Indicate if we have data *************************/
    public boolean hasData(){
        return dataExists;
    }
    
    /* **************** PICK AVERAGE FILE NAME ***************************/
    private String averageFileName(String fileName){
        switch(fileName){
            case BOND_DATA:
                return AVERAGE_BOND_DATA;
            case MOLECULE_DATA:
                return AVERAGE_MOLECULE_DATA;
            case STATE_DATA:
                return AVERAGE_STATE_DATA;
            case SITE_PROPERTY_DATA:
                return AVERAGE_SITE_PROPERTY_DATA;
            default:
                return null;
        }
    }
    
    /* *************** GET THE SIMULATION NAME AND FOLDER ****************/
    
    public String getSimulationName(){
        return simulationName;
    }
    
    public void setSimulationName(String name){
        this.simulationName = name;
    }
    
    public String getDataFolder(){
        return dataFolder;
    }
    
    public void setDataFolder(String folderName){
        this.dataFolder = folderName;
    }
    
    /* ***************  GET AND SET THE DATA FILES *************************/
    public File getMoleculeAverageDataFile(){
        return moleculeDataFile;
    }
    
    public File getBondAverageDataFile(){
        return bondDataFile;
    }
    
    public File getStateAverageDataFile(){
        return stateDataFile;
    }
    
    public File getRunningTimesFile(){
        return runningTimeFile;
    }
    
    public File getRawDataFile(String key){
        return rawDataFiles.get(key);
    }
    
    public File getSiteFile(String key){
        return siteFiles.get(key);
    }
    
    public void setMoleculeAverageDataFile(String fileName){
        if(!fileName.equals("null")){
            moleculeDataFile = new File(dataFolder + fileName);
            // For backwards compatibility, see if we actually read in an absolute path
            try{
                FileReader fr = new FileReader(moleculeDataFile);
            } catch(FileNotFoundException fne){
                // Assume we've been given a absolute path
                moleculeDataFile = new File(fileName);
                // See if this one works
                try{
                    FileReader fr1 = new FileReader(moleculeDataFile);
                } catch(FileNotFoundException fne1){
                    System.out.println("Was not given a correct file location!");
                    fne1.printStackTrace(System.out);
                }
            }
        }
    }
    
    public void setBondAverageDataFile(String fileName){
        if(!fileName.equals("null")){
            bondDataFile = new File(dataFolder + fileName);
            try{
                FileReader fr = new FileReader(bondDataFile);
            } catch(FileNotFoundException fne){
                // Assume we've been given a absolute path
                bondDataFile = new File(fileName);
                // See if this one works
                try{
                    FileReader fr1 = new FileReader(bondDataFile);
                } catch(FileNotFoundException fne1){
                    System.out.println("Was not given a correct file location!");
                    fne1.printStackTrace(System.out);
                }
            }
        }
    }
    
    public void setStateAverageDataFile(String fileName){
        if(!fileName.equals("null")){
            stateDataFile = new File(dataFolder + fileName);
            try{
                FileReader fr = new FileReader(stateDataFile);
            } catch(FileNotFoundException fne){
                // Assume we've been given a absolute path
                stateDataFile = new File(fileName);
                // See if this one works
                try{
                    FileReader fr1 = new FileReader(stateDataFile);
                } catch(FileNotFoundException fne1){
                    System.out.println("Was not given a correct file location!");
                    fne1.printStackTrace(System.out);
                }
            }
        }
    }
    
    public void setRunningTimesFile(String fileName){
        runningTimeFile = new File(dataFolder + fileName);
        try{
            FileReader fr = new FileReader(runningTimeFile);
        } catch(FileNotFoundException fne){
            // Assume we've been given a absolute path
            runningTimeFile = new File(fileName);
            // See if this one works
            try{
                FileReader fr1 = new FileReader(runningTimeFile);
            } catch(FileNotFoundException fne1){
                System.out.println("Was not given a correct file location!");
                fne1.printStackTrace(System.out);
            }
        }
    }
    
    public void setRawDataFile(String key, String fileName){
        if(rawDataFiles == null){
            rawDataFiles = new HashMap<>(100);
        }
        File file = new File(dataFolder + fileName);
        try{
            FileReader fr = new FileReader(file);
        } catch(FileNotFoundException fne){
            // Assume we've been given a absolute path
            file = new File(fileName);
            // See if this one works
            try{
                FileReader fr1 = new FileReader(file);
            } catch(FileNotFoundException fne1){
                System.out.println("Was not given a correct file location!");
                fne1.printStackTrace(System.out);
            }
        }
        rawDataFiles.put(key, file);
    }
    
    public void setSiteFile(String key, String fileName){
        if(siteFiles == null){
            siteFiles = new HashMap<>(100);
        }
        File file = new File(dataFolder + fileName);
        try{
            FileReader fr = new FileReader(file);
        } catch(FileNotFoundException fne){
            // Assume we've been given a absolute path
            file = new File(fileName);
            // See if this one works
            try{
                FileReader fr1 = new FileReader(file);
            } catch(FileNotFoundException fne1){
                System.out.println("Was not given a correct file location!");
                fne1.printStackTrace(System.out);
            }
        }
        siteFiles.put(key, new File(dataFolder + fileName));
    }
    
    /* ************** GET MOLECULE NAMES, BOND NAMES, ETC. ***************/
    
    public ArrayList<String> getMoleculeNames(){
        return moleculeNames;
    }
    
    public ArrayList<String> getBondNames(){
        return bondNames;
    }
    
    public ArrayList<String> getStateNames(){
        return stateNames;
    }
    
    public ArrayList<String> getSiteNames(){
        return siteNames;
    }
    
    /**********************************************************************\
     *               COMPUTE THE TIME POINT AVERAGES                      *
     *  This method goes to each of the "Run" folders specified, looks    *
     *  for the file specified by the given name, gets the data for       *
     *  each run and computes the averages and standard deviations at     *
     *  each time point.                                                  *
     *                                                                    *
     *  @param fileName The name of the file we look for in each folder.  *
     *  @param startIndex The starting folder index.                      *
     *  @param finishIndex The final folder index.                        *
    \**********************************************************************/
    
    public void computeTimePointAverages(String fileName, 
                                            int startIndex, int finishIndex){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        int totalCount = finishIndex - startIndex + 1;
        
        // Open the first file to determine the total number of time points and columns
        File firstFile;
        if(fileName.equals(MOLECULE_DATA) || fileName.equals(BOND_DATA) || fileName.equals(STATE_DATA)){
            firstFile = new File(dataFolder + "Run" + startIndex + "/" + fileName);
        } else {
            firstFile = new File(dataFolder + "Run" + startIndex + "/" + sitePropertyFileName(fileName));
        }
        int times = 0;
        int columns = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(firstFile))){
            Scanner sc = new Scanner(br);
            // Get the total number of columns
            String [] firstLine = sc.nextLine().split(",");
            // System.out.println(firstLine[0] + ", :" + firstLine[1] + ":");
            
            // One column is titled "Time", and there is always a blank entry
            // after the final column.  It turns out that if there is ONLY a blank
            // entry after "Time", then the split method returns an array of 
            // length 2 consisting of {"Time", " "}.  Otherwise, it ignores the 
            // blank and just returns the column headers.
            if(firstLine[firstLine.length-1].equals(" ")){
                columns = firstLine.length - 2;
            } else {
                columns = firstLine.length - 1;
            }
            // Now count the remaining rows to determine the total time points
            while(sc.hasNextLine()){
                sc.nextLine();
                times++;
            }
            sc.close();
        } catch(IOException ioe){
            ioReadExceptionLogger(firstFile.getPath(), ioe);
        }
        
        // If there are no columns then just print a message and return. 
        // NO!  This was messing me up big time!  The rest of the program 
        // expects there to be an average data file, even if there is no data. 
        if(columns == 0){
            System.out.println("The file " + fileName + " did not have any data"
                    + " to operate on. (columns = 0)");
        }
        
        // Set up the various arrays
        String [] columnHeaders = new String[columns+1]; // +1 to add "Time"
        double [] time = new double[times];
        double [][] av = new double[times][columns];
        double [][] var = new double[times][columns];
        double [][] stdev = new double[times][columns];
        
        // Use the first file again to populate the time and columns arrays
        
        try(BufferedReader br = new BufferedReader(new FileReader(firstFile))){
            Scanner sc = new Scanner(br);
            sc.useDelimiter(",");
            
            for(int i=0;i<columnHeaders.length;i++){
                columnHeaders[i] = sc.next().trim();
            }
            
            for(int i=0;i<time.length;i++){
                sc.nextLine();
                time[i] = sc.nextDouble();
            }
            sc.close();
        } catch(IOException ioe){
        	ioReadExceptionLogger(firstFile.getPath(), ioe);
        }
        
        for(int i=0;i<av.length;i++){
            for(int j=0;j<av[0].length;j++){
                av[i][j] = 0;
                var[i][j] = 0;
                stdev[i][j] = 0;
            }
        }
        
        boolean success = true;
        
        
        // First compute the averages
        int c = startIndex;
        
        File file = null;
        BufferedReader br = null;
        Scanner sc = null;
        while(c <= finishIndex){
            
            try{
                if(fileName.equals(MOLECULE_DATA) || fileName.equals(BOND_DATA) || fileName.equals(STATE_DATA)){
                    file = new File(dataFolder + "Run" + c + "/" + fileName);
                } else {
                    file = new File(dataFolder + "Run" + c + "/" + sitePropertyFileName(fileName));
                }
                br = new BufferedReader(new FileReader(file));

                sc = new Scanner(br);
                sc.useDelimiter(",");
                sc.nextLine();
                
                for(int i=0;i<av.length;i++){
                    // Skip the time
                    sc.next();
                    for(int j=0;j<av[0].length;j++){
                        av[i][j] += sc.nextDouble();
                    }
                }
                
            } catch(FileNotFoundException ioe){
                success = false;
                ioReadExceptionLogger(file.getPath(), ioe);
            } finally {
                if(sc != null){
                    sc.close();
                }
                try{
                    if(br != null){
                        br.close();
                    }
                } catch(IOException ioe1){
                    ioExceptionLogger(ioe1);
                }
            }
            
            c++;
            
        }
        
        for(int i=0;i<av.length;i++){
            for(int j=0;j<av[0].length;j++){
                av[i][j] /= totalCount;
            }
        }
        
        // Now loop again to compute the variance
        
        c = startIndex;
        
        while(c <= finishIndex){
            
            try{
                if(fileName.equals(MOLECULE_DATA) || fileName.equals(BOND_DATA) || fileName.equals(STATE_DATA)){
                    file = new File(dataFolder + "Run" + c + "/" + fileName);
                } else {
                    file = new File(dataFolder + "Run" + c + "/" + sitePropertyFileName(fileName));
                }
                br = new BufferedReader(new FileReader(file));
                
                sc = new Scanner(br);
                sc.useDelimiter(",");
                sc.nextLine();
                
                for(int i=0;i<av.length;i++){
                    // Skip the time
                    sc.next();
                    for(int j=0;j<av[0].length;j++){
                        double v = sc.nextDouble();
                        var[i][j] += (v - av[i][j])*(v-av[i][j]);
                    }
                }
                
            } catch(FileNotFoundException ioe){
                success = false;
                ioReadExceptionLogger(file.getPath(), ioe);
            } finally {
                if(sc != null){
                    sc.close();
                }
                try{
                    if(br != null){
                        br.close();
                    }
                } catch(IOException ioe1){
                    ioExceptionLogger(ioe1);
                }
            }
            
            c++;
            
        }
        
        for(int i=0;i<var.length;i++){
            for(int j=0;j<var[0].length;j++){
                if(totalCount == 1){
                    var[i][j] = 0;
                } else {
                    var[i][j] /= (totalCount-1);
                }
                stdev[i][j] = Math.sqrt(var[i][j]);
            }
        }
        
        if(success){
            
            String averageFileName = averageFileName(fileName);
            if(averageFileName == null){
                String fName = sitePropertyFileName(fileName);
                String molAndSite = fName.substring(0,fName.lastIndexOf('_'));
                averageFileName = AVERAGE_SITE_PROPERTY_DATA + "_" + molAndSite;
            }
            
            String outputFileName = simulationName + "_" + averageFileName + "_" 
                                    + startIndex + "_" + finishIndex + ".csv";
            
            File averageFile = new File(dataFolder + "/" + outputFileName);
            
            try(PrintWriter p = new PrintWriter(new FileWriter(averageFile), true)){
                
                p.print("AVERAGES");
                for(int i=0;i<av[0].length+2;i++){
                    p.print(",");
                }
                
                p.println("STANDARD DEVIATIONS");
                
                for(int i=0;i<columnHeaders.length;i++){
                    p.print(columnHeaders[i] + ", ");
                }
                p.print(", ");
                // Skip "time" in the column headers
                for(int i=1;i<columnHeaders.length;i++){
                    p.print(columnHeaders[i] + ", ");
                }
                p.println();
                
                for(int i=0;i<time.length;i++){
                    p.print(time[i] + ", ");
                    for(int j=0;j<av[0].length;j++){
                        p.print(av[i][j] + ", ");
                    }
                    p.print(", ");
                    for(int j=0;j<av[0].length;j++){
                        p.print(stdev[i][j] + ", ");
                    }
                    p.println();
                }
                
            } catch(IOException ioe){
            	ioWriteExceptionLogger(averageFile.getPath(), ioe);
            }
            
            switch(fileName){
                case MOLECULE_DATA:
                    moleculeDataFile = averageFile;
                    break;
                case BOND_DATA:
                    bondDataFile = averageFile;
                    break;
                case STATE_DATA:
                    stateDataFile = averageFile;
                    break;
                default:
                    siteFiles.put(fileName, averageFile);
            }
        }    
        // </editor-fold>
    }
    
    /**********************************************************************\
     *            COLLECT ALL COUNTS FOR ONE SPECIES                      *
     * This method will go through all of the run folders looking for     *
     * data on a given species (or bond, etc) and will return a csv file  *
     * containing all of the data on that species from each run.          *
     * Be aware that the species name must match the column header. So    *
     * if you want all data on the total numbers of species A, you would  *
     * have to use "A_TOTAL" as the speciesName.  For states this is      *
     * even worse because they are output using the full molecule name,   *
     * type name, and state name.                                         *
     *                                                                    *
     *  @param speciesName The name of the species (or bond, etc)         *
     *  @param fileName The name of the file we access in each folder     *
     *  @param startIndex The starting folder index.                      *
     *  @param finishIndex The final folder index.                        *
     *                                                                    *
     *  @return Return the file created.                                  *
    \**********************************************************************/
    
    public File getAllData(String speciesName, String fileName, 
                                    int startIndex, int finishIndex){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        int totalCount = finishIndex - startIndex + 1;
        // Look at the first file to get the total number of time points
        // and the index of the species name
        // Open the first file to determine the total number of time points and columns
        File firstFile = new File(dataFolder + "Run" + startIndex + "/" + fileName);
        int times = 0;
        int columnIndex = -1;
        try(BufferedReader br = new BufferedReader(new FileReader(firstFile))){
            Scanner sc = new Scanner(br);
            // Get the column index
            Scanner firstLine = new Scanner(sc.nextLine());
            firstLine.useDelimiter(",");
            int i = 0;
            while(firstLine.hasNext()){
                if(speciesName.equals(firstLine.next().trim())){
                    columnIndex = i;
                    break;
                }
                i++;
            }
            // Now count the remaining rows to determine the total time points
            while(sc.hasNextLine()){
                sc.nextLine();
                times++;
            }
            firstLine.close();
            sc.close();
        } catch(IOException ioe){
            ioReadExceptionLogger(firstFile.getPath(), ioe);
        }
        
        // Look to see if we actually found the species name
        if(columnIndex == -1){
            System.out.println("Could not find data for " + speciesName + ".");
            return null;
        }
        
        // Set up the various arrays
        double [] time = new double[times];
        double [][] number = new double[times][totalCount];
        
        // Use the firstfile again to populate the times
        try(BufferedReader br = new BufferedReader(new FileReader(firstFile))){
            Scanner sc = new Scanner(br);
            sc.useDelimiter(",");
            for(int i=0;i<time.length;i++){
                sc.nextLine();
                time[i] = sc.nextDouble();
            }
            sc.close();
        } catch(IOException ioe){
        	ioReadExceptionLogger(firstFile.getPath(), ioe);
        }
        
        File file = null;
        FileReader fr = null;
        BufferedReader br = null;
        Scanner sc = null;
        int c = startIndex;
        while(c <= finishIndex){
            
            try{
            	file = new File(dataFolder + "Run" + c + "/" + fileName);
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                
                sc = new Scanner(br);
                sc.useDelimiter(",");
                for(int i=0;i<time.length;i++){
                    sc.nextLine();
                    // Get to the right column
                    for(int j=0;j<columnIndex;j++){
                        sc.next();
                    }
                    number[i][c-startIndex] = sc.nextDouble();
                }
                
            } catch(FileNotFoundException ioe){
            	ioReadExceptionLogger(file.getPath(), ioe);
            } finally {
                if(sc != null){
                    sc.close();
                }
                if(br != null){
                    try{
                        br.close();
                    } catch(IOException bioe){
                        ioExceptionLogger(bioe);
                    }
                }
                if(fr != null){
                    try{
                        fr.close();
                    } catch(IOException fioe){
                        ioExceptionLogger(fioe);
                    }
                }
            }
            
            c++;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(dataFolder).append(simulationName).append("_").append(ALL_DATA).append("_").append(startIndex);
        sb.append("_").append(finishIndex).append("_");  
        
        if(speciesName.contains(":")){
            String [] name = speciesName.split(":");
            if(name.length != 3){
                return null;
            } else {
                sb.append(name[0]).append("_");
                sb.append(name[1]).append("_");
                sb.append(name[2]);
            }
        } else {
            sb.append(speciesName);
        }
        sb.append(".csv");
        
        File allDataFile = new File(sb.toString());
        
        try(PrintWriter p = new PrintWriter(new FileWriter(allDataFile))){
            
            p.println(speciesName);
            p.print("Time, ");
            for(int i=0;i<totalCount;i++){
                p.print("Run" + i + ", ");
            }
            p.println();
            for(int i=0;i<time.length;i++){
                p.print(time[i] + ", ");
                for(int j=0;j<number[i].length;j++){
                    p.print(number[i][j] + ", ");
                }
                p.println();
            }
        } catch(IOException ioe){
            ioWriteExceptionLogger(allDataFile.getPath(), ioe);
        }
        
        rawDataFiles.put(speciesName, allDataFile);
        return allDataFile;
        // </editor-fold>
    }
    
    /**********************************************************************\
     *                MAKE EVERY POSSIBLE RAW DATA FILE                   *
     *  THIS IS VERY VERY INEFFICIENT NOW! I need to revisit this when    *
     *  I have time.  I should really just open each file once.           *
     *  @param startIndex The starting folder index.                      *
     *  @param finishIndex The final folder index.                        *
     \*********************************************************************/
    
    public void makeAllRawDataFiles(int startIndex, int finishIndex){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        for(String name : moleculeNames){
            getAllData(name, MOLECULE_DATA, startIndex, finishIndex);
        }
        for(String name : bondNames){
            getAllData(name, BOND_DATA, startIndex, finishIndex);
        }
        for(String name : stateNames){
            getAllData(name, STATE_DATA, startIndex, finishIndex);
        }
        // </editor-fold>
    }
    
    /********************************************************************\
     *              HISTOGRAMS FOR ONE SPECIES                          *
     *  This method will take in the file generated by the getAllData   *
     *  method and will create a histogram of the counts at each time   *
     *  point. We can specify the max, min, and bin size, if we want.   *
     *                                                                  *
     *  @param file The file we're reading.                             *
     *  @param min The minimum number to count. Zero if null.           *
     *  @param max The maximum number to count. If null, then           *
     *             determined by scanning the file.                     *
     *  @param binSize The size of the bins.  If null, then 1.          *
     *                                                                  * 
    \********************************************************************/
    
    public void createHistograms(File file, Integer min, Integer max, Integer binSize){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        // Only have to read one file. Rather than opening and closing it many
        // times, let's just make an arraylist with each of its lines, skipping
        // the first two lines because they just contain header information.
        ArrayList<String> lines = new ArrayList<>();
        String speciesName = "";
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
           Scanner sc = new Scanner(br);
           speciesName = sc.nextLine();
           if(speciesName.endsWith(",")){
               speciesName = speciesName.substring(0,speciesName.length()-1);
           }
           sc.nextLine();
           while(sc.hasNextLine()){
               lines.add(sc.nextLine());
           }
           sc.close();
        } catch(IOException ioe){
            ioExceptionLogger(ioe);
        }
        

        if(min == null){
            min = 0;
        }
        if(binSize == null){
            binSize = 1;
        }
        if(max == null){
            int maxValue = 0;
            for(String line : lines){
                Scanner sc = new Scanner(line);
                sc.useDelimiter(",");
                // Skip the time
                sc.next();
                while(sc.hasNext()){
                    String s = sc.next();
                    if(!s.equals(" ")){
                        int value = (int)Math.round(Double.parseDouble(s));
                        if(value > maxValue){
                            maxValue = value;
                        }
                    }
                }
                max = maxValue + 1;
                sc.close();
            }
        }
            
        int totalbins = (max-min)/binSize + 1;


        double [] time = new double[lines.size()];
        double [][] counts = new double[lines.size()][totalbins];
        for (double[] count : counts) {
            for (int j = 0; j<count.length; j++) {
                count[j] = 0;
            }
        }
        
        for(int i=0;i<lines.size();i++){
            String line = lines.get(i);
            Scanner sc = new Scanner(line);
            sc.useDelimiter(",");
            time[i] = sc.nextDouble();
            while(sc.hasNext()){
                String s = sc.next();
                if(!s.equals(" ")){
                    int value = 0;
                    try{
                        value = (int)Math.round(Double.parseDouble(s));
                    } catch(NumberFormatException nfe){
                        System.out.println("Tried to read " + s + " as an integer.");
                        System.out.println("lines[" + i + "] = " + line);

                    }
                    int binNumber = (value - min)/binSize;
                    counts[i][binNumber]++;
                }
            }
            sc.close();
        }
        
        // Build our current fileName from the given filename
        String filename = file.getAbsolutePath();
        // Strip off ".csv"
        filename = filename.substring(0,filename.length()-4);
        filename = filename + "_HISTOGRAM.csv";
        // Now write the data to a file
        File outFile = new File(filename);
        try(PrintWriter p = new PrintWriter(new FileWriter(outFile))){
            p.println(speciesName);
            p.println("Histogram Parameters: min = " + min + " max = " + max 
                                                    + " binSize = " + binSize);
            p.println(", Times");
            p.print("Number,");
            for(int i=0;i<time.length;i++){
                p.print(time[i] + ", ");
            }
            p.println();
            // Note the reversed indices!
            for(int i=0;i<counts[0].length;i++){
                int binValue = min + i*binSize;
                p.print(binValue + ", ");
                for(int j=0;j<counts.length;j++){
                    p.print(counts[j][i] + ", ");
                }
                p.println();
            }
        } catch(IOException ioe){
            ioExceptionLogger(ioe);
        }
        // </editor-fold>
    }
    
    /************************************************************************\
     *          FILE FOR SITE PROPERTY DATA                                 *
     *  The number property data is written to one large file containing    *
     *  information on all of the siteNumbers of interest. This file is not *
     *  in a suitable format for the computeTimePointAverages function,     *
     *  so this method will create suitable files for every site.           *
     *                                                                      *                                                
     *  @param runNumber The runNumber we're looking at                     *
    \************************************************************************/
    
    private void formatSitePropertyData(int runNumber){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        File file = new File(dataFolder + "Run" + runNumber + "/" + SITE_PROPERTY_DATA);
        FileReader fr = null;
        BufferedReader br = null;
        Scanner sc = null;

        ArrayList<String> sites = siteNames;
        ArrayList<ArrayList<String>> allData = new ArrayList<>();
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // Look for a line that begin with the molecule name and number index of interest
            while(sc.hasNextLine()){
                Scanner lineScanner = new Scanner(sc.nextLine());
                lineScanner.useDelimiter(",");
                if(lineScanner.hasNext() && lineScanner.next().equals("Molecule")){
                    ArrayList<String> siteData = new ArrayList<>();
                    while(sc.hasNextLine()){
                        String nextLine = sc.nextLine();
                        // break if we've found the next number index line
                        if(nextLine.isEmpty()){
                            break;
                        } else {
                            siteData.add(nextLine);
                        }
                    }
                    allData.add(siteData);
                }
                lineScanner.close();
            }
        } catch(FileNotFoundException fne){
            ioExceptionLogger(fne);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        
        // Now write the data to new files
        File [] fileArray = new File[sites.size()];
        PrintWriter p = null;
        for(int i=0;i<fileArray.length;i++){
            String siteName = sites.get(i);
            File rFile = new File(dataFolder + "Run" + runNumber + "/" 
                + sitePropertyFileName(siteName));
            try {
                p = new PrintWriter(new FileWriter(rFile));
                ArrayList<String> sData = allData.get(i);
                for(String line : sData){
                    p.println(line);
                }
            } catch(IOException ioe){
                ioExceptionLogger(ioe);
            } finally {
                if(p != null){
                    p.close();
                } 
            }
        }
        // </editor-fold>
    }
    
    /*********************************************************************\
     *        CREATE FORMATTED SITE PROPERTY FILES IN EVERY FOLDER       *
     * Goes through each Run folder to create formatted number property  *
     * data.                                                             *
     *                                                                   *
     *  @param startIndex Index of first folder.                         *
     *  @param finishIndex Index of last folder.                         *
    \*********************************************************************/
    
    private void formatAllSitePropertyData(int startIndex, int finishIndex){
        for(int i=startIndex;i<=finishIndex;i++){
            formatSitePropertyData(i);
        }
    }
    
    /**********************************************************************\
     *                CREATE FILENAME FOR SITE PROPERTY DATA              *
     * @param moleculeName The name of the molecule.                      *
     * @param siteIndex The number index we're interested in.             *                                                             *
     * @return The name of the file we look for when computing time point *
     *         averages and other data.                                   *
    \**********************************************************************/
    
    private String sitePropertyFileName(String siteName){
        Scanner sc = new Scanner(siteName);
        String moleculeName = sc.next();
        sc.next();
        String siteIndex = sc.next();
        sc.close();
        return moleculeName + "_Site_" + siteIndex + "_SitePropData.csv";
    }
    
    /*********************************************************************\
     *       COMPUTE AVERAGE SITE DATA FOR ALL SITES OF INTEREST         *
     * @param startIndex Index of first folder.                          *
     * @param finishIndex Index of last folder.                          *
    \*********************************************************************/
    
    public void computeAllSiteAverages(int startIndex, int finishIndex){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        this.formatAllSitePropertyData(startIndex, finishIndex);
        for(String site : siteNames){
            this.computeTimePointAverages(site, startIndex, finishIndex);
        }
        // </editor-fold>
    }
    
    /*********************************************************************\
     *           COMPUTE TIME POINT AVERAGES FOR ALL DATA                *
     * @param startIndex Index of first folder.                          *
     * @param finishIndex Index of last folder.                          *
    \*********************************************************************/
    
    public void computeAllTimePointAverages(int startIndex, int finishIndex){
        this.computeAllSiteAverages(startIndex, finishIndex);
        this.computeTimePointAverages(BOND_DATA, startIndex, finishIndex);
        this.computeTimePointAverages(MOLECULE_DATA, startIndex, finishIndex);
        this.computeTimePointAverages(STATE_DATA, startIndex, finishIndex);
    }
    
    /**********************************************************************\
     *                  SIMULATION RUNNING TIME DATA                      *
     * Identical simulations will take different amounts of time to run   *
     * because the stochastic systems could evolve very differently       *
     * (for example, one system might need to check collisions much more  *
     * frequently because molecules just happened to get bunched up) and  *
     * because they run on different cores on different chips.  This      *
     * method will give me the full list of running times (in seconds)    *
     * and compute the average and standard deviations.                   *
     *                                                                    *
     *  @param startIndex The index of the first run.                     *
     *  @param endIndex The index of the final run.                       *
    \**********************************************************************/
    
    public void getRunningTimes(int startIndex, int endIndex){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        int totalCount = endIndex - startIndex + 1;
        double [] time = new double[totalCount];
        double av = 0; 
        double var = 0;
        double stdev = 0;
        
        File file = null;
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        for(int i=startIndex;i<=endIndex;i++){
            int index = i-startIndex;
            time[index] = 0;
            try{
            	file = new File(dataFolder + "Run" + i + "/" + RUNNING_TIME);
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                sc = new Scanner(br);
                // Skip the words "Running Time:"
                sc.next();
                sc.next();
                // Rest of the line comes in pairs as    number unit
                // Read in both pairs and determine what to add to the time
                while(sc.hasNext()){
                    double value = sc.nextDouble();
                    String unit = sc.next();
                    switch(unit){
                        case DAYS:
                            time[index] += 24*3600*value;
                            break;
                        case DAY:
                            time[index] += 24*3600;
                            break;
                        case HOURS:
                            time[index] += 3600*value;
                            break;
                        case HOUR:
                            time[index] += 3600;
                            break;
                        case MINUTES:
                            time[index] += 60*value;
                            break;
                        case SECONDS:
                            time[index] += value;
                            break;
                        default:
                            System.out.println("DataProcessor.getRunningTimes()"
                                    + " encountered unexpected unit: " + unit);
                    }
                }
                
            } catch(FileNotFoundException ioe){
                ioReadExceptionLogger(file.getPath(), ioe);
            } finally {
                if(sc != null){
                    sc.close();
                }
                if(br != null){
                    try{
                        br.close();
                    } catch(IOException brioe){
                        ioExceptionLogger(brioe);
                    }
                }
                
                if(fr != null){
                    try{
                        fr.close();
                    } catch(IOException frioe){
                        ioExceptionLogger(frioe);
                    }
                }
            }
            
            av += time[index];
        }
        
        av /= totalCount;
        
        // Compute the standard deviation
        for(int i=0;i<totalCount;i++){
            var += (time[i] - av)*(time[i]-av);
        }
        
        if(totalCount > 1){
            var /= (totalCount-1);
        } else {
            var = 0;
        }
        
        stdev = Math.sqrt(var);
        runningTimeFile = new File(dataFolder + "RunningTimes.csv");
        try(PrintWriter p = new PrintWriter(new FileWriter(runningTimeFile))){
            p.println("Index, Times (s), Average (s), St Dev (s)");
            for(int i=startIndex;i<=endIndex;i++){
                int j = i-startIndex;
                if(j==0){
                    p.println(i + ", " + time[j] + ", " + av + ", " + stdev);
                } else {
                    p.println(i + ", " + time[j]);
                }
            }
        } catch (IOException ioe){
            ioWriteExceptionLogger(runningTimeFile.getPath(), ioe);
        }
        
        // </editor-fold>
    }
    
    /***********************************************************************\
     *                     GET ALL STATE NAMES                             *
     *  I want to integrate the data processor with the front end.  The    *
     *  user should just be able to click on a dropdown box to get this    *
     *  data, or at least have it generated automatically.                 *
     *                                                                     *
    \***********************************************************************/
 
    private void saveStateNames(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        stateNames.clear();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(dataFolder + "Run" + 0 + "/" + STATE_DATA);
            br = new BufferedReader(fr);
            sc = new Scanner(br.readLine());
            sc.useDelimiter(",");
            sc.next();
            while(sc.hasNext()){
                String name = sc.next();
                if(name.length() > 1){
                    stateNames.add(name.trim());
                }
            }
        } catch(IOException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        // </editor-fold>
    }
    
    /***********************************************************************\
     *                    GET ALL BOND NAMES                               *
    \***********************************************************************/
    
    private void saveBondNames(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        bondNames.clear();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(dataFolder + "Run" + 0 + "/" + BOND_DATA);
            br = new BufferedReader(fr);
            sc = new Scanner(br.readLine());
            sc.useDelimiter(",");
            sc.next();
            while(sc.hasNext()){
                String name = sc.next();
                if(name.length() > 1){
                    bondNames.add(name.trim());
                }
            }
        } catch(IOException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        // </editor-fold>
    }
    
    /**********************************************************************\
     *                   GET ALL MOLECULE NAMES                           *
    \**********************************************************************/

    private void saveMoleculeNames(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        moleculeNames.clear();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(dataFolder + "Run" + 0 + "/" + MOLECULE_DATA);
            br = new BufferedReader(fr);
            sc = new Scanner(br.readLine());
            sc.useDelimiter(",");
            sc.next();
            while(sc.hasNext()){
                String name = sc.next();
                if(name.length() > 1){
                    moleculeNames.add(name.trim());
                }
            }
        } catch(IOException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        // </editor-fold>
    }
    
    /**********************************************************************\
     *                  DETERMINE ALL SITES WITH DATA                     *
     * An array list of pairs of strings, the first representing the      *
     * molecule name, the second the site with data.  It is an integer,   * 
     * but I'll worry about converting it later.                          *
    \**********************************************************************/
    
    private void saveSitesOfInterest(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        siteNames.clear();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(dataFolder + "Run" + 0 + "/" + SITE_PROPERTY_DATA);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            StringBuilder sb;
            // Look for a line that begin with the molecule name and number index of interest
            while(sc.hasNextLine()){
                Scanner lineScanner = new Scanner(sc.nextLine());
                lineScanner.useDelimiter(",");
                if(lineScanner.hasNext() && lineScanner.next().equals("Molecule")){
                    sb = new StringBuilder();
                    sb.append(lineScanner.next().trim());
                    // Skip "Site Index"
                    lineScanner.next();
                    sb.append(" Site ");
                    sb.append(lineScanner.next().trim());
                    siteNames.add(sb.toString());
                }
                lineScanner.close();
            }
        } catch(FileNotFoundException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        // </editor-fold>
    }
    
    /**********************************************************************\
     *   BUILD HASHAMP BETWEEN SITE IDS AND MOLECULE NAMES/SITETYPES      *
     *   For now, this feature only makes sense if there is no creation   *
     *   or destruction of molecules, which ensures that the same ids are *
     *   linked to the same sites in all runs.  If there are creation or  *
     *   decay reactions, then we'll need a separate map for each run .   *
    \**********************************************************************/
    private void buildSiteIDHashMap(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<String> fileLines = new ArrayList<>();
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(dataFolder + "Run" + 0 + "/" + SITE_IDS);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            while(sc.hasNextLine()){
                fileLines.add(sc.nextLine());
            }
        } catch(FileNotFoundException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        
        siteIDMap = new HashMap<>(5*fileLines.size());
        for(String line : fileLines){
            sc = new Scanner(line);
            sc.useDelimiter(",");
            siteIDMap.put(sc.nextInt(), sc.next());
            sc.close();
        }
        // </editor-fold>
    }
    
    /**********************************************************************\
     *     COMPUTE DISTANCES BETWEEN EACH SITE AND EVERY OTHER SITE       *
     *  Compute distance between each pair of sites at each time point.   *
     *  This method does this for a single run.                           *
     *  @param run The run number.                                        *
    \**********************************************************************/
    
    public void computeDistances(int run){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // First read in all of the distance data
        ArrayList<Integer> siteIDs = new ArrayList<>();
        ArrayList<Double> times = new ArrayList<>();
        ArrayList<ArrayList<Double>> x = new ArrayList<>();
        ArrayList<ArrayList<Double>> y = new ArrayList<>();
        ArrayList<ArrayList<Double>> z = new ArrayList<>();
        
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(dataFolder + "Run" + run + "/" + LOCATION_DATA);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // Read in the first line
            Scanner firstLine = new Scanner(sc.nextLine());
            firstLine.useDelimiter(",");
            // Skip "time"
            firstLine.next();
            while(firstLine.hasNext()){
                String next = firstLine.next().trim();
                if(next.length() > 0){
                    siteIDs.add(Integer.parseInt(next));
                    x.add(new ArrayList<Double>());
                    y.add(new ArrayList<Double>());
                    z.add(new ArrayList<Double>());
                }
            }
            firstLine.close();
            // Now read through the rest of the lines
            while(sc.hasNextLine()){
                Scanner line = new Scanner(sc.nextLine());
                line.useDelimiter(",");
                times.add(line.nextDouble());
                for(int i=0;i<siteIDs.size();i++){
                    String vec = line.next().trim();
                    if(vec.length()>0){
                        Scanner xyz = new Scanner(vec);
                        x.get(i).add(xyz.nextDouble());
                        y.get(i).add(xyz.nextDouble());
                        z.get(i).add(xyz.nextDouble());
                        xyz.close();
                    }
                }
                line.close();
            }
        } catch(FileNotFoundException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
        
        try(PrintWriter p = new PrintWriter(new FileWriter(dataFolder + "Run" + run + "/" + DISTANCE_DATA + ".csv"), true)){
            // Print the top line
            p.print("Time,");
            for(int i=0;i<siteIDs.size();i++){
                for(int j=i+1;j<siteIDs.size();j++){
                    p.print(siteIDs.get(i) + " : " + siteIDs.get(j) + ",");
                }
            }
            p.println();
            for(int i=0;i<times.size();i++){
                p.print(times.get(i) + ",");
                for(int j=0;j<siteIDs.size();j++){
                    for(int k=j+1;k<siteIDs.size();k++){
                        double dx = x.get(j).get(i) - x.get(k).get(i);
                        double dy = y.get(j).get(i) - y.get(k).get(i);
                        double dz = z.get(j).get(i) - z.get(k).get(i);
                        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
                        p.print(distance + ",");
                    }
                }
                p.println();
            }
        } catch(IOException ioe){
            ioExceptionLogger(ioe);
        }
        // </editor-fold>
    }
    
    public void computeDistances(int start, int end){
        for(int i=start;i<=end;i++){
            computeDistances(i);
        }
    }
    
    /**********************************************************************\
     *                  COMPUTE HEAT MAP                                  *
     *   Reads in the distance data file for a given run.                 *
     *                                                                    *
     *   @param run The run number.                                       *
     *   @param cutoff The distance (in nm) between sites which should be *
     *                 considered a contact event.                        *
     *   @param equilTime We give the system some time to equilibrate.    *
     *                    Don't count data before this time.              *
    \**********************************************************************/
    
    public void generateHeatMap(int run, double cutoff, double equilTime){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Read in all of the lines
        ArrayList<String> pairs = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();
        HashMap<String, Integer> countsMap = new HashMap<>(1000);
        ArrayList<String> siteIDs = new ArrayList<>();
        
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
         try{
            fr = new FileReader(dataFolder + "Run" + run + "/" + DISTANCE_DATA + ".csv");
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // Read in the first line
            Scanner firstLine = new Scanner(sc.nextLine());
            firstLine.useDelimiter(",");
            // Skip "Time"
            firstLine.next();
            while(firstLine.hasNext()){
                String next = firstLine.next().trim();
                if(next.length() > 0){
                    pairs.add(next);
                    counts.add(0);
                }
            }
            firstLine.close();
            int totalObservations = 0;
            // Now read in the rest of the lines
            while(sc.hasNextLine()){
                totalObservations++;
                Scanner line = new Scanner(sc.nextLine());
                line.useDelimiter(",");
                // Get the time, but don't store it
                double time = Double.parseDouble(line.next().trim());
                for(int i=0;i<pairs.size();i++){
                    String next = line.next().trim();
                    if(next.length() > 0){
                        // Only add if the distance is shorter than the cutoff
                        double d = Double.parseDouble(next);
                        if(d < cutoff && time > equilTime){
                            Integer number = counts.get(i);
                            number++;
                            counts.set(i, number);
                        }
                    }
                }
                line.close();
            }
            
            for(int i=0;i<pairs.size();i++){
                String pair = pairs.get(i);
                String [] index = pair.split(":");
                
                index[0] = index[0].trim();
                boolean hasFirst = false;
                for(String string : siteIDs){
                    if(string.equals(index[0])){
                        hasFirst = true;
                        break;
                    }
                }
                if(!hasFirst){
                    siteIDs.add(index[0]);
                }
                
                index[1] = index[1].trim();
                boolean hasSecond = false;
                for(String string : siteIDs){
                    if(string.equals(index[1])){
                        hasSecond = true;
                        break;
                    }
                }
                if(!hasSecond){
                    siteIDs.add(index[1]);
                }
                
                String key1 = index[0] + index[1];
                String key2 = index[1] + index[0];
                countsMap.put(key1, counts.get(i));
                countsMap.put(key2, counts.get(i));
            }
            
            for(String id : siteIDs){
                countsMap.put(id+id, totalObservations);
            }
            
        } catch(FileNotFoundException ioe){
            ioExceptionLogger(ioe);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    ioExceptionLogger(bioe);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    ioExceptionLogger(fioe);
                }
            }
        }
         
        try(PrintWriter p = new PrintWriter(new FileWriter(dataFolder + "Run" + run + "/" + HEAT_MAP_DATA + ".csv"))){
            // Write the top line
            // Empty space
            p.print(",");
            for(int i=0;i<siteIDs.size();i++){
                p.print(siteIDs.get(i));
                if(i != siteIDs.size()-1){
                    p.print(",");
                }
            }
            p.println();
            
            for(int i=0;i<siteIDs.size();i++){
                String id1 = siteIDs.get(i);
                p.print(id1 + ",");
                for(int j=0;j<siteIDs.size();j++){
                    String id2 = siteIDs.get(j);
                    p.print(countsMap.get(id1+id2));
                    if(j!= siteIDs.size()-1){
                        p.print(",");
                    }
                }
                p.println();
            }
        }catch(IOException ioe){
            ioExceptionLogger(ioe);
        }
        // </editor-fold>
    }
    
    /***********************************************************************\
     *                     IO EXCEPTION LOGGER                             *
     * @param ioe The IO exception we're logging.                          *
    \***********************************************************************/
    
    private void ioExceptionLogger(IOException ioe){
        System.out.println("Message: " + ioe.getMessage());
        ioe.printStackTrace(System.out);
    }
    
    private void ioReadExceptionLogger(String location, IOException ioe) {
    	logger.logFileReadParseException(location, ioe);
    }
    
    private void ioWriteExceptionLogger(String location, IOException ioe) {
    	logger.logFileWriteException(ioe);
    }
    
    private void displayLog() {
    	logger.displayLogGUI();
        logger.writeLogFile(dataFolder);
    }
    
    public static void main(String [] args){
        
        DataProcessor dp = new DataProcessor(C_LAPTOP + "/PolymerTest_40_SIMULATIONS", "Weak Interaction_SIM");
//        dp.grabNames();
//        dp.computeAllTimePointAverages(0, 0);
//        dp.getRunningTimes(0, 0);
//        dp.makeAllRawDataFiles(0, 0);
        dp.computeDistances(0);
        dp.generateHeatMap(0, 6.5, 0.02);
        
//        DataProcessor dp = new DataProcessor(C_LAPTOP + "/PolymerTest_SIMULATIONS", "Simulation0_SIM");
//        dp.computeDistances(0);
//        dp.generateHeatMap(0, 6.5, 0.02);
        
//        String name = "AlloKin_NoSub_NoAct_LongBond_dt";
//        String [] times = {"01", "02", "05", "10"};
//        DataProcessor [] dp = new DataProcessor[times.length];
//        for(int i=0;i<dp.length;i++){
//            dp[i] = new DataProcessor(Z_DRIVE, name + times[i]);
//            // dp[i].computeAllTimePointAverages(0, 99);
//            dp[i].getRunningTimes(0, 99);
//            System.out.println("Finished " + i + ".");
//        }
        
        
//        DataProcessor processor = new DataProcessor(Z_DRIVE, "AllostericKinase");
//        processor.computeAllTimePointAverages(0, 99);
        
        
//        ArrayList<String> allStates = processor.getStateNames();
//        ArrayList<String> allBonds = processor.getBondNames();
//        System.out.println(allStates);
//        System.out.println(allBonds);
//        System.out.println(processor.getMoleculeNames());
//        System.out.println(processor.getSitesOfInterest());
//        processor.computeTimePointAverages(BOND_DATA, 0, 199);
//        System.out.println("Bonds completed.");
//        processor.computeTimePointAverages(STATE_DATA, 0, 199);
//        System.out.println("States completed.");
        // processor.formatAllSitePropertyData(0, 199, "Substrate", 0);
        // processor.computeTimePointAverages(processor.sitePropertyFileName("Substrate", 0), 0, 199);
//        processor.getRunningTimes(0, 199);
        
        /***************** LOOPER RUNS **********************************/
        /*
        String [] siteNumbers = {"01", "02", "05", "10", "20"};
        for (String number : siteNumbers) {
            DataProcessor processor = new DataProcessor(Z_DRIVE, "Looper_Sites" + number);
            processor.computeTimePointAverages(BOND_DATA, 0, 49);
            // processor.computeTimePointAverages(MOLECULE_DATA, 0, 49);
            processor.formatAllSitePropertyData(0, 49, "Looper", 0);
            processor.formatAllSitePropertyData(0, 49, "Looper", 1);
            processor.computeTimePointAverages(processor.sitePropertyFileName("Looper", 0), 0, 49);
            processor.computeTimePointAverages(processor.sitePropertyFileName("Looper", 1), 0, 49);
            processor.getRunningTimes(0, 49);
            System.out.println("Finished " + number + ".");
        }
        */
        /* ************ VARY BOND LENGTH TESTS ***************************/
        // int [] L = {1,2,3,4,5,10,20,50};
        // int [] L = {100, 200, 500};
        // int [] L = {5, 10, 20, 50, 100, 200, 500, 1000};
//        DataProcessor [] processor = new DataProcessor[L.length];
//        File [] file = new File[L.length];
//        for(int i=0;i<L.length;i++){
//            processor[i] = new DataProcessor(Z_DRIVE, "BindingReactionRev_TwoSpecies_3ns_L_" + L[i] + "A");
//            processor[i].computeTimePointAverages(MOLECULE_DATA, 0, 49);
//            file[i] = processor[i].getAllData("FREE B", MOLECULE_DATA, 0, 49);
//            processor[i].createHistograms(file[i], 0, 61, 1);
//            System.out.println("Finished " + i);
//        }
        /* ********* TRANSITION REACTIONS TESTS **************************/
//        int [] B = {30, 60, 150};
//        DataProcessor [] pnone = new DataProcessor[B.length];
//        DataProcessor [] pfree = new DataProcessor[B.length];
//        DataProcessor [] pbound = new DataProcessor[B.length];
//        File [] fnone = new File[B.length];
//        File [] ffree = new File[B.length];
//        File [] fbound = new File[B.length];
//        
//        for(int i=0;i<B.length;i++){
//            pnone[i] = new DataProcessor(Z_DRIVE, "TransitionReactionNoCondition_B" + B[i]);
//            pnone[i].computeTimePointAverages(STATE_DATA, 0, 49);
//            fnone[i] = pnone[i].getAllData("FREE S : S : State1", STATE_DATA, 0, 49);
//            pnone[i].createHistograms(fnone[i], 0, 60, 1);
//            
//            pfree[i] = new DataProcessor(Z_DRIVE, "TransitionReactionFreeCondition_B" + B[i]);
//            pfree[i].computeTimePointAverages(STATE_DATA, 0, 49);
//            ffree[i] = pfree[i].getAllData("FREE S : S : State1", STATE_DATA, 0, 49);
//            pfree[i].createHistograms(ffree[i], 0, 60, 1);
//            
//            pbound[i] = new DataProcessor(Z_DRIVE, "TransitionReactionBoundCondition_B" + B[i]);
//            pbound[i].computeTimePointAverages(STATE_DATA, 0, 49);
//            fbound[i] = pbound[i].getAllData("FREE S : S : State1", STATE_DATA, 0, 49);
//            pbound[i].createHistograms(fbound[i], 0, 60, 1);
//            
//            System.out.println("Finished B = " + B[i]);
//        }
        /* **************** BINDING REACTION TESTS ***************************/
//        DataProcessor processor = new DataProcessor(Z_DRIVE, "BindingReaction_SingleSpecies_2ns_L_" + 5 + "A");
//        processor.computeTimePointAverages(MOLECULE_DATA, 0, 49);
//        
//        File file = processor.getAllData("FREE A", MOLECULE_DATA, 0, 49);
//        processor.createHistograms(file, 0, 151, 1);
//        processor.getRunningTimes(0, 49);
//        processor.computeTimePointAverages(MOLECULE_DATA, 0, 999);
//        System.out.println("Computed time point averages.");
//        for(int i=0;i<10;i++){
//            File fileA = processor.getAllData("TOTAL A", MOLECULE_DATA, 100*i, 100*i+99);
//            processor.createHistograms(fileA, 0, 101, 1);
//            File fileB = processor.getAllData("TOTAL B", MOLECULE_DATA, 100*i, 100*i+99);
//            processor.createHistograms(fileB, 0, 180, 1);
//            System.out.println("Finished " + (i+1) + " of " + 10 + ".");
//        }
    }
    
}
