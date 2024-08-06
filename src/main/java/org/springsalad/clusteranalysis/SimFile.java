package org.springsalad.clusteranalysis;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class SimFile {

    public static void main(String[] args) throws IOException{
        //tests
        String fileName = "C:\\Users\\imt_w\\Documents\\SpringSalad\\Clustering_tutorial_01\\Clustering_tutorial_01_SIMULATIONS\\Simulation0_SIM_FOLDER\\Simulation0_SIM.txt";
        SimFile simFile = new SimFile(fileName);
        System.out.println(simFile.getSimFolder());
        System.out.println(simFile.getOutFolder("Cluster_stat"));
        System.out.println(Arrays.toString(simFile.getTimeStats()));
        System.out.println(simFile.getMolecules());
        System.out.println(simFile.getNumRuns());
    }

    Path simFilePath;
    private double[] times;
    private LinkedHashMap<String,Integer> molTypeMap;
    private int numRuns;

    public SimFile(String txtfile) throws IOException{
        simFilePath = Paths.get(txtfile);
        loadDataFromFile();
    }

    private void loadDataFromFile() throws IOException{
        // read file
        Scanner scanner = null;
        scanner = new Scanner(simFilePath);

        String line;
        while (scanner.hasNextLine()){
            line = scanner.nextLine();
            if (Pattern.compile("\\*+ TIME INFORMATION \\*+").matcher(line).find()){
                gatherTimeStats(scanner);
            }
            else if (Pattern.compile("\\*+ MOLECULES \\*+").matcher(line).find()){
                gatherMolecules(scanner);
            }
            else if (Pattern.compile("\\*+ SIMULATION STATE \\*+").matcher(line).find()){
                gatherRuns(scanner);
            }
        }
        scanner.close();
    }

    private void gatherTimeStats(Scanner fileSectionScanner){
        times = new double[5]; //total time, dt, dt_spring, dt_dtat, dt_image
        int fillCounter = 0;
        while (fillCounter < 5){
            Scanner lineScanner = new Scanner(fileSectionScanner.nextLine()).useDelimiter("\\s*:\\s*");
            String label = lineScanner.next();
            switch (label){
                case "Total time":
                    times[0] = lineScanner.nextDouble();
                    break;
                case "dt":
                    times[1] = lineScanner.nextDouble();
                    break;
                case "dt_spring":
                    times[2] = lineScanner.nextDouble();
                    break;
                case "dt_data":
                    times[3] = lineScanner.nextDouble();
                    break;
                case "dt_image":
                    times[4] = lineScanner.nextDouble();
                    break;
            }
            fillCounter++;
            lineScanner.close();
        }
    }

    private void gatherMolecules(Scanner fileSectionScanner){
        molTypeMap = new LinkedHashMap<>();
        while (fileSectionScanner.hasNextLine()){
            String line = fileSectionScanner.nextLine();
            if (line.startsWith("*")) break;
            if (line.contains("MOLECULE:")){
                Scanner lineScanner = new Scanner(line);
                lineScanner.next();
                String molName = lineScanner.next();
                molName = molName.substring(1,molName.length()-1);
                int count = 0;  //should we exclude molecules with 0 count?
                while (lineScanner.hasNext()){
                    if (lineScanner.next().equals("Number")){
                        count = lineScanner.nextInt();
                        break;
                    }
                }
                molTypeMap.put(molName, count);
                lineScanner.close();
            }
        }
    }

    private void gatherRuns(Scanner fileSectionScanner){
        while (fileSectionScanner.hasNextLine()){
            String line = fileSectionScanner.nextLine();
            Matcher matcher = Pattern.compile("\\bRuns:\\s*(\\d+)").matcher(line);
            if (matcher.find()){
                numRuns = Integer.parseInt(matcher.group(1));
                break;
            }
        }
    }


    public Path getSimFolder(){
        String simFilePathStr = simFilePath.toString();
        return Paths.get(simFilePathStr.substring(0,simFilePathStr.length()-4) + "_FOLDER");
    }

    public Path getOutFolder(String statName) throws IOException {
        Path outpath =  Paths.get(getSimFolder().toString(), "data", statName);
        Files.createDirectories(outpath);
        return outpath;
    }


    public LinkedHashMap<String,Integer> getMolecules(){
        return molTypeMap;
    }

    public double[] getTimeStats(){
        return times;
    }

    public int getNumRuns(){
        return numRuns;
    }
}
