/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.io.*;

public class RunLauncher implements Runnable {
    
    private final Process [] process;
    private final Thread t;
    private final int totalRuns;
    private final File inputFile;
    private final File [] outputFile;
    private final boolean parallel;
    private final int numberSimultaneous;
    
    public RunLauncher(Process [] process, File inputFile, File [] outputFile, boolean parallel, int numberSimultaneous){
        this.inputFile = inputFile;
        this.totalRuns = outputFile.length;
        this.outputFile = outputFile;
        this.t = new Thread(this);
        this.process = process;
        this.parallel = parallel;
        this.numberSimultaneous = numberSimultaneous;
    }

    private File getSolverExecutable() {

        String os_name = System.getProperty("os.name").toLowerCase();
        String os_arch = System.getProperty("os.arch").toLowerCase();

        if (os_name.contains("linux")) {
            return new File("./localsolvers/linux64/langevin_x64");

        }else if (os_name.contains("windows")) {
            return new File("./localsolvers/win64/langevin_x64.exe");

        }else if (os_name.contains("mac")) {
            if (os_arch.equals("x86_64")) {
                return new File("./localsolvers/macos_x86_64/langevin_x64");
            } else if (os_arch.equals("aarch64")) {
                return new File("./localsolvers/macos_arm64/langevin_arm64");
            } else {
                throw new IllegalStateException("Unsupported architecture: " + os_arch);
            }

        } else {
            throw new IllegalStateException("Unsupported OS: " + os_name);
        }

    }
    
    private void launchSequentialRuns(){

        for(int i=0;i<totalRuns;i++){
            String runCounter = Integer.toString(i);
            // switch statement to determine the platform we are running on, linux, macos or windows
            try{
                ProcessBuilder builder = new ProcessBuilder(
                        getSolverExecutable().getAbsolutePath(),
                        "simulate",
                        "--output-log", outputFile[i].getAbsolutePath(),
                        inputFile.getAbsolutePath(),
                        runCounter);
                builder.inheritIO();
                process[0] = builder.start();
            } catch(IOException ioe2){
                ioe2.printStackTrace(System.out);
            }
            if(process[0] != null){
                try{
                    int exitCode = process[0].waitFor();
                    System.out.println("Process returned exit code " + exitCode);
                } catch(InterruptedException ie){
                    ie.printStackTrace(System.out);
                }
            }
        }
        // </editor-fold>
    }
    
    private void launchParallelRuns(){
        for(int i=0;i<totalRuns;i++){
            String runCounter = Integer.toString(i);
                try{
                    ProcessBuilder builder = new ProcessBuilder(
                            getSolverExecutable().getAbsolutePath(),
                            "simulate",
                            "--output-log", outputFile[i].getAbsolutePath(),
                            inputFile.getAbsolutePath(),
                            runCounter);
                    builder.inheritIO();
                    process[i] = builder.start();
                } catch(IOException ioe2){
                    ioe2.printStackTrace(System.out);
                }
             if(0 == (i+1)%numberSimultaneous){
                // Wait for all the currently running processes to finish. 
                // WOULD BE BETTER TO LAUNCH ANOTHER AS SOON AS ONE FINISHES,
                // BUT I DON'T HAVE TIME TO FIGURE OUT HOW TO DO THAT.
                for(int j=i-numberSimultaneous+1;j<=i;j++){
                    if(process[j] != null){
                        try{
                            process[j].waitFor();
                        } catch(InterruptedException ie){
                            ie.printStackTrace(System.out);
                        }
                    }
                }
            }
        }
        // </editor-fold>
    }
    
    public void start(){
        t.start();
    }
    
    @Override
    public void run(){
        if(parallel){
            launchParallelRuns();
        } else {
            launchSequentialRuns();
        }
    }
    
}
