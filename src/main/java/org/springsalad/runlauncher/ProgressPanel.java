/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.util.Scanner;
import javax.swing.*;
import java.io.*;
import javax.swing.event.ChangeListener;

public class ProgressPanel extends JPanel implements Runnable {
    
    private final Thread t;
    private final JProgressBar progressBar = new JProgressBar();
    private final File outputFile;
    
    public ProgressPanel(String name, int runNumber, File outputFile){
        super();
        t = new Thread(this);
        this.outputFile = outputFile;
        JLabel label;
        if(runNumber != -1){
            label = new JLabel(name + " Run " + runNumber);
        } else {
            label = new JLabel(name);
        }
        this.add(label);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        
        this.add(progressBar);
    }
    
    @Override
    public void run(){
        watchFile();
    }
    
    public void startProgressBar(){
        t.start();
    }
    
    public void watchFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        BufferedReader br = null;
        FileReader fr = null;
        String lastLine = null;
        Scanner lineScanner;
        int percentComplete = 0;
        while(true){
            try{
                Thread.sleep(2000);
            } catch(InterruptedException ire){
                ire.printStackTrace(System.out);
            }
            try{
                fr = new FileReader(outputFile);
                br = new BufferedReader(fr);
                Scanner sc = new Scanner(br);
                // Get to the last line
                while(sc.hasNextLine()){
                    lastLine = sc.nextLine();
                }
                sc.close();
                if(lastLine != null){
                    lineScanner = new Scanner(lastLine);
                    // Skip "Simulation"
                    lineScanner.next();
                    String percent = lineScanner.next();
                    percent = percent.substring(0,percent.length()-1);
                    percentComplete = Integer.parseInt(percent);
                    lineScanner.close();
                }
            } catch(FileNotFoundException fne){
                fne.printStackTrace(System.out);
            } finally {
                if(br != null){
                    try{
                        br.close();
                    } catch(IOException bioe){
                        bioe.printStackTrace(System.out);
                    }
                }
                if(fr != null){
                    try{
                        fr.close();
                    } catch(IOException fioe){
                        fioe.printStackTrace(System.out);
                    }
                }
            }
            progressBar.setValue(percentComplete);
            if(percentComplete == 100){
                break;
            }
        }
        // </editor-fold>
    }
    
    public void addProgressBarListener(ChangeListener listener){
        progressBar.addChangeListener(listener);
    }
    
    public int getProgress(){
        return progressBar.getValue();
    }
    
    public boolean isFinished(){
        return progressBar.getValue() == 100;
    }
    
}
