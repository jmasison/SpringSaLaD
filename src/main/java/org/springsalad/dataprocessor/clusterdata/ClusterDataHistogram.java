/**
 * For other quantities, like the number of bound molecule A, the histogram
 * was a plot of the number of simulations with # A molecules at time t, as a function 
 * of the number of A molecules.  For cluster data I want the histogram 
 * to plot the number of clusters of size X at time t, as a function of size X, averaged
 * over all of the simulations. Thus, I can't reuse the old histogram class. 
 * Fortunately it should be pretty easy to parse this data from the 
 * cluster data file. 
 */
package org.springsalad.dataprocessor.clusterdata;

@Deprecated
public class ClusterDataHistogram {
    
    private final ClusterData clusterData;
    
    /* **************** HISTOGRAM PARAMETERS **************************/
    private int minimum;
    private int maximum;
    private final int actualMinimum; // WILL ALWAYS BE EQUAL TO 1.
    private int actualMaximum; // Over all runs and all times
    private int binSize;
    private int totalBins;
    
    private boolean autoMaximum = true;
    private boolean autoMinimum = true;
    private boolean autoBinSize = true;
    
    /* ****************   HISTOGRAM RESULTS ***************************/
    // First index is the time index, second index corresponds to the bin number
    private double [][] averageSizes;
    private double [][] stDevSizes;
    
    /* ****************  ARRAYLIST OF THE TIMES ***********************/
    private final double [] times;
    
    public ClusterDataHistogram(ClusterData clusterData){
        this.clusterData = clusterData;
        times = clusterData.getTimes();
        actualMinimum = 1;
        minimum = actualMinimum;
        binSize = 1;
        determineActualMaximum();
        maximum = actualMaximum;
    }
    
    /* ******** AUTOMATICALLY DETERMINE THE MAXIMUM NUMBER **************/
    
    private void determineActualMaximum(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        int maxValue = 0;
        int minValue = 1000000;
        for(SingleRun singleRun : clusterData.getSingleRuns()){
            for(ClusterSnapShot css : singleRun.getClusterSnapShots()){
                for(Cluster cluster : css.getClusters()){
                    if(cluster.getTotalMolecules() > maxValue){
                        maxValue = cluster.getTotalMolecules();
                    }
                }
            }
        }
        actualMaximum = maxValue;
        // </editor-fold>
    }
    
    /* *************  CREATE THE HISTOGRAM ARRAYS ***********************/
    // Make this public because we'll construct new histograms each time
    // we change the bin size or max or min
    public void constructHistograms(){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        totalBins = (maximum-minimum)/binSize + 1;
        System.out.println("total bins " + totalBins);
        averageSizes = new double[times.length][totalBins];
        double [][] av2Sizes = new double[times.length][totalBins];
        stDevSizes = new double[times.length][totalBins];
        for(int i=0;i<times.length;i++){
            for(int j=0;j<totalBins;j++){
                averageSizes[i][j] = 0;
                av2Sizes[i][j] = 0;
                stDevSizes[i][j] = 0;
            }
        }
        
        // Loop over time points
        for(int i=0;i<times.length;i++){
            for(SingleRun singleRun : clusterData.getSingleRuns()){
                // Get the correct snapshot
                ClusterSnapShot css = singleRun.getClusterSnapShots().get(i);
                // As a sanity check, we'll make sure the times agree
                if(Math.abs(times[i] - css.getTime()) > 1e-5){
                    System.out.println("TIMES DO NOT AGREE!");
                    System.out.println("times[" + i + "] = " + times[i] + ", css.getTime() = " + css.getTime());
                    System.exit(1);
                }
                
                double [] totals = new double[totalBins];
                for(int j=0;j<totalBins;j++){
                    totals[j] = 0;
                }
                for(Cluster cluster : css.getClusters()){
                    int size = cluster.getTotalMolecules();
                    int binNumber = (size - minimum)/binSize;
                    if(binNumber >= 0 && binNumber < totalBins){
                        totals[binNumber] += 1;
                    }
                }
                // Have to get the total number of unbound molecules outside the cluster loop
                if(minimum <= 1){
                    int binNumber = (1-minimum)/binSize;
                    if(binNumber < totalBins){
                        totals[binNumber] += css.getTotalUnboundMolecules();
                    }
                }
                
                for(int j=0;j<totalBins;j++){
                    averageSizes[i][j] += totals[j];
                    av2Sizes[i][j] += totals[j]*totals[j];
                }
            }
            
            double totalRuns = (double)clusterData.getSingleRuns().size();
            if(totalRuns > 1){
                for(int j=0;j<totalBins;j++){
                    averageSizes[i][j] /= totalRuns;
                    av2Sizes[i][j] /= totalRuns;
                    double variance = av2Sizes[i][j] - averageSizes[i][j]*averageSizes[i][j];
                    // Rescale the variance so it agrees with the usual statistical definition (N-1 degrees of freedom)
                    variance = (totalRuns/(totalRuns-1))*variance;
                    if(variance > 0){
                        stDevSizes[i][j] = Math.sqrt(variance);
                    }   
                }
            }
        }     
        // </editor-fold>
    }
    
    /* ***********  GET AND SET HISTOGRAM PARAMETERS *****************/
    
    public void setMinimum(int minimum){
        this.minimum = minimum;
    }
    
    public int getMinimum(){
        return minimum;
    }
    
    public void setMaximum(int maximum){
        this.maximum = maximum;
    }
    
    public int getMaximum(){
        return maximum;
    }
    
    public void setToActualMaximum(){
        maximum = actualMaximum;
    }
    
    public void setToActualMinimum(){
        minimum = actualMinimum;
    }
    
    public void setBinSize(int binSize){
        this.binSize = binSize;
    }
    
    public int getBinSize(){
        return binSize;
    }
    
    public int getTotalBins(){
        return totalBins;
    }
    
    /* ************** GET AND SET AUTO FLAGS *****************************/
    
    public void setAutoMinimum(boolean bool){
        autoMinimum = bool;
        if(bool){
            minimum = actualMinimum;
        }
    }
    
    public boolean getAutoMinimum(){
        return autoMinimum;
    }
    
    public void setAutoMaximum(boolean bool){
        autoMaximum = bool;
        if(bool){
            maximum = actualMaximum;
        }
    }
    
    public boolean getAutoMaximum(){
        return autoMaximum;
    }
    
    public void setAutoBinSize(boolean bool){
        autoBinSize = bool;
        if(bool){
            binSize = 1;
        }
    }
    
    public boolean getAutoBinSize(){
        return autoBinSize;
    }
    
    /* *****************   GET BIN NAME ********************************/
    
    public String binName(int i){
        // <editor-fold defaultstate="collapsed" desc="Method code">
        if(i < totalBins){
            if(binSize == 1){
                return Integer.toString(minimum + binSize*i);
            } else {
                int lowerBound = minimum + binSize*i;
                int upperBound = lowerBound + binSize - 1;
                return lowerBound + " - " + upperBound;
            }
        } else {
            return "Out of bounds.";
        }
        // </editor-fold>
    }
    
    /* *****************  GET THE TIMES AND DATA ***********************/
    
    public double [] getTimes(){
        return times;
    }
    
    public double getTime(int i){
        return times[i];
    }
    
    public double [][] getAverageSizes(){
        return averageSizes;
    }
    
    public double getAverageSizes(int i, int j){
        return averageSizes[i][j];
    }
    
    public double [][] getStDevSizes(){
        return stDevSizes;
    }
    
    public double getStDevSizes(int i, int j){
        return stDevSizes[i][j];
    }
}
